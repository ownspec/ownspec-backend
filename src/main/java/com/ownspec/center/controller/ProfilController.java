package com.ownspec.center.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.ownspec.center.dto.ImmutableUserDto;
import com.ownspec.center.dto.UserDto;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.repository.UserRepository;

/**
 * Created by nlabrot on 29/09/16.
 */
@RestController(value = "/api/profil")
public class ProfilController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping
    public ResponseEntity profil() {

        UserDto userDto = UserDto.createFromUser(userRepository.findByUsername("admin"));

        ImmutableMap.Builder<Object, Object> propertiesBuilder = ImmutableMap.builder();
        propertiesBuilder.put("statuses" , Status.values());

        return ResponseEntity.ok(ImmutableMap.builder()
                .put("user" , userDto)
                .put("properties", propertiesBuilder.build()));
    }

}
