package com.ownspec.center.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;

import com.ownspec.center.model.audit.Audit;
import lombok.Data;

/**
 * Created by lyrold on 23/08/2016.
 */
@Data
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String company;
    private String phone;
    private String fax;
    private String signature;

    @Temporal(TemporalType.TIMESTAMP)
    private Instant lastConnection;

    @Embedded
    private Audit audit;

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
