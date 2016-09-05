package com.ownspec.center.service.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

/**
 * Created by lyrold on 23/08/2016.
 */
@Service
public class RequirementGitService implements GitService {

    private Git git;

    public RequirementGitService(Git git) {
        this.git = git;
    }

    @Override
    public void commit(String filePath) throws GitAPIException {
        git.add().addFilepattern(filePath).call();
        git.commit().setMessage("Commit file [" + filePath + "]").call();
    }

    public Git getGit() {
        return git;
    }

}
