package com.ownspec.center.repository.user;

import com.ownspec.center.model.user.UserClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created on 18/03/2017
 *
 * @author lyrold
 */
public interface UserClientRepository extends JpaRepository<UserClient, Long> {

  List<UserClient> findAllByClientId(Long id);
}
