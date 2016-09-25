package com.ownspec.center.model.audit;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.ownspec.center.model.User;
import lombok.Data;

/**
 * Created by nlabrot on 24/09/16.
 */
@Data
@Embeddable
public class Audit  {

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    protected Instant createdDate;

    @ManyToOne
    @CreatedBy
    protected User createdUser;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    protected Instant lastModifiedDate;

    @ManyToOne
    @LastModifiedBy
    protected User lastModifiedUser;
}
