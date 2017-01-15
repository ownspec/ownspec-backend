package com.ownspec.center.service.content.generator;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.component.ComponentReferenceRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.component.ComponentService;
import com.ownspec.center.service.content.parser.HtmlComponentContentParser;
import com.ownspec.center.service.content.parser.ParserCallBack;
import com.ownspec.center.service.content.parser.ParserContext;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by nlabrot on 15/01/17.
 */
public class HtmlGeneratorParserCallBack implements ParserCallBack<Element> {

  @Autowired
  private ComponentService componentService;

  @Autowired
  private ComponentReferenceRepository componentReferenceRepository;

  @Autowired
  private WorkflowStatusRepository workflowStatusRepository;

  private boolean forComposition;
  private Path outputDirectory;


  public HtmlGeneratorParserCallBack(boolean forComposition, Path outputDirectory) {
    this.forComposition = forComposition;
    this.outputDirectory = outputDirectory;
  }


  @Override
  public void parseReference(ParserContext parserContext) {
    Component nestedComponent = componentService.findOne(Long.valueOf(parserContext.getNestedComponentId()));

    ComponentReference componentReference = componentReferenceRepository.findOne(Long.valueOf(parserContext.getNestedReferenceId()));

    WorkflowStatus workflowStatus = workflowStatusRepository.findLatestWorkflowStatusByWorkflowInstanceId(componentReference.getTargetWorkflowInstance().getId());

    Element nestedBody = parseNestedComponent(nestedComponent, componentReference.getTargetWorkflowInstance());

    // Create title tag
    if (!forComposition) {
      parserContext.getElement().appendChild(parserContext.getElement().ownerDocument().createElement("div").addClass("requirements-id").text(nestedComponent.getId().toString()));
    }

    //Create content tag
    Element nestedContent = parserContext.getElement().ownerDocument().createElement("div").addClass("requirements-content");

    nestedContent.attr("contenteditable", Boolean.toString(workflowStatus.getStatus().isEditable()));

    while (nestedBody.childNodeSize() > 0){
      nestedContent.appendChild(nestedBody.childNode(0));
    }

    parserContext.getElement().appendChild(nestedContent);
  }

  @Override
  public void parseResource(ParserContext parserContext) {
    try {
      Component nestedComponent = componentService.findOne(Long.valueOf(parserContext.getNestedComponentId()));

      if (forComposition) {
        Resource content = componentService.getContent(nestedComponent, Long.valueOf(parserContext.getNestedWorkflowInstanceId()));

        Path filePath = outputDirectory.resolve(Paths.get(nestedComponent.getFilename()));

        try (InputStream is = content.getInputStream()) {
          Files.copy(is, filePath);
        }
        parserContext.getElement().attr("src", nestedComponent.getFilename());
      } else {
        parserContext.getElement().attr("src", "/api/components/" + nestedComponent.getId() + "/content");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Element endComponent(Element body) {
    return body;
  }




  private Element parseNestedComponent(Component c, WorkflowInstance workflowInstance) {
    HtmlComponentContentParser<Element> contentParser = new HtmlComponentContentParser();
    Resource resource = componentService.getContent(c, workflowInstance.getId());
    return contentParser.parse(resource, this);
  }

}
