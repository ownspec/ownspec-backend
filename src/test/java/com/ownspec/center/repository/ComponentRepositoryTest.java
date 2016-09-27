package com.ownspec.center.repository;

import org.junit.Test;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import com.ownspec.center.AbstractTest;
import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentTypes;

/**
 * Created by nlabrot on 26/09/16.
 */
public class ComponentRepositoryTest extends AbstractTest {


    @Test
    @Transactional
    public void name() throws Exception {
        ComponentDto componentDto = ImmutableComponentDto.newComponentDto()
                .content("doo")
                .title("title").build();

        Component component = componentService.create(componentDto);



    }
}
