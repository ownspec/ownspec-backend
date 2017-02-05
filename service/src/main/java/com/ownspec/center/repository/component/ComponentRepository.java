package com.ownspec.center.repository.component;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentTag;
import com.ownspec.center.model.component.ComponentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import javax.persistence.Tuple;

/**
 * Created by lyrold on 18/09/2016.
 */
public interface ComponentRepository extends JpaRepository<Component, Long>, QueryDslPredicateExecutor<Component> {

  Component findByTitle(String title);

  Component findByType(ComponentType componentType);

  @Query(nativeQuery = true , value = "SELECT C.* FROM COMPONENT C INNER JOIN COMPONENT_TAG CT ON CT.COMPONENT_ID = C.ID INNER JOIN TAG T ON T.ID = CT.TAG_ID WHERE T.LABEL=:label")
  List<Component> findAllByComponentTagTagLabel(@Param("label") String label);




  @Query(nativeQuery = true , value = "SELECT * FROM COMPONENT WHERE ID = :id FOR UPDATE")
  Component findOneAndLock(@Param("id") Long id);

}
