package com.ownspec.center.controller.component;

import com.google.common.collect.ImmutableMap;
import com.ownspec.center.repository.component.AnotherComponentVersionRepository;
import com.ownspec.center.repository.component.ComponentVersionRepository;
import com.ownspec.center.service.UploadService;
import com.ownspec.center.service.component.ComponentConverter;
import com.ownspec.center.service.component.ComponentService;
import com.ownspec.center.service.component.ComponentVersionService;
import com.ownspec.center.service.component.importer.ComponentServiceImporter;
import com.ownspec.center.service.estimation.EstimatedTimeReport;
import com.ownspec.center.service.workflow.WorkflowService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Created by lyrold on 20/09/2016.
 */
@RestController
@RequestMapping("/api/imports/components")
public class ComponentImportController {

  @Autowired
  private ComponentService componentService;

  @Autowired
  private ComponentVersionService componentVersionService;

  @Autowired
  private ComponentConverter componentConverter;

  @Autowired
  private UploadService uploadService;

  @Autowired
  private ComponentVersionRepository componentVersionRepository;

  @Autowired
  private WorkflowService workflowService;

  @Autowired
  private AnotherComponentVersionRepository anotherComponentVersionRepository;

  @Autowired
  private EstimatedTimeReport estimatedTimeReport;

  @Autowired
  private ComponentServiceImporter componentServiceImporter;


  @PostMapping("upload")
  public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
    try {
      String fileId = uploadService.transfert(multipartFile);

      Path path = uploadService.findFile(fileId).get();

      Resource resource = componentServiceImporter.importComponent(path);

      try (InputStream inputStream = resource.getInputStream()) {

        String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        return ResponseEntity.ok(ImmutableMap.of(
            "filename", multipartFile.getOriginalFilename(),
            "fileId", fileId,
            "content", content));
      }

    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

  }


}
