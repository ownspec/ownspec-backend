package com.ownspec.center.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ownspec.center.dto.CommentDto;
import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.model.Comment;
import com.ownspec.center.model.Revision;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.service.ComponentService;

/**
 * Created by lyrold on 20/09/2016.
 */
@RestController
@RequestMapping("/api/components")
public class ComponentController {
  @Autowired
  private ComponentService componentService;

  @RequestMapping
  public List<ComponentDto> findAll(
      @RequestParam(value = "types", required = false) ComponentType[] types,
      @RequestParam(value = "projectId", required = false) Long projectId,
      @RequestParam(value = "content", required = false, defaultValue = "false") Boolean content,
      @RequestParam(value = "workflow", required = false, defaultValue = "false") Boolean workflow,
      @RequestParam(value = "comments", required = false, defaultValue = "false") Boolean comments,
      @RequestParam(value = "references", required = false, defaultValue = "false") Boolean references
                                   ) {
    return componentService.findAll(projectId, types).stream()
                           .map(c -> toDto(c, content, workflow, comments, references))
                           .collect(Collectors.toList());
  }

  @RequestMapping("/{id}")
  public ComponentDto get(@PathVariable("id") Long id,
                          @RequestParam(value = "content", required = false, defaultValue = "false") Boolean content,
                          @RequestParam(value = "workflow", required = false, defaultValue = "false") Boolean workflow,
                          @RequestParam(value = "comments", required = false, defaultValue = "false") Boolean comments,
                          @RequestParam(value = "references", required = false, defaultValue = "false") Boolean references) {
    Component c = componentService.findOne(id);

    return toDto(c, content, workflow, comments, references);
  }


  @RequestMapping(value = "/create", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity create(@RequestBody ComponentDto source) throws IOException, GitAPIException {
    componentService.create(source);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "/{id}/workflow-statuses", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity getWorkflowStatuses(@PathVariable("id") Long id) {

    return ResponseEntity.ok(componentService.getWorkflowStatuses(id));
  }

  @RequestMapping(value = "/{id}/workflow-statuses/{nextStatus}", method = RequestMethod.POST)
  @ResponseBody
  public ComponentDto updateWorkflowStatuses(@PathVariable("id") Long id, @PathVariable("nextStatus") Status nextStatus) {
    Component c = componentService.updateStatus(id, nextStatus);
    return toDto(c, true, true, true, true);
  }

  @RequestMapping(value = "/{id}/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public ResponseEntity update(@PathVariable("id") Long id, @RequestBody ComponentDto source) {
    componentService.update(source, id);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "/{id}/update-content", method = RequestMethod.POST)
  @ResponseBody
  public ComponentDto updateContent(@PathVariable("id") Long id, @RequestBody byte[] content) throws GitAPIException, UnsupportedEncodingException {
    Map<Component, byte[]> innerDraftComponents = componentService.searchForInnerDraftComponents(content);
    Component component = componentService.updateContent(id, content);
    /*if (!innerDraftComponents.isEmpty()) {
      for (Component component : innerDraftComponents.keySet()) {
        componentService.updateContent(component, innerDraftComponents.get(component));
      }
    }*/
    return toDto(component, true, true, true, true);
  }

  @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity delete(@PathVariable("id") Long id) {
    componentService.remove(id);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "/{id}/comments", method = RequestMethod.GET)
  @ResponseBody
  public List<Comment> getComments(@PathVariable("id") Long id) {
    return componentService.getComments(id);
  }

  @RequestMapping(value = "/{id}/comments/add", method = RequestMethod.POST)
  @ResponseBody
  public ComponentDto addComment(@PathVariable("id") Long id, @RequestBody String comment) {
    componentService.addComment(id, comment);
    return toDto(id, true, true, true, true);
  }

  @RequestMapping(value = "/{id}/revisions", method = RequestMethod.GET)
  @ResponseBody
  public List<Revision> getRevisions(@PathVariable("id") Long id) {
    return componentService.getRevisionsForComponent(id);
  }

  @RequestMapping(value = "/{id}/diff", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
  @ResponseBody
  public Resource diff(@PathVariable("id") Long id, @RequestParam(value = "from", required = false) String fromRevision,
                       @RequestParam(value = "to", required = false) String toRevision) {
    return componentService.diff(id, fromRevision, toRevision);
  }

  @PostMapping(value = "/import")
  @ResponseBody
  public ResponseEntity importFrom(@RequestBody Object source) {

    return null;
  }

  @GetMapping(value = "/{id}/export")
  @ResponseBody
  public ResponseEntity export(@PathVariable Long id) {
    return componentService.export(id);
  }


  @PostMapping(value = "/{id}/assign/{userId}")
  @ResponseBody
  public ResponseEntity assignTo(
      @PathVariable("id") Long id,
      @PathVariable("userId") Long userId,
      @RequestParam(value = "autoGrantUserAccess", defaultValue = "false", required = false) boolean autoGrantUserAccess,
      @RequestParam(value = "editable", defaultValue = "false", required = false) boolean editable) {
    return componentService.assignTo(id, userId, autoGrantUserAccess, editable);
  }


  private ComponentDto toDto(Long id, boolean content, boolean workflow, boolean comments, boolean references) {
    return toDto(componentService.findOne(id), content, workflow, comments, references);
  }


  private ComponentDto toDto(Component c, boolean content, boolean workflow, boolean comments, boolean references) {
    ImmutableComponentDto.Builder builder = ComponentDto.newBuilderFromComponent(c);

    Pair<String, String> contentPair = componentService.generateContent(c);

    if (content) {
      builder.content(contentPair.getLeft());
    }

    builder.summary(contentPair.getRight());


    if (workflow) {
      builder.workflowInstances(componentService.getWorkflowStatuses(c.getId()));
    }

    if (comments) {
      builder.comments(componentService.getComments(c.getId()).stream()
                                       .map(CommentDto::createFromComment)
                                       .collect(Collectors.toList()));
    }

    if (references) {
      builder.componentReferences(componentService.getComponentReferences(c.getId()));
    }

    return builder.build();
  }

}
