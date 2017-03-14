package com.ownspec.center.service.content;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.component.ComponentReferenceRepository;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.repository.component.ComponentVersionRepository;
import com.ownspec.center.repository.workflow.WorkflowInstanceRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.AuthenticationService;
import com.ownspec.center.service.GitService;
import com.ownspec.center.service.component.ComponentService;
import com.ownspec.center.service.workflow.WorkflowService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
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
import java.util.concurrent.atomic.AtomicLong;

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
  private ComponentVersionRepository componentVersionRepository;

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

  @Mock
  private WorkflowService workflowService;

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

    Tuple3<ComponentVersion, WorkflowInstance, WorkflowStatus> component = createComponent(0L, 0L, 0L, "filePath");


    initMock(component.v1, component.v2, component.v3, "foo", true);

    htmlContentSaver.save(component.v1, readFileToString(new File("src/test/resources/reference/no/no_reference.html"), UTF_8));

    // update and commit must be called
    verify(gitService).updateAndCommit(eq(component.v1.getComponent().getVcsId()), eq("filePath"), argThat(r -> htmlEquals(new ClassPathResource("reference/no/no_reference_expected.html"), r)), any(), any());

    verify(componentVersionRepository).save(argThat((ComponentVersion w) -> "foo".equals(w.getGitReference())));

    // No saved references
    verify(componentReferenceRepository).deleteBySourceId(eq(component.v1.getId()));
    verify(componentReferenceRepository, never()).save(any(ComponentReference.class));

  }


  @Test
  public void testWithReference() throws Exception {

    AtomicLong counter = new AtomicLong();

    Tuple3<ComponentVersion, WorkflowInstance, WorkflowStatus> component1 = createComponent(1L, 1L, 1L, "filePath1");
    Tuple3<ComponentVersion, WorkflowInstance, WorkflowStatus> component2 = createComponent(2L, 2L, 2L, "filePath2");

    initMock(component1.v1, component1.v2, component1.v3, "hash1", true);
    initMock(component2.v1, component2.v2, component2.v3, "hash2", true);

    when(componentReferenceRepository.save(any(ComponentReference.class))).then(i -> {
          ComponentReference reference = new ComponentReference();
          reference.setId(counter.getAndIncrement());
          return reference;
        }
    );


    htmlContentSaver.save(component1.v1, readFileToString(new File("src/test/resources/reference/one/one_reference.html"), UTF_8));

    // Component 1: commit
    verify(gitService).updateAndCommit(eq(component1.v1.getComponent().getVcsId()), eq("filePath1"), argThat(r -> htmlEquals(new ClassPathResource("reference/one/one_reference_expected_1.html"), r)), any(), any());

    // Component 1: update git reference
    verify(componentVersionRepository).save(argThat((ComponentVersion w) -> w.getId() == 1L && "hash1".equals(w.getGitReference())));

    // One saved references
    verify(componentReferenceRepository).save(argThat((ComponentReference r) ->
            r.getSource().getId().equals(component1.v1.getId()) &&
            r.getTarget().getId().equals(component2.v1.getId())
    ));

    // Component 2: commit
    verify(gitService).updateAndCommit(eq(component2.v1.getComponent().getVcsId()), eq("filePath2"), argThat(r -> htmlEquals(new ClassPathResource("reference/one/one_reference_expected_2.html"), r)), any(), any());
    // Component 2: update git reference
    verify(componentVersionRepository).save(argThat((ComponentVersion w) -> w.getId() == 2L && "hash2".equals(w.getGitReference())));
  }



  @Test
  public void testWithResource() throws Exception {

    AtomicLong counter = new AtomicLong();

    Tuple3<ComponentVersion, WorkflowInstance, WorkflowStatus> component1 = createComponent(1L, 1L, 1L, "filePath1");
    Tuple3<ComponentVersion, WorkflowInstance, WorkflowStatus> component2 = createComponent(2L, ComponentType.RESOURCE, 2L, 2L, "filePath2");

    initMock(component1.v1, component1.v2, component1.v3, "hash1", true);
    initMock(component2.v1, component2.v2, component2.v3, "hash2", true);

    when(componentReferenceRepository.save(any(ComponentReference.class))).then(i -> {
          ComponentReference reference = new ComponentReference();
          reference.setId(counter.getAndIncrement());
          return reference;
        }
    );


    htmlContentSaver.save(component1.v1, readFileToString(new File("src/test/resources/reference/resource/one_reference.html"), UTF_8));

    // Component 1: commit
    verify(gitService).updateAndCommit(eq(component1.v1.getComponent().getVcsId()), eq("filePath1"), argThat(r -> htmlEquals(new ClassPathResource("reference/resource/one_reference_expected_1.html"), r)), any(), any());
    // Component 1: update reference
    verify(componentVersionRepository).save(argThat((ComponentVersion w) -> w.getId() == 1L && "hash1".equals(w.getGitReference())));

    // One saved references
    verify(componentReferenceRepository).save(argThat((ComponentReference r) ->
        r.getSource().getId().equals(component1.v1.getId()) &&
            r.getTarget().getId().equals(component2.v1.getId())
    ));

    verify(workflowStatusRepository).findLatestWorkflowStatusByComponentVersionId(component1.v1.getId());
    verify(workflowStatusRepository).save(argThat((WorkflowStatus w) -> w.getId() == component1.v3.getId() && "hash1".equals(w.getLastGitReference())));

    // No more interaction, the resource is not updated
    verifyNoMoreInteractions(gitService, workflowStatusRepository);
  }


  @Test
  public void testCreateReference() throws Exception {
    AtomicLong counter = new AtomicLong();

    Tuple3<ComponentVersion, WorkflowInstance, WorkflowStatus> component1 = createComponent(1L, 1L, 1L, "filePath1");
    Tuple3<ComponentVersion, WorkflowInstance, WorkflowStatus> component2 = createComponent(2L, 2L, 2L, "filePath2");

    initMock(component1.v1, component1.v2, component1.v3, "hash1", true);
    initMock(component2.v1, component2.v2, component2.v3, "hash2", false);


    when(componentReferenceRepository.save(any(ComponentReference.class))).then(i -> {
          ComponentReference reference = new ComponentReference();
          reference.setId(counter.getAndIncrement());
          return reference;
        }
    );



    htmlContentSaver.save(component1.v1, readFileToString(new File("src/test/resources/reference/create/create_reference.html"), UTF_8));

    // Component 1: commit
    verify(gitService).updateAndCommit(eq(component1.v1.getComponent().getVcsId()), eq("filePath1"), argThat(r -> htmlEquals(new ClassPathResource("reference/create/create_reference_expected_1.html"), r)), any(), any());
    // Component 1: update workflow
    verify(componentVersionRepository).save(argThat((ComponentVersion w) -> w.getId() == 1L && "hash1".equals(w.getGitReference())));

    // Component 1: One saved references
    verify(componentReferenceRepository).save(argThat((ComponentReference r) ->
        r.getSource().getId().equals(component1.v1.getId()) &&
            r.getTarget().getId().equals(component2.v1.getId())
    ));

    // Component 2: create
    verify(componentService).create(any());
    // Component 2: commit
    verify(gitService).updateAndCommit(eq(component2.v1.getComponent().getVcsId()), eq("filePath2"), argThat(r -> htmlEquals(new ClassPathResource("reference/create/create_reference_expected_2.html"), r)), any(), any());
    // Component 2: update git ref
    verify(componentVersionRepository).save(argThat((ComponentVersion w) -> w.getId() == 2L && "hash2".equals(w.getGitReference())));
    // Component : update workflow to draft
    verify(workflowService).updateStatus(component2.v1.getId() , Status.DRAFT , "draft");
  }


  @Test
  public void testNestedCreateReference() throws Exception {
    AtomicLong counter = new AtomicLong();

    Tuple3<ComponentVersion, WorkflowInstance, WorkflowStatus> component1 = createComponent(1L, 1L, 1L, "filePath1");
    Tuple3<ComponentVersion, WorkflowInstance, WorkflowStatus> component2 = createComponent(2L, 2L, 2L, "filePath2");
    Tuple3<ComponentVersion, WorkflowInstance, WorkflowStatus> component3 = createComponent(3L, 3L, 3L, "filePath3");

    initMock(component1.v1, component1.v2, component1.v3, "hash1", true);
    initMock(component2.v1, component2.v2, component2.v3, "hash2", false);
    initMock(component3.v1, component3.v2, component3.v3, "hash3", true);

    when(componentReferenceRepository.save(any(ComponentReference.class))).then(i -> {
          ComponentReference reference = new ComponentReference();
          reference.setId(counter.getAndIncrement());
          return reference;
        }
    );


    htmlContentSaver.save(component1.v1, readFileToString(new File("src/test/resources/reference/create-nested/create_reference.html"), UTF_8));

    // Component 1: commit
    verify(gitService).updateAndCommit(eq(component1.v1.getComponent().getVcsId()), eq("filePath1"), argThat(r -> htmlEquals(new ClassPathResource("reference/create-nested/create_reference_expected_1.html"), r)), any(), any());
    // Component 1: update git ref
    verify(componentVersionRepository).save(argThat((ComponentVersion w) -> w.getId() == 1L && "hash1".equals(w.getGitReference())));
    // Component 1: disable reference
    verify(componentReferenceRepository).deleteBySourceId(component1.v1.getId());

    // Component 1: One saved references 1 => 2
    verify(componentReferenceRepository).save(argThat((ComponentReference r) ->
        r.getSource().getId().equals(component1.v1.getId()) &&
            r.getTarget().getId().equals(component2.v1.getId())
    ));

    // Component 2: create
    verify(componentService).create(any());
    // Component 2: commit
    verify(gitService).updateAndCommit(eq(component2.v1.getComponent().getVcsId()), eq("filePath2"), argThat(r -> htmlEquals(new ClassPathResource("reference/create-nested/create_reference_expected_2.html"), r)), any(), any());
    // Component 2: update git ref
    verify(componentVersionRepository).save(argThat((ComponentVersion w) -> w.getId() == 2L && "hash2".equals(w.getGitReference())));

    // Component 2: One saved references 2 => 3
    verify(componentReferenceRepository).save(argThat((ComponentReference r) ->
        r.getSource().getId().equals(component2.v1.getId()) &&
            r.getTarget().getId().equals(component3.v1.getId())
    ));

    // Component 3: commit
    verify(gitService).updateAndCommit(eq(component3.v1.getComponent().getVcsId()), eq("filePath3"), argThat(r -> htmlEquals(new ClassPathResource("reference/create-nested/create_reference_expected_3.html"), r)), any(), any());
    // Component 3: update git ref
    verify(componentVersionRepository).save(argThat((ComponentVersion w) -> w.getId() == 3L && "hash3".equals(w.getGitReference())));
    // Component 3: disable reference
    verify(componentReferenceRepository).deleteBySourceId(component3.v1.getId());

    Mockito.verifyNoMoreInteractions(componentReferenceRepository, gitService);
  }


  @Test(expected = ComponentCycleException.class)
  public void testCycle() throws Exception {
    Tuple3<ComponentVersion, WorkflowInstance, WorkflowStatus> component1 = createComponent(1L, 1L, 1L, "filePath1");
    htmlContentSaver.save(component1.v1, readFileToString(new File("src/test/resources/reference/cycle/cycle.html"), UTF_8));
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


  protected Tuple3<ComponentVersion, WorkflowInstance, WorkflowStatus> createComponent(Long id, Long workflowInstanceId, Long wsTatusId, String filePath) {
    return createComponent(id, ComponentType.COMPONENT, workflowInstanceId, wsTatusId, filePath);
  }

  protected Tuple3<ComponentVersion, WorkflowInstance, WorkflowStatus> createComponent(Long id, ComponentType componentType, Long workflowInstanceId, Long wsTatusId, String filePath) {

    Component component = new Component();
    component.setId(id);
    component.setType(componentType);

    WorkflowInstance workflowInstance = new WorkflowInstance();
    workflowInstance.setId(workflowInstanceId);

    WorkflowStatus workflowStatus = new WorkflowStatus();
    workflowStatus.setId(wsTatusId);
    workflowStatus.setStatus(Status.DRAFT);
    workflowStatus.setFirstGitReference("abc");
    workflowStatus.setLastGitReference("abc");
    workflowStatus.setWorkflowInstance(workflowInstance);


    ComponentVersion componentVersion = new ComponentVersion();
    componentVersion.setId(id);
    componentVersion.setComponent(component);
    componentVersion.setTitle("TBD");
    componentVersion.setWorkflowInstance(workflowInstance);
    componentVersion.setGitReference("abc");
    componentVersion.setFilename(filePath);
    

    return Tuple.tuple(componentVersion, workflowInstance, workflowStatus);
  }


  protected void initMock(ComponentVersion componentVersion, WorkflowInstance workflowInstance, WorkflowStatus workflowStatus, String hash, boolean exist) {
    if (!exist) {
      when(componentService.create(any())).thenReturn(Pair.of(componentVersion.getComponent(), componentVersion));
    }
    when(componentVersionRepository.findOne(componentVersion.getId())).thenReturn(componentVersion);
    when(workflowStatusRepository.findLatestWorkflowStatusByWorkflowInstanceId(workflowInstance.getId())).thenReturn(workflowStatus);
    when(workflowStatusRepository.findLatestWorkflowStatusByComponentVersionId(componentVersion.getId())).thenReturn(workflowStatus);
    when(gitService.updateAndCommit(eq(componentVersion.getComponent().getVcsId()), eq(componentVersion.getFilename()), any(), any(), any())).thenReturn(hash);
    when(componentReferenceRepository.deleteBySourceId(componentVersion.getId())).thenReturn(1L);
    when(workflowInstanceRepository.findOne(workflowInstance.getId())).thenReturn(workflowInstance);
  }

  protected boolean htmlEquals(Resource expected, Resource actual) {
    try (InputStream expectedIs = expected.getInputStream(); InputStream actualIs = actual.getInputStream()) {
      Document expectedDom = Jsoup.parse(IOUtils.toString(expectedIs, UTF_8));
      Document actualDom = Jsoup.parse(IOUtils.toString(actualIs, UTF_8));
      Assert.assertEquals(expectedDom.body().html(), actualDom.body().html());
      return true;
    } catch (Exception e) {
      return false;
    }
  }


}
