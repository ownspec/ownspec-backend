package com.ownspec.center.model.user;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ownspec.center.model.audit.Auditable;
import lombok.Data;

/**
 * Created by lyrold on 23/08/2016.
 */
@Data
@Entity
public class User implements UserDetails, Auditable<User> {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String company;
    private String phone;
    private String fax;
    private String signature;


    private Instant lastConnection;

    @CreatedDate
    protected Instant createdDate;
    @ManyToOne
    @CreatedBy
    protected User createdUser;
    @LastModifiedDate
    protected Instant lastModifiedDate;
    @ManyToOne
    @LastModifiedBy
    protected User lastModifiedUser;

    @Embedded
    private UserCategory category;

    @Column(columnDefinition = "boolean default false")
    private boolean loggedIn;
    @Column(columnDefinition = "boolean default true")
    private boolean enabled;
    @Column(columnDefinition = "boolean default true")
    private boolean accountNonExpired;
    @Column(columnDefinition = "boolean default true")
    private boolean accountNonLocked;
    @Column(columnDefinition = "boolean default true")
    private boolean credentialsNonExpired;


    public User() {
    }

    public User(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(role));
    }

}
