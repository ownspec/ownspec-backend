package com.ownspec.center.repository;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentTypes;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by lyrold on 18/09/2016.
 */
public interface ComponentRepository extends JpaRepository<Component, Long> {

    Component findByTitle(String title);

    Component findByType(ComponentTypes componentType);
}
