package com.ownspec.center.util;

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

}
