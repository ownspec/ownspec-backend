package com.ownspec.center.model;

import com.ownspec.center.model.audit.Audit;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by lyrold on 27/09/2016.
 */
@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    private String value;

    @Embedded
    private Audit audit;

}
