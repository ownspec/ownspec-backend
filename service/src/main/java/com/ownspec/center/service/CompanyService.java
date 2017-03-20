package com.ownspec.center.service;

import com.ownspec.center.dto.ClientDto;
import com.ownspec.center.dto.CompanyDto;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.Details;
import com.ownspec.center.model.company.Company;
import com.ownspec.center.model.company.CompanyClient;
import com.ownspec.center.model.company.CompanyDetails;
import com.ownspec.center.model.user.UserCompany;
import com.ownspec.center.repository.company.CompanyClientRepository;
import com.ownspec.center.repository.company.CompanyDetailsRepository;
import com.ownspec.center.repository.company.CompanyRepository;
import com.ownspec.center.repository.user.UserCompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created on 12/02/2017
 *
 * @author lyrold
 */
@Service
public class CompanyService {

  @Autowired
  private CompanyRepository companyRepository;

  @Autowired
  private CompanyClientRepository companyClientRepository;

  @Autowired
  private CompanyDetailsRepository companyDetailsRepository;

  @Autowired
  private UserCompanyRepository userCompanyRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private ClientService clientService;

  public List<CompanyDto> findAll(boolean withClients, boolean withUsers) {
    List<CompanyDto> companies = new ArrayList<>();
    companyRepository.findAll().forEach(
        company -> {
          Long id = company.getId();

          companies.add(CompanyDto.from(company,
              companyDetailsRepository.findAllByCompanyId(id),
              withUsers ? getUsers(id) : Collections.EMPTY_LIST,
              withClients ? getClients(id) : Collections.EMPTY_LIST));
        }
    );
    return companies;
  }

  public CompanyDto findOneToDto(Long id, boolean withClients, boolean withUsers) {
    return CompanyDto.from(findOne(id),
        companyDetailsRepository.findAllByCompanyId(id),
        withUsers ? getUsers(id) : Collections.EMPTY_LIST,
        withClients ? getClients(id) : Collections.EMPTY_LIST);
  }

  public Company findOne(Long id) {
    return Objects.requireNonNull(companyRepository.findOne(id));
  }

  public CompanyDto create(CompanyDto source) {
    Company company = new Company();
    company.setHost(source.getHost());
    company.setBusinessIndustry(source.getBusinessIndustry());
    company.setLogoUrl(source.getLogoUrl());
    company.setRegistrationNumber(source.getRegistrationNumber());
    company.setSocial(source.getSocial());

    companyRepository.save(company);

    addDetailss(source.getDetails(), company);
    addUsers(source.getUsers(), company);
    addClients(source.getClients(), company);

    return null;
  }

  public CompanyDto update(Long id, CompanyDto source) {
    return null;
  }

  public void delete(Long id) {

  }


  public List<UserDto> getUsers(Long id) {
    return userCompanyRepository.findAll().stream()
        .map(uc -> UserDto.fromUser(uc.getUser()))
        .collect(Collectors.toList());
  }


  public void addDetailss(List<Details> detailss, Company company) {
    if (detailss != null) {
      detailss.forEach(d -> {
        CompanyDetails companyDetails = new CompanyDetails();
        companyDetails.setCompany(company);
        companyDetails.setDetails(d);

        companyDetailsRepository.save(companyDetails);
      });
    }
  }

  public void addUsers(List<UserDto> users, Company company) {
    if (users != null) {
      users.forEach(u -> {
        UserCompany userCompany = new UserCompany();
        userCompany.setCompany(company);
        userCompany.setUser(userService.findOne(u.getId()));

        userCompanyRepository.save(userCompany);
      });
    }
  }

  public void addClients(List<ClientDto> clients, Company company) {
    if (clients != null) {
      clients.forEach(c -> {
        CompanyClient companyClient = new CompanyClient();
        companyClient.setCompany(company);
        companyClient.setClient(clientService.findOne(c.getId()));

        companyClientRepository.save(companyClient);
      });
    }
  }


  public void removeDetails(Long id, Company company) {
    companyDetailsRepository.deleteByDetailsId(id);
  }

  public void removeUser(Long id, Company company) {
    userCompanyRepository.deleteByUserId(id);
  }

  public void removeClient(Long id, Company company) {
    companyClientRepository.deleteByClientId(id);
  }

  public List<ClientDto> getClients(Long id) {
    return companyClientRepository.findAllByCompanyId(id).stream()
        .map(cc -> ClientDto.from(cc.getClient()))
        .collect(Collectors.toList());
  }
}
