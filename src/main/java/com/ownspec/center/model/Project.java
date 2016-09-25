package com.ownspec.center.model;

import javax.persistence.*;

import org.springframework.data.domain.Persistable;

import com.ownspec.center.model.audit.Audit;
import lombok.Data;

/**
 * Created by lyrold on 23/08/2016.
 */
@Data
@Entity
public class Project implements Persistable<Long> {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String description;

    @Embedded
    private Audit audit;

    @Override
    @Transient
    public boolean isNew() {
        return null == getId();
    }
}
