package com.ownspec.center.repository;

import com.ownspec.center.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by lyrold on 23/08/2016.
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
