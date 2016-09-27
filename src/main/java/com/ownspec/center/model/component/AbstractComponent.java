package com.ownspec.center.model.component;

import com.ownspec.center.model.Comment;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.audit.Audit;
import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.workflow.WorkflowInstance;
import lombok.Data;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany
    @ElementCollection
    private List<Comment> comments = new ArrayList<>();

    @OneToMany
    @ElementCollection
    private List<ComponentReference> children = new ArrayList<>();

//    @ElementCollection
//    protected Map<UserCategory, Quantifiable> quantifiableMap = new HashMap<>();

    protected ComponentTypes type;

    @Column(columnDefinition = "boolean default true")
    protected boolean editable;
    @Column(columnDefinition = "boolean default false")
    protected boolean secret;
    @Column(columnDefinition = "boolean default false")
    protected boolean confidential;

    @Override
    @Transient
    public boolean isNew() {
        return null == getId();
    }
}
