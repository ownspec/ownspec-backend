package com.ownspec.center.controller;

import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.model.Project;
import com.ownspec.center.repository.ProjectRepository;
import com.ownspec.center.util.OsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by lyrold on 18/09/2016.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository repository;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity create(@RequestBody Project project) {

        return ResponseEntity.ok("Project successfully created");
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody Project source) {
        Project target = repository.findOne(id);
        if (target != null) {
            OsUtils.mergeWithNotNullProperties(source, target);
            repository.saveAndFlush(target);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Cannot remove project with id [" + id + "]; cause not found");
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
