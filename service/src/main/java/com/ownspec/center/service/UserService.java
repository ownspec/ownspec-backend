package com.ownspec.center.service;

import static com.ownspec.center.util.OsUtils.mergeWithNotNullProperties;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableMap;
import com.ownspec.center.dto.user.UserCategoryDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by lyrold on 23/08/2016.
 */
@Service
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

  @Autowired
  private EmailService emailService;

  @Autowired
  private CompositionService compositionService;

  @Autowired
  private UserCategoryService userCategoryService;

  @Override
  public User loadUserByUsername(String username) throws UsernameNotFoundException {
    User foundUser = userRepository.findByUsername(username);
    if (foundUser == null) {
      throw new UsernameNotFoundException("Cannot found username [" + username + "]");
    }
    return foundUser;
  }

  public User findOne(Long id) {
    return userRepository.findOne(id);
  }

  public void create(UserDto source, URL requestUrl) throws Exception {
    String username = source.getUsername();
    LOG.info("Start create new user with username [{}]", username);
    if (null != userRepository.findByUsername(username)) {
      throw new UserAlreadyExistsException(username);
    }

    User user = new User();
    user.setUsername(source.getUsername());
    user.setFirstName(source.getFirstName());
    user.setFullName(user.getFirstName() + " " + user.getLastName());
    user.setLastName(source.getLastName());
    user.setEmail(source.getEmail());
    user.setPhone(source.getPhone());
    user.setMobile(source.getMobile());
    user.setRole(source.getRole());
    user.setEnabled(false);
    user.setAccountNonLocked(false);

    UserCategoryDto categoryDto = source.getCategory();
    if (categoryDto != null) {
      user.setCategory(userCategoryService.findOne(categoryDto.getId()));
    }

    LOG.info("Build verification token");
    VerificationToken verificationToken = buildVerificationToken(user);

    LOG.info("Send user the registration confirmation with token [{}]", verificationToken.getToken());
    AbstractMimeMessage message = buildConfirmRegistrationMessage(user, verificationToken.getToken(), requestUrl);
    emailService.send(message);

    LOG.info("Save user and token to repositories");
    userRepository.save(user);
    verificationTokenRepository.save(verificationToken);
  }

  private VerificationToken buildVerificationToken(User user) {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setUser(user);
    verificationToken.setToken(UUID.randomUUID().toString());
    verificationToken.setExpiryDate(DateUtils.addDays(new Date(), 5));

    return verificationToken;
  }

  public User update(UserDto source, Long id) {
    User target = requireNonNull(findOne(id));
    mergeWithNotNullProperties(source, target);

    UserCategoryDto category = source.getCategory();
    if (category != null) {
      target.setCategory(userCategoryService.findOne(category.getId()));
    }
    return userRepository.save(target);
  }

  public User update(User user) {
    return userRepository.save(user);
  }

  public void disable(Long id) {
    User target = requireNonNull(findOne(id));
    target.setEnabled(false);
    target.setAccountNonLocked(false);
    target.setAccountNonExpired(false);
    target.setCredentialsNonExpired(false);
    userRepository.save(target);
  }

  public void sendChangePasswordEmail(Long id, URL requestUrl) {
    User target = requireNonNull(findOne(id));
    AbstractMimeMessage message = buildChangePasswordMessage(target, requestUrl);
    emailService.send(message);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public AbstractMimeMessage buildChangePasswordMessage(User user, URL requestUrl) {
    // Build verification url
    String changePasswordUrl = requestUrl.getProtocol() + "://" +
                               requestUrl.getHost() + ":" +
                               requestUrl.getPort() +
                               "/auth/change-password/user/" + user.getId();

    // Compose email body
    LOG.info("Change password - compose email body");
    String content = compositionService.compose(
        "email/change_password.ftl",
        ImmutableMap.of("changePasswordUrl", changePasswordUrl));

    String emailBody = compositionService.compose(
        "email/abstract_notification",
        ImmutableMap.of(
            "firstName", user.getFirstName(),
            "content", content
        ));
    return AbstractMimeMessage.builder()
        .addRecipient(user.getEmail())
        .subject("Ownspec - Change Password") //todo to be internationalized
        .body(emailBody);
  }

  public AbstractMimeMessage buildConfirmRegistrationMessage(User user, String verificationToken, URL requestUrl) {
    // Build verification url
    String verificationUrl = requestUrl.getProtocol() + "://" +
                             requestUrl.getHost() + ":" +
                             requestUrl.getPort() +
                             "/auth/registration/confirmation/" + verificationToken;
    // Compose email body
    LOG.info("Compose email body");
    String content = compositionService.compose(
        "email/confirm_registration_content",
        ImmutableMap.of("verificationUrl", verificationUrl));

    String emailBody = compositionService.compose(
        "email/abstract_notification",
        ImmutableMap.of(
            "firstName", user.getFirstName(),
            "content", content
        ));
    return AbstractMimeMessage.builder()
        .addRecipient(user.getEmail())
        .subject("Ownspec - Account registration") //todo to be internationalized
        .body(emailBody);
  }

}
