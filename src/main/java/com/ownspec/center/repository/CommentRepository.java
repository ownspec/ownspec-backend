package com.ownspec.center.repository;

import java.util.List;

import com.ownspec.center.model.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by lyrold on 27/09/2016.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

  public List<Comment> findAllByComponentId(Long id, Sort sort);

}
