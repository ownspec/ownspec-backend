package com.ownspec.center.controller.component;

import static com.ownspec.center.util.RequestFilterMode.FAVORITES_ONLY;
import static com.ownspec.center.util.RequestFilterMode.LAST_VISITED_ONLY;
import static org.springframework.http.ResponseEntity.ok;
import static sun.security.krb5.Confounder.longValue;

import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.dto.ComponentVersionDto;
import com.ownspec.center.model.Comment;
import com.ownspec.center.model.Revision;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.service.CommentService;
import com.ownspec.center.service.UploadService;
import com.ownspec.center.service.component.ComponentConverter;
import com.ownspec.center.service.component.ComponentService;
import com.ownspec.center.service.component.ComponentTagService;
import com.ownspec.center.util.RequestFilterMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lyrold on 20/09/2016.
 */
@RestController
@RequestMapping("/api/components")
public class ComponentController {
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


  @RequestMapping
  public List<ComponentDto> findAll(
      @RequestParam(value = "types", required = false) ComponentType[] types,
      @RequestParam(value = "tags", required = false) String[] tags,
      @RequestParam(value = "projectId", required = false) Long projectId,
      @RequestParam(value = "content", required = false, defaultValue = "false") Boolean content,
      @RequestParam(value = "workflow", required = false, defaultValue = "false") Boolean workflow,
      @RequestParam(value = "comments", required = false, defaultValue = "false") Boolean comments,
      @RequestParam(value = "references", required = false, defaultValue = "false") Boolean references,
      @RequestParam(value = "mode", required = false) RequestFilterMode mode,
      @RequestParam(value = "q", required = false) String query

  ) {

    List<Component> components = LAST_VISITED_ONLY.equals(mode) ? componentService.getLastVisited(types[0]) :
        FAVORITES_ONLY.equals(mode) ? componentService.getFavorites(types[0]) :
            componentService.findAll(projectId, types, tags, query);

    return components.stream()
        .map(c -> componentConverter.toDto(c, content, workflow, comments, references))
        .collect(Collectors.toList());
  }

  @RequestMapping("/{id}")
  public ComponentDto get(@PathVariable("id") Long id,
                          @RequestParam(value = "content", required = false, defaultValue = "false") Boolean content,
                          @RequestParam(value = "workflow", required = false, defaultValue = "false") Boolean workflow,
                          @RequestParam(value = "comments", required = false, defaultValue = "false") Boolean comments,
                          @RequestParam(value = "references", required = false, defaultValue = "false") Boolean references) {
    Component c = componentService.findOne(id);

    return componentConverter.toDto(c, content, workflow, comments, references);
  }

  @RequestMapping("/{componentId}/versions")
  public List<ComponentVersionDto> findAllVersion(@PathVariable("componentId") Long componentId,
                                                  @RequestParam(value = "statuses", required = false, defaultValue = "false") Boolean statuses) {

    Component component = componentService.findOne(componentId);

    return componentService.findAllWorkflow(componentId).stream()
        .map(w -> componentConverter.toComponentVersionDto(component, w, false, true, true))
        .collect(Collectors.toList());
  }


  @RequestMapping("/{componentId}/versions/{version}")
  public ComponentVersionDto getByVersion(@PathVariable("componentId") Long componentId, @PathVariable("version") Long workflowInstanceId,
                                          @RequestParam(value = "content", required = false, defaultValue = "false") Boolean content,
                                          @RequestParam(value = "workflow", required = false, defaultValue = "false") Boolean workflow,
                                          @RequestParam(value = "comments", required = false, defaultValue = "false") Boolean comments,
                                          @RequestParam(value = "references", required = false, defaultValue = "false") Boolean references) {
    WorkflowInstance workflowInstance = componentService.findByComponentIdAndWorkflowId(componentId, workflowInstanceId);
    Component c = componentService.findOne(componentId);
    return componentConverter.toComponentVersionDto(c, workflowInstance, false, true, true);
  }


  @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity create(@RequestBody ComponentDto source) throws IOException, GitAPIException {
    Component component = componentService.create(source);
    return ok(componentConverter.toDto(component, false, false, false, false));
  }

  @GetMapping("/{id}/workflow-statuses")
  public ResponseEntity getWorkflowStatuses(@PathVariable("id") Long id) {

    return ok(componentService.getWorkflowInstances(id));
  }

  @PostMapping("/{id}/workflow-statuses/update/{nextStatus}")
  public ComponentDto updateWorkflowStatuses(@PathVariable("id") Long id, @PathVariable("nextStatus") Status nextStatus) {
    Component c = componentService.updateStatus(id, nextStatus);
    return componentConverter.toDto(c, true, true, true, true);
  }

  @PostMapping("/{id}/workflow-statuses/new")
  public ComponentDto newWorkflowInstance(@PathVariable("id") Long id) {
    Component c = componentService.newWorkflowInstance(id);
    return componentConverter.toDto(c, true, true, true, true);
  }

  @RequestMapping(value = "/{id}/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity update(@PathVariable("id") Long id, @RequestBody ComponentDto source) {
    componentService.update(source, id);
    return ok().build();
  }


  @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
  public ResponseEntity delete(@PathVariable("id") Long id) {
    componentService.remove(id);
    return ok().build();
  }

  @RequestMapping(value = "/{id}/comments", method = RequestMethod.GET)
  public List<Comment> getComments(@PathVariable("id") Long id) {
    return commentService.getComments(id);
  }

  @RequestMapping(value = "/{id}/comments/add", method = RequestMethod.POST)
  public ComponentDto addComment(@PathVariable("id") Long id, @RequestBody String comment) {
    commentService.addComment(id, comment);
    return componentConverter.toDto(id, true, true, true, true);
  }

  @RequestMapping(value = "/{id}/revisions", method = RequestMethod.GET)
  public List<Revision> getRevisions(@PathVariable("id") Long id) {
    return componentService.getRevisionsForComponent(id);
  }

  @RequestMapping(value = "/{id}/diff", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
  public Resource diff(@PathVariable("id") Long id, @RequestParam(value = "from", required = false) String fromRevision,
                       @RequestParam(value = "to", required = false) String toRevision) {
    return componentService.diff(id, fromRevision, toRevision);
  }

  @PostMapping(value = "/import")
  public ResponseEntity importFrom(@RequestBody Object source) {

    return null;
  }


//
//  @GetMapping(value = "/{id}/export")
//  @ResponseBody
//  public ResponseEntity export(@PathVariable Long id) {
//    return componentService.export(id);
//  }


  @PostMapping(value = "/{id}/assign/{userId}")
  public ResponseEntity assignTo(
      @PathVariable("id") Long id,
      @PathVariable("userId") Long userId,
      @RequestParam(value = "autoGrantUserAccess", defaultValue = "false", required = false) boolean autoGrantUserAccess,
      @RequestParam(value = "editable", defaultValue = "false", required = false) boolean editable) {
    return componentService.assignTo(id, userId, autoGrantUserAccess, editable);
  }


  @RequestMapping(value = "/{id}/compose", method = RequestMethod.GET)
  public ResponseEntity<Resource> print(@PathVariable("id") Long id) throws IOException {

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_PDF)
        .header("Content-Disposition", "attachment; filename=\"filename.pdf\"")
        .body(componentService.composePdf(id));
  }

  @PostMapping("/{id}/addVisit")
  public ResponseEntity addVisit(@PathVariable("id") Long id) {
    return componentService.addVisit(id);
  }


  @PostMapping("/{id}/tags")
  public ResponseEntity tagsComponent(@PathVariable("id") Long id, @RequestBody List<String> tags) {
    componentTagService.tagsComponent(id, tags);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{componentId}/versions/{workflowInstanceId}/references/{refId}")
  public void updateReference(@PathVariable("componentId") Long sourceComponentId, @PathVariable("workflowInstanceId") Long sourceWorkflowInstanceId,
                              @PathVariable("refId") Long refId, @RequestBody Map v) {

    Object targetWorkflowInstanceId = v.get("targetWorkflowInstanceId");

    if (targetWorkflowInstanceId instanceof String){

      componentService.updateToLatestReference(sourceComponentId, sourceWorkflowInstanceId, refId ,
          ((Number)v.get("targetComponentId")).longValue());

    }else if (targetWorkflowInstanceId instanceof Number){
      componentService.updateReference(sourceComponentId, sourceWorkflowInstanceId, refId ,
          ((Number)v.get("targetComponentId")).longValue() , ((Number)targetWorkflowInstanceId).longValue());

    }


  }


}
