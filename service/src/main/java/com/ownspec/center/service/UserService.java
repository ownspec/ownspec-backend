package com.ownspec.center.service;

import static com.ownspec.center.util.OsUtils.mergeWithNotNullProperties;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableMap;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.exception.UserAlreadyExistsException;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.user.VerificationToken;
import com.ownspec.center.repository.user.UserRepository;
import com.ownspec.center.repository.user.VerificationTokenRepository;
import com.ownspec.center.service.composition.CompositionService;
import com.ownspec.center.util.AbstractMimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by lyrold on 23/08/2016.
 */
@Service
@Slf4j
public class UserService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

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

  public void create(UserDto source, URL requestUrl) throws Exception {
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

    String verificationToken = buildAndSaveVerificationToken(user);

    AbstractMimeMessage message = buildConfirmRegistrationMessage(user, verificationToken, requestUrl);
    emailService.send(message);

    userRepository.save(user);
  }

  private String buildAndSaveVerificationToken(User user) {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setUser(user);
    verificationToken.setToken(UUID.randomUUID().toString());
    verificationToken.setExpiryDate(DateUtils.addDays(new Date(), 5));

    verificationTokenRepository.save(verificationToken);
    return verificationToken.getToken();
  }

  public User update(UserDto source, Long id) {
    User target = requireNonNull(userRepository.findOne(id));
    mergeWithNotNullProperties(source, target);
    return userRepository.save(target);
  }

  public void update(User user) {
    userRepository.save(user);
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

  private AbstractMimeMessage buildConfirmRegistrationMessage(User user, String verificationToken, URL requestUrl) {

    // Build verification url
    String verificationUrl = requestUrl.getProtocol() + "://" +
                             requestUrl.getHost() + ":" +
                             requestUrl.getPort() +
                             "/registrationConfirmation?token=" + verificationToken;
    //todo use standard URI/URL builder;

    // Compose email body
    String content = compositionService.compose(
        "templates/email/confirm_registration_content.ftl",
        ImmutableMap.of("verificationUrl", verificationUrl));

    String emailBody = compositionService.compose(
        "templates/email/abstract_notification.ftl",
        ImmutableMap.of(
            "firstName", user.getFirstName(),
            "content", content
        ));

    return AbstractMimeMessage.builder()
        .addRecipient(user.getEmail())
        .subject("Account registration") //todo to be internationalized
        .body(emailBody);
  }

}
