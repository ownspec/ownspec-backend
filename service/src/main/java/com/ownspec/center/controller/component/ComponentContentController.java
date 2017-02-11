package com.ownspec.center.controller.component;

import static org.springframework.http.ResponseEntity.ok;

import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.service.UploadService;
import com.ownspec.center.service.component.ComponentConverter;
import com.ownspec.center.service.component.ComponentService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Created by nlabrot on 19/12/16.
 */
@RestController
@RequestMapping("/api/components")
public class ComponentContentController {
/*
  @Autowired
  private ComponentService componentService;

  @Autowired
  private ComponentConverter componentConverter;

  @Autowired
  private UploadService uploadService;


  @PostMapping(value = "/{id}/content")
  public ResponseEntity updateContent(@PathVariable("id") Long id, @RequestBody(required = false) byte[] content,
                                      @RequestParam(value = "uploadId", required = false) String uploadId) throws GitAPIException, UnsupportedEncodingException {

    Optional<Resource> optional;

    if (uploadId != null) {
      optional = uploadService.findResource(uploadId);
    } else {
      optional = Optional.of(new ByteArrayResource(content));
    }

    return optional.map(r -> ok(componentConverter.toDto(componentService.updateContent(id, r), false, true, true, true)))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
  }

  @GetMapping(value = "/{id}/content")
  public Resource getContent(@PathVariable("id") Long id) throws GitAPIException, UnsupportedEncodingException {
    Component component = componentService.findOne(id);
    return componentService.getHeadRawContent(component);
  }


  @RequestMapping("/{componentId}/versions/{workflowInstanceId}/content")
  public Resource getContentForVersion(@PathVariable("componentId") Long componentId, @PathVariable("workflowInstanceId") Long workflowInstanceId) throws GitAPIException, UnsupportedEncodingException {
    WorkflowInstance workflowInstance = componentService.findByComponentIdAndWorkflowId(componentId, workflowInstanceId);
    Component component = componentService.findOne(componentId);

    if (component.getType() != ComponentType.RESOURCE) {
      return new ByteArrayResource(componentService.generateContent(component, workflowInstance).getLeft().getBytes(StandardCharsets.UTF_8));
    } else {
      return componentService.getHeadRawContent(component);
    }
  }*/
}
