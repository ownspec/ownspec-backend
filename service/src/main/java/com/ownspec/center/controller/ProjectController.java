package com.ownspec.center.controller;

import static com.ownspec.center.util.RequestFilterMode.FAVORITES_ONLY;
import static com.ownspec.center.util.RequestFilterMode.LAST_VISITED_ONLY;

import com.ownspec.center.dto.ProjectDto;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.Project;
import com.ownspec.center.repository.ProjectRepository;
import com.ownspec.center.repository.user.UserProjectRepository;
import com.ownspec.center.service.ProjectService;
import com.ownspec.center.service.UserService;
import com.ownspec.center.util.RequestFilterMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

  @Autowired
  private UserProjectRepository userProjectRepository;

  @RequestMapping("/{id}")
  public ProjectDto get(@PathVariable("id") Long id) {
    return projectService.findOne(id);
  }

  @PostMapping
  public ResponseEntity create(@RequestBody ProjectDto projectDto) {
    projectService.create(projectDto);
    // TODO: return created with a valid URI
    return ResponseEntity.ok("Project successfully created");
  }

  @PatchMapping(value = "/{id}")
  @ResponseBody
  public ResponseEntity update(@PathVariable("id") Long id, @RequestBody ProjectDto source) {
    return projectService.update(id, source);
  }

  @DeleteMapping(value = "/{id}")
  @ResponseBody
  public ResponseEntity delete(@PathVariable("id") Long id) {
    Project project = projectRepository.findOne(id);
    if (project != null) {
      userProjectRepository.deleteAllByProjectId(id);
      projectRepository.delete(id);
      return ResponseEntity.ok("Project successfully removed");
    } else {
      return ResponseEntity.badRequest().body("Cannot remove project with id [" + id + "]; cause not found");
    }
  }

  @GetMapping
  public List<ProjectDto> findAll(@RequestParam(value = "mode", required = false) RequestFilterMode mode) {

    List<Project> projects = LAST_VISITED_ONLY.equals(mode) ? projectService.getLastVisited() :
                             FAVORITES_ONLY.equals(mode) ? projectService.getFavorites() :
                             projectRepository.findAll();
    return projects.stream()
        .map(p -> {
          List<UserDto> projectUsers = userProjectRepository.findAllByProjectId(p.getId()).stream()
              .map(userProject -> UserDto.fromUser(userProject.getUser()))
              .collect(Collectors.toList());

          return ProjectDto.newBuilderFromProject(p)
              .addAllProjectUsers(projectUsers)
              .build();
        })
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
