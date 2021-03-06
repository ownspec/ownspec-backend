package com.ownspec.center;

/**
 * Created by nlabrot on 26/09/16.
 */

import com.ownspec.center.repository.component.ComponentReferenceRepository;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.repository.component.ComponentVersionRepository;
import com.ownspec.center.repository.tag.TagRepository;
import com.ownspec.center.repository.user.UserRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.GitService;
import com.ownspec.center.service.UserService;
import com.ownspec.center.service.component.ComponentConverter;
import com.ownspec.center.service.component.ComponentService;
import com.ownspec.center.service.component.ComponentVersionService;
import com.ownspec.center.service.workflow.WorkflowConfiguration;
import com.ownspec.center.service.workflow.WorkflowService;
import com.ownspec.emailstore.PersistentMailStore;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OsCenterApplication.class, TestConfiguration.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
/*
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    "classpath:/sql/truncate.sql", "classpath:/sql/load.sql"})
*/
public abstract class AbstractTest {

  @Value("${composition.outputDirectory}")
  private String outputDirectory;

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ComponentRepository componentRepository;


  @Autowired
  protected ComponentVersionRepository componentVersionRepository;

  @Autowired
  protected ComponentService componentService;

  @Autowired
  protected ComponentVersionService componentVersionService;


  @Autowired
  protected WorkflowStatusRepository workflowStatusRepository;

  @Autowired
  protected ComponentReferenceRepository componentReferenceRepository;

  @Autowired
  protected GitService gitService;

  @PersistenceContext
  protected EntityManager entityManager;

  @Autowired
  protected UserRepository userRepository;

  @Autowired
  protected TagRepository tagRepository;

  @Autowired
  protected WorkflowConfiguration workflowConfiguration;

  @Autowired
  protected WorkflowService workflowService;

  @Autowired
  protected PersistentMailStore persistentMailStore;

  @Autowired
  protected ComponentConverter componentConverter;

  @Autowired
  protected UserService userService;


  @Before
  public void init() throws IOException {
    FileUtils.forceMkdir(new File(outputDirectory));
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(userRepository.findOne(0l), ""));
  }


}
