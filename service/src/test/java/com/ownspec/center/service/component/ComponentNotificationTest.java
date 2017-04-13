package com.ownspec.center.service.component;

import com.dumbster.smtp.MailMessage;
import com.ownspec.center.AbstractTest;
import com.ownspec.center.dto.component.ImmutableComponentVersionDto;
import com.ownspec.center.dto.user.ImmutableUserDto;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.model.user.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by nlabrot on 01/10/16.
 */
public class ComponentNotificationTest extends AbstractTest {


  @Test
  @Transactional
  public void testAssigmentNotification() throws Exception {

    ImmutableUserDto userDto = ImmutableUserDto.newUserDto()
        .email("foo@gmail.com")
        .firstName("first")
        .role("ADMIN")
        .username("foo")
        .build();


    User user = userService.create(userDto, new URL("http://localhost"));

    MailMessage createUserMessage = persistentMailStore.getMailMessages().poll(5, TimeUnit.SECONDS);
    Assert.assertNotNull(createUserMessage);


    ImmutableComponentVersionDto componentDto = ImmutableComponentVersionDto.newComponentVersionDto()
        .title("test")
        .type(ComponentType.COMPONENT)
        .content("test0")
        .version("1")
        .build();


    ComponentVersion component = componentService.create(componentDto).getRight();

    persistentMailStore.clearMessages();

    componentVersionService.update(componentConverter.toComponentVersionDtoBuilder(component, false, false, false)
        .assignedTo(userDto.withId(user.getId())).build(), component.getId());

    MailMessage assigmentNotifMessage = persistentMailStore.getMailMessages().poll(5, TimeUnit.SECONDS);
    Assert.assertNotNull(assigmentNotifMessage);

  }

}
