package com.ownspec.center.model;

import com.ownspec.center.model.component.AbstractComponent;
import com.ownspec.center.model.component.ComponentTypes;

import javax.persistence.Entity;

/**
 * Created by lyrold on 23/08/2016.
 */
@Entity
public class Document extends AbstractComponent {

    public Document() {
        setType(ComponentTypes.DOCUMENT);
    }

    private boolean isConfidential;

    public boolean isConfidential() {
        return isConfidential;
    }

    public void setConfidential(boolean confidential) {
        isConfidential = confidential;
    }
}
