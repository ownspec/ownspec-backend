package com.ownspec.center.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ownspec.center.model.Company;
import com.ownspec.center.model.user.UserCompany;
import org.immutables.value.Value;

import javax.annotation.Nullable;

/**
 * Created on 12/02/2017
 *
 * @author lyrold
 */
@Value.Immutable
@Value.Style(builder = "newUserCompanyDto")
@JsonSerialize(as = ImmutableUserCompanyDto.class)
@JsonDeserialize(as = ImmutableUserCompanyDto.class)
public interface UserCompanyDto {

  @Nullable
  Long getId();

  UserDto getUser();

  Company getCompany();

  @Nullable
  Boolean getIsLegalRepresentative();

  static UserCompanyDto fromUserCompany(UserCompany userCompany) {
    return ImmutableUserCompanyDto.newUserCompanyDto()
        .id(userCompany.getId())
        .user(UserDto.fromUser(userCompany.getUser()))
        .company(userCompany.getCompany())
        .isLegalRepresentative(userCompany.isLegalRepresentative())
        .build();
  }
}
