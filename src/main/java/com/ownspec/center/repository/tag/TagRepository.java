package com.ownspec.center.repository.tag;

import com.ownspec.center.model.Tag;
import com.ownspec.center.model.component.ComponentTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by nlabrot on 29/12/16.
 */
public interface TagRepository extends JpaRepository<Tag, Long> {

  Tag findOneByLabel(String label);

}
