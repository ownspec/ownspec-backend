package com.ownspec.center.model.component;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.user.User;
import lombok.Data;

/**
 *
 * A reference is an oriented path between a source
 *
 * Created by nlabrot on 23/09/16.
 */
@Data
@Entity
public class ComponentReference implements Persistable<Long>, Auditable<User> {

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
    public boolean isNew() {
        return null == getId();
    }
}
