package com.ownspec.center.service.estimation;

/**
 * Created by nlabrot on 11/04/17.
 */
public class EstimatedTimeReportHelper {

  public static float computeDurationInDays(float durationInMs) {
    return roundValue(durationInMs / (1000 * 60 * 60 * 8));
  }

  public static float roundValue(double v) {
    return Math.round(1000 * v) / 1000;
  }

}
