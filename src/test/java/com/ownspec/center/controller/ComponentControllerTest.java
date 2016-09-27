package com.ownspec.center.controller;

import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.ownspec.center.AbstractTest;

/**
 * Created by nlabrot on 26/09/16.
 */
public class ComponentControllerTest extends AbstractTest {

    @Test
    public void name() throws Exception {


       mockMvc.perform(MockMvcRequestBuilders.get("/api/components")).andReturn();


        System.out.println("ok");

    }
}
