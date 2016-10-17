package com.ownspec.center;

/**
 * Created by nlabrot on 26/09/16.
 */

import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.ComponentService;
import com.ownspec.center.service.GitService;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OsCenterApplication.class)
@AutoConfigureMockMvc
public abstract class AbstractTest {

  @Value("${composition.outputDirectory}")
  private String outputDirectory;

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ComponentRepository componentRepository;

  @Autowired
  protected ComponentService componentService;

  @Autowired
  protected WorkflowStatusRepository workflowStatusRepository;

  @Autowired
  protected GitService gitService;

  @PersistenceContext
  protected EntityManager entityManager;

  @Before
  public void init() throws IOException {
    FileUtils.forceMkdir(new File(outputDirectory));
  }

}
