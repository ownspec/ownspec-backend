package com.ownspec.center.controller;

import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.repository.ComponentRepository;
import com.ownspec.center.service.ComponentService;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by lyrold on 20/09/2016.
 */
@RestController
@RequestMapping("/api/components")
public class ComponentController {
    @Autowired
    private ComponentService componentService;


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity create(@RequestBody ComponentDto source) throws IOException, GitAPIException {
        componentService.createComponentWith(source);
        return ResponseEntity.ok().build();
    }


    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody ComponentDto source) throws GitAPIException, UnsupportedEncodingException {
        componentService.updateComponentWith(source, id);
        return ResponseEntity.ok().build();
    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") Long id) {
        componentService.removeComponentWith(id);
        return ResponseEntity.ok().build();
    }

}
