package com.ownspec.center.repository.tag;

import com.ownspec.center.model.Tag;
import com.ownspec.center.model.component.ComponentTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by nlabrot on 29/12/16.
 */
public interface ComponentTagRepository extends JpaRepository<ComponentTag, Long> {

  ComponentTag findOneByComponentVersionIdAndTagId(long componentId, long tagId);


  List<ComponentTag> findAllByComponentVersionId(long componentVersionId);

}
