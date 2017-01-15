package com.ownspec.center.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.ownspec.center.AbstractTest;
import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.util.OsUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

/**
 * Created by nlabrot on 23/10/16.
 */
public class ReferenceTest extends AbstractTest {

  @Test
  public void testExtractReference() throws Exception {

    ImmutableComponentDto componentDto = ImmutableComponentDto.newComponentDto()
        .title("test")
        .type(ComponentType.COMPONENT)
        .content("test0")
        .build();

    Assert.assertEquals(0, componentReferenceRepository.findAll().size());

    // Create 3 component
    Component component0 = componentService.create(componentDto);
    Component component1 = componentService.create(componentDto);
    Component component2 = componentService.create(componentDto);
    Component component3 = componentService.create(componentDto);
    componentService.updateStatus(component0.getId(), Status.DRAFT);
    componentService.updateStatus(component1.getId(), Status.DRAFT);
    componentService.updateStatus(component2.getId(), Status.DRAFT);
    componentService.updateStatus(component3.getId(), Status.DRAFT);


    String content = replaces(IOUtils.toString(new ClassPathResource("/reference/reference.html").getInputStream(), UTF_8),
        "$COMPID1$", component1.getId().toString(),
        "$COMPWFID1$", component1.getCurrentWorkflowInstance().getId().toString(),
        "$COMPID2$", component2.getId().toString(),
        "$COMPWFID2$", component2.getCurrentWorkflowInstance().getId().toString(),
        "$COMPID3$", component3.getId().toString(),
        "$COMPWFID3$", component3.getCurrentWorkflowInstance().getId().toString());


    componentService.updateContent(component0, new ByteArrayResource(content.getBytes(UTF_8)));

    List<ComponentReference> component0References = componentReferenceRepository.findAllBySourceId(component0.getId());
    List<ComponentReference> component1References = componentReferenceRepository.findAllBySourceId(component1.getId());
    List<ComponentReference> component2References = componentReferenceRepository.findAllBySourceId(component2.getId());

    Assert.assertEquals("<p>test1</p> \n" +
        "<p>ddddddddddddddddddd</p> \n" +
        "<div class=\"requirements\" data-requirement-id=\"" + component1.getId() + "\" data-workflow-instance-id=\"" + component1.getCurrentWorkflowInstance().getId() + "\" " +
        "data-reference-id=\"" + component0References.get(0).getId() + "\"></div> \n" +
        "<p>ddddddddddddddddddd</p>", OsUtils.toString(componentService.getHeadRawContent(component0)));

    Assert.assertEquals("<p>ddddddddddddddddddd</p> \n" +
        "<p>ddddddddddddddddddd</p> \n" +
        "<div class=\"requirements\" data-requirement-id=\"" + component2.getId() + "\" data-workflow-instance-id=\"" + component2.getCurrentWorkflowInstance().getId() + "\" " +
        "data-reference-id=\"" + component1References.get(0).getId() + "\"></div> \n" +
        "<p>ddddddddddddddddddd</p> \n" +
        "<p>ddddddddddddddddddd</p>", OsUtils.toString(componentService.getHeadRawContent(component1)));

    Assert.assertEquals("<p>ddddddddddddddddddd</p> \n" +
        "<p>ddddddddddddddddddd</p> \n" +
        "<div class=\"requirements\" data-requirement-id=\"" + component3.getId() + "\" data-workflow-instance-id=\"" + component3.getCurrentWorkflowInstance().getId() + "\" " +
        "data-reference-id=\"" + component2References.get(0).getId() + "\"></div> \n" +
        "<p>ddddddddddddddddddd</p> \n" +
        "<p>ddddddddddddddddddd</p>", OsUtils.toString(componentService.getHeadRawContent(component2)));
    Assert.assertEquals("<p>test1</p>", OsUtils.toString(componentService.getHeadRawContent(component3)));


    // Update component 3 status
    componentService.updateStatus(component3.getId(), Status.IN_VALIDATION);

    // Update component 2 content
    content = IOUtils.toString(new ClassPathResource("/reference/reference_updated.html").getInputStream(), UTF_8)
        .replace("$COMPID1$", component1.getId().toString())
        .replace("$COMPWFID1$", component1.getCurrentWorkflowInstance().getId().toString())
        .replace("$COMPID2$", component2.getId().toString())
        .replace("$COMPWFID2$", component2.getCurrentWorkflowInstance().getId().toString())
        .replace("$COMPID3$", component3.getId().toString())
        .replace("$COMPWFID3$", component3.getCurrentWorkflowInstance().getId().toString());

    componentService.updateContent(component2, content.getBytes(UTF_8));

    component2References = componentReferenceRepository.findAllBySourceId(component2.getId());

    // Check component 2 content is updated
    Assert.assertEquals("<p>modified</p> \n" +
        "<div class=\"requirements\" data-requirement-id=\"" + component3.getId() + "\" data-workflow-instance-id=\"" + component3.getCurrentWorkflowInstance().getId() + "\" " +
        "data-reference-id=\"" + component2References.get(0).getId() + "\"></div> \n" +
        "<p>modified</p>", OsUtils.toString(componentService.getHeadRawContent(component2)));

    // But not component 3 as it is IN_VALIDATION
    Assert.assertEquals("<p>test1</p>", OsUtils.toString(componentService.getHeadRawContent(component3)));


    String expected = IOUtils.toString(new ClassPathResource("/reference/expected_content.html").getInputStream(), UTF_8);

    Pair<String, String> generateContent = componentService.generateContent(component0);

    Assert.assertEquals(Jsoup.parse(expected).body().html(), Jsoup.parse(generateContent.getLeft()).body().html());
    Assert.assertEquals("test1 ddddddddddddddddddd 4 ddddddddddddddddddd ddddddddddddddddddd 7 modified 10......", generateContent.getRight());


    componentService.composePdf(component0.getId());
  }


  private String replaces(String content, String... values) {

    for (int i = 0; i < values.length; i += 2) {
      content = content.replace(values[i], values[i + 1]);
    }
    return content;
  }

  private String replacesSeq(String content, String... values) {

    for (int i = 0; i < values.length; i++) {
      content = content.replace("$" + i + "$", values[i + 1]);
    }
    return content;
  }

}
