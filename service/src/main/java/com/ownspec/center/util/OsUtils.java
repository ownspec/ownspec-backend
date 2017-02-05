package com.ownspec.center.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.io.Resource;

import java.beans.FeatureDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lyrold on 15/09/2016.
 */
public final class OsUtils {

  public static String htmlFileToPlainText(String htmlDescriptionPath) {
    try (Stream<String> lines = Files.lines(Paths.get(htmlDescriptionPath))) {
      return lines
          .filter(String::isEmpty)
          .map(line -> line.replaceAll("<.*>", ""))
          .collect(Collectors.joining());
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static void mergeWithNotNullProperties(Object source, Object target) {
    String[] standardPropertiesToIgnored = {"id", "createdDate"};
    BeanUtils.copyProperties(source, target,
        ArrayUtils.addAll(standardPropertiesToIgnored, getNullProperties(source)));
  }

  public static String[] getNullProperties(Object source) {
    final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
    return Stream.of(wrappedSource.getPropertyDescriptors())
        .map(FeatureDescriptor::getName)
        .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
        .toArray(String[]::new);
  }


  public static String toString(Resource resource){

    try (InputStream inputStream = resource.getInputStream()) {
      return IOUtils.toString(inputStream , UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

}
