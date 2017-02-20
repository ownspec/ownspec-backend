package com.ownspec.center.service.component;

import static com.ownspec.center.model.component.QComponent.component;
import static com.ownspec.center.util.OsUtils.mergeWithNotNullProperties;
import static java.util.Objects.requireNonNull;

import com.ownspec.center.dto.ComponentVersionDto;
import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.component.ComponentVersion;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
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
    return componentVersionRepository.save(componentVersion);
  }


  public List<ComponentVersion> findAll(Long componentId) {

    return componentVersionRepository.findAllByComponentId(componentId, new Sort("version"));
  }


  public void updateToLatestTargetReference(Long sourceComponentVersionId, Long refId) {
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
