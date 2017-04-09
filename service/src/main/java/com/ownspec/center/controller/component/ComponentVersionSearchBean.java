package com.ownspec.center.controller.component;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.workflow.Status;
import org.immutables.value.Value;

import java.util.List;
import javax.annotation.Nullable;

/**
 * Created by nlabrot on 23/03/17.
 */
@Value.Immutable
@Value.Style(builder = "newComponentVersionSearchBean")
@JsonSerialize(as = ImmutableComponentVersionSearchBean.class)
@JsonDeserialize(as = ImmutableComponentVersionSearchBean.class)
public interface ComponentVersionSearchBean {

  @Nullable
  Long getProjectId();

  @Nullable
  Boolean isGeneric();

  List<ComponentType> getComponentTypes();

  @Nullable
  String getTitle();

  @Nullable
  String getQuery();

  @Nullable
  Status getStatus();

  @Nullable
  Long getAssigneeId();
}
