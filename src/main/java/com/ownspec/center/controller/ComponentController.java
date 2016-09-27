package com.ownspec.center.controller;

import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.model.Comment;
import com.ownspec.center.model.Revision;
import com.ownspec.center.service.ComponentService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by lyrold on 20/09/2016.
 */
@RestController
@RequestMapping("/api/components")
public class ComponentController {
    @Autowired
    private ComponentService componentService;


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity create(@RequestBody ComponentDto source) throws IOException, GitAPIException {
        componentService.createComponent(source);
        return ResponseEntity.ok().build();
    }


    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody ComponentDto source) throws GitAPIException, UnsupportedEncodingException {
        componentService.updateComponent(source, id);
        return ResponseEntity.ok().build();
    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") Long id) {
        componentService.removeComponent(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}/comments", method = RequestMethod.GET)
    @ResponseBody
    public List<Comment> getComments(@PathVariable("id") Long id) {
        return componentService.getCommentsForComponent(id);
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
