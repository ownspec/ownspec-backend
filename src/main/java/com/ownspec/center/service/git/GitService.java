package com.ownspec.center.service.git;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

/**
 * Created by lyrold on 23/08/2016.
 */
@Service
public interface GitService {

    void commit(String filePath) throws GitAPIException;

}
