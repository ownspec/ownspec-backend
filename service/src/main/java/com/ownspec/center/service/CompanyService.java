package com.ownspec.center.service;

import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.Company;
import com.ownspec.center.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created on 12/02/2017
 *
 * @author lyrold
 */
@Service
public class CompanyService {

  @Autowired
  private CompanyRepository companyRepository;

  public List<Company> findAll() {
    return null;
  }

  public void create(Company source) {

  }

  public void update(Long id, Company source) {

  }

  public void delete(Long id) {

  }

  public List<UserDto> findAllUsersInCompanyWithId(Long id) {
    return null;
  }

  public Company findHost() {
    return companyRepository.findOneByHostTrue();
  }
}
