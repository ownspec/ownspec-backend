package com.ownspec.center.service.component;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.ownspec.center.AbstractTest;
import com.ownspec.center.dto.ChangeDto;
import com.ownspec.center.dto.component.ImmutableComponentVersionDto;
import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowStatus;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by nlabrot on 01/10/16.
 */
public class ComponentServiceTest extends AbstractTest {


  @Test
  @Transactional
  public void testReference() throws Exception {

    ImmutableComponentVersionDto componentDto = ImmutableComponentVersionDto.newComponentVersionDto()
        .title("test")
        .type(ComponentType.COMPONENT)
        .content("test0")
        .version("1")
        .build();

    Assert.assertEquals(0, componentReferenceRepository.findAll().size());

    // Create 3 component
    ComponentVersion component1 = componentService.create(componentDto).getRight();
    ComponentVersion component2 = componentService.create(componentDto).getRight();
    ComponentVersion component3 = componentService.create(componentDto).getRight();
    // Update their statuses to DRAFT to allow content update
    workflowService.updateStatus(component1.getId() , Status.DRAFT , "draft");
    workflowService.updateStatus(component2.getId() , Status.DRAFT , "draft");
    workflowService.updateStatus(component3.getId() , Status.DRAFT , "draft");

    // Update component1 content with a reference to component 2
    component1 = componentVersionService.updateContent(component1.getId(), generateReference(component2.getId(), component2.getWorkflowInstance().getId()).getBytes());

    // Test the created reference to component 2
    Assert.assertEquals(1, componentReferenceRepository.findAll().size());
    List<ComponentReference> workflowInstances = componentReferenceRepository.findAllBySourceId(component1.getId());
    Assert.assertTrue(component1 == workflowInstances.get(0).getSource());
    Assert.assertTrue(component2 == workflowInstances.get(0).getTarget());

    // Update component1 content with a reference to component 3
    component1 = componentVersionService.updateContent(component1.getId(), generateReference(component3.getId(), component3.getWorkflowInstance().getId()).getBytes());

    // Test the created reference to component 3
    Assert.assertEquals(1, componentReferenceRepository.findAll().size());
    workflowInstances = componentReferenceRepository.findAllBySourceId(component1.getId());
    Assert.assertTrue(component1 == workflowInstances.get(0).getSource());
    Assert.assertTrue(component3 == workflowInstances.get(0).getTarget());

  }

  @Test
  public void testWorkflow() throws Exception {

    ImmutableComponentVersionDto componentDto = ImmutableComponentVersionDto.newComponentVersionDto()
        .title("test")
        .type(ComponentType.COMPONENT)
        .content("test0")
        .version("1")
        .build();

    // Create the component
    ComponentVersion componentVersion = componentService.create(componentDto).getRight();

    // save its git reference - create #1
    String startGitReference1 = workflowStatusRepository.findLatestWorkflowStatusByComponentVersionId(componentVersion.getId()).getFirstGitReference();
    String endGitReference1 = workflowStatusRepository.findLatestWorkflowStatusByComponentVersionId(componentVersion.getId()).getLastGitReference();

    // ********************************************
    // Update the state to DRAFT
    workflowService.updateStatus(componentVersion.getId() , Status.DRAFT , "draft");
    // Git reference is null
    Assert.assertNull(workflowStatusRepository.findLatestWorkflowStatusByComponentVersionId(componentVersion.getId()).getLastGitReference());
    Assert.assertNull(workflowStatusRepository.findLatestWorkflowStatusByComponentVersionId(componentVersion.getId()).getFirstGitReference());

    // Update content #1
    componentVersionService.updateContent(componentVersion.getId(), "test1".getBytes(UTF_8));
    // save git reference of last update content #3
    String startGitReference2 = workflowStatusRepository.findLatestWorkflowStatusByComponentVersionId(componentVersion.getId()).getLastGitReference();

    // Update content #2
    componentVersion = componentVersionService.updateContent(componentVersion.getId(), "test2".getBytes(UTF_8));
    // save git reference of last update content #3
    String endGitReference2 = workflowStatusRepository.findLatestWorkflowStatusByComponentVersionId(componentVersion.getId()).getLastGitReference();

    // ********************************************
    // Change status, no content update
    workflowService.updateStatus(componentVersion.getId(), Status.OPEN , "open");
    // Git reference is null
    Assert.assertNull(workflowStatusRepository.findLatestWorkflowStatusByComponentVersionId(componentVersion.getId()).getLastGitReference());
    Assert.assertNull(workflowStatusRepository.findLatestWorkflowStatusByComponentVersionId(componentVersion.getId()).getFirstGitReference());

    // *********************************************
    // Change status
    workflowService.updateStatus(componentVersion.getId(), Status.DRAFT , "draft");
    // Git reference is null
    Assert.assertNull(workflowStatusRepository.findLatestWorkflowStatusByComponentVersionId(componentVersion.getId()).getLastGitReference());
    Assert.assertNull(workflowStatusRepository.findLatestWorkflowStatusByComponentVersionId(componentVersion.getId()).getFirstGitReference());

    // Update content #1
    componentVersion = componentVersionService.updateContent(componentVersion.getId(), "test4".getBytes(UTF_8));
    // save its git reference - update content #1
    String startGitReference3 = workflowStatusRepository.findLatestWorkflowStatusByComponentVersionId(componentVersion.getId()).getLastGitReference();

    // Update content #2
    componentVersion = componentVersionService.updateContent(componentVersion.getId(), "test5".getBytes(UTF_8));
    // save git reference of last update content #2
    String endGitReference3 = workflowStatusRepository.findLatestWorkflowStatusByComponentVersionId(componentVersion.getId()).getLastGitReference();





    List<Pair<WorkflowStatus, List<ChangeDto>>> workflowStatuses = workflowConfiguration.changesExtractor(componentVersion).getChanges();
    //List<WorkflowStatusDto> workflowStatuses = workflowConfiguration.changesExtractor(componentVersion).getChanges();

    Resource file = gitService.getFile(componentVersion.getComponent().getVcsId(), componentVersion.getFilename(), endGitReference1);


    Assert.assertEquals(1, workflowStatuses.get(0).getRight().size());
    Assert.assertEquals(startGitReference1, workflowStatuses.get(0).getRight().get(0).getRevision());
    Assert.assertEquals(endGitReference1, workflowStatuses.get(0).getRight().get(0).getRevision());


    Assert.assertEquals(2, workflowStatuses.get(1).getRight().size());
    Assert.assertEquals(startGitReference2, workflowStatuses.get(1).getRight().get(0).getRevision());
    Assert.assertEquals(endGitReference2, workflowStatuses.get(1).getRight().get(1).getRevision());

    Assert.assertEquals(0, workflowStatuses.get(2).getRight().size());

    Assert.assertEquals(2, workflowStatuses.get(3).getRight().size());
    Assert.assertEquals(startGitReference3, workflowStatuses.get(3).getRight().get(0).getRevision());
    Assert.assertEquals(endGitReference3, workflowStatuses.get(3).getRight().get(1).getRevision());
  }


  private String generateReference(Long componentId, Long workflowInstanceId) {
    return "<div class=\"requirements\" data-os-cv-id=\"" + componentId + "\">\n" +
        "    <div class=\"requirements-id\">"+componentId+"</div>\n" +
        "\n" +
        "    <div class=\"requirements-content\">\n" +
        "        <p>dddddddddddddd</p>\n" +
        "    </div>\n" +
        "</div>\n";

  }

}
