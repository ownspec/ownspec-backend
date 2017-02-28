package com.ownspec.center.model.component;

/**
 * Created by lyrold on 18/09/2016.
 */
public enum ComponentType {
  DOCUMENT("DOC"),
  COMPONENT("CMP"),
  REQUIREMENT("REQ"),
  RESOURCE("RES"),
  TEMPLATE("TMPL"),;

  private final String prefix;

  ComponentType(String prefix) {
    this.prefix = prefix;
  }

  public String getPrefix() {
    return prefix;
  }
}
