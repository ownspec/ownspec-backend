package com.ownspec.center.repository;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Iterables;
import com.ownspec.center.AbstractTest;

/**
 * Created by nlabrot on 02/10/16.
 */
public class WorkflowStatusRepositoryTest extends AbstractTest {


  @Test
  @Transactional
  public void testFindLast() throws Exception {
    Assert.assertEquals(Iterables.getLast(workflowStatusRepository.findAllByComponentId(1L, new Sort("id"))).getId(),
        workflowStatusRepository.findLatestStatusByComponentId(1L).getId());
  }
}
