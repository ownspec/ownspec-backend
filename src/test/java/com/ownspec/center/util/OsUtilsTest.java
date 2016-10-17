package com.ownspec.center.util;

import com.ownspec.center.model.component.Component;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;

/**
 * Created by lyrold on 01/10/2016.
 */
public class OsUtilsTest {
  @Test
  public void mergeWithNotNullProperties() throws Exception {
    Component source = new Component();
    source.setId(108L);
    source.setTitle("Source Title");
    source.setCreatedDate(Instant.MAX);

    Component target = new Component();
    target.setId(1L);
    target.setTitle("Target Title");
    target.setCreatedDate(Instant.MIN);

    OsUtils.mergeWithNotNullProperties(source, target);

    Assert.assertEquals(Long.valueOf(1), target.getId());
    Assert.assertEquals("Source Title", target.getTitle());
    Assert.assertEquals(Instant.MIN, target.getCreatedDate());
  }


}