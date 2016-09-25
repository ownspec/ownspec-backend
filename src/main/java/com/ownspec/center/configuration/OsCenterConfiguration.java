package com.ownspec.center.configuration;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.io.IOException;

/**
 * Created by lyrold on 23/08/2016.
 */
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableJpaAuditing
public class OsCenterConfiguration {

    @Value("${git.repository.path.components}")
    private String componentsGitRepositoryPath;

    @Bean
    public Git git() throws IOException, GitAPIException {
        Git git = Git.init().setDirectory(new File(componentsGitRepositoryPath)).call();
        git.add().addFilepattern(".").call();
        git.commit().setMessage("Adding all files under directory [" + componentsGitRepositoryPath + "]").call();
        return git;
    }

}
