package com.ownspec.center.model.workflow;

/**
 * Created by lyrold on 18/09/2016.
 */
public enum  Status {
    OPEN(true),
    DRAFT(true),
    IN_VALIDATION(false),
    VALIDATED(false),
    CLOSED(false)
    ;

    private final boolean editable;

    Status(boolean isEditable) {
        editable = isEditable;
    }

    public boolean isEditable() {
        return editable;
    }
}
