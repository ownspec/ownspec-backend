package com.ownspec.center.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;
import org.springframework.data.domain.Sort;

import com.ownspec.center.AbstractTest;
import com.ownspec.center.dto.ComponentDto;
import com.ownspec.center.dto.ImmutableComponentDto;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowStatus;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

/**
 * Created by nlabrot on 01/10/16.
 */
public class ComponentServiceTest extends AbstractTest {



    @Test
    public void name() throws Exception {

        ImmutableComponentDto componentDto = ImmutableComponentDto.newComponentDto()
                .title("test")
                .type(ComponentType.COMPONENT)
                .content("test1")
                .build();

        Component component = componentService.create(componentDto);

        componentService.updateContent(component.getId() , "test2".getBytes(UTF_8));
        componentService.updateContent(component.getId() , "test3".getBytes(UTF_8));

        componentService.updateStatus(component.getId() , Status.OPEN);


        List<WorkflowStatus> workflowStatuses = workflowStatusRepository.findAllByComponentId(component.getId(), new Sort("id"));





        Iterable<RevCommit> historyFor = gitService.getHistoryFor(component.getFilePath());

        for (RevCommit revCommit : historyFor) {
            String name = revCommit.name();
            System.out.println(revCommit);

        }


        // fetch git history
        for (int i = 0; i < workflowStatuses.size(); i++) {

            String currentGitReference = workflowStatuses.get(i).getGitReference();
            String lastGitReference = null;

            if (i + 1 < workflowStatuses.size()) {
                lastGitReference = workflowStatuses.get(i + 1).getGitReference();
            }

            // Retrive file history

        }


    }
}
