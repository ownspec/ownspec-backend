package com.ownspec.center.repository.component;

import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.ComponentVersion;

import java.util.List;

/**
 * Created by lyrold on 18/09/2016.
 */
public interface ComponentVersionRepositoryCustom {

  List<ComponentVersion> findAll(Long projectId, Boolean generic, List<ComponentType> types, String query, String sort);


}
