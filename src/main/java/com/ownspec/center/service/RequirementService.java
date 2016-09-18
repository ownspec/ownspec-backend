package com.ownspec.center.service;

import com.ownspec.center.configuration.OsCenterConfiguration;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by lyrold on 15/09/2016.
 */
@Service
public class RequirementService {

    @Autowired
    private OsCenterConfiguration configuration;

    private Git git;

    @PostConstruct
    public void init() throws IOException, GitAPIException {
        git = configuration.git(configuration.getRequirementGitRepositoryPath());
    }

    public void commit(String filePath) throws GitAPIException {
        git.add().addFilepattern(filePath).call();
        git.commit().setMessage("Commit file [" + filePath + "]").call();
    }

    public Git getGit() {
        return git;
    }

}
