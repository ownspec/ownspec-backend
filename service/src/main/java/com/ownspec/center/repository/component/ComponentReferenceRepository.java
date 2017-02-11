package com.ownspec.center.repository.component;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ownspec.center.model.component.ComponentReference;

import java.util.List;

/**
 * Created by lyrold on 18/09/2016.
 */
public interface ComponentReferenceRepository extends JpaRepository<ComponentReference, Long> {


  Long deleteBySourceId(Long sourceId);

  List<ComponentReference> findAllBySourceId(Long sourceId);

  List<ComponentReference> findAllByTargetId(Long targetId);

}
