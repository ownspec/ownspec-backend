package com.ownspec.center.controller;

import com.google.common.collect.ImmutableMap;
import com.ownspec.center.dto.UserDto;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by nlabrot on 29/09/16.
 */
@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @RequestMapping
    @ResponseBody
    public List<User> findAll() {
        return userService.findAll();
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity currentUserProfile() {

        UserDto userDto = UserDto.createFromUser(userService.loadUserByUsername("admin"));

        ImmutableMap.Builder<Object, Object> propertiesBuilder = ImmutableMap.builder();
        propertiesBuilder.put("statuses", Status.values());

        return ResponseEntity.ok(ImmutableMap.builder()
                .put("user", userDto)
                .put("properties", propertiesBuilder.build()));
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity create(@RequestBody UserDto source) {
        userService.create(source);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}/update", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody UserDto source) {
        userService.update(source, id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}/resetPassword")
    @ResponseBody
    public ResponseEntity resetPassword(@PathVariable("id") Long id) {
        userService.resetPassword(id);
        return ResponseEntity.ok().build();
    }

}
