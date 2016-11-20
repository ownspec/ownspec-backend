package com.ownspec.center.service;

import static com.ownspec.center.util.OsUtils.mergeWithNotNullProperties;
import static java.util.Objects.requireNonNull;

import com.ownspec.center.configuration.security.SecretKey;
import com.ownspec.center.dto.UserDto;
import com.ownspec.center.exception.UserAlreadyExistsException;
import com.ownspec.center.model.user.User;
import com.ownspec.center.repository.UserRepository;
import com.ownspec.center.util.AbstractMimeMessage;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lyrold on 23/08/2016.
 */
@Service
@Slf4j
public class AuthenticationService  {
  @Value("${jwt.cookie.name}")
  private String cookieName;

  @Autowired
  private SecretKey secretKey;

  @Autowired
  private UserRepository userRepository;


  @Autowired
  private AuthenticationManager authenticationManager;

  // TODO: a service must not return a response entity, the login must be done by the controller or a facade called by the controller
  public ResponseEntity login(UserDto source) {
    LOG.info("Request login with username [{}]", source.getUsername());
    User target = userRepository.findByUsername(source.getUsername());
    if (target == null) {
      throw new UsernameNotFoundException("Unknown username");
    }
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(source.getUsername(), source.getPassword());
    Authentication authentication = authenticationManager.authenticate(token);
    if (authentication.isAuthenticated()) {
      LOG.info("Authentication succeed");
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String jwtToken = Jwts.builder()
                            .setSubject(target.getUsername())
                            .claim("company", target.getCompany())
                            .claim("role", target.getRole())
                            .signWith(SignatureAlgorithm.HS256, secretKey.getValue())
                            .compact();

      LOG.info("Built token is [{}]", jwtToken);
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.add(HttpHeaders.SET_COOKIE, String.join("=", cookieName, jwtToken) + "; path=/");
      return new ResponseEntity<String>(httpHeaders, HttpStatus.OK);
    } else {
      LOG.warn("Authentication failed");
      return ResponseEntity.badRequest().build();
    }
  }

  public HttpServletResponse logOut(HttpServletResponse response) {
    LOG.info("Request logout");
    Cookie cookie = new Cookie(cookieName, "");
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
    return response;
  }

}
