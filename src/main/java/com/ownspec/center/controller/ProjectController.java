package com.ownspec.center.controller;

import com.ownspec.center.dto.ImmutableProjectDto;
import com.ownspec.center.dto.ProjectDto;
import com.ownspec.center.dto.UserDto;
import com.ownspec.center.model.Project;
import com.ownspec.center.repository.ProjectRepository;
import com.ownspec.center.util.OsUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.ownspec.center.dto.ImmutableProjectDto.newProjectDto;

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

    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody Project source) {
        Project target = repository.findOne(id);
        if (target != null) {
            OsUtils.mergeWithNotNullProperties(source, target);
            repository.save(target);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Cannot remove project with id [" + id + "]; cause not found");
        }
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
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
    public List<ProjectDto> findAll() {
        return repository.findAll().stream()
                .map(p -> newProjectDto()
                        .id(p.getId())
                        .title(p.getTitle())
                        .description(p.getDescription())
                        .createdDate(p.getCreatedDate())
                        .createdUser(UserDto.createFromUser(p.getCreatedUser()))
                        .build())
                .collect(Collectors.toList());
    }


}
