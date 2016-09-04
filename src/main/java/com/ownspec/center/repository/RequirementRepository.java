package com.ownspec.center.repository;

import com.ownspec.center.model.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Created by lyrold on 23/08/2016.
 */
@Repository
public interface RequirementRepository extends JpaRepository<Requirement, Long> {
}
