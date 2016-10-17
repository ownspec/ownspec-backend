package com.ownspec.center.service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.ownspec.center.dto.ChangeDto;
import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.dto.ImmutableChangeDto;
import com.ownspec.center.dto.ImmutableWorkflowStatusDto;
import com.ownspec.center.dto.UserDto;
import com.ownspec.center.dto.WorkflowStatusDto;
import com.ownspec.center.model.Comment;
import com.ownspec.center.model.Project;
import com.ownspec.center.model.Revision;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.QComponent;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.CommentRepository;
import com.ownspec.center.repository.ProjectRepository;
import com.ownspec.center.repository.UserRepository;
import com.ownspec.center.repository.component.ComponentRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;

import static com.ownspec.center.dto.ImmutableChangeDto.newChangeDto;
import static com.ownspec.center.dto.WorkflowStatusDto.newBuilderFromWorkflowStatus;
import static com.ownspec.center.model.component.QComponent.component;
import static com.ownspec.center.model.user.QUser.user;
import static com.ownspec.center.model.workflow.QWorkflowStatus.workflowStatus;
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

  @Autowired
  private SecurityService securityService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProjectRepository projectRepository;


  public List<Component> findAll(Long projectId, ComponentType[] types) {

    List<Predicate> predicates = new ArrayList<>();

    if (projectId != null) {
      predicates.add(component.project.id.eq(projectId));
    } else {
      predicates.add(component.project.id.isNull());
    }

    if (types != null) {
      predicates.add(component.type.in(types));
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
        new ByteArrayResource(defaultIfEmpty(source.getContent(), "").getBytes(UTF_8)), securityService.getAuthentifiedUser(), "");

    Project project = null;

    if (source.getProjectId() != null) {
      project = projectRepository.findOne(source.getProjectId());
    }

    Component component = new Component();
    component.setProject(project);
    component.setTitle(source.getTitle());
    component.setType(source.getType());

    WorkflowStatus workflowStatus = new WorkflowStatus();
    workflowStatus.setComponent(component);
    workflowStatus.setStatus(Status.OPEN);
    workflowStatus.setFirstGitReference(pair.getRight());
    workflowStatus.setLastGitReference(pair.getRight());

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

    if (source.getContent() != null) {
      updateContent(target, source.getContent().getBytes(UTF_8));
    }

    return componentRepository.save(target);
  }

  public Component updateStatus(Long id, Status nextStatus) {
    Component component = requireNonNull(componentRepository.findOne(id));

    WorkflowStatus workflowStatus = new WorkflowStatus();
    workflowStatus.setComponent(component);
    workflowStatus.setStatus(nextStatus);

    component.setCurrentStatus(nextStatus);
    component.setCurrentGitReference(null);

    component = componentRepository.save(component);
    workflowStatus = workflowStatusRepository.save(workflowStatus);

    return component;
  }

  public Component updateContent(Long id, byte[] content) {
    return updateContent(requireNonNull(componentRepository.findOne(id)), content);
  }

  public Component updateContent(Component component, byte[] content) {
    WorkflowStatus workflowStatus = workflowStatusRepository.findLatestStatusByComponentId(component.getId());

    if (!workflowStatus.getStatus().isEditable()) {
      // TODO: 28/09/16 better exception
      throw new RuntimeException("Cannot edit");
    }

    String hash = gitService.updateAndCommit(new ByteArrayResource(content), component.getFilePath(), securityService.getAuthentifiedUser(), "");

    if (workflowStatus.getFirstGitReference() == null) {
      workflowStatus.setFirstGitReference(hash);
      workflowStatus.setLastGitReference(hash);
    } else {
      workflowStatus.setLastGitReference(hash);
    }

    workflowStatusRepository.save(workflowStatus);

    component.setCurrentGitReference(hash);
    componentRepository.save(component);

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
    gitService.deleteAndCommit(target.getFilePath(), securityService.getAuthentifiedUser(), "");
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

  public List<WorkflowStatusDto> getWorkflowStatuses(Long id) {
    Component component = findOne(id);

    List<WorkflowStatus> workflowStatuses = workflowStatusRepository.findAllByComponentId(component.getId(), new Sort("id"));

    List<RevCommit> commits = Lists.reverse(Lists.newArrayList(gitService.getHistoryFor(component.getFilePath())));
    int commitIndex = 0;

    List<WorkflowStatusDto> workflowStatusDtos = new ArrayList<>();

    for (WorkflowStatus workflowStatus : workflowStatuses) {

      List<ChangeDto> changeDtos = new ArrayList<>();

      if (workflowStatus.getFirstGitReference() != null) {


        Validate.isTrue(commits.get(commitIndex).name().equals(workflowStatus.getFirstGitReference()));

        while (commitIndex < commits.size()) {
          RevCommit revCommit = commits.get(commitIndex);

          User commiter = userRepository.findByUsername(revCommit.getAuthorIdent().getName());

          changeDtos.add(newChangeDto()
              .date(Instant.ofEpochSecond((long) revCommit.getCommitTime()))
              .revision(revCommit.name())
              .user(UserDto.createFromUser(commiter))
              .build());

          if (revCommit.name().equals(workflowStatus.getLastGitReference())) {
            commitIndex++;
            break;
          }
          commitIndex++;
        }
      }

      ImmutableWorkflowStatusDto.Builder builder = newBuilderFromWorkflowStatus(workflowStatus);
      builder.changes(changeDtos);

      workflowStatusDtos.add(builder.build());
    }

    return workflowStatusDtos;

  }
}
