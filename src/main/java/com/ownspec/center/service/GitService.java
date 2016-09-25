package com.ownspec.center.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.ownspec.center.model.User;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


/**
 * Created by lyrold on 15/09/2016.
 */
@Service
@Slf4j
public class GitService {

    @Autowired
    private Git git;

    //    @Autowired
    private User currentUser;


    public void updateAndCommit(Resource resource, String filePath) {
        File contentFile = new File(filePath);
        LOG.info("creating Document file [{}]", contentFile.getAbsoluteFile());

        try (FileOutputStream os = new FileOutputStream(contentFile); InputStream is = resource.getInputStream()) {
            IOUtils.copy(is, os);
        } catch (Exception e) {
            LOG.error("An error has occurred when writing file", e);
            // TODO: 24/09/16 Create custom exception
            throw new RuntimeException(e);
        }
        commit(contentFile.getAbsolutePath());
    }

    public File createAndCommit(Resource resource) {
        File contentFile = new File(getGit().getRepository().getWorkTree(), UUID.randomUUID() + ".html");
        updateAndCommit(resource, contentFile.getAbsolutePath());
        return contentFile;
    }

    public void deleteAndCommit(String filePath) {
        try {
            FileUtils.forceDelete(new File(filePath));
            commit(filePath);
        } catch (Exception e) {
            // TODO: 24/09/16 Create custom exception
            throw new RuntimeException(e);
        }
    }


    public void commit(String filePath) {
        try {
            git.add().addFilepattern(filePath).call();
            git
                    .commit()
                    .setAuthor(currentUser.getFirstName(), currentUser.getUsername())
                    .setMessage("Defaut commit message for changed file [" + filePath + "]").call();
        } catch (GitAPIException e) {
            // TODO: 24/09/16 Create custom exception
            throw new RuntimeException(e);
        }
    }

    public Iterable<RevCommit> getHistoryFor(String filePath) throws GitAPIException {
        return git.log().addPath(filePath).call();
    }

    public Git getGit() {
        return git;
    }


}
