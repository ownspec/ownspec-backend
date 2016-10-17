package com.ownspec.center.repository.component;

import java.util.List;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Created by lyrold on 18/09/2016.
 */
public interface ComponentRepository extends JpaRepository<Component, Long>, QueryDslPredicateExecutor<Component> {

  Component findByTitle(String title);

  Component findByType(ComponentType componentType);

}
