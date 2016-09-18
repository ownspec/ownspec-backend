package com.ownspec.center.controller;

import com.ownspec.center.model.Document;
import com.ownspec.center.model.User;
import com.ownspec.center.repository.DocumentRepository;
import com.ownspec.center.service.GitService;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.ownspec.center.util.OsUtils.htmlFileToPlainText;

/**
 * Created by lyrold on 18/09/2016.
 */
@RestController
@RequestMapping("/documents")
public class DocumentController {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    DocumentRepository repository;

    @Autowired
    GitService gitService;

    //    @Autowired
    User currentUser;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity create(@RequestBody Document Document) throws IOException, GitAPIException {

        if (Document.getContent() != null) {
            File htmlDescriptionFile = new File(
                    gitService.getGit().getRepository().getWorkTree(),
                    UUID.randomUUID() + ".html");
            LOG.info("creating Document file [{}]", htmlDescriptionFile.getAbsoluteFile());

            try (FileOutputStream outputStream = new FileOutputStream(htmlDescriptionFile)) {
                outputStream.write(Document.getContent().getBytes());
            } catch (Exception e) {
                LOG.error("An error has occurred when writing file", e);
            }
            gitService.commit(htmlDescriptionFile.getAbsolutePath());
            Document.setHtmlContentFilePath(htmlDescriptionFile.getAbsolutePath());
            Document.setContent(htmlFileToPlainText(htmlDescriptionFile.getAbsolutePath()));
        }
        Document.setAuthor(currentUser);
        repository.saveAndFlush(Document);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity update(@PathVariable("id") Long id) throws GitAPIException {
        Document requestedDocument = repository.findOne(id);
        if (requestedDocument != null) {
            requestedDocument.setContent(htmlFileToPlainText(requestedDocument.getHtmlContentFilePath()));
            gitService.commit(requestedDocument.getHtmlContentFilePath());
            repository.saveAndFlush(requestedDocument);
            return ResponseEntity.ok("Document successfully updated");
        } else {
            return ResponseEntity.badRequest().body("Cannot remove Document with id [" + id + "]; cause not found");
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") Long id) {
        Document Document = repository.findOne(id);
        if (Document != null) {
            try {
                FileUtils.forceDelete(new File(Document.getHtmlContentFilePath()));
                repository.delete(id);
                return ResponseEntity.ok("Document successfully removed");
            } catch (IOException e) {
                LOG.error("An error has occurred when removing file:", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
            }
        } else {
            return ResponseEntity.badRequest().body("Cannot remove Document with id [" + id + "]; cause not found");
        }
    }

    @RequestMapping
    public List<Document> findAll() {
        return repository.findAll();
    }

}
