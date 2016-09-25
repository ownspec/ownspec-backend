package com.ownspec.center.model.component;

import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;

import com.ownspec.center.model.Project;
import com.ownspec.center.model.audit.Audit;
import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.workflow.WorkflowInstance;
import lombok.Data;

/**
 * Created by lyrold on 16/09/2016.
 */
@Data
@MappedSuperclass
public abstract class AbstractComponent implements Auditable, Persistable<Long> {
    @Id
    @GeneratedValue
    protected Long id;

    protected String title;
    protected String filePath;

    @Embedded
    private Audit audit;

    // Project which owns this WorkflowStatus
    @ManyToOne
    protected Project project;

    // WorkflowInstance which owns this WorkflowStatus
    @ManyToOne
    private WorkflowInstance currentWorkflowInstance;


    protected ComponentTypes type;
    protected boolean editable = true;
    protected boolean secret;

    protected boolean confidential;

    @Override
    @Transient
    public boolean isNew() {
        return null == getId();
    }
}
