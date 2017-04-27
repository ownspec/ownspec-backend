package com.ownspec;

import static com.ownspec.center.dto.ImmutableEstimatedTimeDto.newEstimatedTimeDto;
import static com.ownspec.center.dto.component.ImmutableComponentReferenceDto.newComponentReferenceDto;
import static com.ownspec.center.dto.component.ImmutableComponentVersionDto.newComponentVersionDto;
import static com.ownspec.center.dto.user.ImmutableUserCategoryDto.newUserCategoryDto;

import com.ownspec.center.dto.component.ImmutableComponentVersionDto;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.service.estimation.EstimatedTimeReport;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * Created by nlabrot on 23/04/17.
 */
public class TestJasper {


  @Test
  public void name() throws Exception {

    EstimatedTimeReport estimatedTimeReport = new EstimatedTimeReport("../reports/");


    ImmutableComponentVersionDto.Builder builder = newComponentVersionDto()
        .code("CODE-1")
        .title("foo bar")
        .type(ComponentType.COMPONENT)
        .addEstimatedTimes(newEstimatedTimeDto()
            .duration("1d").durationInMs(Duration.ofHours(10).toMillis())
            .userCategory(newUserCategoryDto()
                .id(1l)
                .hourlyPrice(10.0)
                .name("doo1")
                .build())
            .build())
        .addEstimatedTimes(newEstimatedTimeDto()
            .duration("1d").durationInMs(Duration.ofHours(10).toMillis())
            .userCategory(newUserCategoryDto()
                .id(2l)
                .hourlyPrice(10.0)
                .name("doo2")
                .build())
            .build());

    builder.addComponentReferences(newComponentReferenceDto()
        .source(builder.build())
        .target(newComponentVersionDto()
            .code("CODE-2")
            .title("foo bar")
            .type(ComponentType.COMPONENT).build())
        .build()
    );


    byte[] byteArray = estimatedTimeReport.generateExcelReport(builder.build()).getByteArray();

    //ByteArrayResource resource = new ByteArrayResource(result.toByteArray());
    Files.write(Paths.get("target/foo.xls"), byteArray);

  }
}
