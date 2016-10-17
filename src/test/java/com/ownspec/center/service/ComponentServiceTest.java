package com.ownspec.center.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.ownspec.center.AbstractTest;
import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.dto.WorkflowStatusDto;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowStatus;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.eclipse.jdt.internal.compiler.parser.Parser.name;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

/**
 * Created by nlabrot on 01/10/16.
 */
public class ComponentServiceTest extends AbstractTest {

  @Test
  public void testWorkflow() throws Exception {

    ImmutableComponentDto componentDto = ImmutableComponentDto.newComponentDto()
        .title("test")
        .type(ComponentType.COMPONENT)
        .content("test1")
        .build();

    // Create the component
    Component component = componentService.create(componentDto);

    // save its git reference - update content #1
    String startGitReference1 = component.getCurrentGitReference();
    // Update content #2
    componentService.updateContent(component.getId(), "test2".getBytes(UTF_8));
    // Update content #3
    component = componentService.updateContent(component.getId(), "test3".getBytes(UTF_8));
    // save git reference of last update content #3
    String endGitReference1 = component.getCurrentGitReference();

    // Change status, no content update
    component = componentService.updateStatus(component.getId(), Status.OPEN);
    Assert.assertNull(component.getCurrentGitReference());


    // Change status
    component = componentService.updateStatus(component.getId(), Status.OPEN);
    // Update content #1
    component = componentService.updateContent(component.getId(), "test4".getBytes(UTF_8));
    // save its git reference - update content #1
    String startGitReference2 = component.getCurrentGitReference();

    // Update content #2
    component = componentService.updateContent(component.getId(), "test5".getBytes(UTF_8));
    // save git reference of last update content #2
    String endGitReference2 = component.getCurrentGitReference();

    List<WorkflowStatusDto> workflowStatuses = componentService.getWorkflowStatuses(component.getId());

    System.out.println(workflowStatuses);

    Assert.assertEquals(3, workflowStatuses.get(0).getChanges().size());
    Assert.assertEquals(startGitReference1, workflowStatuses.get(0).getChanges().get(0).getRevision());
    Assert.assertEquals(endGitReference1, workflowStatuses.get(0).getChanges().get(2).getRevision());

    Assert.assertEquals(0, workflowStatuses.get(1).getChanges().size());

    Assert.assertEquals(2, workflowStatuses.get(2).getChanges().size());
    Assert.assertEquals(startGitReference2, workflowStatuses.get(2).getChanges().get(0).getRevision());
    Assert.assertEquals(endGitReference2, workflowStatuses.get(2).getChanges().get(1).getRevision());


  }
}
