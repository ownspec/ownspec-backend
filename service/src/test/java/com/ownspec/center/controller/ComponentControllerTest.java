package com.ownspec.center.controller;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ownspec.center.AbstractTest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * Created by nlabrot on 26/09/16.
 */
public class ComponentControllerTest extends AbstractTest {

  @Value("classpath:/controller/findall.json")
  private Resource resourceFindAll;

  @Value("classpath:/controller/findall_by_types.json")
  private Resource resourceFindAllByType;


  @Test
  public void testFindAll() throws Exception {

    try (InputStream is = resourceFindAll.getInputStream()) {
      MvcResult mvcResult = mockMvc.perform(get("/api/components"))
          .andExpect(content().json(IOUtils.toString(is, UTF_8)))
          .andReturn();
    }
  }


  @Test
  public void testFindAllByType() throws Exception {

    try (InputStream is = resourceFindAllByType.getInputStream()) {
      MvcResult mvcResult = mockMvc.perform(get("/api/components?types=REQUIREMENT"))
          .andExpect(content().json(IOUtils.toString(is, UTF_8)))
          .andReturn();
    }
  }
}
