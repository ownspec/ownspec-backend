package com.ownspec.center.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.Business;
import com.ownspec.center.model.Details;
import com.ownspec.center.model.Social;
import com.ownspec.center.model.company.Company;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Created on 19/03/2017
 *
 * @author lyrold
 */
@Value.Immutable
@Value.Style(builder = "newCompanyDto")
@JsonSerialize(as = ImmutableCompanyDto.class)
@JsonDeserialize(as = ImmutableCompanyDto.class)
public interface CompanyDto {

  @Nullable
  Long getId();

  @Nullable
  String getName();

  @Nullable
  String getRegistrationNumber();

  @Nullable
  Boolean getHost();

  @Nullable
  String getLogoUrl();

  @Nullable
  Business.Industry getBusinessIndustry();

  @Nullable
  Social getSocial();

  @Nullable
  List<Details> getDetails();

  @Nullable
  List<ClientDto> getClients();

  @Nullable
  List<UserDto> getUsers();

  @Nullable
  Instant getCreatedDate();

  @Nullable
  UserDto getCreatedUser();

  @Nullable
  Instant getLastModifiedDate();

  @Nullable
  UserDto getLastModifiedUser();


  static CompanyDto from(Company source, List<Details> detailss, List<UserDto> users, List<ClientDto> clients) {
    return ImmutableCompanyDto.newCompanyDto()
        .id(source.getId())
        .createdDate(source.getCreatedDate())
        .createdUser(UserDto.fromUser(source.getCreatedUser()))
        .lastModifiedUser(UserDto.fromUser(source.getLastModifiedUser()))
        .lastModifiedDate(source.getLastModifiedDate())

        .name(source.getName())
        .registrationNumber(source.getRegistrationNumber())
        .host(source.isHost())
        .logoUrl(source.getLogoUrl())
        .businessIndustry(source.getBusinessIndustry())
        .social(source.getSocial())
        .details(detailss)
        .users(users)
        .clients(clients)
        .build();
  }

}
