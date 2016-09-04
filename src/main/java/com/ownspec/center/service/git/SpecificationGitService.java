package com.ownspec.center.service.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

/**
 * Created by lyrold on 23/08/2016.
 */
@Service
public class SpecificationGitService implements GitService {

    Git git;

    public SpecificationGitService(Git git) {
        this.git = git;
    }

    @Override
    public void commit(String filePath) throws GitAPIException {
        git.add().addFilepattern(filePath).call();
        git.commit().setMessage("Commit file [" + filePath + "]").call();

    }
}
