package com.ownspec.center.service;

import com.ownspec.center.model.User;
import com.ownspec.center.model.component.AbstractComponent;
import com.ownspec.center.util.OsUtils;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import static com.ownspec.center.util.OsUtils.htmlFileToPlainText;

/**
 * Created by lyrold on 19/09/2016.
 */
@Service
public class ComponentService {
    private static final Logger LOG = LoggerFactory.getLogger(ComponentService.class);

    @Autowired
    private GitService gitService;

    //    @Autowired
    private User currentUser;

    public ResponseEntity createComponentWith(AbstractComponent source, JpaRepository repository) throws GitAPIException {
        if (source.getContent() != null) {
            File htmlContentFile = new File(
                    gitService.getGit().getRepository().getWorkTree(),
                    UUID.randomUUID() + ".html");
            LOG.info("creating Document file [{}]", htmlContentFile.getAbsoluteFile());

            try (FileOutputStream outputStream = new FileOutputStream(htmlContentFile)) {
                outputStream.write(source.getContent().getBytes());
            } catch (Exception e) {
                LOG.error("An error has occurred when writing file", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            gitService.commit(htmlContentFile.getAbsolutePath());
            source.setHtmlContentFilePath(htmlContentFile.getAbsolutePath());
            source.setContent(htmlFileToPlainText(htmlContentFile.getAbsolutePath()));
        }
        source.setAuthor(currentUser);
        repository.saveAndFlush(source);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity updateComponentWith(AbstractComponent source, Long id, JpaRepository repository) throws GitAPIException {
        AbstractComponent target = (AbstractComponent) repository.findOne(id);
        if (target != null) {
            OsUtils.mergeWithNotNullProperties(source, target);
            if (source.getContent() != null) {
                gitService.commit(target.getHtmlContentFilePath());
                //todo need to sleep
            }
            target.setUpdatedDate(new Date());
            repository.saveAndFlush(target);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Cannot update component with id [" + id + "]; cause not found");
        }
    }


    public ResponseEntity removeComponentWith(Long id, JpaRepository repository) {
        AbstractComponent source = (AbstractComponent) repository.findOne(id);
        if (source != null) {
            try {
                FileUtils.forceDelete(new File(source.getHtmlContentFilePath()));
                repository.delete(id);
                return ResponseEntity.ok("Successfully removed");
            } catch (IOException e) {
                LOG.error("An error has occurred when removing file:", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
            }
        } else {
            return ResponseEntity.badRequest().body("Cannot remove component with id [" + id + "]; cause not found");
        }
    }


}
