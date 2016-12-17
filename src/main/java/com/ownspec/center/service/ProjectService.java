package com.ownspec.center.service;

import com.ownspec.center.dto.ProjectDto;
import com.ownspec.center.dto.UserProjectDto;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.user.UserProject;
import com.ownspec.center.repository.ProjectRepository;
import com.ownspec.center.repository.user.UserProjectRepository;
import com.ownspec.center.util.OsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 16/12/2016
 *
 * @author lyrold
 */
@Service
@Transactional
@Slf4j
public class ProjectService {

  @Autowired
  ProjectRepository projectRepository;

  @Autowired
  UserProjectRepository userProjectRepository;

  @Autowired
  AuthenticationService authenticationService;

  @Autowired
  UserService userService;

  public ResponseEntity addVisit(Long id) {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    UserProject foundUserProject = userProjectRepository.findOneByUserIdAndProjectId(
        authenticatedUser.getId(), id);

    if (foundUserProject == null) {
      UserProject userProject = new UserProject();
      userProject.setUser(authenticatedUser);
      userProject.setProject(projectRepository.findOne(id));
      userProject.setVisitedTime(1L);
      userProjectRepository.save(userProject);
    } else {
      foundUserProject.addVisit();
      userProjectRepository.save(foundUserProject);
    }

    return ResponseEntity.ok().build();
  }

  public List<ProjectDto> getFavorites() {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    return userProjectRepository.findTop3ByUserIdAndFavoriteTrueOrderByLastModifiedDateDesc(authenticatedUser.getId()).stream()
        .map(up -> ProjectDto.fromProject(up.getProject()))
        .collect(Collectors.toList());
  }

  public ResponseEntity updateFavorite(Long id, boolean isFavorite) {
    User authenticatedUser = authenticationService.getAuthenticatedUser();

    UserProject upa = userProjectRepository.findOneByUserIdAndProjectId(
        authenticatedUser.getId(), id);

    if (upa == null) {
      UserProject newUpa = new UserProject();
      newUpa.setUser(authenticatedUser);
      newUpa.setProject(projectRepository.findOne(id));
      newUpa.setFavorite(isFavorite);
      userProjectRepository.save(newUpa);
    } else {
      upa.setFavorite(isFavorite);
      userProjectRepository.save(upa);
    }

    return ResponseEntity.ok().build();
  }

  public List<ProjectDto> getLastVisited() {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    return userProjectRepository.findTop3ByUserIdOrderByLastModifiedDateDesc(authenticatedUser.getId()).stream()
        .map(up -> ProjectDto.fromProject(up.getProject()))
        .collect(Collectors.toList());
  }

  public ProjectDto findOne(Long id) {
    Project project = projectRepository.findOne(id);
    if (project != null) {
      return ProjectDto.newBuilderFromProject(project)
          .projectUsers(
              userProjectRepository.findAllByProjectId(project.getId()).stream()
                  .map(UserProjectDto::fromUserProject)
                  .collect(Collectors.toList()))
          .build();
    }
    return null;
  }

  public ResponseEntity update(Long id, ProjectDto source) {
    Project target = projectRepository.findOne(id);
    if (target != null) {
      // Update project
      OsUtils.mergeWithNotNullProperties(source, target);
      projectRepository.save(target);

      List<UserProjectDto> projectUsers = source.getProjectUsers();
      if (projectUsers != null && !projectUsers.isEmpty()) {
        for (UserProjectDto projectUser : projectUsers) {
          addProjectUser(id, projectUser.getUser().getUsername());
        }
      }

      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().body("Cannot remove project with id [" + id + "]; cause not found");
    }
  }


  private void addProjectUser(Long projectId, String username) {
    // Search if exists
    User user = userService.loadUserByUsername(username);
    UserProject foundUP = userProjectRepository.findOneByUserIdAndProjectId(user.getId(), projectId);

    // Create if doesn't
    if (foundUP != null) {
      UserProject newUserProject = new UserProject();
      newUserProject.setUser(user);
      newUserProject.setProject(projectRepository.findOne(projectId));
      userProjectRepository.save(newUserProject);
    }
  }


  public ResponseEntity removeUserFromProject(Long projectId, String username) {
    User user = userService.loadUserByUsername(username);
    userProjectRepository.deleteByUserIdAndProjectId(user.getId(), projectId);
    return ResponseEntity.ok().build();
  }
}
