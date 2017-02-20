package com.ownspec.center.repository;

import com.ownspec.center.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 12/02/2017
 *
 * @author lyrold
 */
public interface CompanyRepository extends JpaRepository<Company,Long>{

  Company findOneByHostTrue();
}