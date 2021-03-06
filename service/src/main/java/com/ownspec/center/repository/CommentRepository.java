package com.ownspec.center.repository;

import com.ownspec.center.model.component.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by lyrold on 27/09/2016.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

  List<Comment> findAllByComponentId(Long id, Sort sort);

}
