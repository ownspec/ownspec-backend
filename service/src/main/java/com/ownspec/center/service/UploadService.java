package com.ownspec.center.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.PostConstruct;

/**
 * Created by nlabrot on 18/12/16.
 */
@Service
public class UploadService {


  @Value("${java.io.tmpdir}")
  private String tempDir;

  @PostConstruct
  public void init() {
    System.out.println("ok");
  }


  public String transfert(MultipartFile multipartFile) throws IOException {
    String fileId = UUID.randomUUID().toString();

    multipartFile.transferTo(Paths.get(System.getProperty("java.io.tmpdir"), fileId).toFile());

    return fileId;
  }

  public Optional<Resource> findResource(String id) {
    return findFile(id).map(p -> new FileSystemResource(p.toFile()));
  }

  public Optional<Path> findFile(String id) {
    Path path = Paths.get(System.getProperty("java.io.tmpdir"), id);

    if (Files.exists(path)){
      return Optional.of(path);
    }else{
      return Optional.empty();
    }
  }


}
