package com.ownspec.center.service;

import com.ownspec.center.exception.UserAlreadyExistsException;
import com.ownspec.center.model.user.User;
import com.ownspec.center.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by lyrold on 23/08/2016.
 */
@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User foundUser = userRepository.findByUsername(username);
        if (foundUser == null) {
            throw new UsernameNotFoundException("Cannot found username [" + username + "]");
        }
        return foundUser;
    }

    public User register(String username, String password) {
        if (null != userRepository.findByUsername(username)) {
            throw new UserAlreadyExistsException(username);
        }
        return new User(username, password);
    }

    public void update(User user) {
        userRepository.saveAndFlush(user);
    }

    public void logIn(User user) {

    }

    public void logOut(User user) {

    }

}
