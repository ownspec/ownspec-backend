package com.ownspec.center.model.component;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

import com.ownspec.center.model.Comment;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.WorkflowInstance;
import lombok.Data;

/**
 * Created by lyrold on 23/08/2016.
 */
@Data
@Entity
public class Component implements Auditable<User>, Persistable<Long> {

    @Id
    @GeneratedValue
    protected Long id;

    protected String title;
    protected String filePath;


    // Project which owns this WorkflowStatus
    @ManyToOne
    protected Project project;

    // WorkflowInstance which owns this WorkflowStatus
    @ManyToOne
    private WorkflowInstance currentWorkflowInstance;



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


    @CreatedDate
    protected Instant createdDate;
    @ManyToOne
    @CreatedBy
    protected User createdUser;
    @LastModifiedDate
    protected Instant lastModifiedDate;
    @ManyToOne
    @LastModifiedBy
    protected User lastModifiedUser;




}
