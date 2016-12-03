package com.ownspec.center.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Functions;
import com.ownspec.center.model.component.ComponentReference;
import org.apache.commons.lang.Validate;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Assert;
import org.junit.Test;
import org.outerj.daisy.diff.DaisyDiff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.InputSource;

import static com.ownspec.center.model.component.QComponent.component;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.eclipse.jdt.internal.compiler.parser.Parser.name;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by nlabrot on 01/10/16.
 */
public class ComponentServiceTest extends AbstractTest {


  @Test
  @Transactional
  public void testReference() throws Exception {

    ImmutableComponentDto componentDto = ImmutableComponentDto.newComponentDto()
        .title("test")
        .type(ComponentType.COMPONENT)
        .content("test0")
        .build();

    Assert.assertEquals(0, componentReferenceRepository.findAll().size());

    // Create 3 component
    Component component1 = componentService.create(componentDto);
    componentService.updateStatus(component1.getId() , Status.DRAFT);
    Component component2 = componentService.create(componentDto);
    componentService.updateStatus(component2.getId() , Status.DRAFT);
    Component component3 = componentService.create(componentDto);
    componentService.updateStatus(component3.getId() , Status.DRAFT);

    // Update component1 content with a reference to component 2
    component1 = componentService.updateContent(component1.getId(), generateReference(component2.getId(), component2.getCurrentWorkflowInstance().getId()).getBytes());

    // Test the created reference to component 2
    Assert.assertEquals(1, componentReferenceRepository.findAll().size());
    List<ComponentReference> workflowInstances = componentReferenceRepository.findAllBySourceIdAndSourceWorkflowInstanceId(component1.getId(), component1.getCurrentWorkflowInstance().getId());
    Assert.assertTrue(component1 == workflowInstances.get(0).getSource());
    Assert.assertTrue(component1.getCurrentWorkflowInstance() == workflowInstances.get(0).getSourceWorkflowInstance());
    Assert.assertTrue(component2 == workflowInstances.get(0).getTarget());
    Assert.assertTrue(component2.getCurrentWorkflowInstance() == workflowInstances.get(0).getTargetWorkflowInstance());

    // Update component1 content with a reference to component 3
    component1 = componentService.updateContent(component1.getId(), generateReference(component3.getId(), component3.getCurrentWorkflowInstance().getId()).getBytes());

    // Test the created reference to component 3
    Assert.assertEquals(1, componentReferenceRepository.findAll().size());
    workflowInstances = componentReferenceRepository.findAllBySourceIdAndSourceWorkflowInstanceId(component1.getId(), component1.getCurrentWorkflowInstance().getId());
    Assert.assertTrue(component1 == workflowInstances.get(0).getSource());
    Assert.assertTrue(component1.getCurrentWorkflowInstance() == workflowInstances.get(0).getSourceWorkflowInstance());
    Assert.assertTrue(component3 == workflowInstances.get(0).getTarget());
    Assert.assertTrue(component3.getCurrentWorkflowInstance() == workflowInstances.get(0).getTargetWorkflowInstance());

  }

  @Test
  public void testWorkflow() throws Exception {

    ImmutableComponentDto componentDto = ImmutableComponentDto.newComponentDto()
        .title("test")
        .type(ComponentType.COMPONENT)
        .content("test0")
        .build();

    // Create the component
    Component component = componentService.create(componentDto);

    // save its git reference - create #1
    String startGitReference1 = workflowStatusRepository.findLatestWorkflowStatusByComponentId(component.getId()).getFirstGitReference();
    String endGitReference1 = workflowStatusRepository.findLatestWorkflowStatusByComponentId(component.getId()).getLastGitReference();

    // ********************************************
    // Update the state to DRAFT
    componentService.updateStatus(component.getId() , Status.DRAFT);
    // Git reference is null
    Assert.assertNull(workflowStatusRepository.findLatestWorkflowStatusByComponentId(component.getId()).getLastGitReference());
    Assert.assertNull(workflowStatusRepository.findLatestWorkflowStatusByComponentId(component.getId()).getFirstGitReference());

    // Update content #1
    componentService.updateContent(component.getId(), "test1".getBytes(UTF_8));
    // save git reference of last update content #3
    String startGitReference2 = workflowStatusRepository.findLatestWorkflowStatusByComponentId(component.getId()).getLastGitReference();

    // Update content #2
    component = componentService.updateContent(component.getId(), "test2".getBytes(UTF_8));
    // save git reference of last update content #3
    String endGitReference2 = workflowStatusRepository.findLatestWorkflowStatusByComponentId(component.getId()).getLastGitReference();

    // ********************************************
    // Change status, no content update
    component = componentService.updateStatus(component.getId(), Status.OPEN);
    // Git reference is null
    Assert.assertNull(workflowStatusRepository.findLatestWorkflowStatusByComponentId(component.getId()).getLastGitReference());
    Assert.assertNull(workflowStatusRepository.findLatestWorkflowStatusByComponentId(component.getId()).getFirstGitReference());

    // *********************************************
    // Change status
    component = componentService.updateStatus(component.getId(), Status.DRAFT);
    // Git reference is null
    Assert.assertNull(workflowStatusRepository.findLatestWorkflowStatusByComponentId(component.getId()).getLastGitReference());
    Assert.assertNull(workflowStatusRepository.findLatestWorkflowStatusByComponentId(component.getId()).getFirstGitReference());

    // Update content #1
    component = componentService.updateContent(component.getId(), "test4".getBytes(UTF_8));
    // save its git reference - update content #1
    String startGitReference3 = workflowStatusRepository.findLatestWorkflowStatusByComponentId(component.getId()).getLastGitReference();

    // Update content #2
    component = componentService.updateContent(component.getId(), "test5".getBytes(UTF_8));
    // save git reference of last update content #2
    String endGitReference3 = workflowStatusRepository.findLatestWorkflowStatusByComponentId(component.getId()).getLastGitReference();




    List<WorkflowStatusDto> workflowStatuses = componentService.getWorkflowStatuses(component.getId()).get(0).getWorkflowStatuses();

    Resource file = gitService.getFile(component.getFilePath(), endGitReference1);


    Assert.assertEquals(1, workflowStatuses.get(0).getChanges().size());
    Assert.assertEquals(startGitReference1, workflowStatuses.get(0).getChanges().get(0).getRevision());
    Assert.assertEquals(endGitReference1, workflowStatuses.get(0).getChanges().get(0).getRevision());


    Assert.assertEquals(2, workflowStatuses.get(1).getChanges().size());
    Assert.assertEquals(startGitReference2, workflowStatuses.get(1).getChanges().get(0).getRevision());
    Assert.assertEquals(endGitReference2, workflowStatuses.get(1).getChanges().get(1).getRevision());

    Assert.assertEquals(0, workflowStatuses.get(2).getChanges().size());

    Assert.assertEquals(2, workflowStatuses.get(3).getChanges().size());
    Assert.assertEquals(startGitReference3, workflowStatuses.get(3).getChanges().get(0).getRevision());
    Assert.assertEquals(endGitReference3, workflowStatuses.get(3).getChanges().get(1).getRevision());
  }


  private String generateReference(Long componentId, Long workflowInstanceId) {
    return "<div class=\"requirements\" data-requirement-id=\"" + componentId + "\" data-workflow-instance-id=\"" + workflowInstanceId + "\">\n" +
        "    <div class=\"requirements-id\">23</div>\n" +
        "\n" +
        "    <div class=\"requirements-content\">\n" +
        "        <p>dddddddddddddd</p>\n" +
        "    </div>\n" +
        "</div>\n";

  }

}
