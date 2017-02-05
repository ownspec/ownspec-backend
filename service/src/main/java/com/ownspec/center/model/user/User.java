package com.ownspec.center.model.user;

import com.ownspec.center.model.MainSequenceConstants;
import com.ownspec.center.model.audit.AbstractAuditable;
import com.ownspec.center.model.audit.Auditable;
import com.ownspec.center.model.persistable.Persistable;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by lyrold on 23/08/2016.
 */
@Data
@Entity
@Table(name = "OSUSER")
public class User extends AbstractAuditable implements UserDetails, Persistable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = MainSequenceConstants.SEQUENCE_GENERATOR_NAME)
  @Column(name = "ID")
  private Long id;

  @Column(name = "USERNAME", unique = true)
  private String username;

  @Column(name = "EMAIL")
  private String email;

  @Column(name = "PASSWORD")
  private String password;

  @Column(name = "ROLE")
  private String role;

  @Column(name = "FULL_NAME")
  private String fullName;

  @Column(name = "FIRST_NAME")
  private String firstName;
  @Column(name = "LAST_NAME")
  private String lastName;
  @Column(name = "COMPANY")
  private String company;
  @Column(name = "PHONE")
  private String phone;
  @Column(name = "MOBILE")
  private String mobile;
  @Column(name = "FAX")
  private String fax;
  @Column(name = "SIGNATURE")
  private String signature;

  @Column(name = "LAST_CONNECTION")
  private Instant lastConnection;

  @Embedded
  private UserPreference preference;

  @ManyToOne
  @JoinColumn(name = "CATEGORY_ID")
  private UserCategory category;

  @Column(name = "LOGGED_IN")
  private boolean loggedIn;

  @Column(name = "ENABLED")
  private boolean enabled;
  @Column(name = "ACCOUNT_NON_EXPIRED")
  private boolean accountNonExpired;
  @Column(name = "ACCOUNT_NON_LOCKED")
  private boolean accountNonLocked;
  @Column(name = "CREDENTIALS_NON_EXPIRED")
  private boolean credentialsNonExpired;

  // TODO: what's the purpose of this field?
  @Column(name = "EMPOWERED_SECRET")
  private boolean empoweredSecret;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Arrays.asList(new SimpleGrantedAuthority(role));
  }

}
