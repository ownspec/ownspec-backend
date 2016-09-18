package com.ownspec.center.model.component;

import com.ownspec.center.model.Status;
import com.ownspec.center.model.User;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by lyrold on 16/09/2016.
 */
@MappedSuperclass
public abstract class AbstractComponent {
    @Id
    @GeneratedValue
    protected Long id;

    protected String title;
    protected String content;
    protected String htmlContentFilePath;
    protected String gitReference;
    protected Date creationDate = new Date();
    protected Status status;
    protected ComponentTypes type;
    protected boolean editable = true;
    protected boolean secret;
    @OneToOne
    protected User author;

    @OneToMany
    private List<Component> children;


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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHtmlContentFilePath() {
        return htmlContentFilePath;
    }

    public void setHtmlContentFilePath(String htmlContentFilePath) {
        this.htmlContentFilePath = htmlContentFilePath;
    }

    public String getGitReference() {
        return gitReference;
    }

    public void setGitReference(String gitReference) {
        this.gitReference = gitReference;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    private void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isSecret() {
        return secret;
    }

    public void setSecret(boolean secret) {
        this.secret = secret;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ComponentTypes getType() {
        return type;
    }

    public void setType(ComponentTypes type) {
        this.type = type;
    }

    public List<Component> getChildren() {
        return children;
    }

    public void setChildren(List<Component> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", htmlContentFilePath='" + htmlContentFilePath + '\'' +
                ", gitReference='" + gitReference + '\'' +
                ", creationDate=" + creationDate +
                ", status=" + status +
                ", type=" + type +
                ", editable=" + editable +
                ", secret=" + secret +
                ", author=" + author +
                ", children=" + children +
                '}';
    }
}
