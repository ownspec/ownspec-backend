package com.ownspec.center.controller;

import com.google.common.collect.ImmutableMap;
import com.ownspec.center.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.UUID;

/**
 * Created by nlabrot on 17/12/16.
 */
@RestController("/api/upload")
public class UploadController {

  @Autowired
  private UploadService uploadService;


  @PostMapping
  public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
    try {
      String fileId = uploadService.transfert(multipartFile);

      return ResponseEntity.ok(ImmutableMap.of(
          "filename", multipartFile.getOriginalFilename(),
          "fileId", fileId));

    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

  }
}
