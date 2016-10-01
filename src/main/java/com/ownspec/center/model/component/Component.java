package com.ownspec.center.model.component;

import com.ownspec.center.model.Project;
import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.Status;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.time.Instant;

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


    // Denormalized from workflow status
    @Enumerated(EnumType.STRING)
    protected Status currentStatus;
    protected String currentGitReference;


//    @ElementCollection
//    protected Map<UserCategory, Quantifiable> quantifiableMap = new HashMap<>();

    @Enumerated(EnumType.STRING)
    protected ComponentType type;

    @Column(columnDefinition = "boolean default true")
    protected boolean editable;
    @Column(columnDefinition = "boolean default false")
    protected boolean secret;
    @Column(columnDefinition = "boolean default false")
    protected boolean confidential;
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

    @Override
    @Transient
    public boolean isNew() {
        return null == getId();
    }

}
