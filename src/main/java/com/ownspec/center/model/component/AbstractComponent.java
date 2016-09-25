package com.ownspec.center.model.component;

import com.ownspec.center.model.Project;
import com.ownspec.center.model.Quantifiable;
import com.ownspec.center.model.UserCategory;
import com.ownspec.center.model.audit.Audit;
import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.workflow.WorkflowInstance;
import lombok.Data;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

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
    protected String version;

    @Embedded
    private Audit audit;

    // Project which owns this WorkflowStatus
    @ManyToOne
    protected Project project;

    // WorkflowInstance which owns this WorkflowStatus
    @ManyToOne
    private WorkflowInstance currentWorkflowInstance;

//    @ElementCollection
//    protected Map<UserCategory, Quantifiable> quantifiableMap = new HashMap<>();

    protected ComponentTypes type;
    protected Boolean editable = true;
    protected Boolean secret = false;
    protected Boolean confidential = false;

    @Override
    @Transient
    public boolean isNew() {
        return null == getId();
    }
}
