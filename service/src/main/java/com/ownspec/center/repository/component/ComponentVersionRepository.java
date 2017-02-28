package com.ownspec.center.repository.component;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.ComponentVersion;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by lyrold on 18/09/2016.
 */
public interface ComponentVersionRepository extends JpaRepository<ComponentVersion, Long>, QueryDslPredicateExecutor<ComponentVersion> {


  Component findByTitle(String title);

  @Query(nativeQuery = true, value =
      "SELECT CV.* FROM COMPONENT_VERSION CV " +
          "INNER JOIN COMPONENT_TAG CT ON CT.COMPONENT_VERSION_ID = CV.ID " +
          "INNER JOIN TAG T ON T.ID = CT.TAG_ID WHERE T.LABEL=:label")
  List<ComponentVersion> findAllByComponentTagTagLabel(@Param("label") String label);

  @Query(nativeQuery = true, value =
      "SELECT CV.* FROM COMPONENT_VERSION CV " +
          "WHERE CV.COMPONENT_ID=:componentId " +
          "ORDER BY CREATED_DATE DESC " +
          "LIMIT 1")
  ComponentVersion findLatestByComponentId(@Param("componentId") Long componentId);


  ComponentVersion findOneByComponentIdAndId(long id, long componentId);

  List<ComponentVersion> findAllByComponentId(Long componentId, Sort sort);

  List<ComponentVersion> findAllByComponentTypeIn(List<ComponentType> componentTypes);


  @Query(nativeQuery = true, value = "SELECT * FROM COMPONENT_VERSION WHERE ID = :id FOR UPDATE")
  ComponentVersion findOneAndLock(@Param("id") Long id);
}
