package com.ownspec.center.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ownspec.center.model.user.User;


/**
 * Created by lyrold on 23/08/2016.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
@EnableJpaRepositories(basePackages ="com.ownspec.center")
@EntityScan(
        basePackages = {"com.ownspec.center.model"},
        basePackageClasses = {Jsr310JpaConverters.class }
)
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

    // TODO: 29/09/16 temporary until we handle authentication
    @Bean
    public User currentUser(){
        User user = new User("foo", "bar");
        user.setFirstName("firstname");
        return user;
    }

}
