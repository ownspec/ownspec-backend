package com.ownspec.center.repository.company;

import com.ownspec.center.model.Details;
import com.ownspec.center.model.company.CompanyDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created on 18/03/2017
 *
 * @author lyrold
 */
public interface CompanyDetailsRepository extends JpaRepository<CompanyDetails, Long> {
  CompanyDetails findOneByDetailsId(Long id);

  List<Details> findAllByCompanyId(Long companyId);

  void deleteByDetailsId(Long id);

}
