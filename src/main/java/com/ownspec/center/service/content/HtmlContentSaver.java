package com.ownspec.center.service.content;

import static com.ownspec.center.dto.ImmutableComponentDto.newComponentDto;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.component.ComponentReferenceRepository;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.repository.workflow.WorkflowInstanceRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.AuthenticationService;
import com.ownspec.center.service.ComponentService;
import com.ownspec.center.service.GitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by nlabrot on 03/11/16.
 */
@Slf4j
public class HtmlContentSaver {

  public static final String DATA_REQUIREMENT_ID = "data-requirement-id";
  public static final String DATA_REQUIREMENT_SCM_REF = "data-requirement-scm-ref";
  public static final String DATA_WORKFLOW_INSTANCE_ID = "data-workflow-instance-id";
  @Autowired
  private WorkflowStatusRepository workflowStatusRepository;

  @Autowired
  private ComponentRepository componentRepository;

  @Autowired
  private ComponentReferenceRepository componentReferenceRepository;

  @Autowired
  private GitService gitService;

  @Autowired
  private WorkflowInstanceRepository workflowInstanceRepository;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private ComponentService componentService;

  // Keep the insertion order, insertion order are reversed depth first
  // eg nested components A => B => C will be inserted C => B => A
  private Map<String, ComponentContent> referencesByComponent = new LinkedHashMap<>();

  private DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);


  public void save(Component c, byte[] contentAsByteArray) {
    save(c, new String(contentAsByteArray));
  }


  public void save(Component component, String content) {
    extractReferences(component, content);
    save();
  }


  private void extractReferences(Component component, String content) {
    ComponentContent componentContent = new ComponentContent(component.getId().toString());
    componentContent.componentId = component.getId();
    componentContent.workflowInstanceId = component.getCurrentWorkflowInstance().getId();

    graph.addVertex(componentContent.tempId);

    parseComponent(content, new SaveParseCallBack(componentContent));
  }


  protected void save() {

    // Detect cycle
    CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector(graph);

    if (cycleDetector.detectCycles()) {
      throw new ComponentCycleException(cycleDetector.findCycles());
    }

    for (String tempId : referencesByComponent.keySet()) {

      ComponentContent componentContent = referencesByComponent.get(tempId);

      Component component;

      if (componentContent.componentId == null) {
        // Create the component
        component = componentService.create(newComponentDto()
            .title("TBD")
            .type(ComponentType.COMPONENT).build());
        componentContent.componentId = component.getId();
        componentContent.workflowInstanceId = component.getCurrentWorkflowInstance().getId();

      } else {
        // Find the component
        component = componentRepository.findOne(componentContent.componentId);
      }

      WorkflowStatus workflowStatus = workflowStatusRepository.findLatestWorkflowStatusByComponentId(component.getId());

      componentContent.scmReference = workflowStatus.getLastGitReference();


      if (!workflowStatus.getStatus().isEditable()) {
        LOG.info("Component [{}] is not editable", component);
        continue;
      }

      if (!componentContent.workflowInstanceId.equals(workflowStatus.getWorkflowInstance().getId())) {
        LOG.info("Current workflowInstanceId [{}] is not equal to the submitted one provided [{}]",
            workflowStatus.getWorkflowInstance().getId(), componentContent.workflowInstanceId);
        continue;
      }


      Document document = Jsoup.parse(componentContent.content);
      parseComponent(document.body(), new UpdateReferenceCallBack(componentContent));
      componentContent.content = document.body().html();

      // Update content


      String hash = gitService.updateAndCommit(
          new ByteArrayResource(componentContent.content.getBytes(UTF_8)),
          component.getFilePath(),
          authenticationService.getAuthenticatedUser(), "");
      if (hash == null) {
        // No modification continue
        continue;
      }

      if (workflowStatus.getFirstGitReference() == null) {
        workflowStatus.setFirstGitReference(hash);
        workflowStatus.setLastGitReference(hash);
      } else {
        workflowStatus.setLastGitReference(hash);
      }

      workflowStatusRepository.save(workflowStatus);

      componentRepository.save(component);


      Long deletedRef = 0L;

      if (!componentContent.created) {
        // Delete old refs if not created
        componentReferenceRepository.deleteBySourceIdAndSourceWorkflowInstanceId(component.getId(), workflowStatus.getWorkflowInstance().getId());
      }

      // Save the new references
      for (String refTempId : componentContent.references) {
        ComponentContent referenceComponentContent = referencesByComponent.get(refTempId);

        ComponentReference componentReference = new ComponentReference();
        componentReference.setSource(component);
        componentReference.setSourceWorkflowInstance(component.getCurrentWorkflowInstance());
        componentReference.setTarget(componentRepository.findOne(referenceComponentContent.componentId));
        componentReference.setTargetWorkflowInstance(workflowInstanceRepository.findOne(referenceComponentContent.workflowInstanceId));
        componentReferenceRepository.save(componentReference);
      }
    }
  }

  public Map<Long, List<Pair<Long, Long>>> getReferencesByComponent() {
    return null;
  }


  private static class ComponentContent {
    private String tempId;
    private Long componentId;
    private Long workflowInstanceId;
    private String content;
    private String scmReference;
    private List<String> references = new ArrayList<>();
    private boolean created = false;

    public ComponentContent(String tempId) {
      this.tempId = tempId;
    }

    public ComponentContent() {
      tempId = UUID.randomUUID().toString();
      created = true;
    }
  }


  private interface ParseCallBack<T> {
    void parseReference(Element element, String nestedComponentId, String nestedWorkflowInstanceId, String nestedScmRefernece);

    T endComponent(Element parent);
  }


  class UpdateReferenceCallBack implements ParseCallBack {

    ComponentContent componentContent;
    int referenceIndex = 0;

    public UpdateReferenceCallBack(ComponentContent componentContent) {
      this.componentContent = componentContent;
    }

    @Override
    public void parseReference(Element element, String nestedComponentId, String nestedWorkflowInstanceId, String nestedScmRefernece) {
      ComponentContent refComponentContent = referencesByComponent.get(componentContent.references.get(referenceIndex++));
      element.attr(DATA_REQUIREMENT_ID, refComponentContent.componentId.toString());
      element.attr(DATA_WORKFLOW_INSTANCE_ID, refComponentContent.workflowInstanceId.toString());
    }

    @Override
    public Object endComponent(Element parent) {
      return null;
    }
  }

  class SaveParseCallBack implements ParseCallBack {
    ComponentContent componentContent;

    public SaveParseCallBack(ComponentContent componentContent) {
      this.componentContent = componentContent;
    }

    @Override
    public void parseReference(Element element, String nestedComponentId, String nestedWorkflowInstanceId, String nestedScmRefernece) {

      ComponentContent nestedComponent;

      if (!nestedComponentId.startsWith("_")) {
        nestedComponent = new ComponentContent(nestedComponentId);
        nestedComponent.componentId = Long.valueOf(nestedComponentId);
        nestedComponent.workflowInstanceId = Long.valueOf(nestedWorkflowInstanceId);
        nestedComponent.scmReference = nestedScmRefernece;
      } else {
        nestedComponent = new ComponentContent();
      }
      graph.addVertex(nestedComponent.tempId);
      graph.addEdge(componentContent.tempId, nestedComponent.tempId);

      componentContent.references.add(nestedComponent.tempId);

      // extract reference from the nested reference content (second children, first children is the title)
      parseComponent(element.children().get(1), new SaveParseCallBack(nestedComponent));
      // remove all the childrens to keep only the reference
      element.empty();
    }

    @Override
    public ComponentContent endComponent(Element parent) {
      componentContent.content = parent.html();
      referencesByComponent.put(componentContent.tempId, componentContent);
      return componentContent;
    }
  }


  private <T> T parseComponent(String content, ParseCallBack<T> cb) {
    Document document = Jsoup.parse(content);
    return parseComponent(document.body(), cb);
  }


  private <T> T parseComponent(Element parent, ParseCallBack<T> cb) {
    Deque<Element> stack = new LinkedList<>(parent.children());
    while (!stack.isEmpty()) {
      Element element = stack.pop();

      if ("div".equals(element.nodeName()) && element.hasAttr(DATA_REQUIREMENT_ID)) {
        String nestedComponentId = element.attr(DATA_REQUIREMENT_ID);
        String nestedWorkflowInstanceId = element.attr(DATA_WORKFLOW_INSTANCE_ID);
        String nestedScmReference = element.attr(DATA_REQUIREMENT_SCM_REF);

        cb.parseReference(element, nestedComponentId, nestedWorkflowInstanceId, nestedScmReference);

      } else {
        stack.addAll(element.children());
      }
    }
    return cb.endComponent(parent);
  }

}
