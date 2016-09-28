package com.ownspec.center.repository.workflow;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ownspec.center.model.workflow.WorkflowStatus;

/**
 * Created by lyrold on 18/09/2016.
 */
public interface WorkflowStatusRepository extends JpaRepository<WorkflowStatus, Long> {

}
