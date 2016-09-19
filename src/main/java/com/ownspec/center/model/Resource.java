package com.ownspec.center.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by lyrold on 19/09/2016.
 */
@Entity
public class Resource {

    @Id
    @GeneratedValue
    private Long id;

    private String filePath;

    public Resource(String filePath) {
        this.filePath = filePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
