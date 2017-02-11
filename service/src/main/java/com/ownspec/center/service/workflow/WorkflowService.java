package com.ownspec.center.service.workflow;

import static java.util.Objects.requireNonNull;

import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.repository.component.ComponentVersionRepository;
import com.ownspec.center.repository.workflow.WorkflowInstanceRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by nlabrot on 20/10/16.
 */
@Service
public class WorkflowService {

  @Autowired
  private WorkflowInstanceRepository workflowInstanceRepository;

  @Autowired
  private WorkflowStatusRepository workflowStatusRepository;

  @Autowired
  private ComponentRepository componentRepository;

  @Autowired
  private ComponentVersionRepository componentVersionRepository;

  public ComponentVersion updateStatus(Long cvId, Status nextStatus) {
    // Lock
    ComponentVersion componentVersion = requireNonNull(componentVersionRepository.findOneAndLock(cvId));

    // Retrieve current workflow status
    WorkflowStatus currentWorkflowStatus = workflowStatusRepository.findLatestWorkflowStatusByWorkflowInstanceId(componentVersion.getWorkflowInstance().getId());

    // Check if the transition is possible
    if (!currentWorkflowStatus.getStatus().isAllowedTransition(nextStatus)) {
      throw new IllegalStateException("Illegal transition");
    }

    // Create and save the next status
    WorkflowStatus nextWorkflowStatus = new WorkflowStatus();
    nextWorkflowStatus.setStatus(nextStatus);
    nextWorkflowStatus.setWorkflowInstance(componentVersion.getWorkflowInstance());
    nextWorkflowStatus.setOrder(currentWorkflowStatus.getOrder() + 1);
    nextWorkflowStatus = workflowStatusRepository.save(nextWorkflowStatus);

    return componentVersion;
  }

  public Pair<WorkflowInstance, WorkflowStatus> createNew(String hash){
    // Workflow Instance
    WorkflowInstance workflowInstance = new WorkflowInstance();
    workflowInstance.setClosed(false);

    WorkflowStatus workflowStatus = new WorkflowStatus();
    workflowStatus.setStatus(Status.OPEN);
    workflowStatus.setFirstGitReference(hash);
    workflowStatus.setLastGitReference(hash);
    workflowStatus.setWorkflowInstance(workflowInstance);

    workflowInstance = workflowInstanceRepository.save(workflowInstance);
    workflowStatus = workflowStatusRepository.save(workflowStatus);

    return Pair.of(workflowInstance, workflowStatus);
  }

  public Pair<WorkflowInstance, WorkflowStatus> createNew(){

    return createNew(null);
  }




}
