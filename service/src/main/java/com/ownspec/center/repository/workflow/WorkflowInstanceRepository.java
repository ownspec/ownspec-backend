package com.ownspec.center.repository.workflow;

import com.ownspec.center.model.workflow.WorkflowInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by lyrold on 18/09/2016.
 */
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long> {

  @Query(nativeQuery = true, value =
      "SELECT WI.* FROM WORKFLOW_INSTANCE WI " +
          "INNER JOIN COMPONENT_VERSION CV ON CV.WORKFLOW_INSTANCE_ID = WI.ID " +
          "INNER JOIN COMPONENT C ON C.ID = CV.COMPONENT_ID " +
          "WHERE C.ID=:id " +
          "ORDER BY WI.ID")
  List<WorkflowInstance> findAllByComponentId(@Param("id") Long id);

}
