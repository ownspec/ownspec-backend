package com.ownspec.center.dto;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.workflow.Status;

/**
 * Created by nlabrot on 27/09/16.
 */
@Value.Immutable
@Value.Style(builder = "newWorkflowInstanceDto")
@JsonSerialize(as = ImmutableWorkflowInstanceDto.class)
@JsonDeserialize(as = ImmutableWorkflowInstanceDto.class)
public interface WorkflowInstanceDto {

    Long getId();
    Status getCurrentStatus();

}
