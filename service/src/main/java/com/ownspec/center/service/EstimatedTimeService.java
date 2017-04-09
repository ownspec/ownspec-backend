package com.ownspec.center.service;

import com.ownspec.center.dto.EstimatedTimeDto;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.model.component.EstimatedTime;
import com.ownspec.center.repository.EstimatedTimeRepository;
import com.ownspec.center.util.DateUtils;
import com.ownspec.center.util.DurationUtils;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

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

  public EstimatedTime persistEstimation(ComponentVersion target, EstimatedTimeDto estimatedTimeDto) {
    EstimatedTime estimatedTime = estimatedTimeRepository.findOneByComponentVersionIdAndUserCategoryId(target.getId(), estimatedTimeDto.getUserCategory().getId());

    if (estimatedTime == null) {
      LOG.info("Adding {}'s estimated time for component with ID {}", estimatedTimeDto.getUserCategory().getName(), target.getId());
      estimatedTime = new EstimatedTime();
    }

    estimatedTime.setUserCategory(estimatedTimeDto.getUserCategory());
    estimatedTime.setDuration(estimatedTimeDto.getDuration());
    estimatedTime.setDurationInMs(DurationUtils.getDurationSeconds(estimatedTimeDto.getDuration(), 8 * 60 * 60l, 5 * 8 * 60 * 60l, DateUtils.Duration.MINUTE, Locale.getDefault()));
    estimatedTime.setComponentVersion(target);

    PeriodFormatter formatter = new PeriodFormatterBuilder()
        .appendDays().appendSuffix("d ")
        .appendHours().appendSuffix("h ")
        .appendMinutes().appendSuffix("min")
        .toFormatter();

    Period p = formatter.parsePeriod("2d 5h 30min");


    return estimatedTimeRepository.save(estimatedTime);
  }

  public void persistEstimations(ComponentVersion target, List<EstimatedTimeDto> estimatedTimeDtos) {

    // Delete deleted estimation
    estimatedTimeRepository.findAllByComponentVersionId(target.getId())
        .stream()
        .filter(e -> estimatedTimeDtos.stream().noneMatch(ed -> ed.getId().equals(e.getId())))
        .forEach(estimatedTimeRepository::delete);

    estimatedTimeDtos.forEach(e -> persistEstimation(target, e));
  }


}
