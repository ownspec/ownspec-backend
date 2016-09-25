package com.ownspec.center.service;

import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.model.User;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.util.OsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.UnsupportedEncodingException;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

/**
 * Created by lyrold on 19/09/2016.
 */
@Service
@Transactional
public class ComponentService {
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
