package com.ownspec.center.controller;

import com.ownspec.center.dto.ClientDto;
import com.ownspec.center.dto.CompanyDto;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.Details;
import com.ownspec.center.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created on 12/02/2017
 *
 * @author lyrold
 */
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping(value = "/api/companies")
public class CompanyController {

  @Autowired
  private CompanyService companyService;

  @GetMapping
  @ResponseBody
  public List<CompanyDto> findAll(
      @RequestParam(value = "withUsers", required = false, defaultValue = "true") boolean withUsers,
      @RequestParam(value = "withClients", required = false, defaultValue = "true") boolean withClients
  ) {
    return companyService.findAll(withClients, withUsers);
  }

  @GetMapping("/{id}")
  @ResponseBody
  public CompanyDto findOne(
      @PathVariable("id") Long id,
      @RequestParam(value = "withUsers", required = false, defaultValue = "true") boolean withUsers,
      @RequestParam(value = "withClients", required = false, defaultValue = "true") boolean withClients
  ) {
    return companyService.findOneToDto(id, withClients, withUsers);
  }

  @PostMapping
  @ResponseBody
  public ResponseEntity<CompanyDto> create(@RequestBody CompanyDto source) {
    return ResponseEntity.ok(companyService.create(source));
  }

  @PatchMapping("/{id}")
  @ResponseBody
  public ResponseEntity<CompanyDto> update(@PathVariable("id") Long id, @RequestBody CompanyDto source) {
    return ResponseEntity.ok(companyService.update(id, source));
  }

  @DeleteMapping("/{id}")
  @ResponseBody
  public ResponseEntity delete(@PathVariable("id") Long id) {
    companyService.delete(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}/users")
  @ResponseBody
  public List<UserDto> getUsers(@PathVariable("id") Long id) {
    return companyService.getUsers(id);
  }

  @PatchMapping("/{id}/users")
  @ResponseBody
  public ResponseEntity addUsers(@PathVariable("id") Long id, @RequestBody List<UserDto> users) {
    companyService.addUsers(users, companyService.findOne(id));
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}/users/{userId}")
  @ResponseBody
  public ResponseEntity removeUser(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
    companyService.removeUser(userId, companyService.findOne(id));
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}/clients")
  @ResponseBody
  public List<ClientDto> getClients(@PathVariable("id") Long id) {
    return companyService.getClients(id);
  }

  @PatchMapping("/{id}/clients")
  @ResponseBody
  public ResponseEntity addClients(@PathVariable("id") Long id, @RequestBody List<ClientDto> clients) {
    companyService.addClients(clients, companyService.findOne(id));
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}/clients/{clientId}")
  @ResponseBody
  public ResponseEntity removeClient(@PathVariable("id") Long id, @PathVariable("clientId") Long clientId) {
    companyService.removeClient(clientId, companyService.findOne(id));
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{id}/details")
  @ResponseBody
  public ResponseEntity addDetailss(@PathVariable("id") Long id, @RequestBody List<Details> detailss) {
    companyService.addDetailss(detailss, companyService.findOne(id));
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{id}/details/{detailsId}")
  @ResponseBody
  public ResponseEntity removeDetails(@PathVariable("id") Long id, @PathVariable("detailsId") Long detailsId) {
    companyService.removeDetails(detailsId, companyService.findOne(id));
    return ResponseEntity.ok().build();
  }

}
