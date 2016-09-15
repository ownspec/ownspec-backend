package com.ownspec.center.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by lyrold on 23/08/2016.
 */
@Entity
public class Requirement {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String description;
    private String htmlDescriptionPath;
    private String gitReference;
    private Date creationDate;

    @OneToMany
    private List<Requirement> children;

    @OneToOne
    private User author;

    private boolean isEditable;
    private boolean isPrivate;

    @Column
    @ElementCollection(targetClass = String.class)
    private List<String> issues;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<String> getIssues() {
        return issues;
    }

    public void setIssues(List<String> issues) {
        this.issues = issues;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setIsEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Requirement> getChildren() {
        return children;
    }

    public void setChildren(List<Requirement> children) {
        this.children = children;
    }

    public String getHtmlDescriptionPath() {
        return htmlDescriptionPath;
    }

    public void setHtmlDescriptionPath(String htmlDescriptionPath) {
        this.htmlDescriptionPath = htmlDescriptionPath;
    }

    public String getGitReference() {
        return gitReference;
    }

    public void setGitReference(String gitReference) {
        this.gitReference = gitReference;
    }

}
