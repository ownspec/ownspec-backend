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
 * Created by nlabrot on 22/09/16.
 */
@Data
@Entity
public class WorkflowStatus {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Component workflowComponent;

    @ManyToOne
    private WorkflowInstance workflowInstance;

    private String gitReference;

    @Enumerated(EnumType.STRING)
    protected Status status;

    @Embedded
    private Audit audit;

}
