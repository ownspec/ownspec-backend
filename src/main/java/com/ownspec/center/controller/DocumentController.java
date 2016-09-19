package com.ownspec.center.controller;

import com.ownspec.center.model.Document;
import com.ownspec.center.model.User;
import com.ownspec.center.repository.DocumentRepository;
import com.ownspec.center.service.ComponentService;
import com.ownspec.center.service.GitService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by lyrold on 18/09/2016.
 */
@RestController
@RequestMapping("/documents")
public class DocumentController {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private DocumentRepository repository;

    @Autowired
    private GitService gitService;

    @Autowired
    private ComponentService componentService;

    //    @Autowired
    private User currentUser;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity create(@RequestBody Document source) throws IOException, GitAPIException {
        return componentService.createComponentWith(source, repository);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity update(@PathVariable("id") Long id, @RequestBody Document source) throws GitAPIException {
        return componentService.updateComponentWith(source, id, repository);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") Long id) {
        return componentService.removeComponentWith(id, repository);
    }


    @RequestMapping
    public List<Document> findAll() {
        return repository.findAll();
    }

}
