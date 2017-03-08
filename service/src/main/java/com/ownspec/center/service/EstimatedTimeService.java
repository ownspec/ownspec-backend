package com.ownspec.center.service;

import com.ownspec.center.dto.EstimatedTimeDto;
import com.ownspec.center.model.EstimatedTime;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.repository.EstimatedTimeRepository;
import com.ownspec.center.util.OsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created on 11/12/2016
 *
 * @author lyrold
 */
@Service
@Slf4j
public class EstimatedTimeService {

  @Autowired
  private EstimatedTimeRepository estimatedTimeRepository;

  public List<EstimatedTime> getEstimatedTimes(Long componentVersionId) {
    return estimatedTimeRepository.findAllByComponentVersionId(componentVersionId);
  }

  public EstimatedTime addOrUpdateEstimatedTime(ComponentVersion target, EstimatedTimeDto estimatedTimeDto) {
    EstimatedTime estimatedTime = estimatedTimeRepository.findOneByComponentVersionIdAndUserCategoryId(target.getId(), estimatedTimeDto.getUserCategory().getId());

    if (estimatedTime != null) {
      OsUtils.mergeWithNotNullProperties(estimatedTimeDto, estimatedTime);
    } else {
      LOG.info("Adding {}'s estimated time for component with ID {}", estimatedTimeDto.getUserCategory().getName(), target.getId());
      estimatedTime = new EstimatedTime();
      estimatedTime.setUserCategory(estimatedTimeDto.getUserCategory());
      estimatedTime.setTime(estimatedTimeDto.getTime());
      estimatedTime.setTimeUnit(estimatedTimeDto.getTimeUnit());
      estimatedTime.setComponentVersion(target);
    }

    return estimatedTimeRepository.save(estimatedTime);
  }

  public void addAndUpdateEstimatedTimes(ComponentVersion target, List<EstimatedTimeDto> estimatedTimeDtos) {
    if (estimatedTimeDtos != null) {
      estimatedTimeDtos.forEach(e -> addOrUpdateEstimatedTime(target, e));
    }
  }


}
