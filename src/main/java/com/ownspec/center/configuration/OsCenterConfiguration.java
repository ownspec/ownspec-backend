package com.ownspec.center.configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.ownspec.center.model.user.User;
import com.ownspec.center.service.SecurityService;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.io.IOException;


/**
 * Created by lyrold on 23/08/2016.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "")
@EnableJpaRepositories(basePackages = "com.ownspec.center")
@EntityScan(
        basePackages = {"com.ownspec.center.model"},
        basePackageClasses = {Jsr310JpaConverters.class}
)
public class OsCenterConfiguration {

    @Value("${git.repository.path.components}")
    private String componentsGitRepositoryPath;

    @Autowired
    private SecurityService securityService;

    @Bean
    public Git git() throws IOException, GitAPIException {
        Git git = Git.init().setDirectory(new File(componentsGitRepositoryPath)).call();
        git.add().addFilepattern(".").call();
        git.commit().setMessage("Adding all files under directory [" + componentsGitRepositoryPath + "]").call();
        return git;
    }

    @Bean
    public AuditorAware auditorAware(){
        return () -> securityService.getAuthentifiedUser();
    }


    @Bean
    public Module collectionModule(){
            return new GuavaModule();
                //.registerModule(new HppcModule())
                //.registerModule(new PCollectionsModule())
    }

}
