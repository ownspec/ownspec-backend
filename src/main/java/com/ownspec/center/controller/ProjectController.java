package com.ownspec.center.controller;

import com.ownspec.center.model.Project;
import com.ownspec.center.repository.ProjectRepository;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by lyrold on 18/09/2016.
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository repository;


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity create() {
        return ResponseEntity.ok("Project successfully created");
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public String update(@PathVariable("id") Long id) throws GitAPIException {
        Project requestedProject = repository.findOne(id);
        if (requestedProject != null) {
            // Do something
            repository.saveAndFlush(requestedProject);
            return "Project successfully updated";
        } else {
            return "Cannot remove project with id [" + id + "]; cause not found";
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") Long id) {
        Project project = repository.findOne(id);
        if (project != null) {
            repository.delete(id);
            return ResponseEntity.ok("Project successfully removed");
        } else {
            return ResponseEntity.badRequest().body("Cannot remove project with id [" + id + "]; cause not found");
        }
    }

    @RequestMapping
    public List<Project> findAll() {
        return repository.findAll();
    }


}
