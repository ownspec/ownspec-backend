package com.ownspec.center.service.estimation;

import com.ownspec.center.dto.EstimatedTimeDto;
import com.ownspec.center.dto.component.ComponentReferenceDto;
import com.ownspec.center.dto.component.ComponentVersionDto;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by nlabrot on 11/04/17.
 */
@Component
public class EstimatedTimeReport {


  private final String reportBasePath;

  public EstimatedTimeReport(@Value("${report.basePath}") String reportBasePath) {
    this.reportBasePath = reportBasePath;
  }

  public static class Estimation {
    private double estimation;
    private ComponentVersionDto componentVersionDto;
    private int level = 0;

    public Estimation(ComponentVersionDto componentVersionDto, int level) {
      this.componentVersionDto = componentVersionDto;

      for (EstimatedTimeDto estimatedTimeDto : componentVersionDto.getEstimatedTimes()) {
        this.estimation += estimatedTimeDto.getDurationInMs();
      }

      this.level = level;
    }

    public double getEstimation() {
      return estimation;
    }

    public ComponentVersionDto getComponentVersionDto() {
      return componentVersionDto;
    }

    public int getLevel() {
      return level;
    }
  }

  public List<EstimatedComponentVersion> generateReport(ComponentVersionDto componentVersionDto) throws JRException, IOException {
    List<EstimatedComponentVersion> flat = new LinkedList<>();

    constructTree(componentVersionDto, 1, flat);

    return flat;
  }

  public ByteArrayResource generateExcelReport(ComponentVersionDto componentVersionDto) throws JRException, IOException {
    List<EstimatedComponentVersion> flat = new LinkedList<>();

    constructTree(componentVersionDto, 1, flat);

    JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(flat);

    Map parameters = new HashMap();

    JasperPrint jasperPrint = JasperFillManager.fillReport(new File(reportBasePath, "estimation.jasper").toString(), parameters, beanColDataSource);

    JRXlsExporter exporter = new JRXlsExporter();
    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

    ByteArrayOutputStream result = new ByteArrayOutputStream();

    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(result));
    exporter.exportReport();

    return new ByteArrayResource(result.toByteArray());

  }

  private EstimatedComponentVersion constructTree(ComponentVersionDto cv, int level, List<EstimatedComponentVersion> flat) {
    EstimatedComponentVersion current = new EstimatedComponentVersion(0, cv);
    current.addEstimates(cv.getEstimatedTimes());
    flat.add(current);

    for (ComponentReferenceDto referenceDto : current.getComponentVersion().getComponentReferences()) {
      EstimatedComponentVersion child = constructTree(referenceDto.getTarget(), level + 1, flat);
      current.addChildrenEstimates(child.getEstimatedTimesPerCategory());
    }

    return current;
  }
}
