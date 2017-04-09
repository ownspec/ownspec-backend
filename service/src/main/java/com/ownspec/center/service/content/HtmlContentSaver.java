package com.ownspec.center.service.content;

import static com.ownspec.center.dto.component.ImmutableComponentVersionDto.newComponentVersionDto;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.component.ComponentReferenceRepository;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.repository.component.ComponentVersionRepository;
import com.ownspec.center.repository.workflow.WorkflowInstanceRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.AuthenticationService;
import com.ownspec.center.service.GitService;
import com.ownspec.center.service.component.ComponentService;
import com.ownspec.center.service.component.ComponentVersionService;
import com.ownspec.center.service.content.parser.HtmlComponentContentParser;
import com.ownspec.center.service.content.parser.ParserCallBack;
import com.ownspec.center.service.content.parser.ParserContext;
import com.ownspec.center.service.workflow.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by nlabrot on 03/11/16.
 */
@Slf4j
public class HtmlContentSaver {

  public static final String DATA_REFERENCE_ID = "data-reference-id";

  public static final String DATA_EDITABLE = "data-os-editable";

  public static final String DATA_LOADED = "data-os-cv-loaded";

  public static final String DATA_COMPONENT_VERSION_ID = "data-os-cv-id";
  public static final String DATA_REQUIREMENT_SCM_REF = "data-requirement-scm-ref";


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

  @Autowired
  private ComponentVersionService componentVersionService;

  @Autowired
  private ComponentVersionRepository componentVersionRepository;


  @Autowired
  private WorkflowService workflowService;


  // Keep the insertion order, insertion order are reversed depth first
  // eg nested components A => B => C will be inserted C => B => A
  //private Map<String, ComponentVersionContent> parsedComponents = new LinkedHashMap<>();

  private DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
  private ComponentVersionContent root;

  private List<ComponentVersionContent> componentVersionContents = new ArrayList<>();


  public void save(ComponentVersion c, byte[] contentAsByteArray) {
    save(c, new String(contentAsByteArray));
  }


  public void save(ComponentVersion componentVersion, String content) {
    extractReferences(componentVersion, content);
    save();
  }


  private void extractReferences(ComponentVersion componentVersion, String content) {
    root = new ComponentVersionContent(componentVersion.getId().toString());
    root.componentVersionId = componentVersion.getId();
    root.editable = true;

    graph.addVertex(root.tempCvId);

    componentVersionContents.add(root);

    HtmlComponentContentParser.parse(content, new SaveParseCallBack(root));
  }


  protected void save() {

    // Detect cycle
    CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector(graph);

    if (cycleDetector.detectCycles()) {
      throw new ComponentCycleException(cycleDetector.findCycles());
    }

    // Iterate over the children hierarchy


    for (int i = componentVersionContents.size() - 1; i >= 0; i--) {


      ComponentVersionContent componentContent = componentVersionContents.get(i);

      ComponentVersion componentVersion;

      if (componentContent.created) {
        // Create the component
        componentVersion = componentService.create(newComponentVersionDto()
            .title("TBD")
            .version("1")
            .type(ComponentType.COMPONENT).build()).getRight();

        // Update the component status to draft
        workflowService.updateStatus(componentVersion.getId(), Status.DRAFT, "draft");

        componentContent.componentVersionId = componentVersion.getId();

      } else {
        // Find the component
        componentVersion = componentVersionRepository.findOne(componentContent.componentVersionId);
      }

      // Resource can't be updated through html content saver
      if (componentVersion.getComponent().getType() == ComponentType.RESOURCE) {
        continue;
      }


      WorkflowStatus workflowStatus = workflowStatusRepository.findLatestWorkflowStatusByComponentVersionId(componentVersion.getId());

      componentContent.scmReference = workflowStatus.getLastGitReference();


      if (!workflowStatus.getStatus().isEditable() || !componentContent.editable) {
        LOG.info("Component [{}] is not editable", componentVersion);
        continue;
      }

/*
      if (!componentContent.workflowInstanceId.equals(workflowStatus.getWorkflowInstance().getId())) {
        LOG.info("Current workflowInstanceId [{}] is not equal to the submitted one provided [{}]",
            workflowStatus.getWorkflowInstance().getId(), componentContent.workflowInstanceId);
        continue;
      }
*/


      Long deletedRef = 0L;

      if (!componentContent.created) {
        // Delete old refs if not created
        componentReferenceRepository.deleteBySourceId(componentVersion.getId());
      }

      // Save the new references
      for (ComponentVersionContent referenceComponentContent : componentContent.childrens.values()) {
        //ComponentVersionContent referenceComponentContent = parsedComponents.get(refTempId);

        ComponentReference componentReference = new ComponentReference();
        componentReference.setSource(componentVersion);
        componentReference.setTarget(componentVersionRepository.findOne(referenceComponentContent.componentVersionId));
        componentReference = componentReferenceRepository.save(componentReference);

        referenceComponentContent.componentReferenceId = componentReference.getId();
        // Store the reference id
        //componentContent.referencedTargetToReferenceId.put(refTempId, componentReference.getId());
      }

      // Update the reference with the reference id
      Document document = Jsoup.parse(componentContent.content);
      parseComponent(document.body(), new UpdateReferenceCallBack(componentContent));
      componentContent.content = document.body().html();

      componentVersionService.updateRawContent(componentVersion, new ByteArrayResource(componentContent.content.getBytes(UTF_8)));

      // Update content
      /*String hash = gitService.updateAndCommit(componentVersion.getComponent().getVcsId(), componentVersion.getFilename(), new ByteArrayResource(componentContent.content.getBytes(UTF_8)),
          authenticationService.getAuthenticatedUser(), "");
      if (hash == null) {
        // No modification continue
        continue;
      }*/
    }
  }

  public Map<Long, List<Pair<Long, Long>>> getParsedComponents() {
    return null;
  }


  private static class ComponentVersionContent {
    private String tempRefId;
    private String tempCvId;
    private Long componentVersionId;
    private Long componentReferenceId;
    private String content;
    private String scmReference;
    private boolean editable = true;

    private Map<String, ComponentVersionContent> childrens = new HashMap<>();

    private boolean created = false;


    public ComponentVersionContent(String cvId) {
      tempCvId = cvId;
      componentVersionId = Long.valueOf(cvId);
      tempRefId = UUID.randomUUID().toString();
    }

    public ComponentVersionContent() {
      created = true;
      tempRefId = UUID.randomUUID().toString();
      tempCvId = UUID.randomUUID().toString();
    }
  }


  class UpdateReferenceCallBack implements ParserCallBack {

    ComponentVersionContent componentContent;

    public UpdateReferenceCallBack(ComponentVersionContent componentContent) {
      this.componentContent = componentContent;
    }

    @Override
    public void parseReference(ParserContext parserContext) {
      parserContext.getElement().attr(DATA_REFERENCE_ID, componentContent.childrens.get(parserContext.getNestedReferenceId()).componentReferenceId.toString());
      // TODO: remove attr once https://github.com/jhy/jsoup/issues/799 is released
      parserContext.getElement().attr(DATA_COMPONENT_VERSION_ID, componentContent.childrens.get(parserContext.getNestedReferenceId()).componentVersionId.toString());
    }

    @Override
    public void parseResource(ParserContext parserContext) {
      parserContext.getElement().attr(DATA_REFERENCE_ID, componentContent.childrens.get(parserContext.getNestedReferenceId()).componentReferenceId.toString());
      // TODO: remove attr once https://github.com/jhy/jsoup/issues/799 is released
      parserContext.getElement().attr(DATA_COMPONENT_VERSION_ID, componentContent.childrens.get(parserContext.getNestedReferenceId()).componentVersionId.toString());
    }

    @Override
    public Object endComponent(Element parent) {
      return null;
    }
  }

  class SaveParseCallBack implements ParserCallBack {
    ComponentVersionContent owner;

    public SaveParseCallBack(ComponentVersionContent componentContent) {
      this.owner = componentContent;
    }

    @Override
    public void parseReference(ParserContext parserContext) {

      ComponentVersionContent nestedComponent;


      if (!parserContext.getNestedComponentId().startsWith("_")) {
        nestedComponent = new ComponentVersionContent(parserContext.getNestedComponentId());
        parserContext.getElement().attr(HtmlContentSaver.DATA_REFERENCE_ID, nestedComponent.tempRefId);
      } else {
        // New component
        nestedComponent = new ComponentVersionContent();
        parserContext.getElement().attr(HtmlContentSaver.DATA_COMPONENT_VERSION_ID, nestedComponent.tempCvId);
        parserContext.getElement().attr(HtmlContentSaver.DATA_REFERENCE_ID, nestedComponent.tempRefId);
      }

      // Cycle detector
      graph.addVertex(nestedComponent.tempCvId);
      graph.addEdge(owner.tempCvId, nestedComponent.tempCvId);

      owner.childrens.put(nestedComponent.tempRefId, nestedComponent);

      componentVersionContents.add(nestedComponent);


      Element contentElement = parserContext.getElement().children().get(1);
      nestedComponent.editable =
          StringUtils.isBlank(contentElement.attr(DATA_EDITABLE)) ||
              StringUtils.equals(contentElement.attr(DATA_EDITABLE), "true");


      if (nestedComponent.editable) {
        // If the component is editable it may have been modified
        // extract reference from the nested reference content (second children, first children is the title)
        parseComponent(contentElement, new SaveParseCallBack(nestedComponent));
      }

      // remove all the childrens to keep only the reference
      parserContext.getElement().empty();
    }


    @Override
    public void parseResource(ParserContext parserContext) {
      ComponentVersionContent nestedComponent = new ComponentVersionContent(parserContext.getNestedComponentId());
      parserContext.getElement().attr(HtmlContentSaver.DATA_REFERENCE_ID, nestedComponent.tempRefId);
      //owner.referencedTarget.add(nestedComponent.tempId);

      owner.childrens.put(nestedComponent.tempRefId, nestedComponent);
      componentVersionContents.add(nestedComponent);

      parseComponent(parserContext.getElement(), new SaveParseCallBack(nestedComponent));
    }


    @Override
    public ComponentVersionContent endComponent(Element parent) {
      owner.content = parent.html();
      return owner;
    }
  }


  private <T> T parseComponent(String content, ParserCallBack<T> cb) {
    return HtmlComponentContentParser.parse(content, cb);
  }


  private <T> T parseComponent(Element parent, ParserCallBack<T> cb) {
    return HtmlComponentContentParser.parse(parent, cb);
  }

}
