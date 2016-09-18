package com.ownspec.center.controller;

import com.ownspec.center.model.Requirement;
import com.ownspec.center.model.User;
import com.ownspec.center.repository.RequirementRepository;
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
 * Created by lyrold on 23/08/2016.
 */
@RestController
@RequestMapping("/requirements")
public class RequirementController {

    private static final Logger LOG = LoggerFactory.getLogger(RequirementController.class);

    @Autowired
    RequirementRepository repository;

    @Autowired
    GitService gitService;

    //    @Autowired
    User currentUser;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity create(@RequestBody Requirement Requirement) throws IOException, GitAPIException {

        if (Requirement.getContent() != null) {
            File htmlDescriptionFile = new File(
                    gitService.getGit().getRepository().getWorkTree(),
                    UUID.randomUUID() + ".html");
            LOG.info("creating Requirement file [{}]", htmlDescriptionFile.getAbsoluteFile());

            try (FileOutputStream outputStream = new FileOutputStream(htmlDescriptionFile)) {
                outputStream.write(Requirement.getContent().getBytes());
            } catch (Exception e) {
                LOG.error("An error has occurred when writing file", e);
            }
            gitService.commit(htmlDescriptionFile.getAbsolutePath());
            Requirement.setHtmlContentFilePath(htmlDescriptionFile.getAbsolutePath());
            Requirement.setContent(htmlFileToPlainText(htmlDescriptionFile.getAbsolutePath()));
        }
        Requirement.setAuthor(currentUser);
        repository.saveAndFlush(Requirement);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity update(@PathVariable("id") Long id) throws GitAPIException {
        Requirement requestedRequirement = repository.findOne(id);
        if (requestedRequirement != null) {
            requestedRequirement.setContent(htmlFileToPlainText(requestedRequirement.getHtmlContentFilePath()));
            gitService.commit(requestedRequirement.getHtmlContentFilePath());
            repository.saveAndFlush(requestedRequirement);
            return ResponseEntity.ok("Requirement successfully updated");
        } else {
            return ResponseEntity.badRequest().body("Cannot remove Requirement with id [" + id + "]; cause not found");
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") Long id) {
        Requirement Requirement = repository.findOne(id);
        if (Requirement != null) {
            try {
                FileUtils.forceDelete(new File(Requirement.getHtmlContentFilePath()));
                repository.delete(id);
                return ResponseEntity.ok("Requirement successfully removed");
            } catch (IOException e) {
                LOG.error("An error has occurred when removing file:", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
            }
        } else {
            return ResponseEntity.badRequest().body("Cannot remove Requirement with id [" + id + "]; cause not found");
        }
    }

    @RequestMapping
    public List<Requirement> findAll() {
        return repository.findAll();
    }

}
