package com.ownspec.center.util;


import com.ownspec.center.util.DateUtils.Duration;
import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationUtils {
  private static final String UNIT_DAY = "core.durationutils.unit.day";
  private static final String UNIT_HOUR = "core.durationutils.unit.hour";
  private static final String UNIT_MINUTE = "core.durationutils.unit.minute";
  private static final Pattern COUNT_WITH_OPTIONAL_UNITS = Pattern.compile("([,\\.\\xA0\'\\p{Nd}]+)\\s*(?:([^,\\s]+),?)?\\s*");

  public DurationUtils() {
  }

  public static long getDurationInMs(String durationStr, long secondsPerDay, long secondsPerWeek, Duration defaultUnit, Locale locale) {
    Map durationTokens = getDurationTokens();
    return getDurationInMs(durationStr, secondsPerDay, secondsPerWeek, defaultUnit, locale, durationTokens);
  }

  public static long getDurationInMs(String durationStr, long secondsPerDay, long secondsPerWeek, Duration defaultUnit, Locale locale, Map<String, Duration> tokens) throws InvalidDurationException {
    if (StringUtils.isBlank(durationStr)) {
      return 0L;
    } else {
      durationStr = durationStr.trim();
      NumberFormat nf = DecimalFormat.getNumberInstance(locale);
      long seconds = 0L;
      Matcher m = COUNT_WITH_OPTIONAL_UNITS.matcher(durationStr);

      while (m.lookingAt()) {
        ParsePosition pp = new ParsePosition(0);
        String number = m.group(1);
        Number n = nf.parse(number, pp);
        if (pp.getIndex() != number.length()) {
          throw new InvalidDurationException("Bad number \'" + number + "\' in duration string \'" + durationStr + "\'");
        }

        String unitName = m.group(2);
        Duration unit;
        if (unitName != null) {
          unit = (Duration) tokens.get(unitName);
          if (unit == null) {
            throw new InvalidDurationException("No unit for \'" + unitName + "\'");
          }
        } else {
          unit = defaultUnit;
        }

        long s = (long) ((double) unit.getModifiedSeconds(secondsPerDay, secondsPerWeek) * n.doubleValue());
        if (unit != defaultUnit && s % 60L != 0L) {
          throw new InvalidDurationException("Durations must be in whole minutes");
        }

        seconds += s;
        m.region(m.end(), durationStr.length());
      }

      if (m.regionStart() != durationStr.length()) {
        throw new InvalidDurationException("Invalid characters in duration: " + durationStr);
      } else {
        return seconds * 1000;
      }
    }
  }

  public static Map<String, Duration> getDurationTokens() {
    HashMap tokens = new HashMap();
    tokens.put("d", Duration.DAY);
    tokens.put("h", Duration.HOUR);
    tokens.put("m", Duration.MINUTE);
    Duration[] arr$ = Duration.values();
    int len$ = arr$.length;

    for (int i$ = 0; i$ < len$; ++i$) {
      Duration d = arr$[i$];
      String n = d.name().toLowerCase();
      //tokens.put(i18n.getText("core.dateutils." + n), d);
      //tokens.put(i18n.getText("core.dateutils." + n + "s"), d);
    }

    return tokens;
  }

 /* private static String getDurationToken(I18nTextProvider i18n, String unit) {
    String s = i18n.getText(unit);
    return s.replace("{0}", "").trim();
  }*/
}
