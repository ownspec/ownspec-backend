package com.ownspec.center.repository.component;

import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.model.user.User;
import com.ownspec.center.model.workflow.Status;

import java.util.List;

/**
 * Created by lyrold on 18/09/2016.
 */
public interface ComponentVersionRepositoryCustom {

  List<User> findAllAssignee(Long projectId, Boolean generic, List<ComponentType> types, String query, String sort);

  List<ComponentVersion> findAll(Long projectId, Boolean generic, List<ComponentType> types,
                                 String query,
                                 Status status,
                                 User assignee,
                                 String sort);


}
