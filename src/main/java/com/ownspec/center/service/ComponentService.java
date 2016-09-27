package com.ownspec.center.service;

import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.model.Comment;
import com.ownspec.center.model.Revision;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.user.User;
import com.ownspec.center.repository.CommentRepository;
import com.ownspec.center.repository.component.ComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.ownspec.center.util.OsUtils.mergeWithNotNullProperties;
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
    private ComponentRepository componentRepository;

    //    @Autowired
    private User currentUser;

    public Component createComponent(ComponentDto source) throws UnsupportedEncodingException {
        Component component = new Component();
        component.setTitle(source.getTitle());
        File contentFile = gitService.createAndCommit(new ByteArrayResource(defaultIfEmpty(source.getContent(), "").getBytes("UTF-8")));
        component.setFilePath(contentFile.getAbsolutePath());
        return componentRepository.save(component);
    }

    public Component updateComponent(ComponentDto source, Long id) throws UnsupportedEncodingException {
        Component target = requireNonNull(componentRepository.findOne(id));
        mergeWithNotNullProperties(source, target);
        gitService.updateAndCommit(new ByteArrayResource(defaultIfEmpty(source.getContent(), "").getBytes("UTF-8")), target.getFilePath());
        return componentRepository.save(target);
    }


    public void removeComponent(Long id) {
        Component target = requireNonNull(componentRepository.findOne(id));
        gitService.deleteAndCommit(target.getFilePath());
        componentRepository.delete(id);
    }

    public List<Comment> getCommentsForComponent(Long id) {
        Component target = requireNonNull(componentRepository.findOne(id));
        return target.getComments();
    }

    public Component addCommentForComponent(Long id, Comment comment) {
        Component target = requireNonNull(componentRepository.findOne(id));
        target.getComments().add(comment);
        return componentRepository.save(target);
    }

    public List<Revision> getRevisionsForComponent(Long id){
        return null;
    }
}
