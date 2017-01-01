package com.ownspec.center.repository;

import com.ownspec.center.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by nlabrot on 29/12/16.
 */
public interface TagRepository extends JpaRepository<Tag, Long> {

  Tag findOneByLabel(String label);


}
