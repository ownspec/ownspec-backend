package com.ownspec.center.service.component;

import com.ownspec.center.model.Tag;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.model.component.ComponentTag;
import com.ownspec.center.repository.tag.ComponentTagRepository;
import com.ownspec.center.repository.component.ComponentRepository;
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
  private ComponentRepository componentRepository;


  public void tagsComponent(long id, List<String> label) {
    Component component = componentRepository.findOne(id);
    label.forEach(l -> tagComponent(component , l));
  }


  public void tagComponent(long id, String label) {
    Component component = componentRepository.findOne(id);
    tagComponent(component , label);
  }

  public void tagComponent(Component component, List<String> label) {
    label.forEach(l -> tagComponent(component , l));
  }

  public void tagComponent(Component component, String label) {

    Tag tag = tagService.createTagIfNotExist(label);

    ComponentTag componentTag = componentTagRepository.findOneByComponentIdAndTagId(component.getId(), tag.getId());

    // TODO: handle concurrent creation
    if (componentTag == null) {
      componentTag = new ComponentTag();
      componentTag.setComponent(component);
      componentTag.setTag(tag);
      componentTagRepository.save(componentTag);
    }
  }

}
