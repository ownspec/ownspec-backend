package com.ownspec.center.repository.user;

import com.ownspec.center.model.user.UserCompany;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 18/03/2017
 *
 * @author lyrold
 */
public interface UserCompanyRepository extends JpaRepository<UserCompany, Long> {

  UserCompany findOneByUserId(Long id);

  void deleteByUserId(Long id);
}
