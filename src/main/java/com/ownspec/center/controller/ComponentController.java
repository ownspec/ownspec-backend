package com.ownspec.center.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.primitives.Booleans;
import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.dto.ImmutableWorkflowStatusDto;
import com.ownspec.center.dto.UserDto;
import com.ownspec.center.dto.WorkflowStatusDto;
import com.ownspec.center.model.Comment;
import com.ownspec.center.model.Revision;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.ComponentService;

import static com.ownspec.center.dto.ImmutableComponentDto.newComponentDto;
import static com.ownspec.center.dto.ImmutableWorkflowStatusDto.newWorkflowStatusDto;

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
                .map(c -> {
                    return newComponentDto()
                            .id(c.getId())
                            .title(c.getTitle())
                            .type(c.getType())
                            .content(componentService.getContent(c))
                            .currentStatus(c.getCurrentStatus())
                            .createdDate(c.getCreatedDate())
                            .createdUser(UserDto.createFromUser(c.getCreatedUser()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    @RequestMapping("/{id}")
    public ComponentDto get(@PathVariable("id") Long id,
                            @RequestParam(value = "content", required = false, defaultValue = "false") Boolean content,
                            @RequestParam(value = "workflow", required = false, defaultValue = "false") Boolean workflow
    ) {
        Component c = componentService.findOne(id);

        ImmutableComponentDto.Builder builder = newComponentDto()
                .id(c.getId())
                .title(c.getTitle())
                .type(c.getType())
                .currentStatus(c.getCurrentStatus())
                .createdDate(c.getCreatedDate())
                .createdUser(UserDto.createFromUser(c.getCreatedUser()));

        if (content) {
            builder.content(componentService.getContent(c));
        }

        if (workflow) {
            builder.workflowStatuses(workflowStatusRepository.findAllByComponentId(id, new Sort("id"))
                    .stream()
                    .map(s -> newWorkflowStatusDto().id(s.getId()).status(s.getStatus()).build())
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity create(@RequestBody ComponentDto source) throws IOException, GitAPIException {
        componentService.create(source);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}/workflow-statuses", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getWorkflowStatuses(@PathVariable("id") Long id) throws GitAPIException, UnsupportedEncodingException {
        componentService.getWorkflowStatuses(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}/update", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody ComponentDto source) throws GitAPIException, UnsupportedEncodingException {
        componentService.update(source, id);
        return ResponseEntity.ok().build();
    }


    @RequestMapping(value = "/{id}/update-content", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity updateContent(@PathVariable("id") Long id, @RequestBody byte[] content) throws GitAPIException, UnsupportedEncodingException {
        componentService.updateContent(id, content);
        return ResponseEntity.ok().build();
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
    public ResponseEntity addComment(@PathVariable("id") Long id, @RequestBody Comment comment) {
        componentService.addCommentForComponent(id, comment);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}/revisions", method = RequestMethod.GET)
    @ResponseBody
    public List<Revision> getRevisions(@PathVariable("id") Long id) {
        return componentService.getRevisionsForComponent(id);
    }
}
