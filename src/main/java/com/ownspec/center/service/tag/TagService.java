package com.ownspec.center.service.tag;

import static com.google.common.base.Preconditions.checkArgument;

import com.ownspec.center.model.Tag;
import com.ownspec.center.repository.TagRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by nlabrot on 30/12/16.
 */
@Service
public class TagService {

  @Autowired
  private TagRepository tagRepository;


  public Tag createTagIfNotExist(String label) {
    checkArgument(StringUtils.isNotBlank(label));

    Tag tag = tagRepository.findOneByLabel(label);

    if (tag != null) {
      return tag;
    } else {
      // TODO: handle concurrent creation
      tag = new Tag();
      tag.setLabel(label);
      return tagRepository.save(tag);
    }
  }


}
