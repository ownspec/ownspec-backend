package com.ownspec.center.service.component;

import static com.ownspec.center.dto.ImmutableChangeDto.newChangeDto;
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
import com.ownspec.center.dto.EstimatedTimeDto;
import com.ownspec.center.dto.ImmutableWorkflowInstanceDto;
import com.ownspec.center.dto.UserDto;
import com.ownspec.center.dto.WorkflowInstanceDto;
import com.ownspec.center.dto.WorkflowStatusDto;
import com.ownspec.center.model.DistributionLevel;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.Revision;
import com.ownspec.center.model.Task;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.user.UserComponent;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.ProjectRepository;
import com.ownspec.center.repository.TaskRepository;
import com.ownspec.center.repository.component.ComponentReferenceRepository;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.repository.user.UserComponentRepository;
import com.ownspec.center.repository.user.UserRepository;
import com.ownspec.center.repository.workflow.WorkflowInstanceRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.AuthenticationService;
import com.ownspec.center.service.CompositionService;
import com.ownspec.center.service.EmailService;
import com.ownspec.center.service.EstimatedTimeService;
import com.ownspec.center.service.GitService;
import com.ownspec.center.service.content.ContentConfiguration;
import com.ownspec.center.service.content.HtmlContentGenerator;
import com.ownspec.center.util.AbstractMimeMessage;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
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


  @Autowired
  private GitService gitService;

  @Autowired
  private ComponentRepository componentRepository;

  @Autowired
  private ComponentReferenceRepository componentReferenceRepository;

  @Autowired
  private WorkflowInstanceRepository workflowInstanceRepository;

  @Autowired
  private EstimatedTimeService estimatedTimeService;

  @Autowired
  private WorkflowStatusRepository workflowStatusRepository;


  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private EmailService emailService;

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private ContentConfiguration contentConfiguration;

  @Autowired
  private CompositionService compositionService;

  @Autowired
  private UserComponentRepository userComponentRepository;


  public List<Component> findAll(Long projectId, ComponentType[] types, String query) {

    List<Predicate> predicates = new ArrayList<>();

    if (projectId != null) {
      predicates.add(component.project.id.eq(projectId));
    } else {
      predicates.add(component.project.id.isNull());
    }

    if (StringUtils.isNotBlank(query)) {
      predicates.add(component.title.containsIgnoreCase(query));
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
        new ByteArrayResource(defaultIfEmpty(source.getContent(), "test").getBytes(UTF_8)), authenticationService.getAuthenticatedUser(), "");

    Project project = null;

    if (source.getProjectId() != null) {
      project = projectRepository.findOne(source.getProjectId());
    }

    Component component = new Component();
    component.setProject(project);
    component.setTitle(source.getTitle());
    component.setType(source.getType());

    WorkflowInstance workflowInstance = new WorkflowInstance();
    workflowInstance.setComponent(component);

    WorkflowStatus workflowStatus = new WorkflowStatus();
    workflowStatus.setStatus(Status.OPEN);
    workflowStatus.setFirstGitReference(pair.getRight());
    workflowStatus.setLastGitReference(pair.getRight());
    workflowStatus.setWorkflowInstance(workflowInstance);

    component.setCurrentWorkflowInstance(workflowInstance);
    component.setFilePath(pair.getLeft().getAbsolutePath());

    component.setCoverageStatus(source.getCoverageStatus());
    component.setRequirementType(source.getRequirementType());

    if (source.getEstimatedTimes() != null) {
      for (EstimatedTimeDto estimatedTimeDto : source.getEstimatedTimes()) {
        estimatedTimeService.addEstimatedTime(component, estimatedTimeDto);
      }
    }
    workflowInstanceRepository.save(workflowInstance);
    component = componentRepository.save(component);
    workflowStatus = workflowStatusRepository.save(workflowStatus);

    return component;
  }

  public Component findOne(Long id) {
    return componentRepository.findOne(id);
  }

  /**
   * Update component
   * The component is locked to ensure concurrent modification
   *
   * @param source
   * @param id
   * @return
   */
  public Component update(ComponentDto source, Long id) {
    // Lock
    Component target = requireNonNull(componentRepository.findOneAndLock(id));
    mergeWithNotNullProperties(source, target);

    if (source.getContent() != null) {
      updateContent(target, source.getContent().getBytes(UTF_8));
    }

    return componentRepository.save(target);
  }

  public Component updateStatus(Long id, Status nextStatus) {
    // Lock
    Component component = requireNonNull(componentRepository.findOneAndLock(id));

    // Retrieve current workflow status
    WorkflowStatus currentWorkflowStatus = workflowStatusRepository.findLatestWorkflowStatusByWorkflowInstanceId(component.getCurrentWorkflowInstance().getId());

    // Check if the transition is possible
    if (!currentWorkflowStatus.getStatus().isAllowedTransition(nextStatus)) {
      throw new IllegalStateException("Illegal transition");
    }

    // Create and stave the next status
    WorkflowStatus workflowStatus = new WorkflowStatus();
    workflowStatus.setStatus(nextStatus);
    workflowStatus.setWorkflowInstance(component.getCurrentWorkflowInstance());
    workflowStatus = workflowStatusRepository.save(workflowStatus);

    return component;
  }

  public Component newWorkflowInstance(Long id) {
    // Lock
    Component component = requireNonNull(componentRepository.findOneAndLock(id));

    // Retrieve current workflow status
    WorkflowStatus currentWorkflowStatus = workflowStatusRepository.findLatestWorkflowStatusByWorkflowInstanceId(component.getCurrentWorkflowInstance().getId());

    // Check if the current status is final
    if (!currentWorkflowStatus.getStatus().isFinal()) {
      throw new IllegalStateException("Illegal transition");
    }

    // Retrieve current workflow status
    WorkflowStatus lastWorkflowStatusWithGit =
        workflowStatusRepository.findLatestWorkflowStatusWithGitReferenceByWorkflowInstanceId(component.getCurrentWorkflowInstance().getId());


    // Create and stave the next status
    WorkflowInstance workflowInstance = new WorkflowInstance();
    workflowInstance.setComponent(component);

    WorkflowStatus workflowStatus = new WorkflowStatus();
    workflowStatus.setStatus(Status.OPEN);
    workflowStatus.setWorkflowInstance(workflowInstance);
    // Use the last know git reference
    // Maybe a new git commit will be more suitable
    //workflowStatus.setFirstGitReference(lastWorkflowStatusWithGit.getLastGitReference());
    //workflowStatus.setLastGitReference(lastWorkflowStatusWithGit.getLastGitReference());

    component.setCurrentWorkflowInstance(workflowInstance);

    workflowInstanceRepository.save(workflowInstance);
    component = componentRepository.save(component);
    workflowStatus = workflowStatusRepository.save(workflowStatus);

    return component;
  }

  public Component updateContent(Long id, byte[] content) {
    return updateContent(requireNonNull(componentRepository.findOneAndLock(id)), content);
  }

  public Component updateContent(Component component, byte[] content) {
    // Lock
    component = requireNonNull(componentRepository.findOneAndLock(component.getId()));

    // Retrieve current workflow status
    WorkflowStatus currentWorkflowStatus = workflowStatusRepository.findLatestWorkflowStatusByWorkflowInstanceId(component.getCurrentWorkflowInstance().getId());

    // Check if the transition is legit
    if (currentWorkflowStatus.getStatus().isEditable()) {
      contentConfiguration.htmlContentSaver().save(component, content);
    } else {
      throw new IllegalStateException("Content update is not allowed for this statue");
    }
    return component;
  }


  public Pair<String, String> generateContent(Component c) {
    return contentConfiguration.htmlContentGenerator().generate(c);
  }

  public WorkflowStatus getCurrentStatus(Long id) {
    return workflowStatusRepository.findLatestWorkflowStatusByComponentId(id);
  }


  public Resource getHeadRawContent(Component c) {
    try {
      if (c.getFilePath() != null) {
        return gitService.getFile(c.getFilePath());
      } else {
        throw new IllegalStateException("component file does not exist");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Resource getRawContent(Component c, String ref) {
    try {
      if (c.getFilePath() != null) {
        return gitService.getFile(c.getFilePath(), ref);
      } else {
        throw new IllegalStateException("component file does not exist");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public void remove(Long id) {
    Component target = requireNonNull(componentRepository.findOne(id));
    gitService.deleteAndCommit(target.getFilePath(), authenticationService.getAuthenticatedUser(), "");
    componentRepository.delete(id);
  }


  public List<Revision> getRevisionsForComponent(Long id) {
    return null;
  }

  public List<WorkflowInstanceDto> getWorkflowStatuses(Long id) {
    Component component = findOne(id);
    List<RevCommit> commits = Lists.reverse(Lists.newArrayList(gitService.getHistoryFor(component.getFilePath())));

    int commitIndex = 0;

    List<WorkflowInstanceDto> workflowInstanceDtos = new ArrayList<>();

    for (WorkflowInstance workflowInstance : workflowInstanceRepository.findAllByComponentId(component.getId(), new Sort("id"))) {

      ImmutableWorkflowInstanceDto.Builder workflowInstanceDto = newBuilderFromWorkflowInstance(workflowInstance);

      List<WorkflowStatusDto> workflowStatusDtos = new ArrayList<>();

      for (WorkflowStatus workflowStatus : workflowStatusRepository.findAllByWorkflowInstanceId(workflowInstance.getId(), new Sort("id"))) {
        List<ChangeDto> changeDtos = new ArrayList<>();

        // Workflow status has an index and we have not finished parsing commit
        // commitIndex may be = commits.size then the current workflow instance has not commit, eg. a new workflow instance is created
        if (workflowStatus.getFirstGitReference() != null && commitIndex < commits.size()) {
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
          .currentWorkflowStatus(workflowStatusDtos.get(workflowStatusDtos.size() - 1))
          .build());
    }

    return workflowInstanceDtos;

  }

  public Map<Component, byte[]> searchForInnerDraftComponents(byte[] content) {
    //todo
    return null;
  }


  public Resource diff(Long id, String fromRevision, String toRevision) {
    Component component = findOne(id);
    return gitService.diff(component.getFilePath(), fromRevision, toRevision);
  }

  public ResponseEntity assignTo(Long componentId, Long userId, boolean autoGrantUserAccess, boolean editable) {
    Component component = findOne(componentId);
    User requester = authenticationService.getAuthenticatedUser();
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

    // TODO: does make it sense?
    // NLA: I don't think so
    updateStatus(componentId, editable ? Status.OPEN : Status.IN_VALIDATION);
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


  public Resource composePdf(Long id) throws IOException {

    Component component = findOne(id);

    Pair<String, String> generate = contentConfiguration.htmlContentGenerator().generate(component, true);

    return compositionService.htmlToPdf(component, new ByteArrayResource(generate.getLeft().getBytes(UTF_8)));
  }

  public ResponseEntity addVisit(Long id) {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    UserComponent foundUserComponent = userComponentRepository.findOneByUserIdAndComponentId(
        authenticatedUser.getId(), id);

    if (foundUserComponent == null) {
      Component component = componentRepository.findOne(id);
      UserComponent userComponent = new UserComponent();
      userComponent.setUser(authenticatedUser);
      userComponent.setComponent(component);
      userComponent.setComponentType(component.getType());
      userComponent.setVisitedTime(1L);
      userComponentRepository.save(userComponent);
    } else {
      foundUserComponent.addVisit();
      userComponentRepository.save(foundUserComponent);
    }

    return ResponseEntity.ok().build();
  }

  public List<ComponentDto> getLastVisited(ComponentType componentType) {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    return userComponentRepository.findTop3ByUserIdAndComponentTypeOrderByLastModifiedDateDesc(authenticatedUser.getId(), componentType).stream()
        .map(uc -> ComponentDto.fromComponent(uc.getComponent()))
        .collect(Collectors.toList());
  }

  public List<ComponentDto> getFavorites(ComponentType type) {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    return userComponentRepository.findTop3ByUserIdAndComponentTypeAndFavoriteTrueOrderByLastModifiedDateDesc(authenticatedUser.getId(), type)
        .stream()
        .map(uc -> ComponentDto.fromComponent(uc.getComponent()))
        .collect(Collectors.toList());
  }

}
