package com.ownspec.center.controller.component;

import com.ownspec.center.dto.PaginatedResult;
import com.ownspec.center.dto.component.ComponentVersionDto;
import com.ownspec.center.dto.workflow.WorkflowStatusDto;
import com.ownspec.center.model.component.ComponentType;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.model.workflow.Status;
import com.ownspec.center.model.workflow.WorkflowStatus;
import com.ownspec.center.repository.component.AnotherComponentVersionRepository;
import com.ownspec.center.repository.component.ComponentVersionRepository;
import com.ownspec.center.service.UploadService;
import com.ownspec.center.service.component.ComponentConverter;
import com.ownspec.center.service.component.ComponentService;
import com.ownspec.center.service.component.ComponentVersionService;
import com.ownspec.center.service.estimation.EstimatedTimeReport;
import com.ownspec.center.service.workflow.WorkflowService;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by lyrold on 20/09/2016.
 */
@RestController
@RequestMapping("/api/component-versions")
public class ComponentVersionController {

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


  @GetMapping
  public PaginatedResult<ComponentVersionDto> findAllVersion(

      @RequestParam(value = "projectId", required = false) Long projectId,
      @RequestParam(value = "q", required = false) String query,
      @RequestParam(value = "sort", required = false) String sort,
      @RequestParam(value = "types", required = false) Optional<List<ComponentType>> types,
      @RequestParam(value = "statuses", required = false, defaultValue = "false") Boolean statuses,
      @RequestParam(value = "references", required = false, defaultValue = "false") Boolean references,
      @RequestParam(value = "usePoints", required = false, defaultValue = "false") Boolean usePoints,
      @RequestParam(value = "generic", required = false, defaultValue = "false") Boolean generic) {

    ComponentVersionSearchBean searchBean = ImmutableComponentVersionSearchBean.newComponentVersionSearchBean()
        .projectId(projectId)
        .isGeneric(generic)
        .componentTypes(types.orElse(Collections.emptyList()))
        .query(query)
        .build();

    return anotherComponentVersionRepository.findAll(searchBean, 0, 100);
  }

  @GetMapping("{id}")
  public ComponentVersionDto findOne(@PathVariable("id") Long id, @RequestParam(value = "statuses", required = false, defaultValue = "false") Boolean statuses,
                                     @RequestParam(value = "references", required = false, defaultValue = "false") Boolean references,
                                     @RequestParam(value = "usePoints", required = false, defaultValue = "false") Boolean usePoints) {

    return componentConverter.toComponentVersionDto(componentVersionRepository.findOne(id), statuses, references, usePoints);
  }

  @PatchMapping(value = "/{id}")
  public ComponentVersionDto update(@PathVariable("id") Long id, @RequestBody ComponentVersionDto componentVersion) {
    return componentConverter.toComponentVersionDto(componentVersionService.update(componentVersion, id), true, true, true);
  }


  @PostMapping(value = "/{id}/content")
  public ResponseEntity updateContent(@PathVariable("id") Long id, @RequestBody(required = false) byte[] content,
                                      @RequestParam(value = "uploadId", required = false) String uploadId) throws GitAPIException, UnsupportedEncodingException {

    Optional<Resource> optional;

    if (uploadId != null) {
      optional = uploadService.findResource(uploadId);
    } else {
      optional = Optional.of(new ByteArrayResource(content));
    }

    return optional.map(r -> ResponseEntity.ok(componentConverter.toComponentVersionDto(componentVersionService.updateContent(id, r), false, false, false)))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
  }


  @GetMapping(value = "/{id}/content")
  public ResponseEntity<Resource> getContent(@PathVariable("id") Long id) throws GitAPIException, UnsupportedEncodingException {
    ComponentVersion component = componentVersionRepository.findOne(id);
    return ResponseEntity.ok().cacheControl(CacheControl.noCache()).body(componentService.getHeadRawContent(component));
  }

  @GetMapping(value = "/{id}/resolved-content")
  public ResponseEntity<String> getResolvedContent(@PathVariable("id") Long id) throws GitAPIException, UnsupportedEncodingException {
    ComponentVersion component = componentVersionRepository.findOne(id);
    return ResponseEntity.ok().cacheControl(CacheControl.noCache()).body(componentService.generateContent(component).getLeft());
  }


  @PostMapping("/{id}/workflow-statuses")
  public WorkflowStatusDto updateWorkflowStatus(@PathVariable("id") Long id, @RequestBody Map next) {

    if (!"new".equals(next.get("nextStatus").toString())){
      WorkflowStatus workflowStatus = workflowService.updateStatus(id, Status.valueOf(next.get("nextStatus").toString()),
          next.get("reason").toString());
      return WorkflowStatusDto.newBuilderFromWorkflowStatus(workflowStatus).build();
    }else{
      return WorkflowStatusDto.newBuilderFromWorkflowStatus(componentService.newWorkflowInstance(id).v3).build();
    }

  }

  @GetMapping(value = "/{id}/estimated-times")
  public ResponseEntity getEstimatedTimes(@PathVariable("id") Long cvId, @RequestParam(value = "output", required = false) String mode) throws IOException, JRException {
    ComponentVersionDto componentVersionDto = componentVersionService.resolveReferencesHierarchicaly(cvId);

    if (StringUtils.isBlank(mode)) {
      return ResponseEntity.ok(estimatedTimeReport.generateReport(componentVersionDto));
    } else {
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.xls\"")
          .contentType(MediaType.APPLICATION_OCTET_STREAM).body(estimatedTimeReport.generateExcelReport(componentVersionDto));
    }
  }


  // TODO: refactor the url, purpose: be consistent between content, resolved-content and compose, use mediatype instead
  @GetMapping(value = "/{id}/compose")
  public ResponseEntity<Resource> print(@PathVariable("id") Long id) throws IOException {

    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_PDF)
        .header("Content-Disposition", "attachment; filename=\"filename.pdf\"")
        .body(componentVersionService.composePdf(id));
  }


  @PostMapping("{sourceId}/references/{refId}/{targetId}")
  public void updateReference(@PathVariable("sourceId") Long sourceComponentVersionId, @PathVariable("refId") Long refId,
                              @PathVariable("targetId") String targetComponentVersionId) {




    if (NumberUtils.isNumber(targetComponentVersionId)) {
      componentVersionService.updateTargetReference(sourceComponentVersionId, refId, NumberUtils.toLong(targetComponentVersionId));
    } else  {
      componentVersionService.updateToLatestTargetReference(sourceComponentVersionId, refId);

    }
  }


}
