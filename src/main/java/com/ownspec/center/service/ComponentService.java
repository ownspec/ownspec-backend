package com.ownspec.center.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.model.Comment;
import com.ownspec.center.model.Revision;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.CommentRepository;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.repository.workflow.WorkflowInstanceRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;

import static com.ownspec.center.util.OsUtils.mergeWithNotNullProperties;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

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
    private ComponentRepository componentRepository;

    @Autowired
    private WorkflowInstanceRepository workflowInstanceRepository;

    @Autowired
    private WorkflowStatusRepository workflowStatusRepository;

    @Autowired
    private CommentRepository commentRepository;

    //    @Autowired
    private User currentUser;

    public Component create(ComponentDto source) throws UnsupportedEncodingException {
        Component component = new Component();
        component.setTitle(source.getTitle());

        WorkflowInstance workflowInstance = new WorkflowInstance();
        workflowInstance.setComponent(component);
        workflowInstance.setCurrentStatus(Status.OPEN);
        workflowInstance.setCurrentGitReference("dd");

        WorkflowStatus workflowStatus = new WorkflowStatus();
        workflowStatus.setWorkflowComponent(component);
        workflowStatus.setWorkflowInstance(workflowInstance);
        workflowStatus.setStatus(Status.OPEN);
        workflowStatus.setGitReference("dd");

        // TODO: 27/09/16 return git reference too
        File contentFile = gitService.createAndCommit(new ByteArrayResource(defaultIfEmpty(source.getContent(), "").getBytes("UTF-8")));
        component.setFilePath(contentFile.getAbsolutePath());
        component = componentRepository.save(component);

        workflowInstance = workflowInstanceRepository.save(workflowInstance);
        workflowStatus = workflowStatusRepository.save(workflowStatus);

        return component;
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

    public List<Comment> getComments(Long componentId) {
        return commentRepository.findAllByComponentId(componentId);
    }

    public Comment addCommentForComponent(Long id, Comment comment) {
        Component target = requireNonNull(componentRepository.findOne(id));
        comment.setComponent(target);
        return commentRepository.save(comment);
    }

    public List<Revision> getRevisionsForComponent(Long id){
        return null;
    }

    public List<Component> findAll() {
        return componentRepository.findAll();
    }
}
