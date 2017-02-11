package com.ownspec.center.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.jdom2.filter.Filters.text;

import com.ownspec.center.AbstractTest;
import com.ownspec.center.dto.ComponentVersionDto;
import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.dto.ImmutableComponentVersionDto;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.ComponentVersion;
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

    ComponentVersionDto componentDto = ImmutableComponentVersionDto.newComponentVersionDto()
        .title("test")
        .type(ComponentType.COMPONENT)
        .content("test0")
        .version("1")
        .build();



    // Create 4 component
    ComponentVersion component0 = componentService.create(componentDto).getRight();
    ComponentVersion component1 = componentService.create(componentDto).getRight();
    ComponentVersion component2 = componentService.create(componentDto).getRight();
    ComponentVersion component3 = componentService.create(componentDto).getRight();
    // Update their statuses to DRAFT to allow content update
    workflowService.updateStatus(component0.getId(), Status.DRAFT);
    workflowService.updateStatus(component1.getId(), Status.DRAFT);
    workflowService.updateStatus(component2.getId(), Status.DRAFT);
    workflowService.updateStatus(component3.getId(), Status.DRAFT);


    // Update the content of component0
    String content = replaces(IOUtils.toString(new ClassPathResource("/reference/reference.html").getInputStream(), UTF_8),
        "$COMPID1$", component1.getId().toString(),
        "$COMPID2$", component2.getId().toString(),
        "$COMPID3$", component3.getId().toString());

    componentService.updateContent(component0, new ByteArrayResource(content.getBytes(UTF_8)));

    // Check that references have been created between the 4 components
    List<ComponentReference> component0References = componentReferenceRepository.findAllBySourceId(component0.getId());
    List<ComponentReference> component1References = componentReferenceRepository.findAllBySourceId(component1.getId());
    List<ComponentReference> component2References = componentReferenceRepository.findAllBySourceId(component2.getId());

    component0 = componentVersionRepository.findOne(component0.getId());
    component1 = componentVersionRepository.findOne(component1.getId());
    component2 = componentVersionRepository.findOne(component2.getId());
    component3 = componentVersionRepository.findOne(component3.getId());


    Assert.assertEquals("<p>test1</p> \n" +
        "<p>ddddddddddddddddddd</p> \n" +
        "<div class=\"requirements\" data-os-cv-id=\"" + component1.getId() + "\" " +
        "data-reference-id=\"" + component0References.get(0).getId() + "\"></div> \n" +
        "<p>ddddddddddddddddddd</p>", OsUtils.toString(componentService.getHeadRawContent(component0)));

    Assert.assertEquals("<p>ddddddddddddddddddd</p> \n" +
        "<p>ddddddddddddddddddd</p> \n" +
        "<div class=\"requirements\" data-os-cv-id=\"" + component2.getId() + "\" " +
        "data-reference-id=\"" + component1References.get(0).getId() + "\"></div> \n" +
        "<p>ddddddddddddddddddd</p> \n" +
        "<p>ddddddddddddddddddd</p>", OsUtils.toString(componentService.getHeadRawContent(component1)));

    Assert.assertEquals("<p>ddddddddddddddddddd</p> \n" +
        "<p>ddddddddddddddddddd</p> \n" +
        "<div class=\"requirements\" data-os-cv-id=\"" + component3.getId() + "\" " +
        "data-reference-id=\"" + component2References.get(0).getId() + "\"></div> \n" +
        "<p>ddddddddddddddddddd</p> \n" +
        "<p>ddddddddddddddddddd</p>", OsUtils.toString(componentService.getHeadRawContent(component2)));
    Assert.assertEquals("<p>test1</p>", OsUtils.toString(componentService.getHeadRawContent(component3)));


    // Update component 3 status
    workflowService.updateStatus(component3.getId(), Status.IN_VALIDATION);



    // Update component 2 content
    content = IOUtils.toString(new ClassPathResource("/reference/reference_updated.html").getInputStream(), UTF_8)
        .replace("$COMPID1$", component1.getId().toString())
        .replace("$COMPID2$", component2.getId().toString())
        .replace("$COMPID3$", component3.getId().toString());

    componentService.updateContent(component2, content.getBytes(UTF_8));


    component0 = componentVersionRepository.findOne(component0.getId());
    component1 = componentVersionRepository.findOne(component1.getId());
    component2 = componentVersionRepository.findOne(component2.getId());
    component3 = componentVersionRepository.findOne(component3.getId());

    component0References = componentReferenceRepository.findAllBySourceId(component0.getId());
    component1References = componentReferenceRepository.findAllBySourceId(component1.getId());
    component2References = componentReferenceRepository.findAllBySourceId(component2.getId());

    // Check component 2 content is updated
    Assert.assertEquals("<p>modified</p> \n" +
        "<div class=\"requirements\" data-os-cv-id=\"" + component3.getId() + "\" " +
        "data-reference-id=\"" + component2References.get(0).getId() + "\"></div> \n" +
        "<p>modified</p>", OsUtils.toString(componentService.getHeadRawContent(component2)));

    // But not component 3 as it is IN_VALIDATION
    Assert.assertEquals("<p>test1</p>", OsUtils.toString(componentService.getHeadRawContent(component3)));


    // Check component 0 content
    String expected = IOUtils.toString(new ClassPathResource("/reference/expected_content.html").getInputStream(), UTF_8);

    expected = replaces(expected,
        "$COMPID1$", component1.getId().toString(),
        "$COMPID2$", component2.getId().toString(),
        "$COMPID3$", component3.getId().toString(),
        "$COMPREFID1$", component0References.get(0).getId().toString(),
        "$COMPREFID2$", component1References.get(0).getId().toString(),
        "$COMPREFID3$", component2References.get(0).getId().toString()
    );

    Pair<String, String> generateContent = componentService.generateContent(component0);

    Assert.assertEquals(Jsoup.parse(expected).body().html(), Jsoup.parse(generateContent.getLeft()).body().html());
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
