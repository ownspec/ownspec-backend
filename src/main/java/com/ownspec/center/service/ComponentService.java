package com.ownspec.center.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.model.Comment;
import com.ownspec.center.model.Revision;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.QComponent;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.CommentRepository;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.ownspec.center.util.OsUtils.mergeWithNotNullProperties;
import static com.querydsl.core.types.dsl.Expressions.booleanOperation;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

/**
 * Created by lyrold on 19/09/2016.
 */
@Service
@Transactional
@Slf4j
public class ComponentService {

    @Autowired
    private GitService gitService;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private WorkflowStatusRepository workflowStatusRepository;

    @Autowired
    private CommentRepository commentRepository;

    public List<Component> findAll(Long projectId, ComponentType[] types) {

        List<Predicate> predicates = new ArrayList<>();

        if (projectId != null) {
            predicates.add(QComponent.component.project.id.eq(projectId));
        } else {
            predicates.add(QComponent.component.project.id.isNull());
        }

        if (types != null) {
            predicates.add(QComponent.component.type.in(types));
        }

        if (!predicates.isEmpty()) {
            return Lists.newArrayList(componentRepository.findAll(predicates.size() == 1 ? predicates.get(0) : booleanOperation(Ops.AND, predicates.toArray(new Predicate[0]))));
        } else {
            return componentRepository.findAll();
        }
    }

    public Component create(ComponentDto source) {
        // TODO: 27/09/16 handle case if transaction fails
        Pair<File, String> pair = gitService.createAndCommit(
                new ByteArrayResource(defaultIfEmpty(source.getContent(), "").getBytes(UTF_8)));

        Component component = new Component();
        component.setTitle(source.getTitle());

        WorkflowStatus workflowStatus = new WorkflowStatus();
        workflowStatus.setComponent(component);
        workflowStatus.setStatus(Status.OPEN);

        component.setCurrentStatus(Status.OPEN);
        component.setCurrentGitReference(pair.getRight());
        component.setFilePath(pair.getLeft().getAbsolutePath());

        component = componentRepository.save(component);

        workflowStatus = workflowStatusRepository.save(workflowStatus);

        return component;
    }

    public Component findOne(Long id) {
        return componentRepository.findOne(id);
    }


    public Component update(ComponentDto source, Long id) {
        Component target = requireNonNull(componentRepository.findOne(id));
        mergeWithNotNullProperties(source, target);
        gitService.updateAndCommit(new ByteArrayResource(defaultIfEmpty(source.getContent(), "").getBytes(UTF_8)), target.getFilePath());
        return componentRepository.save(target);
    }

    public void updateStatus(Long id, Status nextStatus) {
        Component component = requireNonNull(componentRepository.findOne(id));

        WorkflowStatus workflowStatus = new WorkflowStatus();
        workflowStatus.setComponent(component);
        workflowStatus.setStatus(nextStatus);

        component.setCurrentStatus(nextStatus);

        component = componentRepository.save(component);
        workflowStatus = workflowStatusRepository.save(workflowStatus);
    }

    public Component updateContent(Long id, byte[] content) {
        Component component = requireNonNull(componentRepository.findOne(id));

        if (!component.getCurrentStatus().isEditable()) {
            // TODO: 28/09/16 better exception
            throw new RuntimeException("Cannot edit");
        }

        componentRepository.save(component);

        gitService.updateAndCommit(new ByteArrayResource(content), component.getFilePath());

        return component;
    }

    public String getContent(Component c) {
        try {
            if (c.getFilePath() != null) {

                return FileUtils.readFileToString(new File(c.getFilePath()), "UTF-8");
            } else {
                return "";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(Long id) {
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

    public List<Revision> getRevisionsForComponent(Long id) {
        return null;
    }

    public void getWorkflowStatuses(Long id) {

    }
}
