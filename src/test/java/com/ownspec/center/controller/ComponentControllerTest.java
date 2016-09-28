package com.ownspec.center.controller;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ownspec.center.AbstractTest;

/**
 * Created by nlabrot on 26/09/16.
 */
public class ComponentControllerTest extends AbstractTest {

    @Test
    public void testFindAll() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/components"))
                .andExpect(MockMvcResultMatchers.content().json(
                        "[ {\n" +
                                "  \"id\" : 1,\n" +
                                "  \"title\" : \"My first dummy component\",\n" +
                                "  \"type\" : \"COMPONENT\",\n" +
                                "  \"createdDate\" : \"2016-01-01T11:10:10Z\",\n" +
                                "  \"createdUser\" : {\n" +
                                "    \"username\" : \"admin\",\n" +
                                "    \"firstName\" : \"admnistrator\",\n" +
                                "    \"lastName\" : \"admnistrator\"\n" +
                                "  },\n" +
                                "  \"lastModifiedDate\" : null,\n" +
                                "  \"lastModifiedUser\" : null,\n" +
                                "  \"content\" : \"doo\",\n" +
                                "  \"currentStatus\" : null\n" +
                                "}, {\n" +
                                "  \"id\" : 2,\n" +
                                "  \"title\" : \"Second dummy component\",\n" +
                                "  \"type\" : \"COMPONENT\",\n" +
                                "  \"createdDate\" : \"2016-01-01T11:10:10Z\",\n" +
                                "  \"createdUser\" : {\n" +
                                "    \"username\" : \"admin\",\n" +
                                "    \"firstName\" : \"admnistrator\",\n" +
                                "    \"lastName\" : \"admnistrator\"\n" +
                                "  },\n" +
                                "  \"lastModifiedDate\" : null,\n" +
                                "  \"lastModifiedUser\" : null,\n" +
                                "  \"content\" : \"\",\n" +
                                "  \"currentStatus\" : null\n" +
                                "}, {\n" +
                                "  \"id\" : 3,\n" +
                                "  \"title\" : \"Third dummy component\",\n" +
                                "  \"type\" : \"COMPONENT\",\n" +
                                "  \"createdDate\" : \"2016-01-01T11:10:10Z\",\n" +
                                "  \"createdUser\" : {\n" +
                                "    \"username\" : \"admin\",\n" +
                                "    \"firstName\" : \"admnistrator\",\n" +
                                "    \"lastName\" : \"admnistrator\"\n" +
                                "  },\n" +
                                "  \"lastModifiedDate\" : null,\n" +
                                "  \"lastModifiedUser\" : null,\n" +
                                "  \"content\" : \"\",\n" +
                                "  \"currentStatus\" : null\n" +
                                "}, {\n" +
                                "  \"id\" : 4,\n" +
                                "  \"title\" : \"Ownspec COMPONENT\",\n" +
                                "  \"type\" : \"COMPONENT\",\n" +
                                "  \"createdDate\" : \"2016-01-01T11:10:10Z\",\n" +
                                "  \"createdUser\" : {\n" +
                                "    \"username\" : \"admin\",\n" +
                                "    \"firstName\" : \"admnistrator\",\n" +
                                "    \"lastName\" : \"admnistrator\"\n" +
                                "  },\n" +
                                "  \"lastModifiedDate\" : null,\n" +
                                "  \"lastModifiedUser\" : null,\n" +
                                "  \"content\" : \"\",\n" +
                                "  \"currentStatus\" : null\n" +
                                "} ]"
                ))
                .andReturn();

    }
}
