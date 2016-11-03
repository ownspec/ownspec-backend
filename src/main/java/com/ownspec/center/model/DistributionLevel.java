package com.ownspec.center.model;

/**
 * com.ownspec.center.model
 * <p>
 * DistributionLevel
 *
 * @author lyrold
 * @since 2016-11-01
 */
public enum DistributionLevel {
  PUBLIC(1, "Information flows freely, without any protection"),
  INTERNAL(2, "Default Level; Information flows freely but only within the organization"),
  RESTRICTED(3, "Information are disclosed only to persons who are concerned and mentioned"),
  SECRET(4, "Required secret empowerment; the disclosure of information could harm the strategic interests, safety or the existence of the organization.");


  private int level;
  private String description;

  DistributionLevel(int level, String description) {
    this.level = level;
    this.description = description;
  }

  public int getLevel() {
    return level;
  }

  public String getDescription() {
    return description;
  }
}
