package com.ownspec.center.service;

import static java.util.Objects.requireNonNull;

import com.ownspec.center.model.component.Comment;
import com.ownspec.center.model.component.Component;
import com.ownspec.center.repository.CommentRepository;
import com.ownspec.center.repository.component.ComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by nlabrot on 04/12/16.
 */
@Service
public class CommentService {


  @Autowired
  private ComponentRepository componentRepository;


  @Autowired
  private CommentRepository commentRepository;

  public List<Comment> getComments(Long componentId) {
    return commentRepository.findAllByComponentId(componentId, new Sort(Sort.Direction.DESC, "id"));
  }

  public Comment addComment(Long componentId, String value) {
    Comment comment = new Comment();
    comment.setValue(value);
    Component target = requireNonNull(componentRepository.findOne(componentId));
    comment.setComponent(target);
    return commentRepository.save(comment);
  }

}
