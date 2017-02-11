package com.ownspec.center.service.component;

import static com.ownspec.center.model.DistributionLevel.PUBLIC;
import static com.ownspec.center.model.component.ComponentType.RESOURCE;
import static com.ownspec.center.util.OsUtils.mergeWithNotNullProperties;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

import com.ownspec.center.dto.ComponentVersionDto;
import com.ownspec.center.dto.EstimatedTimeDto;
import com.ownspec.center.dto.UserComponentDto;
import com.ownspec.center.model.DistributionLevel;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.user.UserComponent;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.ProjectRepository;
import com.ownspec.center.repository.component.ComponentReferenceRepository;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.repository.component.ComponentVersionRepository;
import com.ownspec.center.repository.user.UserComponentRepository;
import com.ownspec.center.repository.user.UserRepository;
import com.ownspec.center.repository.workflow.WorkflowInstanceRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.AuthenticationService;
import com.ownspec.center.service.EmailService;
import com.ownspec.center.service.EstimatedTimeService;
import com.ownspec.center.service.GitService;
import com.ownspec.center.service.UploadService;
import com.ownspec.center.service.UserService;
import com.ownspec.center.service.composition.CompositionService;
import com.ownspec.center.service.content.ContentConfiguration;
import com.ownspec.center.service.workflow.WorkflowService;
import com.ownspec.center.util.AbstractMimeMessage;
import com.ownspec.center.util.OsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
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
  private ComponentVersionRepository componentVersionRepository;


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
  private ContentConfiguration contentConfiguration;

  @Autowired
  private CompositionService compositionService;

  @Autowired
  private UserComponentRepository userComponentRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private UploadService uploadService;

  @Autowired
  private ComponentTagService componentTagService;

  @Autowired
  private WorkflowService workflowService;


  public List<Component> findAll(Long projectId, ComponentType[] types, String[] tags, String query) {
    return null;
/*
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
    }*/
  }


  public Pair<Component, ComponentVersion> create(ComponentVersionDto source) {
    // TODO: 27/09/16 handle case if transaction fails

    Resource resource = null;

    if (source.getUploadedFileId() == null) {
      resource = new ByteArrayResource(defaultIfEmpty(source.getContent(), "test").getBytes(UTF_8));
    } else {
      resource = uploadService.findResource(source.getUploadedFileId()).get();
    }

    String filename;

    if (source.getType() != RESOURCE) {
      filename = UUID.randomUUID().toString() + ".html";
    } else {
      filename = source.getFilename();
    }

    Project project = null;

    if (source.getProjectId() != null) {
      project = projectRepository.findOne(source.getProjectId());
    }


    // Component
    Component component = new Component();
    component.setProject(project);
    component.setType(source.getType());
    component.setVcsId(UUID.randomUUID().toString());


    // Update
    String hash = gitService.updateAndCommit(component.getVcsId(), filename, resource, authenticationService.getAuthenticatedUser(), "");

    // Workflow Instance
    Pair<WorkflowInstance, WorkflowStatus> workflowStatusPair = workflowService.createNew(hash);


    // Component Version
    ComponentVersion componentVersion = new ComponentVersion();
    componentVersion.setComponent(component);
    componentVersion.setTitle(source.getTitle());
    componentVersion.setFilename(filename);
    componentVersion.setVersion("1");
    componentVersion.setGitReference(hash);
    componentVersion.setWorkflowInstance(workflowStatusPair.getLeft());
    componentVersion.setCoverageStatus(source.getCoverageStatus());
    componentVersion.setRequirementType(source.getRequirementType());


    List<EstimatedTimeDto> estimatedTimes = source.getEstimatedTimes();
    if (estimatedTimes != null) {
      for (EstimatedTimeDto estimatedTime : estimatedTimes) {
        estimatedTimeService.addEstimatedTime(componentVersion, estimatedTime);
      }
    }

    List<UserComponentDto> componentUsers = source.getComponentUsers();
    if (componentUsers != null) {
      for (UserComponentDto componentUser : componentUsers) {
        UserComponent userComponent = new UserComponent();
        Component c = componentRepository.findOne(componentUser.getComponent().getId());
        userComponent.setComponent(c);
        userComponent.setUser(userService.loadUserByUsername(componentUser.getUser().getUsername()));
        userComponentRepository.save(userComponent);
      }
    }

    component = componentRepository.save(component);
    componentVersion = componentVersionRepository.save(componentVersion);

    componentTagService.tagComponent(componentVersion, source.getTags());

    return Pair.of(component, componentVersion);
  }

  public Component findOne(Long id) {
    return componentRepository.findOne(id);
  }

  /**
   * Update component
   * The component is locked to ensure concurrent modification
   *
   * @param source
   * @param componentVersionId
   * @return
   */
  public ComponentVersion update(ComponentVersionDto source, Long componentVersionId) {
    // Lock
    ComponentVersion componentVersion = requireNonNull(componentVersionRepository.findOneAndLock(componentVersionId));
    mergeWithNotNullProperties(source, componentVersion);
    componentTagService.tagComponent(componentVersion, source.getTags());
    return componentVersionRepository.save(componentVersion);
  }


  public ComponentVersion newWorkflowInstance(Long id) {
    // Lock
    ComponentVersion componentVersion = requireNonNull(componentVersionRepository.findOneAndLock(id));

    // Retrieve current workflow status
    WorkflowStatus currentWorkflowStatus = workflowStatusRepository.findLatestWorkflowStatusByWorkflowInstanceId(componentVersion.getWorkflowInstance().getId());

    // Check if the current status is final
    if (!currentWorkflowStatus.getStatus().isFinal()) {
      throw new IllegalStateException("Illegal transition");
    }


    // Create new Workflow Instance
    Pair<WorkflowInstance, WorkflowStatus> workflowStatusPair = workflowService.createNew();

    // Create new component version
    ComponentVersion nextComponentVersion = new ComponentVersion(componentVersion);
    // Increment the version
    nextComponentVersion.setVersion(String.valueOf(Long.valueOf(componentVersion.getVersion()) + 1));
    nextComponentVersion.setWorkflowInstance(workflowStatusPair.getLeft());

    // TODO: clone the link

    componentVersion = componentVersionRepository.save(componentVersion);

    return componentVersion;
  }

  public ComponentVersion updateContent(Long id, byte[] b) {
    return updateContent(id, new ByteArrayResource(b));
  }

  public ComponentVersion updateContent(Long id, Resource resource) {
    return updateContent(requireNonNull(componentVersionRepository.findOneAndLock(id)), resource);
  }

  public ComponentVersion updateContent(ComponentVersion component, byte[] b) {
    return updateContent(component, new ByteArrayResource(b));
  }

  public ComponentVersion updateContent(ComponentVersion componentVersion, Resource resource) {
    // Lock
    componentVersion = requireNonNull(componentVersionRepository.findOneAndLock(componentVersion.getId()));

    // Retrieve current workflow status
    WorkflowStatus currentWorkflowStatus = workflowStatusRepository.findLatestWorkflowStatusByWorkflowInstanceId(componentVersion.getWorkflowInstance().getId());

    // Check if the transition is legit
    if (currentWorkflowStatus.getStatus().isEditable()) {

      if (RESOURCE != componentVersion.getComponent().getType()) {
        contentConfiguration.htmlContentSaver().save(componentVersion, OsUtils.toString(resource));
      } else {
        gitService.updateAndCommit(componentVersion.getComponent().getVcsId(), componentVersion.getFilename(), resource, authenticationService.getAuthenticatedUser(), "");
      }

    } else {
      throw new IllegalStateException("Content update is not allowed for this statue");
    }
    return componentVersion;
  }


  public Pair<String, String> generateContent(ComponentVersion c) {
    return contentConfiguration.htmlContentGenerator().generate(c);
  }

  public WorkflowStatus getCurrentStatus(Long id) {
    return workflowStatusRepository.findLatestWorkflowStatusByComponentVersionId(id);
  }


  public Resource getHeadRawContent(ComponentVersion c) {
    try {
      return gitService.getFile(c.getComponent().getVcsId().toString(), c.getFilename());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Resource getRawContent(ComponentVersion c, String ref) {
    try {
      return gitService.getFile(c.getComponent().getVcsId().toString(), c.getFilename(), ref);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Resource getContent(ComponentVersion c) {
    try {

      return gitService.getFile(c.getComponent().getVcsId(), c.getFilename(), c.getGitReference());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public void remove(Long id) {
    ComponentVersion component = requireNonNull(componentVersionRepository.findOne(id));
    gitService.deleteAndCommit(component.getComponent().getVcsId(), component.getFilename(), authenticationService.getAuthenticatedUser(), "");
    componentRepository.delete(id);
  }



  public Resource diff(Long id, String fromRevision, String toRevision) {
    ComponentVersion component = componentVersionRepository.findOne(id);
    return gitService.diff(component.getComponent().getVcsId(), component.getFilename(), fromRevision, toRevision);
  }

  public ResponseEntity assignTo(Long componentVersionId, Long userId, boolean autoGrantUserAccess, boolean editable) {
    ComponentVersion componentVersion = componentVersionRepository.findOne(componentVersionId);
    User requester = authenticationService.getAuthenticatedUser();
    User assignedUser = userRepository.findOne(userId);

    // Check distribution level regarding user, if grantUserAccess option is not ticked
    if (!PUBLIC.equals(componentVersion.getDistributionLevel())) {
      if (autoGrantUserAccess) {
        grantUserAccess(componentVersion, requester, assignedUser);
      } else {
        checkDistributionLevel(componentVersion, requester, assignedUser);
      }
    }

    // Update and assign componentVersion to user
    WorkflowInstance currentWorkflowInstance = componentVersion.getWorkflowInstance();

    // TODO: does make it sense?
    // NLA: I don't think so
    componentVersion.setAssignedTo(assignedUser);

    // Notify user
    AbstractMimeMessage message = AbstractMimeMessage.builder()
        .addRecipient(assignedUser.getEmail())
        .addRecipientCc(requester.getEmail())
        .subject(componentVersion.getComponent().getType() + "-" + componentVersion.getId() + " has been assigned to you")
        .body("Click on the following link..."); //todo create body
    emailService.send(message);

    // TODO: remove entity from service
    return ResponseEntity.ok().build();
  }

  private void checkDistributionLevel(ComponentVersion component, User requester, User assignedUser) {
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

  private String grantUserAccess(ComponentVersion component, User requester, User user) {
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

    ComponentVersion component = componentVersionRepository.findOne(id);


    Path tempDirectory = Files.createTempDirectory("foo");

    Path compo = contentConfiguration.compositionHtmlContentGenerator().generate(component, true, tempDirectory);

    return compositionService.flyingHtmlToPdf(compo);
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
      userComponent.setVisitedTime(1L);
      userComponentRepository.save(userComponent);
    } else {
      foundUserComponent.addVisit();
      userComponentRepository.save(foundUserComponent);
    }

    return ResponseEntity.ok().build();
  }

  public List<Component> getLastVisited(ComponentType componentType) {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    return userComponentRepository.findTop3ByUserIdAndComponentTypeOrderByLastModifiedDateDesc(authenticatedUser.getId(), componentType).stream()
        .map(uc -> componentRepository.findOne(uc.getComponent().getId()))
        .collect(Collectors.toList());
  }

  public List<Component> getFavorites(ComponentType type) {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    return userComponentRepository.findTop3ByUserIdAndComponentTypeAndFavoriteTrueOrderByLastModifiedDateDesc(authenticatedUser.getId(), type)
        .stream()
        .map(uc -> componentRepository.findOne(uc.getComponent().getId()))
        .collect(Collectors.toList());
  }



  public void updateReference(Long sourceComponentVersionId, Long refId, long targetComponentVersionId) {
    ComponentReference componentReference = componentReferenceRepository.findOne(refId);
    componentReference.setTarget(componentVersionRepository.findOne(targetComponentVersionId));
    componentReferenceRepository.save(componentReference);
  }

  public void updateToLatestReference(Long sourceComponentVersionId, Long refId, long targetComponentVersionId) {
    ComponentReference componentReference = componentReferenceRepository.findOne(refId);
    componentReference.setTarget(componentVersionRepository.findOne(targetComponentVersionId));
    componentReferenceRepository.save(componentReference);
  }


  public List<ComponentReference> findUsePoints(Long targetComponentId, Long targetWorkflowInstanceId) {

    return componentReferenceRepository.findAllByTargetId(targetComponentId);


  }
}
