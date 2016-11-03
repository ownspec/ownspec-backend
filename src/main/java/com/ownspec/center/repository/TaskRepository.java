package com.ownspec.center.repository;

import com.ownspec.center.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * com.ownspec.center.repository
 * <p>
 * TaskRepository
 *
 * @author lyrold
 * @since 2016-11-01
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
}
