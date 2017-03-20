package com.ownspec.center.repository.company;

import com.ownspec.center.model.company.CompanyClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created on 18/03/2017
 *
 * @author lyrold
 */
public interface CompanyClientRepository extends JpaRepository<CompanyClient, Long> {

  List<CompanyClient> findAllByCompanyId(Long companyId);

  void deleteByClientId(Long id);
}
