package com.ownspec.center.model.component;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.ownspec.center.model.audit.Audit;
import lombok.Data;

/**
 *
 * A reference is an oriented path between a source
 *
 * Created by nlabrot on 23/09/16.
 */
@Data
@Entity
public class ComponentReference {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Component source;

    private Long sourceWorkflowInstanceId;


    @ManyToOne
    private Component targetComponent;
    // A reference target a component in a specific workflow cycle
    private Long targetWorkflowInstanceId;

    @Embedded
    private Audit audit;
}
