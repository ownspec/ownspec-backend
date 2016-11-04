package com.ownspec.center.service.content;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.component.ComponentReferenceRepository;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.repository.workflow.WorkflowInstanceRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.GitService;
import com.ownspec.center.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by nlabrot on 03/11/16.
 */
@Slf4j
public class HtmlContentSaver {

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
  private SecurityService securityService;

  private Map<Long, List<Pair<Long, Long>>> referencesByComponent = new HashMap<>();


  public void save(Component c, byte[] contentAsByteArray) {
    save(c, new String(contentAsByteArray));
  }


  public void save(Component component, String content) {
    try {
      Document document = Jsoup.parse(content);

      extractReference(component, document.body());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  private void extractReference(Component component, Element parent) {
    try {
      WorkflowStatus workflowStatus = workflowStatusRepository.findLatestStatusByWorkflowInstanceComponentId(component.getId());

      if (!workflowStatus.getStatus().isEditable()) {
        LOG.info("Component [{}] is not editable", component);
        return;
      }


      List<Pair<Long, Long>> references = new ArrayList<>();

      Deque<Element> stack = new LinkedList<>(parent.children());

      while (!stack.isEmpty()) {
        Element element = stack.pop();

        if ("div".equals(element.nodeName()) && element.hasAttr("data-requirement-id")) {
          Long targetComponentId = Long.valueOf(element.attr("data-requirement-id"));
          references.add(Pair.of(targetComponentId, Long.valueOf(element.attr("data-workflow-instance-id"))));
          Component nestedComponent = componentRepository.findOne(targetComponentId);
          // extract reference from the nested reference content (second children, first children is the title)
          extractReference(nestedComponent, element.children().get(1));
          // remove all the childrens to keep only the reference
          element.empty();
        } else {
          stack.addAll(element.children());
        }
      }

      // Update content
      String content = parent.html();
      String hash = gitService.updateAndCommit(new ByteArrayResource(content.getBytes(UTF_8)), component.getFilePath(), securityService.getAuthenticatedUser(), "");
      if (hash == null) {
        // No modification
        return;
      }

      if (workflowStatus.getFirstGitReference() == null) {
        workflowStatus.setFirstGitReference(hash);
        workflowStatus.setLastGitReference(hash);
      } else {
        workflowStatus.setLastGitReference(hash);
      }

      workflowStatusRepository.save(workflowStatus);

      component.getCurrentWorkflowInstance().setCurrentGitReference(hash);
      componentRepository.save(component);


      // Delete old refs
      Long deletedRef = componentReferenceRepository.deleteBySourceIdAndSourceWorkflowInstanceId(component.getId(),
          component.getCurrentWorkflowInstance().getId());

      // Save the new references
      for (Pair<Long, Long> reference : references) {
        ComponentReference componentReference = new ComponentReference();
        componentReference.setSource(component);
        componentReference.setSourceWorkflowInstance(component.getCurrentWorkflowInstance());
        componentReference.setTarget(componentRepository.findOne(reference.getLeft()));
        componentReference.setTargetWorkflowInstance(workflowInstanceRepository.findOne(reference.getRight()));
        componentReferenceRepository.save(componentReference);
      }

      referencesByComponent.put(component.getId() , references);

      LOG.info("Component [{}], Deleted [{}], Created [{}]", component, deletedRef, references.size());

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Map<Long, List<Pair<Long, Long>>> getReferencesByComponent() {
    return referencesByComponent;
  }
}
