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

  List<WorkflowStatus> findAllByComponentId(long id, Sort sort);

  @Query("FROM WorkflowStatus status WHERE status.component.id=:id AND status.id IN " +
      "(SELECT MAX(status2.id) FROM WorkflowStatus status2 WHERE status2.component.id=:id)")
  WorkflowStatus findLatestStatusByComponentId(@Param("id") Long id);


}
