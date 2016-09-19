package com.ownspec.center.controller;

import com.ownspec.center.model.Requirement;
import com.ownspec.center.model.User;
import com.ownspec.center.repository.RequirementRepository;
import com.ownspec.center.service.ComponentService;
import com.ownspec.center.service.GitService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by lyrold on 23/08/2016.
 */
@RestController
@RequestMapping("/requirements")
public class RequirementController {

    private static final Logger LOG = LoggerFactory.getLogger(RequirementController.class);

    @Autowired
    RequirementRepository repository;

    @Autowired
    GitService gitService;

    @Autowired
    ComponentService componentService;

    //    @Autowired
    User currentUser;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity create(@RequestBody Requirement source) throws IOException, GitAPIException {
        return componentService.createComponentWith(source, repository);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody Requirement source) throws GitAPIException {
        return componentService.updateComponentWith(source, id, repository);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") Long id) {
        return componentService.removeComponentWith(id, repository);
    }

    @RequestMapping
    public List<Requirement> findAll() {
        return repository.findAll();
    }

}
