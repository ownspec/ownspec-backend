package com.ownspec.center.service.content;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.component.ComponentReferenceRepository;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.repository.workflow.WorkflowInstanceRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.AuthenticationService;
import com.ownspec.center.service.GitService;
import com.ownspec.center.service.component.ComponentService;
import org.apache.commons.io.IOUtils;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nlabrot on 12/11/16.
 */
public class HtmlContentSaverTest {

  @InjectMocks
  private HtmlContentSaver htmlContentSaver = new HtmlContentSaver();

  @Mock
  private WorkflowStatusRepository workflowStatusRepository;

  @Mock
  private ComponentRepository componentRepository;

  @Mock
  private ComponentReferenceRepository componentReferenceRepository;

  @Mock
  private GitService gitService;

  @Mock
  private WorkflowInstanceRepository workflowInstanceRepository;

  @Mock
  private ComponentService componentService;

  @Mock
  private AuthenticationService authenticationService;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    User user = new User();
    user.setUsername("foo");
    user.setEmail("foo");
    when(authenticationService.getAuthenticatedUser()).thenReturn(user);
  }

  @Test
  public void testSave() throws Exception {

    Tuple3<Component, WorkflowInstance, WorkflowStatus> component = createComponent(0L, 0L, 0L, "filePath");


    initMock(component.v1, component.v2, component.v3, "foo", true);

    htmlContentSaver.save(component.v1, readFileToString(new File("src/test/resources/reference/no/no_reference.html"), UTF_8));

    // update and commit must be called
    verify(gitService).updateAndCommit(argThat(r -> htmlEquals(new ClassPathResource("reference/no/no_reference_expected.html"), r)), eq("filePath"), any(), any());

    verify(workflowStatusRepository).save(argThat((WorkflowStatus w) -> "foo".equals(w.getLastGitReference())));

    // No saved references
    verify(componentReferenceRepository, never()).save(any(ComponentReference.class));
  }


  @Test
  public void testWithReference() throws Exception {

    Tuple3<Component, WorkflowInstance, WorkflowStatus> component1 = createComponent(1L, 1L, 1L, "filePath1");
    Tuple3<Component, WorkflowInstance, WorkflowStatus> component2 = createComponent(2L, 2L, 2L, "filePath2");

    initMock(component1.v1, component1.v2, component1.v3, "hash1", true);
    initMock(component2.v1, component2.v2, component2.v3, "hash2", true);

    htmlContentSaver.save(component1.v1, readFileToString(new File("src/test/resources/reference/one/one_reference.html"), UTF_8));

    // Component 1: commit
    verify(gitService).updateAndCommit(argThat(r -> htmlEquals(new ClassPathResource("reference/one/one_reference_expected_1.html"), r)), eq("filePath1"), any(), any());
    // Component 1: update reference
    verify(workflowStatusRepository).save(argThat((WorkflowStatus w) -> w.getId().equals(1L) && "hash1".equals(w.getLastGitReference())));

    // One saved references
    verify(componentReferenceRepository).save(argThat((ComponentReference r) ->
        r.getSource().getId().equals(component1.v1.getId()) &&
            r.getSourceWorkflowInstance().getId().equals(component1.v2.getId()) &&
            r.getTarget().getId().equals(component2.v1.getId()) &&
            r.getTargetWorkflowInstance().getId().equals(component2.v2.getId())
    ));

    // Component 2: commit
    verify(gitService).updateAndCommit(argThat(r -> htmlEquals(new ClassPathResource("reference/one/one_reference_expected_2.html"), r)), eq("filePath2"), any(), any());
    // Component 2: update reference
    verify(workflowStatusRepository).save(argThat((WorkflowStatus w) -> w.getId().equals(2L) && "hash2".equals(w.getLastGitReference())));
  }

  @Test
  public void testCreateReference() throws Exception {

    Tuple3<Component, WorkflowInstance, WorkflowStatus> component1 = createComponent(1L, 1L, 1L, "filePath1");
    Tuple3<Component, WorkflowInstance, WorkflowStatus> component2 = createComponent(2L, 2L, 2L, "filePath2");

    initMock(component1.v1, component1.v2, component1.v3, "hash1", true);
    initMock(component2.v1, component2.v2, component2.v3, "hash2", false);

    htmlContentSaver.save(component1.v1, readFileToString(new File("src/test/resources/reference/create/create_reference.html"), UTF_8));

    // Component 1: commit
    verify(gitService).updateAndCommit(argThat(r -> htmlEquals(new ClassPathResource("reference/create/create_reference_expected_1.html"), r)), eq("filePath1"), any(), any());
    // Component 1: update workflow
    verify(workflowStatusRepository).save(argThat((WorkflowStatus w) -> w.getId().equals(1L) && "hash1".equals(w.getLastGitReference())));

    // Component 1: One saved references
    verify(componentReferenceRepository).save(argThat((ComponentReference r) ->
        r.getSource().getId().equals(component1.v1.getId()) &&
            r.getSourceWorkflowInstance().getId().equals(component1.v2.getId()) &&
            r.getTarget().getId().equals(component2.v1.getId()) &&
            r.getTargetWorkflowInstance().getId().equals(component2.v2.getId())
    ));

    // Component 2: create
    verify(componentService).create(any());
    // Component 2: commit
    verify(gitService).updateAndCommit(argThat(r -> htmlEquals(new ClassPathResource("reference/create/create_reference_expected_2.html"), r)), eq("filePath2"), any(), any());
    // Component 2: update workflow
    verify(workflowStatusRepository).save(argThat((WorkflowStatus w) -> w.getId().equals(2L) && "hash2".equals(w.getLastGitReference())));
  }


  @Test
  public void testNestedCreateReference() throws Exception {

    Tuple3<Component, WorkflowInstance, WorkflowStatus> component1 = createComponent(1L, 1L, 1L, "filePath1");
    Tuple3<Component, WorkflowInstance, WorkflowStatus> component2 = createComponent(2L, 2L, 2L, "filePath2");
    Tuple3<Component, WorkflowInstance, WorkflowStatus> component3 = createComponent(3L, 3L, 3L, "filePath3");

    initMock(component1.v1, component1.v2, component1.v3, "hash1", true);
    initMock(component2.v1, component2.v2, component2.v3, "hash2", false);
    initMock(component3.v1, component3.v2, component3.v3, "hash3", true);

    htmlContentSaver.save(component1.v1, readFileToString(new File("src/test/resources/reference/create-nested/create_reference.html"), UTF_8));

    // Component 1: commit
    verify(gitService).updateAndCommit(argThat(r -> htmlEquals(new ClassPathResource("reference/create-nested/create_reference_expected_1.html"), r)), eq("filePath1"), any(), any());
    // Component 1: update workflow
    verify(workflowStatusRepository).save(argThat((WorkflowStatus w) -> w.getId().equals(1L) && "hash1".equals(w.getLastGitReference())));
    // Component 1: delete reference
    verify(componentReferenceRepository).deleteBySourceIdAndSourceWorkflowInstanceId(component1.v1.getId(), component1.v2.getId());

    // Component 1: One saved references 1 => 2
    verify(componentReferenceRepository).save(argThat((ComponentReference r) ->
        r.getSource().getId().equals(component1.v1.getId()) &&
            r.getSourceWorkflowInstance().getId().equals(component1.v2.getId()) &&
            r.getTarget().getId().equals(component2.v1.getId()) &&
            r.getTargetWorkflowInstance().getId().equals(component2.v2.getId())
    ));

    // Component 2: create
    verify(componentService).create(any());
    // Component 2: commit
    verify(gitService).updateAndCommit(argThat(r -> htmlEquals(new ClassPathResource("reference/create-nested/create_reference_expected_2.html"), r)), eq("filePath2"), any(), any());
    // Component 2: update workflow
    verify(workflowStatusRepository).save(argThat((WorkflowStatus w) -> w.getId().equals(2L) && "hash2".equals(w.getLastGitReference())));

    // Component 2: One saved references 2 => 3
    verify(componentReferenceRepository).save(argThat((ComponentReference r) ->
        r.getSource().getId().equals(component2.v1.getId()) &&
            r.getSourceWorkflowInstance().getId().equals(component2.v2.getId()) &&
            r.getTarget().getId().equals(component3.v1.getId()) &&
            r.getTargetWorkflowInstance().getId().equals(component3.v2.getId())
    ));

    // Component 3: commit
    verify(gitService).updateAndCommit(argThat(r -> htmlEquals(new ClassPathResource("reference/create-nested/create_reference_expected_3.html"), r)), eq("filePath3"), any(), any());
    // Component 3: update workflow
    verify(workflowStatusRepository).save(argThat((WorkflowStatus w) -> w.getId().equals(3L) && "hash3".equals(w.getLastGitReference())));
    // Component 3: delete reference
    verify(componentReferenceRepository).deleteBySourceIdAndSourceWorkflowInstanceId(component3.v1.getId(), component3.v2.getId());

    Mockito.verifyNoMoreInteractions(componentReferenceRepository, gitService);
  }


  @Test(expected = ComponentCycleException.class)
  public void testCycle() throws Exception {
    Tuple3<Component, WorkflowInstance, WorkflowStatus> component1 = createComponent(1L, 1L, 1L, "filePath1");
    htmlContentSaver.save(component1.v1, readFileToString(new File("src/test/resources/reference/cycle/create_reference.html"), UTF_8));
  }


  private String toString(Resource v) {
    try (InputStream inputStream = v.getInputStream()) {
      return IOUtils.toString(inputStream, UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  private String toString(String f) {
    try (InputStream inputStream = new ClassPathResource(f).getInputStream()) {
      return IOUtils.toString(inputStream, UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  protected Tuple3<Component, WorkflowInstance, WorkflowStatus> createComponent(Long id, Long workflowInstanceId, Long wsTatusId, String filePath) {

    Component component = new Component();
    component.setId(id);
    component.setTitle("TBD");
    component.setType(ComponentType.COMPONENT);

    WorkflowInstance workflowInstance = new WorkflowInstance();
    workflowInstance.setId(workflowInstanceId);
    workflowInstance.setComponent(component);

    WorkflowStatus workflowStatus = new WorkflowStatus();
    workflowStatus.setId(wsTatusId);
    workflowStatus.setStatus(Status.DRAFT);
    workflowStatus.setFirstGitReference("abc");
    workflowStatus.setLastGitReference("abc");
    workflowStatus.setWorkflowInstance(workflowInstance);

    component.setCurrentWorkflowInstance(workflowInstance);
    component.setFilePath(filePath);

    return Tuple.tuple(component, workflowInstance, workflowStatus);
  }

  protected void initMock(Component component, WorkflowInstance workflowInstance, WorkflowStatus workflowStatus, String hash, boolean exist) {
    if (!exist) {
      when(componentService.create(any())).thenReturn(component);
    }
    when(componentRepository.findOne(component.getId())).thenReturn(component);
    when(workflowStatusRepository.findLatestWorkflowStatusByComponentId(component.getId())).thenReturn(workflowStatus);
    when(gitService.updateAndCommit(any(), eq(component.getFilePath()), any(), any())).thenReturn(hash);
    when(componentReferenceRepository.deleteBySourceIdAndSourceWorkflowInstanceId(component.getId(), workflowInstance.getId())).thenReturn(1L);
    when(workflowInstanceRepository.findOne(workflowInstance.getId())).thenReturn(workflowInstance);
  }

  protected boolean htmlEquals(Resource expected, Resource actual) {
    try (InputStream expectedIs = expected.getInputStream(); InputStream actualIs = actual.getInputStream()) {
      Document expectedDom = Jsoup.parse(IOUtils.toString(expectedIs, UTF_8));
      Document actualDom = Jsoup.parse(IOUtils.toString(actualIs, UTF_8));
      return expectedDom.body().html().equals(actualDom.body().html());
    } catch (Exception e) {
      return false;
    }
  }


}
