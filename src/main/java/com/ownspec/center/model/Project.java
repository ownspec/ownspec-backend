package com.ownspec.center.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by lyrold on 23/08/2016.
 */
@Entity
public class Project {

    @Id
    @GeneratedValue
    private long id;
    private String title;
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @OneToMany
    private List<Requirement> requirements;

    @OneToMany
    private List<Document> documents;

    public Project() {
        creationDate = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }
}
