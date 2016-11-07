package com.ownspec.center.model.workflow;

import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lyrold on 18/09/2016.
 */
public enum Status {
  OPEN(false, false),
  DRAFT(true, false),
  IN_VALIDATION(false, false),
  VALIDATED(false, false),
  CLOSED(false, true);

  private final boolean editable;
  private final boolean _final;

  Status(boolean isEditable, boolean _final) {
    editable = isEditable;
    this._final = _final;
  }

  public boolean isEditable() {
    return editable;
  }

  public boolean isFinal() {
    return _final;
  }

  public List<Status> getTransitions() {
    return getTransitions(this);
  }

  public static List<Status> getTransitions(Status status) {
    switch (status) {
      case OPEN:
        return Arrays.asList(DRAFT);
      case DRAFT:
        return Arrays.asList(IN_VALIDATION);
      case IN_VALIDATION:
        return Arrays.asList(DRAFT, VALIDATED);
      case VALIDATED:
        return Arrays.asList(CLOSED);
      case CLOSED:
      default:
        return emptyList();

    }
  }
}
