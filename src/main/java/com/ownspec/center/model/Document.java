package com.ownspec.center.model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by lyrold on 23/08/2016.
 */
@Entity
public class Document extends AbstractComponent {

    @OneToMany
    private List<Component> children;

    private boolean isConfidential;

    public boolean isConfidential() {
        return isConfidential;
    }

    public void setConfidential(boolean confidential) {
        isConfidential = confidential;
    }

    public List<Component> getChildren() {
        return children;
    }

    public void setChildren(List<Component> children) {
        this.children = children;
    }
}
