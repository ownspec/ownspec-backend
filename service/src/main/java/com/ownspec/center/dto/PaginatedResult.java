package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * Created by nlabrot on 03/05/17.
 */
@Value.Immutable
@Value.Style(builder = "newPaginatedResult")
@JsonSerialize(as = ImmutablePaginatedResult.class)
@JsonDeserialize(as = ImmutablePaginatedResult.class)
public interface PaginatedResult<T> {

  long getOffset();

  long getSize();

  long getTotal();

  List<T> getResult();
}
