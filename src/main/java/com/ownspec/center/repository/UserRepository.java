package com.ownspec.center.repository;

import com.ownspec.center.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by lyrold on 23/08/2016.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findById(String id);

    User findByUsername(String username);

    User findByFirstName(String firstName);

    User findByLastName(String lastName);

    User findByEmail(String email);
}
