package com.ownspec.center.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ownspec.center.dto.UserDto;
import com.ownspec.center.model.Comment;
import com.ownspec.center.model.Revision;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.ComponentService;

import static com.ownspec.center.dto.ImmutableComponentDto.newComponentDto;

/**
 * Created by lyrold on 20/09/2016.
 */
@RestController
@RequestMapping("/api/components")
public class ComponentController {
  @Autowired
  private ComponentService componentService;

  @Autowired
  private WorkflowStatusRepository workflowStatusRepository;

  @RequestMapping
  public List<ComponentDto> findAll(
      @RequestParam(value = "types", required = false) ComponentType[] types,
      @RequestParam(value = "projectId", required = false) Long projectId
  ) {
    return componentService.findAll(projectId, types).stream()
        .map(c -> toDto(c, true, false, false))
        .collect(Collectors.toList());
  }

  @RequestMapping("/{id}")
  public ComponentDto get(@PathVariable("id") Long id,
                          @RequestParam(value = "content", required = false, defaultValue = "false") Boolean content,
                          @RequestParam(value = "workflow", required = false, defaultValue = "false") Boolean workflow,
                          @RequestParam(value = "comments", required = false, defaultValue = "false") Boolean comments) {
    Component c = componentService.findOne(id);

    return toDto(c, content, workflow, comments);
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
  public ComponentDto updatetWorkflowStatuses(@PathVariable("id") Long id, @PathVariable("nextStatus") Status nextStatus) {
    Component c = componentService.updateStatus(id, nextStatus);
    return toDto(c, true, true, true);
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
    return toDto(component, true, true, true);
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
    return toDto(id, true, true, true);
  }

  @RequestMapping(value = "/{id}/revisions", method = RequestMethod.GET)
  @ResponseBody
  public List<Revision> getRevisions(@PathVariable("id") Long id) {
    return componentService.getRevisionsForComponent(id);
  }

  @PostMapping(value = "/import")
  @ResponseBody
  public ResponseEntity importFrom(@RequestBody Object source) {

    return null;
  }

  @GetMapping(value = "/{id}/export")
  @ResponseBody
  public ResponseEntity export(@RequestBody Long id) {
    return null;
  }

  private ComponentDto toDto(Long id, boolean content, boolean workflow, Boolean comments) {
    return toDto(componentService.findOne(id), content, workflow, comments);
  }


  private ComponentDto toDto(Component c, boolean content, boolean workflow, Boolean comments) {
    ImmutableComponentDto.Builder builder = newComponentDto()
        .id(c.getId())
        .projectId(c.getProject() != null ? c.getProject().getId() : null)
        .title(c.getTitle())
        .type(c.getType())
        .currentStatus(c.getCurrentStatus())
        .createdDate(c.getCreatedDate())
        .createdUser(UserDto.createFromUser(c.getCreatedUser()));

    if (content) {
      builder.content(componentService.getContent(c));
    }

    if (workflow) {
      builder.workflowStatuses(componentService.getWorkflowStatuses(c.getId()));
    }

    if (comments) {
      builder.comments(componentService.getComments(c.getId()).stream()
          .map(CommentDto::createFromComment)
          .collect(Collectors.toList()));
    }

    return builder.build();
  }

}
