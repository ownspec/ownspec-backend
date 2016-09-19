package com.ownspec.center.controller;

import com.ownspec.center.model.Requirement;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.repository.ComponentRepository;
import com.ownspec.center.service.ComponentService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by lyrold on 20/09/2016.
 */
@RestController
@RequestMapping("/components")
public class ComponentController {
    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentRepository repository;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity create(@RequestBody Requirement source) throws IOException, GitAPIException {
        return componentService.createComponentWith(source, repository);
    }


    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody Component source) throws GitAPIException {
        return componentService.updateComponentWith(source, id, repository);
    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") Long id) {
        return componentService.removeComponentWith(id, repository);
    }

}
