package com.ownspec.center.controller;

import com.ownspec.center.model.Requirement;
import com.ownspec.center.model.User;
import com.ownspec.center.repository.RequirementRepository;
import com.ownspec.center.service.RequirementService;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RequirementsController {

    private static final Logger LOG = LoggerFactory.getLogger(RequirementsController.class);

    @Autowired
    RequirementRepository repository;

    @Autowired
    RequirementService requirementService;

    //    @Autowired
    User currentUser;

    @RequestMapping("/requirements")
    public List<Requirement> getAll() {
        return repository.findAll();
    }

    @RequestMapping(value = "/requirements/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity create(@RequestBody Requirement requirement) throws IOException, GitAPIException {

        if (requirement.getHtmlDescriptionContent() != null) {
            File htmlDescriptionFile = new File(
                    requirementService.getGit().getRepository().getWorkTree(),
                    UUID.randomUUID() + ".html");
            LOG.info("creating requirement file [{}]", htmlDescriptionFile.getAbsoluteFile());

            try (FileOutputStream outputStream = new FileOutputStream(htmlDescriptionFile)) {
                outputStream.write(requirement.getHtmlDescriptionContent().getBytes());
            } catch (Exception e) {
                LOG.error("An error has occurred when writing file", e);
            }
            requirementService.commit(htmlDescriptionFile.getAbsolutePath());
            requirement.setHtmlDescriptionPath(htmlDescriptionFile.getAbsolutePath());
            requirement.setDescription(htmlFileToPlainText(htmlDescriptionFile.getAbsolutePath()));
        }
        requirement.setAuthor(currentUser);
        repository.saveAndFlush(requirement);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/requirements/update/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public String update(@PathVariable("id") Long id) throws GitAPIException {
        Requirement requestedRequirement = repository.findOne(id);
        if (requestedRequirement != null) {
            requestedRequirement.setDescription(htmlFileToPlainText(requestedRequirement.getHtmlDescriptionPath()));
            requirementService.commit(requestedRequirement.getHtmlDescriptionPath());
            repository.saveAndFlush(requestedRequirement);
            return "Requirement successfully updated";
        } else {
            return "Cannot remove requirement with id [" + id + "]; cause not found";
        }

    }

    @RequestMapping(value = "/requirements/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public String delete(@PathVariable("id") Long id) {
        Requirement requirement = repository.findOne(id);
        if (requirement != null) {
            try {
                FileUtils.forceDelete(new File(requirement.getHtmlDescriptionPath()));
                repository.delete(id);
                return "Requirement successfully removed";
            } catch (IOException e) {
                LOG.error("An error has occurred when removing file:", e);
                return "";
            }
        } else {
            return "Cannot remove requirement with id [" + id + "]; cause not found";
        }

    }
}
