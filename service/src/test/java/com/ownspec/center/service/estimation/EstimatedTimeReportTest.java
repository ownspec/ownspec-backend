package com.ownspec.center.service.estimation;

import static com.ownspec.center.dto.component.ImmutableComponentReferenceDto.newComponentReferenceDto;
import static com.ownspec.center.dto.component.ImmutableComponentVersionDto.newComponentVersionDto;

import com.ownspec.center.AbstractTest;
import com.ownspec.center.dto.component.ImmutableComponentVersionDto;
import com.ownspec.center.model.component.ComponentType;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by nlabrot on 11/04/17.
 */
public class EstimatedTimeReportTest extends AbstractTest {

  @Autowired
  private EstimatedTimeReport estimatedTimeReport;


  @Test
  public void name() throws Exception {

    ImmutableComponentVersionDto.Builder builder = newComponentVersionDto()
        .code("CODE-1")
        .title("foo bar")
        .type(ComponentType.COMPONENT);

    builder.addComponentReferences(newComponentReferenceDto()
        .source(builder.build())
        .target(newComponentVersionDto()
            .code("CODE-2")
            .title("foo bar")
            .type(ComponentType.COMPONENT).build())
        .build()
    );


    ByteArrayResource resource = estimatedTimeReport.generateExcelReport(builder.build());

    Files.write(Paths.get("target/foo.xls"), IOUtils.toByteArray(resource.getInputStream()));

    System.out.println(resource);

  }
}
