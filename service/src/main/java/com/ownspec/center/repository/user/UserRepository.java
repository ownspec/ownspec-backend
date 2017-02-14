package com.ownspec.center.repository.user;

import com.ownspec.center.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by lyrold on 23/08/2016.
 */
public interface UserRepository extends JpaRepository<User, Long> {

  User findByUsername(String username);
}
