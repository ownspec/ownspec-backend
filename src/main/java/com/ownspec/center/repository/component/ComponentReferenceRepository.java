package com.ownspec.center.repository.component;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.workflow.WorkflowInstance;

/**
 * Created by lyrold on 18/09/2016.
 */
public interface ComponentReferenceRepository extends JpaRepository<ComponentReference, Long> {

}
