package com.ownspec.center.service.workflow;

import static com.ownspec.center.dto.ImmutableChangeDto.newChangeDto;
import static com.ownspec.center.dto.WorkflowInstanceDto.newBuilderFromWorkflowInstance;
import static com.ownspec.center.dto.WorkflowStatusDto.newBuilderFromWorkflowStatus;

import com.google.common.collect.Lists;
import com.ownspec.center.dto.ChangeDto;
import com.ownspec.center.dto.ImmutableWorkflowInstanceDto;
import com.ownspec.center.dto.user.UserDto;
import com.ownspec.center.dto.WorkflowInstanceDto;
import com.ownspec.center.dto.WorkflowStatusDto;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.WorkflowInstance;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.user.UserRepository;
import com.ownspec.center.repository.workflow.WorkflowInstanceRepository;
import com.ownspec.center.repository.workflow.WorkflowStatusRepository;
import com.ownspec.center.service.GitService;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;

/**
 * Created by nlabrot on 27/01/17.
 */
public class ChangesExtractor {

  @Autowired
  private GitService gitService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private WorkflowInstanceRepository workflowInstanceRepository;

  @Autowired
  private WorkflowStatusRepository workflowStatusRepository;

  private final ComponentVersion componentVersion;

  private List<RevCommit> commits;

  public ChangesExtractor(ComponentVersion componentVersion) {
    this.componentVersion = componentVersion;
  }

  @PostConstruct
  public void init() {
    commits = Lists.reverse(Lists.newArrayList(gitService.getHistoryFor(componentVersion.getComponent().getVcsId(), componentVersion.getFilename())));
  }


  public List<Pair<WorkflowStatus, List<ChangeDto>>> getChanges() {
    List<WorkflowStatus> workflowStatuses = workflowStatusRepository.findAllByWorkflowInstanceId(componentVersion.getWorkflowInstance().getId(), new Sort("id"));
    return getChanges(workflowStatuses);

  }


  public List<Pair<WorkflowStatus, List<ChangeDto>>> getChanges(List<WorkflowStatus> workflowStatuses) {

    List<Pair<WorkflowStatus, List<ChangeDto>>> results = new ArrayList<>();

    String firstGitReference = workflowStatuses.stream().map(WorkflowStatus::getFirstGitReference).filter(Objects::nonNull).findFirst().get();

    AtomicInteger commitIndex = new AtomicInteger();
    for (RevCommit commit : commits) {
      if (commit.name().equals(firstGitReference)) {
        break;
      }
      commitIndex.incrementAndGet();
    }

    for (WorkflowStatus workflowStatus : workflowStatuses) {
      List<ChangeDto> changeDtos = getChangeDtos(commitIndex, workflowStatus);

      results.add(Pair.of(workflowStatus, changeDtos));
    }
    return results;
  }

  public List<WorkflowInstanceDto> getWorkflowInstances() {

    AtomicInteger commitIndex = new AtomicInteger();

    List<WorkflowInstanceDto> workflowInstanceDtos = new ArrayList<>();

    for (WorkflowInstance workflowInstance : workflowInstanceRepository.findAllByComponentId(componentVersion.getId())) {

      ImmutableWorkflowInstanceDto.Builder workflowInstanceDto = newBuilderFromWorkflowInstance(workflowInstance);

      List<WorkflowStatusDto> workflowStatusDtos = new ArrayList<>();

      for (WorkflowStatus workflowStatus : workflowStatusRepository.findAllByWorkflowInstanceId(workflowInstance.getId(), new Sort("id"))) {
        List<ChangeDto> changeDtos = getChangeDtos(commitIndex, workflowStatus);
        workflowStatusDtos.add(newBuilderFromWorkflowStatus(workflowStatus)
            .changes(changeDtos)
            .build());
      }
      workflowInstanceDtos.add(workflowInstanceDto
          .workflowStatuses(workflowStatusDtos)
          .currentWorkflowStatus(workflowStatusDtos.get(workflowStatusDtos.size() - 1))
          .build());
    }

    return workflowInstanceDtos;

  }

  private List<ChangeDto> getChangeDtos(AtomicInteger commitIndex, WorkflowStatus workflowStatus) {
    List<ChangeDto> changeDtos = new ArrayList<>();

    // Workflow status has an index and we have not finished parsing commit
    // commitIndex may be = commits.size then the current workflow instance has not commit, eg. a new workflow instance is created
    if (workflowStatus.getFirstGitReference() != null && commitIndex.get() < commits.size()) {
      Validate.isTrue(commits.get(commitIndex.get()).name().equals(workflowStatus.getFirstGitReference()));

      while (commitIndex.get() < commits.size()) {
        RevCommit revCommit = commits.get(commitIndex.get());

        User commiter = userRepository.findByUsername(revCommit.getAuthorIdent().getName());

        changeDtos.add(newChangeDto()
            .date(Instant.ofEpochSecond((long) revCommit.getCommitTime()))
            .revision(revCommit.name())
            .user(UserDto.fromUser(commiter))
            .build());

        if (revCommit.name().equals(workflowStatus.getLastGitReference())) {
          commitIndex.incrementAndGet();
          break;
        }
        commitIndex.incrementAndGet();
      }
    }
    return changeDtos;
  }

}
