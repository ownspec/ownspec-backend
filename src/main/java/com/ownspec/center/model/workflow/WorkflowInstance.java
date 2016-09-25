package com.ownspec.center.model.workflow;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.audit.Audit;
import lombok.Data;

/**
 * Created by nlabrot on 24/09/16.
 */
@Data
@Entity
public class WorkflowInstance {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Component component;

    // Denormalized from workflow status
    @Enumerated(EnumType.STRING)
    protected Status currentStatus;
    protected String currentGitReference;

    @Embedded
    private Audit audit;
}
