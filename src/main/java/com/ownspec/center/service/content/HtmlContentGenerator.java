package com.ownspec.center.service.content;

import static com.ownspec.center.service.content.HtmlContentSaver.DATA_REQUIREMENT_ID;
import static com.ownspec.center.service.content.HtmlContentSaver.DATA_WORKFLOW_INSTANCE_ID;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.repository.workflow.WorkflowInstanceRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.component.ComponentService;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by nlabrot on 03/11/16.
 */
public class HtmlContentGenerator {

  @Autowired
  private ComponentService componentService;

  @Autowired
  private ComponentRepository componentRepository;

  @Value("${component.content.summary-length:80}")
  private int summaryLength;

  @Autowired
  private WorkflowInstanceRepository workflowInstanceRepository;

  @Autowired
  private WorkflowStatusRepository workflowStatusRepository;
  private boolean forComposition;

  public Pair<String, String> generate(Component c) {
    return generate(c, false);
  }


  public Pair<String, String> generate(Component c, boolean forComposition) {

    try {
      this.forComposition = forComposition;

      // find latest workflow status having a git reference
      WorkflowStatus workflowStatus = workflowStatusRepository.findLatestWorkflowStatusWithGitReferenceByComponentId(c.getId());

      Document document;
      try (InputStream is = componentService.getRawContent(c, workflowStatus.getLastGitReference()).getInputStream()) {
        document = Jsoup.parse(is, "UTF-8", c.getFilePath());
      }

      Element body = document.getElementsByTag("body").first();

      generateContent(c, document, body);

      String substring = body.text().replaceAll("(?<=.{" + summaryLength + "})\\b.*", "...");

      return Pair.of(body.html(), substring);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void generateContent(Component c, Document doc, Element parent) throws IOException {

    Deque<Element> stack = new LinkedList<>(parent.children());

    while (!stack.isEmpty()) {
      Element element = stack.pop();

      if ("div".equals(element.nodeName()) && element.hasAttr(DATA_REQUIREMENT_ID)) {
        Long nestedComponentId = Long.valueOf(element.attr(DATA_REQUIREMENT_ID));
        Long nestedWorkflowInstanceId = Long.valueOf(element.attr(DATA_WORKFLOW_INSTANCE_ID));

        Component nestedComponent = componentRepository.findOne(nestedComponentId);

        List<WorkflowStatus> nestedWorkflowStatuses = workflowStatusRepository.findAllByWorkflowInstanceId(nestedWorkflowInstanceId, new Sort(Sort.Direction.DESC, "id"));

        String lastGitReference = nestedWorkflowStatuses.stream()
            .map(WorkflowStatus::getLastGitReference)
            .filter(Objects::nonNull)
            .findFirst().get();

        WorkflowStatus lastWorkflowStatus = nestedWorkflowStatuses.get(0);

        // Add git ref attribute
        //element.attr(DATA_REQUIREMENT_SCM_REF, nestedWorkflowInstance.getCurrentGitReference());


        // Retrieve file content associated to the git reference
        Document nestedDocument;
        try (InputStream is = componentService.getRawContent(nestedComponent, lastGitReference).getInputStream()) {
          nestedDocument = Jsoup.parse(is, "UTF-8", nestedComponent.getFilePath());
        }
        Element nestedBody = nestedDocument.getElementsByTag("body").first();

        // Create title tag
        if (!forComposition) {
          element.appendChild(doc.createElement("div").addClass("requirements-id").text(nestedComponent.getId().toString()));
        }

        // Extract reference from the nested reference content (second children, first children is the title)
        generateContent(nestedComponent, nestedDocument, nestedBody);

        //Create content tag
        Element nestedContent = doc.createElement("div").addClass("requirements-content");

        nestedContent.attr("contenteditable", Boolean.toString(lastWorkflowStatus.getStatus().isEditable()));

        new ArrayList<>(nestedBody.childNodes()).forEach(nestedContent::appendChild);
        element.appendChild(nestedContent);

      } else {
        stack.addAll(element.children());
      }
    }
  }

}
