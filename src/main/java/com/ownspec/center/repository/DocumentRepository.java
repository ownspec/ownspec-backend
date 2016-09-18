package com.ownspec.center.repository;

import com.ownspec.center.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by lyrold on 23/08/2016.
 */
public interface DocumentRepository extends JpaRepository<Document, Long> {
}
