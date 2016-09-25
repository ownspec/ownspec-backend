package com.ownspec.center.service;

import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.model.User;
import com.ownspec.center.model.component.AbstractComponent;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.repository.ComponentRepository;
import com.ownspec.center.util.OsUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static com.ownspec.center.util.OsUtils.htmlFileToPlainText;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

/**
 * Created by lyrold on 19/09/2016.
 */
@Service
@Transactional
public class ComponentService {
    private static final Logger LOG = LoggerFactory.getLogger(ComponentService.class);

    @Autowired
    private GitService gitService;

    @Autowired
    private ComponentRepository repository;

    //    @Autowired
    private User currentUser;

    public Component createComponentWith(ComponentDto source) throws UnsupportedEncodingException {
        Component component = new Component();
        component.setTitle(source.getTitle());
        File contentFile = gitService.createAndCommit(new ByteArrayResource(defaultIfEmpty(source.getContent(), "").getBytes("UTF-8")));
        component.setFilePath(contentFile.getAbsolutePath());
        return repository.save(component);
    }

    public Component updateComponentWith(ComponentDto source, Long id) throws UnsupportedEncodingException {
        Component target = requireNonNull(repository.findOne(id));
        OsUtils.mergeWithNotNullProperties(source, target);
        gitService.updateAndCommit(new ByteArrayResource(defaultIfEmpty(source.getContent(), "").getBytes("UTF-8")), target.getFilePath());
        return repository.save(target);
    }


    public void removeComponentWith(Long id) {
        Component source = requireNonNull(repository.findOne(id));
        gitService.deleteAndCommit(source.getFilePath());
        repository.delete(id);
    }


}
