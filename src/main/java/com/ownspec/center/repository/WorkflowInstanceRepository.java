package com.ownspec.center.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentTypes;
import com.ownspec.center.model.workflow.WorkflowInstance;

/**
 * Created by lyrold on 18/09/2016.
 */
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long> {

}
