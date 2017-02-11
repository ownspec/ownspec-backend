package com.ownspec.center.controller.component;

import com.ownspec.center.dto.ComponentVersionDto;
import com.ownspec.center.model.component.ComponentVersion;
import com.ownspec.center.repository.component.ComponentVersionRepository;
import com.ownspec.center.service.CommentService;
import com.ownspec.center.service.UploadService;
import com.ownspec.center.service.component.ComponentConverter;
import com.ownspec.center.service.component.ComponentService;
import com.ownspec.center.service.component.ComponentTagService;
import com.ownspec.center.service.component.ComponentVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lyrold on 20/09/2016.
 */
@RestController
@RequestMapping("/api/components/{componentId}/versions")
public class ComponentComponentVersionController {

  @Autowired
  private ComponentService componentService;

  @Autowired
  private ComponentVersionService componentVersionService;

  @Autowired
  private CommentService commentService;

  @Autowired
  private ComponentConverter componentConverter;

  @Autowired
  private UploadService uploadService;

  @Autowired
  private ComponentTagService componentTagService;

  @Autowired
  private ComponentVersionRepository componentVersionRepository;


  @GetMapping
  public List<ComponentVersionDto> findAllVersion(@PathVariable("componentId") Long componentId,
                                                  @RequestParam(value = "statuses", required = false, defaultValue = "false") Boolean statuses,
                                                  @RequestParam(value = "references", required = false, defaultValue = "false") Boolean references,
                                                  @RequestParam(value = "usePoints", required = false, defaultValue = "false") Boolean usePoints) {

    return componentVersionRepository.findAllByComponentId(componentId, new Sort("version"))
        .stream()
        .map(cv -> componentConverter.toComponentVersionDto(cv, statuses, references, usePoints))
        .collect(Collectors.toList());
  }


  @GetMapping("{componentVersionId}")
  public ComponentVersionDto getByVersion(@PathVariable("componentId") Long componentId, @PathVariable("componentVersionId") Long componentVersionId,
                                          @RequestParam(value = "statuses", required = false, defaultValue = "false") Boolean statuses,
                                          @RequestParam(value = "references", required = false, defaultValue = "false") Boolean references,
                                          @RequestParam(value = "usePoints", required = false, defaultValue = "false") Boolean usePoints) {

    ComponentVersion componentVersion = componentVersionRepository.findOneByComponentIdAndId(componentId, componentVersionId);

    return componentConverter.toComponentVersionDto(componentVersion, statuses, references, usePoints);
  }

  @PostMapping("{componentVersionId}/references/{refId}")
  public void updateReference(@PathVariable("componentId") Long sourceComponentId, @PathVariable("componentVersionId") Long sourceComponentVersionId,
                              @PathVariable("refId") Long refId, @RequestBody Map v) {

    Object targetComponentVersionId = v.get("targetComponentVersionId");

    if (targetComponentVersionId instanceof String) {
      componentVersionService.updateToLatestTargetReference(sourceComponentVersionId, refId);
    } else if (targetComponentVersionId instanceof Number) {
      componentVersionService.updateTargetReference(sourceComponentVersionId, refId, ((Number) targetComponentVersionId).longValue());
    }
  }

}
