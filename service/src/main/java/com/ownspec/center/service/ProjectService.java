package com.ownspec.center.service;

import com.ownspec.center.dto.ProjectDto;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.component.ComponentCodeCounter;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.user.UserProject;
import com.ownspec.center.repository.ComponentCodeCounterRepository;
import com.ownspec.center.repository.ProjectRepository;
import com.ownspec.center.repository.user.UserProjectRepository;
import com.ownspec.center.util.OsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

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
  private ProjectRepository projectRepository;

  @Autowired
  private UserProjectRepository userProjectRepository;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private ComponentCodeCounterRepository componentCodeCounterRepository;


  @Autowired
  private UserService userService;

  public Project create(@RequestBody ProjectDto projectDto) {

    ComponentCodeCounter ccc = new ComponentCodeCounter();
    ccc.setKey(projectDto.getKey());
    ccc = componentCodeCounterRepository.save(ccc);


    Project project = new Project();
    project.setTitle(projectDto.getTitle());
    project.setDescription(projectDto.getDescription());
    project.setComponentCodeCounter(ccc);

    UserDto manager = projectDto.getManager();
    if (manager != null) {
      project.setManager(userService.loadUserByUsername(manager.getUsername()));
      project = projectRepository.save(project);

      // Add manager in projectUsers
      UserProject userProject = new UserProject();
      userProject.setUser(userService.loadUserByUsername(manager.getUsername()));
      userProject.setProject(project);
      userProjectRepository.save(userProject);
    } else {
      project = projectRepository.save(project);
    }

    List<UserDto> projectUsers = projectDto.getProjectUsers();
    if (projectUsers != null) {
      for (UserDto user : projectUsers) {
        UserProject userProject = new UserProject();
        userProject.setUser(userService.loadUserByUsername(user.getUsername()));
        userProject.setProject(project);

        userProjectRepository.save(userProject);
      }
    }
    return project;
  }

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

  public List<Project> getFavorites() {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    return userProjectRepository.findTop3ByUserIdAndFavoriteTrueOrderByLastModifiedDateDesc(authenticatedUser.getId()).stream()
        .map(UserProject::getProject)
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

  public List<Project> getLastVisited() {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    return userProjectRepository.findTop3ByUserIdOrderByLastModifiedDateDesc(authenticatedUser.getId()).stream()
        .map(UserProject::getProject)
        .collect(Collectors.toList());
  }

  public ProjectDto findOne(Long id) {
    Project project = projectRepository.findOne(id);
    if (project != null) {
      return ProjectDto.newBuilderFromProject(project)
          .manager(UserDto.fromUser(project.getManager()))
          .projectUsers(
              userProjectRepository.findAllByProjectId(project.getId()).stream()
                  .map(userProject -> UserDto.fromUser(userProject.getUser()))
                  .collect(Collectors.toList()))
          .build();
    }
    return null;
  }

  public ResponseEntity update(Long id, ProjectDto source) {
    LOG.info("Update project with id [{}]", id);
    Project target = projectRepository.findOne(id);
    if (target != null) {
      // Update project
      LOG.info("Merge and save project with id [{}]", target.getId());
      OsUtils.mergeWithNotNullProperties(source, target);
      projectRepository.save(target);

      List<UserDto> projectUsers = source.getProjectUsers();
      if (projectUsers != null && !projectUsers.isEmpty()) {
        LOG.info("Update project users");
        for (UserDto user : projectUsers) {
          addUserToProject(id, user.getUsername());
        }
      }

      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().body("Cannot remove project with id [" + id + "]; cause not found");
    }
  }

  private void addUserToProject(Long projectId, String username) {
    User user = userService.loadUserByUsername(username);
    UserProject foundUP = userProjectRepository.findOneByUserIdAndProjectId(user.getId(), projectId);

    // Create if doesn't
    if (foundUP == null) {
      LOG.info("Add user [{}] to project with id [{}]", username, projectId);
      UserProject newUserProject = new UserProject();
      newUserProject.setUser(user);
      newUserProject.setProject(projectRepository.findOne(projectId));
      userProjectRepository.save(newUserProject);
    } else {
      LOG.info("User [{}] already exists in project with id [{}]", username, projectId);
    }
  }

  public ResponseEntity removeUserFromProject(Long projectId, String username) {
    LOG.info("Remove user [{}] from project where projectId=[{}] and username=[{}]", username, projectId);
    User user = userService.loadUserByUsername(username);
    userProjectRepository.deleteByUserIdAndProjectId(user.getId(), projectId);
    return ResponseEntity.ok().build();
  }
}
