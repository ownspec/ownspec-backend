package com.ownspec.center.repository.workflow;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ownspec.center.model.workflow.WorkflowStatus;

/**
 * Created by lyrold on 18/09/2016.
 */
public interface WorkflowStatusRepository extends JpaRepository<WorkflowStatus, Long> {

  List<WorkflowStatus> findAllByWorkflowInstanceComponentId(long id, Sort sort);

  List<WorkflowStatus> findAllByWorkflowInstanceId(long id, Sort sort);

  @Query("FROM WorkflowStatus status WHERE status.workflowInstance.component.id=:id AND status.id IN " +
      "(SELECT MAX(status2.id) FROM WorkflowStatus status2 WHERE status2.workflowInstance.component.id=:id)")
  WorkflowStatus findLatestWorkflowStatusByComponentId(@Param("id") Long id);

  @Query("FROM WorkflowStatus status WHERE status.workflowInstance.id=:id AND status.id IN " +
      "(SELECT MAX(status2.id) FROM WorkflowStatus status2 WHERE status2.workflowInstance.id=:id)")
  WorkflowStatus findLatestWorkflowStatusByWorkflowInstanceId(@Param("id") Long id);

}
