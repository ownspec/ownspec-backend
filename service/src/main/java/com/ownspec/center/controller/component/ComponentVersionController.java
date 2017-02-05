package com.ownspec.center.controller.component;

import com.ownspec.center.dto.ComponentReferenceDto;
import com.ownspec.center.dto.ComponentVersionDto;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentReference;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.service.CommentService;
import com.ownspec.center.service.UploadService;
import com.ownspec.center.service.component.ComponentConverter;
import com.ownspec.center.service.component.ComponentService;
import com.ownspec.center.service.component.ComponentTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lyrold on 20/09/2016.
 */
@RestController
@RequestMapping("/api/components/{componentId}/versions")
public class ComponentVersionController {
  @Autowired
  private ComponentService componentService;

  @Autowired
  private CommentService commentService;

  @Autowired
  private ComponentConverter componentConverter;

  @Autowired
  private UploadService uploadService;

  @Autowired
  private ComponentTagService componentTagService;


  @RequestMapping()
  public List<ComponentVersionDto> findAllVersion(@PathVariable("componentId") Long componentId,
                                                  @RequestParam(value = "statuses", required = false, defaultValue = "false") Boolean statuses,
                                                  @RequestParam(value = "references", required = false, defaultValue = "false") Boolean references,
                                                  @RequestParam(value = "usePoints", required = false, defaultValue = "false") Boolean usePoints) {

    Component component = componentService.findOne(componentId);

    return componentService.findAllWorkflow(componentId).stream()
        .map(w -> componentConverter.toComponentVersionDto(component, w, false, statuses, references, usePoints))
        .collect(Collectors.toList());
  }


  @RequestMapping("{version}")
  public ComponentVersionDto getByVersion(@PathVariable("componentId") Long componentId, @PathVariable("version") Long workflowInstanceId,
                                          @RequestParam(value = "statuses", required = false, defaultValue = "false") Boolean statuses,
                                          @RequestParam(value = "references", required = false, defaultValue = "false") Boolean references,
                                          @RequestParam(value = "usePoints", required = false, defaultValue = "false") Boolean usePoints) {
    WorkflowInstance workflowInstance = componentService.findByComponentIdAndWorkflowId(componentId, workflowInstanceId);

    Component c = componentService.findOne(componentId);
    return componentConverter.toComponentVersionDto(c, workflowInstance, false, statuses, references, usePoints);
  }

  @PostMapping("{workflowInstanceId}/references/{refId}")
  public void updateReference(@PathVariable("componentId") Long sourceComponentId, @PathVariable("workflowInstanceId") Long sourceWorkflowInstanceId,
                              @PathVariable("refId") Long refId, @RequestBody Map v) {

    Object targetWorkflowInstanceId = v.get("targetWorkflowInstanceId");

    if (targetWorkflowInstanceId instanceof String) {

      componentService.updateToLatestReference(sourceComponentId, sourceWorkflowInstanceId, refId,
          ((Number) v.get("targetComponentId")).longValue());

    } else if (targetWorkflowInstanceId instanceof Number) {
      componentService.updateReference(sourceComponentId, sourceWorkflowInstanceId, refId,
          ((Number) v.get("targetComponentId")).longValue(), ((Number) targetWorkflowInstanceId).longValue());

    }

  }

  @GetMapping("{workflowInstanceId}/use-points")
  public List<ComponentReferenceDto> findUsePoints(@PathVariable("componentId") Long targetComponentId, @PathVariable("workflowInstanceId") Long targetWorkflowInstanceId) {
    List<ComponentReference> usePoints = componentService.findUsePoints(targetComponentId, targetWorkflowInstanceId);
    return componentConverter.convertReferences(usePoints);
  }
}
