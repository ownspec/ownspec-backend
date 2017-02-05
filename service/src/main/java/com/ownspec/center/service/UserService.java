package com.ownspec.center.service;

import static com.ownspec.center.util.OsUtils.mergeWithNotNullProperties;
import static java.util.Objects.requireNonNull;

import com.ownspec.center.dto.UserDto;
import com.ownspec.center.exception.UserAlreadyExistsException;
import com.ownspec.center.model.user.User;
import com.ownspec.center.repository.user.UserRepository;
import com.ownspec.center.service.composition.CompositionService;
import com.ownspec.center.util.AbstractMimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  @Autowired
  private PasswordEncoder encoder;

  @Autowired
  private CompositionService compositionService;


  @Override
  public User loadUserByUsername(String username) throws UsernameNotFoundException {
    User foundUser = userRepository.findByUsername(username);
    if (foundUser == null) {
      throw new UsernameNotFoundException("Cannot found username [" + username + "]");
    }
    return foundUser;
  }

  public User create(UserDto source) {
    String username = source.getUsername();
    if (null != userRepository.findByUsername(username)) {
      throw new UserAlreadyExistsException(username);
    }

    User user = new User();
    user.setUsername(source.getUsername());
    user.setPassword(encoder.encode(source.getPassword()));
    user.setFirstName(source.getFirstName());
    user.setLastName(source.getLastName());
    user.setEmail(source.getEmail());
    user.setRole(source.getRole());
    user.setEnabled(false);
    user.setAccountNonLocked(false);
    user.setSignature(buildDefaultSignature(user));

    //todo: Set token
    AbstractMimeMessage message = getConfirmRegistrationMessage(user);
    emailService.send(message);

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
    AbstractMimeMessage message = getResetPasswordMessage(target);
    emailService.send(message);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  private AbstractMimeMessage getResetPasswordMessage(User user) {
    return null;
  }

  private AbstractMimeMessage getConfirmRegistrationMessage(User user) {
    String confirmationLink;
    AbstractMimeMessage message = AbstractMimeMessage.builder()
                                                     .addRecipient(user.getEmail())
                                                     .subject("Account registration") //todo to be internationalized
                                                     .body("Dear "); //todo

    return message;
  }

  private String buildDefaultSignature(User user) {
    Map<String, Object> model = new HashMap<>();
    model.put("firstname", user.getFirstName());
    model.put("lastname", user.getLastName());
    model.put("phone", user.getPhone());
    model.put("email", user.getEmail());

    return compositionService.compose("templates/email/signature.ftl", model);
  }

}