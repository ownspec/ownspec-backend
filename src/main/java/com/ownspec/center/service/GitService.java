package com.ownspec.center.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.UUID;

import com.ownspec.center.model.user.User;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;


/**
 * Created by lyrold on 15/09/2016.
 */
@Service
@Slf4j
public class GitService {

    @Autowired
    private Git git;

    public String updateAndCommit(Resource resource, String filePath, User user, String message) {
        File contentFile = new File(filePath);
        LOG.info("creating Document file [{}]", contentFile.getAbsoluteFile());

        try (FileOutputStream os = new FileOutputStream(contentFile); InputStream is = resource.getInputStream()) {
            IOUtils.copy(is, os);
        } catch (Exception e) {
            LOG.error("An error has occurred when writing file", e);
            // TODO: 24/09/16 Create custom exception
            throw new RuntimeException(e);
        }
        return commit(contentFile.getAbsolutePath(), user, message);
    }

    public Pair<File, String> createAndCommit(Resource resource, User user, String message) {
        File contentFile = new File(git.getRepository().getWorkTree(), UUID.randomUUID() + ".html");

        String hash = updateAndCommit(resource, contentFile.getAbsolutePath(), user, message);
        return Pair.of(contentFile, hash);
    }

    public void deleteAndCommit(String filePath, User user, String message) {
        try {
            FileUtils.forceDelete(new File(filePath));
            commit(filePath, user, message);
        } catch (Exception e) {
            // TODO: 24/09/16 Create custom exception
            throw new RuntimeException(e);
        }
    }


    public String commit(String filePath, User user, String message) {
        try {

            git.add().addFilepattern(relativize(filePath)).call();
            RevCommit revCommit = git
                    .commit()
                    .setAuthor(user.getUsername(), user.getEmail())
                    .setMessage(message).call();

            return revCommit.getId().name();

        } catch (GitAPIException e) {
            // TODO: 24/09/16 Create custom exception
            throw new RuntimeException(e);
        }
    }


    public Iterable<RevCommit> getHistoryFor(String filePath) {
        try {
            return git.log().addPath(relativize(filePath)).call();
        } catch (GitAPIException e) {
            // TODO: 24/09/16 Create custom exception
            throw new RuntimeException(e);
        }
    }

    public Git getGit() {
        return git;
    }


    private String relativize(String filePath) {
        return git.getRepository().getWorkTree().toPath().normalize().toAbsolutePath().relativize(Paths.get(filePath).toAbsolutePath()).toString();
    }
}
