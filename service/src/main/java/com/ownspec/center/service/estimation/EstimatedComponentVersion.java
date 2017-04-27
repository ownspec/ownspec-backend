package com.ownspec.center.service.estimation;

import com.ownspec.center.dto.EstimatedTimeDto;
import com.ownspec.center.dto.component.ComponentVersionDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nlabrot on 23/04/17.
 */
public class EstimatedComponentVersion {

  private int level;
  private ComponentVersionDto componentVersion;

  private List<EstimateWithPrice> estimatedTimesPerCategory = new ArrayList<>();
  private List<EstimateWithPrice> childrenEstimatedTimesPerCategory = new ArrayList<>();
  private List<EstimateWithPrice> totalEstimatedTimesPerCategory = new ArrayList<>();

  private EstimateWithPrice childrenEstimatedTime = new EstimateWithPrice(0, 0);
  private EstimateWithPrice totalEstimatedTime = new EstimateWithPrice(0, 0);
  private EstimateWithPrice estimatedTime = new EstimateWithPrice(0, 0);

  public EstimatedComponentVersion(int level, ComponentVersionDto componentVersion) {
    this.level = level;
    this.componentVersion = componentVersion;
  }

  public void addChildrenEstimates(List<EstimateWithPrice> childrenEstimatedTimes) {
    childrenEstimatedTimes.forEach(e -> {
      merge(this.childrenEstimatedTimesPerCategory, new EstimateWithPrice(e));
      merge(this.totalEstimatedTimesPerCategory, new EstimateWithPrice(e));
      childrenEstimatedTime.add(new EstimateWithPrice(e));
      totalEstimatedTime.add(new EstimateWithPrice(e));
    });
  }


  public void addEstimates(List<EstimatedTimeDto> addEstimatedTimes) {
    addEstimatedTimes.forEach(dto -> {
      merge(this.estimatedTimesPerCategory, new EstimateWithPrice(dto));
      merge(this.totalEstimatedTimesPerCategory, new EstimateWithPrice(dto));
      estimatedTime.add(new EstimateWithPrice(dto));
      totalEstimatedTime.add(new EstimateWithPrice(dto));
    });
  }

  private void merge(List<EstimateWithPrice> estimates, EstimateWithPrice e) {
    List<EstimateWithPrice> newEstimates = new ArrayList<>();
    for (EstimateWithPrice c : estimates) {
      if (c.getUserCategory().getId().equals(e.getUserCategory().getId())) {
        c.add(e);
        return;
      }
    }
    estimates.add(e);
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public ComponentVersionDto getComponentVersion() {
    return componentVersion;
  }

  public void setComponentVersion(ComponentVersionDto componentVersion) {
    this.componentVersion = componentVersion;
  }

  public List<EstimateWithPrice> getEstimatedTimesPerCategory() {
    return estimatedTimesPerCategory;
  }

  public void setEstimatedTimesPerCategory(List<EstimateWithPrice> estimatedTimesPerCategory) {
    this.estimatedTimesPerCategory = estimatedTimesPerCategory;
  }

  public List<EstimateWithPrice> getChildrenEstimatedTimesPerCategory() {
    return childrenEstimatedTimesPerCategory;
  }

  public void setChildrenEstimatedTimesPerCategory(List<EstimateWithPrice> childrenEstimatedTimesPerCategory) {
    this.childrenEstimatedTimesPerCategory = childrenEstimatedTimesPerCategory;
  }

  public List<EstimateWithPrice> getTotalEstimatedTimesPerCategory() {
    return totalEstimatedTimesPerCategory;
  }

  public void setTotalEstimatedTimesPerCategory(List<EstimateWithPrice> totalEstimatedTimesPerCategory) {
    this.totalEstimatedTimesPerCategory = totalEstimatedTimesPerCategory;
  }

  public EstimateWithPrice getChildrenEstimatedTime() {
    return childrenEstimatedTime;
  }

  public void setChildrenEstimatedTime(EstimateWithPrice childrenEstimatedTime) {
    this.childrenEstimatedTime = childrenEstimatedTime;
  }

  public EstimateWithPrice getTotalEstimatedTime() {
    return totalEstimatedTime;
  }

  public void setTotalEstimatedTime(EstimateWithPrice totalEstimatedTime) {
    this.totalEstimatedTime = totalEstimatedTime;
  }

  public EstimateWithPrice getEstimatedTime() {
    return estimatedTime;
  }

  public void setEstimatedTime(EstimateWithPrice estimatedTime) {
    this.estimatedTime = estimatedTime;
  }
}
