package com.ownspec.center.repository.user;

import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.user.UserComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created on 16/12/2016
 *
 * @author lyrold
 */
public interface UserComponentRepository extends JpaRepository<UserComponent, Long> {

  UserComponent findOneByUserIdAndComponentId(Long userId, Long componentId);

  List<UserComponent> findTop3ByUserIdAndComponentTypeAndFavoriteTrueOrderByLastModifiedDateDesc(Long userId,ComponentType componentType);

  //todo define sql query to avoid componentType column in UserComponent
  List<UserComponent> findTop3ByUserIdAndComponentTypeOrderByLastModifiedDateDesc(Long userId, ComponentType componentType);
}
