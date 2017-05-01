package com.ownspec.center.repository.user;

import com.ownspec.center.model.user.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created on 16/12/2016
 *
 * @author lyrold
 */
public interface UserProjectRepository extends JpaRepository<UserProject, Long> {

  UserProject findOneByUserIdAndProjectId(Long userId, Long projectId);

  List<UserProject> findTop3ByUserIdAndFavoriteTrueOrderByLastModifiedDateDesc(Long userId);

  List<UserProject> findTop3ByUserIdOrderByLastModifiedDateDesc(Long id);

  List<UserProject> findAllByProjectId(Long id);

  void deleteByUserIdAndProjectId(Long userId, Long projectId);

  void deleteAllByProjectId(Long projectId);
}
