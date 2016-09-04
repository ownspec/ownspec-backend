package com.ownspec.center.configuration;

import com.ownspec.center.service.git.RequirementGitService;
import com.ownspec.center.service.git.SpecificationGitService;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.io.IOException;

/**
 * Created by lyrold on 23/08/2016.
 */
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
public class OsCenterConfiguration {


    @Value("${git.repository.path.requirement}")
    String requirementGitRepositoryPath;

    @Value("${git.repository.path.specification}")
    String specificationGitRepositoryPath;

    @Bean
    public RequirementGitService requirementGitService() throws IOException, GitAPIException {
        Git git = git(requirementGitRepositoryPath);
        return new RequirementGitService(git);
    }

    @Bean
    public SpecificationGitService specificationGitService() throws IOException, GitAPIException {
        Git git = git(specificationGitRepositoryPath);
        return new SpecificationGitService(git);
    }

    @Bean
    @Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
    public Git git(String repositoryPath) throws IOException, GitAPIException {
        Git git = Git.init().setDirectory(new File(repositoryPath)).call();
        git.add().addFilepattern(".").call();
        git.commit().setMessage("Adding all files under directory [" + repositoryPath + "]").call();
        return git;
    }

}
