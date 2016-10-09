package com.ownspec.center.model.user;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created by lyrold on 02/10/2016.
 */
@Data
@Embeddable
public class UserPreference {

    private String language;

    private String timezone;

    @Column(columnDefinition = "boolean default true")
    private boolean notifyMeOnNewTask;

    @Column(columnDefinition = "boolean default true")
    private boolean notifyMeOnWatchedDocumentChange;
}
