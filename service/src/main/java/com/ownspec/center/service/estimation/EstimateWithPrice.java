package com.ownspec.center.service.estimation;

import com.ownspec.center.dto.EstimatedTimeDto;
import com.ownspec.center.dto.user.UserCategoryDto;

/**
 * Created by nlabrot on 23/04/17.
 */
public class EstimateWithPrice {

  private float estimateInMs;
  private float price;

  private UserCategoryDto userCategory;

  public EstimateWithPrice() {
  }


  public EstimateWithPrice(EstimateWithPrice toClone) {
    this.estimateInMs = toClone.getEstimateInMs();
    this.price = toClone.getPrice();
    this.userCategory = toClone.getUserCategory();
  }

  public EstimateWithPrice(float estimateInMs, float price) {
    this.estimateInMs = estimateInMs;
    this.price = price;
  }


  public EstimateWithPrice(EstimatedTimeDto e) {
    if (e.getUserCategory().getHourlyPrice() > 0) {
      this.price = (float) ((e.getDurationInMs() / (1000 * 60 * 60f)) * e.getUserCategory().getHourlyPrice());
      this.estimateInMs = e.getDurationInMs();
    }
    this.userCategory = e.getUserCategory();
  }

  public float getEstimateInMs() {
    return estimateInMs;
  }

  public void setEstimateInMs(float estimateInMs) {
    this.estimateInMs = estimateInMs;
  }

  public float getPrice() {
    return price;
  }

  public void setPrice(float price) {
    this.price = price;
  }

  public void add(EstimateWithPrice estimateWithPrice) {
    this.price += estimateWithPrice.price;
    this.estimateInMs += estimateWithPrice.estimateInMs;
  }


  public UserCategoryDto getUserCategory() {
    return userCategory;
  }
}
