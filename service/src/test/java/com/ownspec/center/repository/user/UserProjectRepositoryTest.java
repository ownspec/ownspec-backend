package com.ownspec.center.repository.user;

import com.ownspec.center.AbstractTest;
import com.ownspec.center.model.user.UserProject;
import com.ownspec.center.repository.ProjectRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created on 17/12/2016
 *
 * @author lyrold
 */
public class UserProjectRepositoryTest extends AbstractTest {

  @Autowired
  ProjectRepository projectRepository;

  @Autowired
  UserProjectRepository userProjectRepository;

  @Test
  public void findOneByUserIdAndProjectId() throws Exception {
    for (Long index = 1L; index <= projectRepository.count(); index++) {
      UserProject userProject = new UserProject();
      userProject.setUser(userRepository.findOne(index));
      userProject.setFavorite(index % 2 == 0);
      userProject.setProject(projectRepository.findOne(index));
      userProjectRepository.save(userProject);
    }

    UserProject userProject1 = userProjectRepository.findOneByUserIdAndProjectId(1L, 1L);
    Assert.assertEquals("JPMichel", userProject1.getProject().getTitle());
    Assert.assertEquals("lyrold", userProject1.getUser().getUsername());

    UserProject userProject2 = userProjectRepository.findOneByUserIdAndProjectId(2L, 2L);
    Assert.assertEquals("OS-center", userProject2.getProject().getTitle());
    Assert.assertEquals("b.ramos@ownspec.com", userProject2.getUser().getUsername());

  }

  @Test
  public void findTop3ByUserIdAndFavoriteTrueOrderByLastModifiedDateDesc() throws Exception {

    for (Long index = 1L; index <= projectRepository.count(); index++) {
      UserProject userProject = new UserProject();
      userProject.setUser(userRepository.findOne(4L));
      userProject.setProject(projectRepository.findOne(index));
      userProject.setFavorite(true);
      userProject.setLastModifiedDate(Instant.now().plus(index, ChronoUnit.MINUTES));
      userProjectRepository.save(userProject);
    }

    Assert.assertEquals(projectRepository.count(), userProjectRepository.count());

    List<UserProject> userProjects = userProjectRepository.findTop3ByUserIdAndFavoriteTrueOrderByLastModifiedDateDesc(4L);
    Assert.assertEquals(3, userProjects.size());

    for (UserProject userProject : userProjects) {
      Assert.assertEquals("n.labrot@ownspec.com", userProject.getUser().getUsername());
    }
  }

  @Test
  public void findTop3ByUserIdOrderByLastModifiedDateDesc() throws Exception {
    for (Long index = 1L; index <= projectRepository.count(); index++) {
      UserProject userProject = new UserProject();
      userProject.setUser(userRepository.findOne(4L));
      userProject.setProject(projectRepository.findOne(index));
      userProject.setFavorite(false);
      userProject.setLastModifiedDate(Instant.now().plus(index, ChronoUnit.MINUTES));
      userProjectRepository.save(userProject);
    }

    Assert.assertEquals(projectRepository.count(), userProjectRepository.count());

    List<UserProject> userProjects = userProjectRepository.findTop3ByUserIdOrderByLastModifiedDateDesc(4L);
    Assert.assertEquals(3, userProjects.size());

    for (UserProject userProject : userProjects) {
      Assert.assertEquals("n.labrot@ownspec.com", userProject.getUser().getUsername());
    }

  }

  @Test
  public void findAllByProjectId() throws Exception {

  }

  @Test
  public void deleteByUserIdAndProjectId() throws Exception {

  }

}