package com.ownspec.center.repository;

import com.ownspec.center.model.Tag;
import com.ownspec.center.model.component.ComponentTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by nlabrot on 29/12/16.
 */
public interface ComponentTagRepository extends JpaRepository<ComponentTag, Long> {

  ComponentTag findOneByComponentIdAndTagId(long componentId, long tagId);

  List<ComponentTag> findOneByComponentId(long componentId);
}
