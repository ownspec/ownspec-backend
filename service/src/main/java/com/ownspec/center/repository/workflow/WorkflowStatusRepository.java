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



  List<WorkflowStatus> findAllByWorkflowInstanceId(long id, Sort sort);

  @Query(nativeQuery = true , value =
      "SELECT WS.* " +
          "FROM WORKFLOW_STATUS WS " +
          "INNER JOIN WORKFLOW_INSTANCE WI ON WI.ID = WS.WORKFLOW_INSTANCE_ID " +
          "INNER JOIN COMPONENT_VERSION CV ON CV.WORKFLOW_INSTANCE_ID = WI.ID " +
          "WHERE CV.ID=:id " +
          "ORDER BY WS.WSORDER DESC " +
          "LIMIT 1")
  WorkflowStatus findLatestWorkflowStatusByComponentVersionId(@Param("id") Long id);

  @Query(nativeQuery = true , value =
      "SELECT WS.* " +
          "FROM WORKFLOW_STATUS WS " +
          "INNER JOIN WORKFLOW_INSTANCE WI ON WI.ID = WS.WORKFLOW_INSTANCE_ID " +
          "WHERE WI.ID=:id " +
          "ORDER BY WS.WSORDER DESC " +
          "LIMIT 1")
  WorkflowStatus findLatestWorkflowStatusByWorkflowInstanceId(@Param("id") Long id);

}
