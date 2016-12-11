package com.ownspec.center.repository;

import com.ownspec.center.model.EstimatedTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created on 11/12/2016
 *
 * @author lyrold
 */
public interface EstimatedTimeRepository extends JpaRepository<EstimatedTime, Long> {

  List<EstimatedTime> findAllByComponentId(Long id);

}
