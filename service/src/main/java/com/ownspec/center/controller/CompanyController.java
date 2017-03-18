package com.ownspec.center.controller;

import com.ownspec.center.model.company.Company;
import com.ownspec.center.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created on 12/02/2017
 *
 * @author lyrold
 */
@RestController
@RequestMapping(value = "/api/companies")
public class CompanyController {

  @Autowired
  private CompanyService companyService;


  @GetMapping
  @ResponseBody
  public List<Company> findAll() {
    return companyService.findAll();
  }

  @GetMapping("/host")
  @ResponseBody
  public Company findHost() {
    return companyService.findHost();
  }


  @PostMapping("/new")
  @ResponseBody
  public ResponseEntity create(@RequestBody Company source) {
    companyService.create(source);
    return ResponseEntity.ok().build();
  }
  
  @PostMapping("/{id}")
  @ResponseBody
  public ResponseEntity update(@PathVariable("id")Long id, @RequestBody Company source) {
    companyService.update(id, source);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(name = "/{id}")
  @ResponseBody
  public ResponseEntity delete(@PathVariable("id") Long id) {
    companyService.delete(id);
    return ResponseEntity.ok().build();
  }


//  @GetMapping("/{id}/users")
//  @ResponseBody
//  public List<UserDto> findAllUsersInCompanyWithId(@PathVariable("id") Long id) {
//    return companyService.findAllUsersInCompanyWithId(id);
//  }



}
