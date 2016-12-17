package com.ownspec.center.controller;

import static com.ownspec.center.util.RequestFilterMode.FAVORITES_ONLY;
import static com.ownspec.center.util.RequestFilterMode.LAST_VISITED_ONLY;

import com.ownspec.center.dto.ProjectDto;
import com.ownspec.center.model.Project;
import com.ownspec.center.repository.ProjectRepository;
import com.ownspec.center.service.ProjectService;
import com.ownspec.center.service.UserService;
import com.ownspec.center.util.OsUtils;
import com.ownspec.center.util.RequestFilterMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lyrold on 18/09/2016.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private ProjectService projectService;

  @RequestMapping("/{id}")
  public ProjectDto get(@PathVariable("id") Long id) {
    return projectService.findOne(id);
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity create(@RequestBody ProjectDto projectDto) {
    Project project = new Project();

    if (projectDto.getManager() != null) {
      project.setManager(userService.loadUserByUsername(projectDto.getManager().getUsername()));
    }
    OsUtils.mergeWithNotNullProperties(projectDto, project);
    projectRepository.save(project);
    return ResponseEntity.ok("Project successfully created");
  }

  @RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity update(@PathVariable("id") Long id, @RequestBody ProjectDto source) {
    return projectService.update(id, source);
  }

  @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity delete(@PathVariable("id") Long id) {
    Project project = projectRepository.findOne(id);
    if (project != null) {
      projectRepository.delete(id);
      return ResponseEntity.ok("Project successfully removed");
    } else {
      return ResponseEntity.badRequest().body("Cannot remove project with id [" + id + "]; cause not found");
    }
  }

  @GetMapping
  public List<ProjectDto> findAll(@RequestParam(value = "mode", required = false) RequestFilterMode mode) {

    if (LAST_VISITED_ONLY.equals(mode)) {
      return projectService.getLastVisited();
    } else if (FAVORITES_ONLY.equals(mode)) {
      return projectService.getFavorites();
    }//todo: to be improved
    return projectRepository.findAll().stream()
        .map(ProjectDto::fromProject)
        .collect(Collectors.toList());
  }

  @PostMapping("/{id}/addVisit")
  @ResponseBody
  public ResponseEntity addVisit(@PathVariable("id") Long id) {
    return projectService.addVisit(id);
  }

  @PostMapping("/{id}/{isFavorite}")
  @ResponseBody
  public ResponseEntity saveAsFavorite(@PathVariable("id") Long id, @PathVariable("isFavorite") boolean isFavorite) {
    return projectService.updateFavorite(id, isFavorite);
  }

  @DeleteMapping("/{id}/{username}")
  @ResponseBody
  public ResponseEntity removeUserFromProject(@PathVariable("id") Long id, @PathVariable("username") String userName) {
    return projectService.removeUserFromProject(id, userName);
  }

}
