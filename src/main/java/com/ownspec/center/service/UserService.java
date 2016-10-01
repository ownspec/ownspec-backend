package com.ownspec.center.service;

import com.ownspec.center.dto.UserDto;
import com.ownspec.center.exception.UserAlreadyExistsException;
import com.ownspec.center.model.user.User;
import com.ownspec.center.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ownspec.center.util.OsUtils.mergeWithNotNullProperties;
import static java.util.Objects.requireNonNull;

/**
 * Created by lyrold on 23/08/2016.
 */
@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User foundUser = userRepository.findByUsername(username);
        if (foundUser == null) {
            throw new UsernameNotFoundException("Cannot found username [" + username + "]");
        }
        return foundUser;
    }

    public User logIn(User user) {
        return null;
    }

    public void logOut(User user) {

    }

    public User create(UserDto source) {
        String username = source.getUsername();
        if (null != userRepository.findByUsername(username)) {
            throw new UserAlreadyExistsException(username);
        }

        User user = new User();
        user.setUsername(source.getUsername());
        user.setPassword(source.getPassword());
        user.setFirstName(source.getFirstName());
        user.setLastName(source.getLastName());
        user.setEmail(source.getEmail());
        user.setRole(source.getRole());
        user.setEnabled(false);
        user.setAccountNonLocked(false);

        emailService.sendConfirmRegistrationNotification(user);

        userRepository.save(user);
        return user;
    }

    public User update(UserDto source, Long id) {
        User target = requireNonNull(userRepository.findOne(id));
        mergeWithNotNullProperties(source, target);
        return userRepository.save(target);
    }

    public void delete(Long id) {
        userRepository.delete(id);
    }

    public void resetPassword(Long id) {
        User target = requireNonNull(userRepository.findOne(id));
        emailService.sendResetPasswordNotification(target);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

}
