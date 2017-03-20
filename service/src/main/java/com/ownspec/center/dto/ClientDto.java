package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.Business;
import com.ownspec.center.model.Client;
import com.ownspec.center.model.Details;
import com.ownspec.center.model.Social;
import org.immutables.value.Value;

import javax.annotation.Nullable;

/**
 * Created on 19/03/2017
 *
 * @author lyrold
 */
@Value.Immutable
@Value.Style(builder = "newClientDto")
@JsonSerialize(as = ImmutableClientDto.class)
@JsonDeserialize(as = ImmutableClientDto.class)
public interface ClientDto {

  @Nullable
  Long getId();

  @Nullable
  String getLogoUrl();

  @Nullable
  Business.Industry getBusinessIndustry();

  @Nullable
  Social getSocial();

  @Nullable
  Details getDetails();

  static ImmutableClientDto from(Client source) {
    return ImmutableClientDto.newClientDto()
        .id(source.getId())
        .logoUrl(source.getLogoUrl())
        .details(source.getDetails())
        .businessIndustry(source.getBusinessIndustry())
        .social(source.getSocial())
        .build();
  }
}
