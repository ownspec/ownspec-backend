package com.ownspec.center.repository.workflow;

import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.model.workflow.WorkflowStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by lyrold on 18/09/2016.
 */
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long> {

  List<WorkflowInstance> findAllByComponentId(@Param("id") Long id, Sort orders);

  @Query("FROM WorkflowInstance wi WHERE wi.component.id=:id AND wi.id IN " +
      "(SELECT MAX(wi2.id) FROM WorkflowInstance wi2 WHERE wi2.component.id=:id)")
  WorkflowInstance findLatestByComponentId(@Param("id") Long id);


  WorkflowInstance findByIdAndComponentId(Long workflowIstanceId, Long componentId);

}
