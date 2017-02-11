package com.ownspec.center.service.component;

import com.ownspec.center.model.Tag;
import com.ownspec.center.model.component.ComponentTag;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.repository.component.ComponentVersionRepository;
import com.ownspec.center.repository.tag.ComponentTagRepository;
import com.ownspec.center.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by nlabrot on 30/12/16.
 */
@Service
@Transactional
public class ComponentTagService {

  @Autowired
  private TagService tagService;

  @Autowired
  private ComponentTagRepository componentTagRepository;


  @Autowired
  private ComponentVersionRepository componentVersionRepository;


  public void tagsComponentVersion(long id, List<String> label) {
    ComponentVersion component = componentVersionRepository.findOne(id);
    label.forEach(l -> tagComponent(component , l));
  }


  public void tagComponentVersion(long id, String label) {
    ComponentVersion component = componentVersionRepository.findOne(id);
    tagComponent(component , label);
  }

  public void tagComponent(ComponentVersion component, List<String> label) {
    label.forEach(l -> tagComponent(component , l));
  }

  public void tagComponent(ComponentVersion component, String label) {

    Tag tag = tagService.createTagIfNotExist(label);

    ComponentTag componentTag = componentTagRepository.findOneByComponentVersionIdAndTagId(component.getId(), tag.getId());

    // TODO: handle concurrent creation
    if (componentTag == null) {
      componentTag = new ComponentTag();
      componentTag.setComponentVersion(component);
      componentTag.setTag(tag);
      componentTagRepository.save(componentTag);
    }
  }

}
