package com.ownspec.center.service;

import com.ownspec.center.AbstractTest;
import com.ownspec.center.dto.ImmutableUserDto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * com.ownspec.center.service
 * <p>
 * UserServiceTest
 *
 * @author lyrold
 * @since 2016-10-16
 */
public class UserServiceTest extends AbstractTest {
  @Autowired
  UserService userService;

  @Autowired
  private AuthenticationService authenticationService;

  @Test
  public void login_success() throws Exception {

    ImmutableUserDto userDto = ImmutableUserDto.newUserDto()
                                               .username("lyrold")
                                               .password("lyrold")
                                               .build();
    ResponseEntity token = authenticationService.login(userDto);

//    Assert.assertEquals("", token); todo
  }

  @Test(expected = BadCredentialsException.class)
  public void login_badCredentials() throws Exception {

    ImmutableUserDto userDto = ImmutableUserDto.newUserDto()
                                               .username("lyrold")
                                               .password("foo")
                                               .build();
    authenticationService.login(userDto);
  }

}