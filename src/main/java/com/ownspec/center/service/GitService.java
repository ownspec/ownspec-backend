package com.ownspec.center.service;

import com.ownspec.center.model.User;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lyrold on 15/09/2016.
 */
@Service
public class GitService {

    @Autowired
    private Git git;

    //    @Autowired
    private User currentUser;

    public void commit(String filePath) throws GitAPIException {
        git.add().addFilepattern(filePath).call();
        git
                .commit()
                .setAuthor(currentUser.getFirstName(), currentUser.getUsername())
                .setMessage("Defaut commit message for changed file [" + filePath + "]").call();
    }

    public Iterable<RevCommit> getHistoryFor(String filePath) throws GitAPIException {
        return git.log().addPath(filePath).call();
    }

    public Git getGit() {
        return git;
    }


}
