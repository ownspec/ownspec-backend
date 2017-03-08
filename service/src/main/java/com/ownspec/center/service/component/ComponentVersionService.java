package com.ownspec.center.service.component;

import static com.ownspec.center.util.OsUtils.mergeWithNotNullProperties;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

import com.ownspec.center.dto.ComponentVersionDto;
import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.ComponentVersion;
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
import com.ownspec.center.util.OsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by lyrold on 19/09/2016.
 */
@Service
@Transactional
@Slf4j
public class ComponentVersionService {


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

    estimatedTimeService.addAndUpdateEstimatedTimes(componentVersion, source.getEstimatedTimes());

    Resource resource = null;
    if (source.getUploadedFileId() == null) {
      resource = new ByteArrayResource(defaultIfEmpty(source.getContent(), "test").getBytes(UTF_8));
    } else {
      resource = uploadService.findResource(source.getUploadedFileId()).get();
    }


    updateContent(componentVersion, resource);

    return componentVersionRepository.save(componentVersion);
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

      if (ComponentType.RESOURCE != componentVersion.getComponent().getType()) {
        contentConfiguration.htmlContentSaver().save(componentVersion, OsUtils.toString(resource));
      } else {
        updateRawContent(componentVersion, resource);
      }

    } else {
      throw new IllegalStateException("Content update is not allowed for this statue");
    }
    return componentVersion;
  }

  public ComponentVersion updateRawContent(ComponentVersion componentVersion, Resource resource) {
    // Retrieve current workflow status
    WorkflowStatus currentWorkflowStatus = workflowStatusRepository.findLatestWorkflowStatusByWorkflowInstanceId(componentVersion.getWorkflowInstance().getId());

    // Check if the transition is legit
    if (currentWorkflowStatus.getStatus().isEditable()) {
      String hash = gitService.updateAndCommit(componentVersion.getComponent().getVcsId(), componentVersion.getFilename(), resource, authenticationService.getAuthenticatedUser(), "");
      // Update content
      if (hash != null) {
        currentWorkflowStatus.updateGitReference(hash);
        workflowStatusRepository.save(currentWorkflowStatus);
        componentVersion.setGitReference(hash);
        componentVersion = componentVersionRepository.save(componentVersion);
      }
    } else {
      throw new IllegalStateException("Content update is not allowed for this statue");
    }
    return componentVersion;
  }


  public List<ComponentVersion> findAll(Long componentId) {

    return componentVersionRepository.findAllByComponentId(componentId, new Sort("version"));
  }


  public void updateToLatestTargetReference(long sourceComponentVersionId, long refId) {
    ComponentReference componentReference = componentReferenceRepository.findOne(refId);
    // Validate reference
    Validate.isTrue(componentReference.getSource().getId() == sourceComponentVersionId);
    componentReference.setTarget(componentVersionRepository.findLatestByComponentId(componentReference.getTarget().getComponent().getId()));
    componentReferenceRepository.save(componentReference);
  }

  public void updateTargetReference(long sourceComponentVersionId, long refId, long targetComponentVersionId) {
    ComponentReference componentReference = componentReferenceRepository.findOne(refId);
    // Validate reference
    Validate.isTrue(componentReference.getSource().getId() == sourceComponentVersionId);
    componentReference.setTarget(componentVersionRepository.findOne(targetComponentVersionId));
    componentReferenceRepository.save(componentReference);
  }

  public Resource composePdf(Long id) throws IOException {

    ComponentVersion component = componentVersionRepository.findOne(id);

    Path tempDirectory = Files.createTempDirectory("foo");

    Path compo = contentConfiguration.compositionHtmlContentGenerator().generate(component, true, tempDirectory);

    return compositionService.flyingHtmlToPdf(compo);
  }


}
