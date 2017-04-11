package com.ownspec.center.service.estimation;

import com.ownspec.center.dto.EstimatedTimeDto;
import com.ownspec.center.dto.component.ComponentReferenceDto;
import com.ownspec.center.dto.component.ComponentVersionDto;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by nlabrot on 11/04/17.
 */
@Component
public class EstimatedTimeReport {


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

  public ByteArrayResource generateReport(ComponentVersionDto componentVersionDto) throws JRException, IOException {


    List<Estimation> flat = new LinkedList<>();

    flat.add(new Estimation(componentVersionDto, 0));

    for (int i = 0; i < flat.size(); i++) {
      Estimation current = flat.get(i);

      for (ComponentReferenceDto referenceDto : current.componentVersionDto.getComponentReferences()) {
        flat.add(i + 1, new Estimation(referenceDto.getTarget(), current.level + 1));
      }
    }

    Path tempFile = Files.createTempFile("jasper", "b");

    JRBeanCollectionDataSource beanColDataSource;
    Map parameters;


    try (OutputStream outputStream = Files.newOutputStream(tempFile)) {

      JasperCompileManager.getInstance(DefaultJasperReportsContext.getInstance())
          .compileToStream(new ClassPathResource("report.jrxml").getInputStream(), outputStream);
    }


    beanColDataSource = new JRBeanCollectionDataSource(flat);

    parameters = new HashMap();

    JasperPrint jasperPrint = JasperFillManager.fillReport(tempFile.toString(),
        parameters, beanColDataSource);

    JRXlsExporter exporter = new JRXlsExporter();
    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

    ByteArrayOutputStream result = new ByteArrayOutputStream();

    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(result));

    exporter.exportReport();

    return new ByteArrayResource(result.toByteArray());
  }
}
