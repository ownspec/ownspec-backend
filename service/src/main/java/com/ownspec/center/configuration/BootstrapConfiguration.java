package com.ownspec.center.configuration;

import com.ownspec.center.model.user.User;
import com.ownspec.center.model.user.UserCategory;
import com.ownspec.center.model.user.UserGroup;
import com.ownspec.center.model.user.UserPreference;
import com.ownspec.center.repository.user.UserCategoryRepository;
import com.ownspec.center.repository.user.UserGroupRepository;
import com.ownspec.center.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.UUID;

/**
 * Created on 13/02/2017
 *
 * @author lyrold
 */
@Configuration
public class BootstrapConfiguration {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserCategoryRepository userCategoryRepository;

  @Autowired
  private UserGroupRepository userGroupRepository;

  @Bean
  public User createSystemUser() {
    if (userRepository.count() != 0) {
      return null;
    }
    UserGroup group = new UserGroup();
    group.setName("System Administrators");


    UserCategory category = new UserCategory();
    category.setName("System Administrator");
    category.setHourlyPrice(0d);

    UserPreference preference = new UserPreference();
    preference.setLanguage("en");
    preference.setTimezone("foo");

    User u = new User();
    u.setUsername("system.admin");
    u.setPassword("$2a$06$OhekMKiSRgT/YYNv.57J1uqZJ60z9wefWl135v5MvpirQwjYdAaxe");
    u.setRole("ADMIN");
    u.setFirstName("System");
    u.setLastName("Administrator");
    u.setFullName(u.getFirstName() + " " + u.getLastName());
    u.setEmail("system.administrator@ownspec.com");
    u.setPhone("333");
    u.setMobile("333");
    u.setCategory(category);
    u.setGroup(group);
    u.setLastConnection(Instant.now());
    u.setEnabled(true);
    u.setAccountNonExpired(true);
    u.setAccountNonLocked(true);
    u.setCredentialsNonExpired(true);
    u.setPreference(preference);

    SecurityContextHolder.getContext().setAuthentication(
        new AnonymousAuthenticationToken(UUID.randomUUID().toString(), u, u.getAuthorities()));
    userGroupRepository.save(group);
    userCategoryRepository.save(category);
    userRepository.save(u);

    return u;
  }
}
