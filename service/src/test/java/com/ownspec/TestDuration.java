package com.ownspec;

import com.ownspec.center.util.DateUtils;
import com.ownspec.center.util.DurationUtils;
import org.junit.Test;

import java.util.Locale;

/**
 * Created by nlabrot on 06/04/17.
 */
public class TestDuration {

  @Test
  public void name() throws Exception {

    long durationSeconds = DurationUtils.getDurationSeconds("2d", 1, 1, DateUtils.Duration.DAY, Locale.getDefault());

    System.out.println(durationSeconds);

  }
}
