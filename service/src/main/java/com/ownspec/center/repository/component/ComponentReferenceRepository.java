package com.ownspec.center.repository.component;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ownspec.center.model.component.ComponentReference;

import java.util.List;

/**
 * Created by lyrold on 18/09/2016.
 */
public interface ComponentReferenceRepository extends JpaRepository<ComponentReference, Long> {


  Long deleteBySourceIdAndSourceWorkflowInstanceId(Long sourceId, Long wrkflowInstanceId);

  List<ComponentReference> findAllBySourceIdAndSourceWorkflowInstanceId(Long sourceId, Long sourceWorkflowInstanceId);
  List<ComponentReference> findAllBySourceId(Long sourceId);

  List<ComponentReference> findAllByTargetId(Long targetId);
  List<ComponentReference> findAllByTargetIdAndTargetWorkflowInstanceId(Long targetId, Long targetWorkflowInstanceId);

}
