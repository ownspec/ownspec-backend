package com.ownspec.center.service.content;

import java.util.Set;

/**
 * Created by nlabrot on 13/11/16.
 */
public class ComponentCycleException extends RuntimeException {

  private final Set<String> cycles;

  public ComponentCycleException(Set<String> cycles) {
    this.cycles = cycles;
  }
}
