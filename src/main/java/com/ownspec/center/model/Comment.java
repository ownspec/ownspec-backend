package com.ownspec.center.model;

import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.user.User;
import lombok.Data;

import javax.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

/**
 * Created by lyrold on 27/09/2016.
 *
 *
 *
 */
@Data
@Entity
public class Comment implements Auditable<User>, Persistable<Long> {
    @Id
    @GeneratedValue
    private Long id;

    private String value;

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

    @ManyToOne
    private Component component;

    @Override public boolean isNew() {
        return null == getId();
    }
}
