package com.ownspec.center.repository.user;

import com.ownspec.center.model.user.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 12/02/2017
 *
 * @author lyrold
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

  VerificationToken findOneByToken(String token);

  VerificationToken findOneByUserId(Long id);

}
