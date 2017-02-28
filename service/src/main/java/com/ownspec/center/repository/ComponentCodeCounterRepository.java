package com.ownspec.center.repository;

import com.ownspec.center.model.Project;
import com.ownspec.center.model.component.ComponentCodeCounter;
import com.ownspec.center.model.component.ComponentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by lyrold on 23/08/2016.
 */
public interface ComponentCodeCounterRepository extends JpaRepository<ComponentCodeCounter, Long> {


  @Query(nativeQuery = true, value = "SELECT * FROM COMPONENT_CODE_COUNTER WHERE ID = :id FOR UPDATE")
  ComponentCodeCounter findOneAndLockById(@Param("id") Long id);

  @Query(nativeQuery = true, value = "SELECT * FROM COMPONENT_CODE_COUNTER WHERE GENERIC=true FOR UPDATE")
  ComponentCodeCounter findGenericAndLock();

}
