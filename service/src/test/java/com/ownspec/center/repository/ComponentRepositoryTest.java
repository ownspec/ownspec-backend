package com.ownspec.center.repository;

import com.ownspec.center.dto.ComponentVersionDto;
import com.ownspec.center.dto.ImmutableComponentVersionDto;
import com.ownspec.center.model.component.ComponentVersion;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.ownspec.center.AbstractTest;
import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.model.component.Component;

/**
 * Created by nlabrot on 26/09/16.
 */
public class ComponentRepositoryTest extends AbstractTest {


  @Test
  @Transactional
  public void name() throws Exception {
    ComponentVersionDto componentDto = ImmutableComponentVersionDto.newComponentVersionDto()
        .content("doo")
        .title("title").build();

    ComponentVersion component = componentService.create(componentDto).getRight();


  }

  @Test
  public void name1() throws Exception {
    componentVersionRepository.findAllByComponentTagTagLabel("dd");
    //componentRepository.findAll2();

  }
}
