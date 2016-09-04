package com.ownspec.center.repository;

import com.ownspec.center.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * Created by lyrold on 23/08/2016.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findById(String id);

    User findByFirstName(String firstName);

    User findByLastName(String lastName);
}
