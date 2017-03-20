package com.ownspec.center.controller.component;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    Component component = componentService.findOneToDto(id);
    return componentService.getHeadRawContent(component);
  }


  @RequestMapping("/{componentId}/versions/{workflowInstanceId}/content")
  public Resource getContentForVersion(@PathVariable("componentId") Long componentId, @PathVariable("workflowInstanceId") Long workflowInstanceId) throws GitAPIException, UnsupportedEncodingException {
    WorkflowInstance workflowInstance = componentService.findByComponentIdAndWorkflowId(componentId, workflowInstanceId);
    Component component = componentService.findOneToDto(componentId);

    if (component.getType() != ComponentType.RESOURCE) {
      return new ByteArrayResource(componentService.generateContent(component, workflowInstance).getLeft().getBytes(StandardCharsets.UTF_8));
    } else {
      return componentService.getHeadRawContent(component);
    }
  }*/
}
