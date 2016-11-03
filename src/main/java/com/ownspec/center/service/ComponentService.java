package com.ownspec.center.service;

import static com.ownspec.center.dto.ComponentDto.newBuilderFromComponent;
import static com.ownspec.center.dto.ImmutableChangeDto.newChangeDto;
import static com.ownspec.center.dto.ImmutableComponentReferenceDto.newComponentReferenceDto;
import static com.ownspec.center.dto.WorkflowInstanceDto.newBuilderFromWorkflowInstance;
import static com.ownspec.center.dto.WorkflowStatusDto.newBuilderFromWorkflowStatus;
import static com.ownspec.center.model.DistributionLevel.PUBLIC;
import static com.ownspec.center.model.component.QComponent.component;
import static com.ownspec.center.util.OsUtils.mergeWithNotNullProperties;
import static com.querydsl.core.types.dsl.Expressions.booleanOperation;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

import com.google.common.collect.Lists;
import com.ownspec.center.dto.ChangeDto;
import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.dto.ComponentReferenceDto;
import com.ownspec.center.dto.ImmutableWorkflowInstanceDto;
import com.ownspec.center.dto.UserDto;
import com.ownspec.center.dto.WorkflowInstanceDto;
import com.ownspec.center.dto.WorkflowStatusDto;
import com.ownspec.center.model.Comment;
import com.ownspec.center.model.DistributionLevel;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.Revision;
import com.ownspec.center.model.Task;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.CommentRepository;
import com.ownspec.center.repository.ProjectRepository;
import com.ownspec.center.repository.TaskRepository;
import com.ownspec.center.repository.UserRepository;
import com.ownspec.center.repository.component.ComponentReferenceRepository;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.repository.workflow.WorkflowInstanceRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.util.AbstractMimeMessage;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.revwalk.RevCommit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lyrold on 19/09/2016.
 */
@Service
@Transactional
@Slf4j
public class ComponentService {

  @Value("${component.content.summary-length:80}")
  private int summaryLength;

  @Autowired
  private GitService gitService;

  @Autowired
  private ComponentRepository componentRepository;

  @Autowired
  private ComponentReferenceRepository componentReferenceRepository;

  @Autowired
  private WorkflowInstanceRepository workflowInstanceRepository;

  @Autowired
  private WorkflowStatusRepository workflowStatusRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private SecurityService securityService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private EmailService emailService;

  @Autowired
  private TaskRepository taskRepository;


  public List<Component> findAll(Long projectId, ComponentType[] types) {

    List<Predicate> predicates = new ArrayList<>();

    if (projectId != null) {
      predicates.add(component.project.id.eq(projectId));
    } else {
      predicates.add(component.project.id.isNull());
    }

    if (types != null) {
      predicates.add(component.type.in(types));
    }

    if (!predicates.isEmpty()) {
      return Lists.newArrayList(componentRepository.findAll(predicates.size() == 1 ? predicates.get(0) : booleanOperation(Ops.AND, predicates.toArray(new Predicate[0]))));
    } else {
      return componentRepository.findAll();
    }
  }

  public Component create(ComponentDto source) {
    // TODO: 27/09/16 handle case if transaction fails
    Pair<File, String> pair = gitService.createAndCommit(
        new ByteArrayResource(defaultIfEmpty(source.getContent(), "test").getBytes(UTF_8)), securityService.getAuthenticatedUser(), "");

    Project project = null;

    if (source.getProjectId() != null) {
      project = projectRepository.findOne(source.getProjectId());
    }

    Component component = new Component();
    component.setProject(project);
    component.setTitle(source.getTitle());
    component.setType(source.getType());

    WorkflowInstance workflowInstance = new WorkflowInstance();
    workflowInstance.setCurrentGitReference(pair.getRight());
    workflowInstance.setCurrentStatus(Status.OPEN);
    workflowInstance.setComponent(component);

    WorkflowStatus workflowStatus = new WorkflowStatus();
    workflowStatus.setStatus(Status.OPEN);
    workflowStatus.setFirstGitReference(pair.getRight());
    workflowStatus.setLastGitReference(pair.getRight());
    workflowStatus.setWorkflowInstance(workflowInstance);

    component.setCurrentWorkflowInstance(workflowInstance);
    component.setFilePath(pair.getLeft().getAbsolutePath());

    workflowInstanceRepository.save(workflowInstance);
    component = componentRepository.save(component);
    workflowStatus = workflowStatusRepository.save(workflowStatus);

    return component;
  }

  public Component findOne(Long id) {
    return componentRepository.findOne(id);
  }

  public Component update(ComponentDto source, Long id) {
    Component target = requireNonNull(componentRepository.findOne(id));
    mergeWithNotNullProperties(source, target);

    if (source.getContent() != null) {
      updateContent(target, source.getContent().getBytes(UTF_8));
    }

    return componentRepository.save(target);
  }

  public Component updateStatus(Long id, Status nextStatus) {
    Component component = requireNonNull(componentRepository.findOne(id));

    WorkflowStatus workflowStatus = new WorkflowStatus();
    workflowStatus.setStatus(nextStatus);
    workflowStatus.setWorkflowInstance(component.getCurrentWorkflowInstance());

    component.getCurrentWorkflowInstance().setCurrentStatus(nextStatus);
    component.getCurrentWorkflowInstance().setCurrentGitReference(null);


    component = componentRepository.save(component);
    workflowStatus = workflowStatusRepository.save(workflowStatus);

    return component;
  }

  public Component updateContent(Long id, byte[] content) {
    return updateContent(requireNonNull(componentRepository.findOne(id)), content);
  }

  public Component updateContent(Component component, byte[] content) {


    extractAndCreateReference(component, content);

    return component;
  }

  public String getContent(Component c) {
    try {
      if (c.getFilePath() != null) {

        return FileUtils.readFileToString(new File(c.getFilePath()), "UTF-8");
      } else {
        return "";
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void remove(Long id) {
    Component target = requireNonNull(componentRepository.findOne(id));
    gitService.deleteAndCommit(target.getFilePath(), securityService.getAuthenticatedUser(), "");
    componentRepository.delete(id);
  }

  public List<Comment> getComments(Long componentId) {
    return commentRepository.findAllByComponentId(componentId, new Sort(Sort.Direction.DESC, "id"));
  }

  public Comment addComment(Long id, String value) {
    Comment comment = new Comment();
    comment.setValue(value);
    Component target = requireNonNull(componentRepository.findOne(id));
    comment.setComponent(target);
    return commentRepository.save(comment);
  }

  public List<Revision> getRevisionsForComponent(Long id) {
    return null;
  }

  public List<WorkflowInstanceDto> getWorkflowStatuses(Long id) {
    Component component = findOne(id);


    //List<WorkflowStatus> workflowStatuses = workflowStatusRepository.findAllByWorkflowInstanceComponentId(component.getId(), new Sort("id"));

    List<RevCommit> commits = Lists.reverse(Lists.newArrayList(gitService.getHistoryFor(component.getFilePath())));


    int commitIndex = 0;


    List<WorkflowInstanceDto> workflowInstanceDtos = new ArrayList<>();

    for (WorkflowInstance workflowInstance : workflowInstanceRepository.findAllByComponentId(component.getId(), new Sort("id"))) {

      ImmutableWorkflowInstanceDto.Builder workflowInstanceDto = newBuilderFromWorkflowInstance(workflowInstance);

      List<WorkflowStatusDto> workflowStatusDtos = new ArrayList<>();

      for (WorkflowStatus workflowStatus : workflowStatusRepository.findAllByWorkflowInstanceId(workflowInstance.getId(), new Sort("id"))) {

        List<ChangeDto> changeDtos = new ArrayList<>();

        if (workflowStatus.getFirstGitReference() != null) {


          Validate.isTrue(commits.get(commitIndex).name().equals(workflowStatus.getFirstGitReference()));

          while (commitIndex < commits.size()) {
            RevCommit revCommit = commits.get(commitIndex);

            User commiter = userRepository.findByUsername(revCommit.getAuthorIdent().getName());

            changeDtos.add(newChangeDto()
                               .date(Instant.ofEpochSecond((long) revCommit.getCommitTime()))
                               .revision(revCommit.name())
                               .user(UserDto.createFromUser(commiter))
                               .build());

            if (revCommit.name().equals(workflowStatus.getLastGitReference())) {
              commitIndex++;
              break;
            }
            commitIndex++;
          }
        }

        workflowStatusDtos.add(newBuilderFromWorkflowStatus(workflowStatus)
                                   .changes(changeDtos)
                                   .build());


      }
      workflowInstanceDtos.add(workflowInstanceDto
                                   .workflowStatuses(workflowStatusDtos)
                                   .build());
    }

    return workflowInstanceDtos;

  }

  public Map<Component, byte[]> searchForInnerDraftComponents(byte[] content) {
    //todo
    return null;
  }



  public Pair<String, String> generateContent(Component c){

    String content = getContent(c);

    Document document = Jsoup.parse(content);

    Element body = document.getElementsByTag("body").first();

    generateContent(c, document, body);

    String substring = body.text().replaceAll("(?<=.{"+summaryLength+"})\\b.*", "...");

    return Pair.of(body.html() , substring);
  }

  private void generateContent(Component c, Document doc, Element parent) {


    Deque<Element> stack = new LinkedList<>(parent.children());

    while (!stack.isEmpty()) {
      Element element = stack.pop();

      if ("div".equals(element.nodeName()) && element.hasAttr("data-requirement-id")) {
        Long targetComponentId = Long.valueOf(element.attr("data-requirement-id"));
        Component nestedComponent = componentRepository.findOne(targetComponentId);

        Document nestedDocument = Jsoup.parse(getContent(nestedComponent));
        Element nestedBody = nestedDocument.getElementsByTag("body").first();

        // Create title tag
        element.appendChild(doc.createElement("div").addClass("requirements-id").text(nestedComponent.getId().toString()));

        // Extract reference from the nested reference content (second children, first children is the title)
        generateContent(nestedComponent, nestedDocument, nestedBody);

        //Create content tag
        Element nestedContent = doc.createElement("div").addClass("requirements-content");
        new ArrayList<>(nestedBody.childNodes()).forEach(nestedContent::appendChild);
        element.appendChild(nestedContent);

      } else {
        stack.addAll(element.children());
      }
    }

  }


  public void extractAndCreateReference(Component c, byte[] contentAsByteArray) {
    extractReference(c, new String(contentAsByteArray));
  }


  public void extractReference(Component component, String content) {
    try {
      Document document = Jsoup.parse(content);

      extractReference(component, document, document.getElementsByTag("body").first());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  public void extractReference(Component component, Document document, Element parent) {
    try {
      WorkflowStatus workflowStatus = workflowStatusRepository.findLatestStatusByWorkflowInstanceComponentId(component.getId());

      if (!workflowStatus.getStatus().isEditable()) {
        LOG.info("Component [{}] is not editable", component);
        return;
      }


      List<Pair<Long, Long>> references = new ArrayList<>();

      Deque<Element> stack = new LinkedList<>(parent.children());

      while (!stack.isEmpty()) {
        Element element = stack.pop();

        if ("div".equals(element.nodeName()) && element.hasAttr("data-requirement-id")) {
          Long targetComponentId = Long.valueOf(element.attr("data-requirement-id"));
          references.add(Pair.of(targetComponentId, Long.valueOf(element.attr("data-workflow-instance-id"))));
          Component nestedComponent = componentRepository.findOne(targetComponentId);
          // extract reference from the nested reference content (second children, first children is the title)
          extractReference(nestedComponent, document, element.children().get(1));
          // remove all the childrens to keep only the reference
          element.empty();
        } else {
          stack.addAll(element.children());
        }
      }

      // Update content
      String content = parent.html();
      String hash = gitService.updateAndCommit(new ByteArrayResource(content.getBytes(UTF_8)), component.getFilePath(), securityService.getAuthenticatedUser(), "");
      if (hash == null) {
        return;
      }

      if (workflowStatus.getFirstGitReference() == null) {
        workflowStatus.setFirstGitReference(hash);
        workflowStatus.setLastGitReference(hash);
      } else {
        workflowStatus.setLastGitReference(hash);
      }

      workflowStatusRepository.save(workflowStatus);

      component.getCurrentWorkflowInstance().setCurrentGitReference(hash);
      componentRepository.save(component);


      // Delete old refs
      Long deletedRef = componentReferenceRepository.deleteBySourceIdAndSourceWorkflowInstanceId(component.getId(),
          component.getCurrentWorkflowInstance().getId());

      // Save the new references
      for (Pair<Long, Long> reference : references) {
        ComponentReference componentReference = new ComponentReference();
        componentReference.setSource(component);
        componentReference.setSourceWorkflowInstance(component.getCurrentWorkflowInstance());
        componentReference.setTarget(componentRepository.findOne(reference.getLeft()));
        componentReference.setTargetWorkflowInstance(workflowInstanceRepository.findOne(reference.getRight()));
        componentReferenceRepository.save(componentReference);
      }

      LOG.info("Component [{}], Deleted [{}], Created [{}]", component, deletedRef, references.size());


    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Resource diff(Long id, String fromRevision, String toRevision) {
    Component component = findOne(id);

    if (fromRevision == null) {
      List<RevCommit> commits = Lists.reverse(Lists.newArrayList(gitService.getHistoryFor(component.getFilePath())));

      for (int i = 0; i < commits.size(); i++) {
        if (commits.get(i).getId().name().equals(toRevision)) {
          if (i != 0) {
            fromRevision = commits.get(i - 1).getId().name();
            break;
          } else {
            return null;
          }
        }
      }
    }
    return gitService.doDiff(component.getFilePath(), fromRevision, toRevision);
  }


  public List<ComponentReferenceDto> getComponentReferences(Long componentId) {
    Component component = findOne(componentId);

    return componentReferenceRepository.findAllBySourceIdAndSourceWorkflowInstanceId(component.getId(), component.getCurrentWorkflowInstance().getId())
        .stream()
        .map(r -> newComponentReferenceDto()
            .source(newBuilderFromComponent(component).build())
            .sourceWorkflowInstance(newBuilderFromWorkflowInstance(r.getSourceWorkflowInstance()).build())
            .target(newBuilderFromComponent(r.getTarget()).build())
            .targetWorkflowInstance(newBuilderFromWorkflowInstance(r.getTargetWorkflowInstance()).build())
            .build()
        ).collect(Collectors.toList());
  }

  public ResponseEntity assignTo(Long componentId, Long userId, boolean autoGrantUserAccess, boolean editable) {
    Component component = findOne(componentId);
    User requester = securityService.getAuthenticatedUser();
    User assignedUser = userRepository.findOne(userId);

    // Check distribution level regarding user, if grantUserAccess option is not ticked
    if (!PUBLIC.equals(component.getDistributionLevel())) {
      if (autoGrantUserAccess) {
        grantUserAccess(component, requester, assignedUser);
      } else {
        checkDistributionLevel(component, requester, assignedUser);
      }
    }

    // Update and assign component to user
    WorkflowInstance currentWorkflowInstance = component.getCurrentWorkflowInstance();
    currentWorkflowInstance.setCurrentStatus(editable ? Status.OPEN : Status.IN_VALIDATION); //todo does make it sense?
    component.setCurrentWorkflowInstance(currentWorkflowInstance);
    component.setEditable(editable);
    component.setAssignedTo(assignedUser);

    // Notify user
    AbstractMimeMessage message = AbstractMimeMessage.builder()
                                                     .addRecipient(assignedUser.getEmail())
                                                     .addRecipientCc(requester.getEmail())
                                                     .subject(component.getType() + "-" + component.getId() + " has been assigned to you")
                                                     .body("Click on the following link..."); //todo create body
    emailService.send(message);

    //Create new task for assigned user
    Task task = new Task();
    task.setOwner(assignedUser);

    // Track and save change
    componentRepository.save(component);
    userRepository.save(assignedUser);
    taskRepository.save(task);

    return ResponseEntity.ok().build();
  }

  private void checkDistributionLevel(Component component, User requester, User assignedUser) {
    DistributionLevel componentDistributionLevel = component.getDistributionLevel();
    switch (componentDistributionLevel) {
      case INTERNAL:
        break;

      case RESTRICTED:
        break;

      case SECRET:
        break;
    }
  }

  private String grantUserAccess(Component component, User requester, User user) {
    DistributionLevel componentDistributionLevel = component.getDistributionLevel();

    switch (componentDistributionLevel) {
      case INTERNAL:
        // Check user company or role in case of admin
        break;

      case RESTRICTED:
        break;

      case SECRET:
        if (!"admin".equals(requester.getRole())) {
          // Request admin granted access for user
          return "A SECRET EMPOWERMENT request, for user [" + user.getUsername() + "] has been to your admin";

        } else {
          return "Success";
        }
    }

    return "";
  }
}
