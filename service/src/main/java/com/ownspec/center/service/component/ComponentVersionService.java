package com.ownspec.center.service.component;

import static com.ownspec.center.model.component.QComponent.component;

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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  public void updateTargetReference(Long sourceComponentVersionId, Long refId, long targetComponentVersionId) {
    ComponentReference componentReference = componentReferenceRepository.findOne(refId);
    // Validate reference
    Validate.isTrue(componentReference.getSource().getId() == sourceComponentVersionId);
    componentReference.setTarget(componentVersionRepository.findOne(targetComponentVersionId));
    componentReferenceRepository.save(componentReference);
  }
}
