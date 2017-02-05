package com.ownspec.center.service;

import com.ownspec.center.dto.EstimatedTimeDto;
import com.ownspec.center.model.EstimatedTime;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.repository.EstimatedTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created on 11/12/2016
 *
 * @author lyrold
 */
@Service
public class EstimatedTimeService {

  @Autowired
  private EstimatedTimeRepository estimatedTimeRepository;

  public List<EstimatedTime> getEstimatedTimes(Long componentId) {
    return estimatedTimeRepository.findAllByComponentId(componentId);
  }

  public EstimatedTime addEstimatedTime(Component target, EstimatedTimeDto estimatedTimeDto) {
    EstimatedTime estimatedTime = new EstimatedTime();
    estimatedTime.setUserCategory(estimatedTimeDto.getUserCategory());
    estimatedTime.setTime(estimatedTimeDto.getTime());
    estimatedTime.setTimeUnit(estimatedTimeDto.getTimeUnit());
    estimatedTime.setComponent(target);

    return estimatedTimeRepository.save(estimatedTime);
  }

}
