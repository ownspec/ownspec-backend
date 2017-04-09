package com.ownspec.center.util;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {
  private static Category log = Category.getInstance(DateUtils.class);
  private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?|\\.\\d+)(.+)");
  public static final long SECOND_MILLIS;
  public static final long MINUTE_MILLIS;
  public static final long HOUR_MILLIS;
  public static final long DAY_MILLIS;
  public static final long MONTH_MILLIS;
  public static final long YEAR_MILLIS;
  public static final String AM = "am";
  public static final String PM = "pm";
  private static final int[] CALENDAR_PERIODS;
  private final ResourceBundle resourceBundle;
  public static final DateFormat ISO8601DateFormat;

  public DateUtils(ResourceBundle resourceBundle) {
    this.resourceBundle = resourceBundle;
  }

  public static boolean equalTimestamps(Timestamp t1, Timestamp t2) {
    return Math.abs(t1.getTime() - t2.getTime()) < 10L;
  }

  public String dateDifferenceBean(long dateA, long dateB, long resolution, ResourceBundle resourceBundle) {
    return dateDifference(dateA, dateB, resolution, resourceBundle);
  }

  public static String dateDifference(long dateA, long dateB, long resolution, ResourceBundle resourceBundle) {
    StringBuffer sb = new StringBuffer();
    long difference = Math.abs(dateB - dateA);
    --resolution;
    long months = difference / DateUtils.Duration.MONTH.getMilliseconds();
    if (months > 0L) {
      difference %= DateUtils.Duration.MONTH.getMilliseconds();
      if (months > 1L) {
        sb.append(months).append(" ").append(getText(resourceBundle, "core.dateutils.months")).append(", ");
      } else {
        sb.append(months).append(" ").append(getText(resourceBundle, "core.dateutils.month")).append(", ");
      }
    }

    if (resolution < 0L) {
      return sb.length() == 0 ? "0 " + getText(resourceBundle, "core.dateutils.months") : sb.substring(0, sb.length() - 2);
    } else {
      --resolution;
      long days = difference / DateUtils.Duration.DAY.getMilliseconds();
      if (days > 0L) {
        difference %= DateUtils.Duration.DAY.getMilliseconds();
        if (days > 1L) {
          sb.append(days).append(" ").append(getText(resourceBundle, "core.dateutils.days")).append(", ");
        } else {
          sb.append(days).append(" ").append(getText(resourceBundle, "core.dateutils.day")).append(", ");
        }
      }

      if (resolution < 0L) {
        return sb.length() == 0 ? "0 " + getText(resourceBundle, "core.dateutils.days") : sb.substring(0, sb.length() - 2);
      } else {
        --resolution;
        long hours = difference / DateUtils.Duration.HOUR.getMilliseconds();
        if (hours > 0L) {
          difference %= DateUtils.Duration.HOUR.getMilliseconds();
          if (hours > 1L) {
            sb.append(hours).append(" ").append(getText(resourceBundle, "core.dateutils.hours")).append(", ");
          } else {
            sb.append(hours).append(" ").append(getText(resourceBundle, "core.dateutils.hour")).append(", ");
          }
        }

        if (resolution < 0L) {
          return sb.length() == 0 ? "0 " + getText(resourceBundle, "core.dateutils.hours") : sb.substring(0, sb.length() - 2);
        } else {
          --resolution;
          long minutes = difference / DateUtils.Duration.MINUTE.getMilliseconds();
          if (minutes > 0L) {
            difference %= DateUtils.Duration.MINUTE.getMilliseconds();
            if (minutes > 1L) {
              sb.append(minutes).append(" ").append(getText(resourceBundle, "core.dateutils.minutes")).append(", ");
            } else {
              sb.append(minutes).append(" ").append(getText(resourceBundle, "core.dateutils.minute")).append(", ");
            }
          }

          if (resolution < 0L) {
            return sb.length() == 0 ? "0 " + getText(resourceBundle, "core.dateutils.minutes") : sb.substring(0, sb.length() - 2);
          } else {
            --resolution;
            long seconds = difference / DateUtils.Duration.SECOND.getMilliseconds();
            if (seconds > 0L) {
              if (seconds > 1L) {
                sb.append(seconds).append(" ").append(getText(resourceBundle, "core.dateutils.seconds")).append(", ");
              } else {
                sb.append(seconds).append(" ").append(getText(resourceBundle, "core.dateutils.second")).append(", ");
              }
            }

            return resolution <= 0L && sb.length() == 0 ? "0 " + getText(resourceBundle, "core.dateutils.seconds") : (sb.length() > 2 ? sb.substring(0, sb.length() - 2) : "");
          }
        }
      }
    }
  }

  public static String formatDateISO8601(Date ts) {
    return ISO8601DateFormat.format(ts);
  }

  public static boolean validDuration(String s) {
    try {
      getDuration(s);
      return true;
    } catch (InvalidDurationException var2) {
      return false;
    }
  }

  public static long getDuration(String durationStr) throws InvalidDurationException {
    return getDuration(durationStr, DateUtils.Duration.MINUTE);
  }

  public static long getDuration(String durationStr, DateUtils.Duration defaultUnit) throws InvalidDurationException {
    return getDurationSeconds(durationStr, DateUtils.Duration.DAY.getSeconds(), DateUtils.Duration.WEEK.getSeconds(), defaultUnit);
  }

  public static long getDuration(String durationStr, int hoursPerDay, int daysPerWeek) throws InvalidDurationException {
    return getDuration(durationStr, hoursPerDay, daysPerWeek, DateUtils.Duration.MINUTE);
  }

  public static long getDuration(String durationStr, int hoursPerDay, int daysPerWeek, DateUtils.Duration defaultUnit) throws InvalidDurationException {
    long secondsInDay = (long) hoursPerDay * DateUtils.Duration.HOUR.getSeconds();
    long secondsPerWeek = (long) daysPerWeek * secondsInDay;
    return getDurationSeconds(durationStr, secondsInDay, secondsPerWeek, defaultUnit);
  }

/*  public static long getDurationWithNegative(String durationStr) throws InvalidDurationException {
    String cleanedDurationStr = TextUtils.noNull(durationStr).trim();
    if (!TextUtils.stringSet(cleanedDurationStr)) {
      return 0L;
    } else {
      boolean negative = false;
      if (cleanedDurationStr.charAt(0) == 45) {
        negative = true;
      }

      return negative ? 0L - getDuration(cleanedDurationStr.substring(1)) : getDuration(cleanedDurationStr);
    }
  }*/

  public static long getDurationSeconds(String durationStr, long secondsPerDay, long secondsPerWeek, DateUtils.Duration defaultUnit) throws InvalidDurationException {
    long time = 0L;
    if (StringUtils.isBlank(durationStr)) {
      return 0L;
    } else {
      durationStr = durationStr.trim().toLowerCase();
      if (durationStr.indexOf(" ") > 0) {
        for (StringTokenizer ex = new StringTokenizer(durationStr, ", "); ex.hasMoreTokens(); time += getDurationSeconds(ex.nextToken(), secondsPerDay, secondsPerWeek, defaultUnit)) {
          ;
        }
      } else {
        try {
          time = Long.parseLong(durationStr.trim()) * defaultUnit.getModifiedSeconds(secondsPerDay, secondsPerWeek);
        } catch (Exception var17) {
          Matcher matcher = DURATION_PATTERN.matcher(durationStr);
          if (!matcher.matches()) {
            throw new InvalidDurationException("Unable to parse duration string: " + durationStr);
          }

          String numberAsString = matcher.group(1);
          BigDecimal number = new BigDecimal(numberAsString);
          long unit = getUnit(matcher.group(2), secondsPerDay, secondsPerWeek);
          BigDecimal seconds = number.multiply(BigDecimal.valueOf(unit));

          try {
            seconds.divide(BigDecimal.valueOf(60L)).intValueExact();
            time = (long) seconds.intValueExact();
          } catch (ArithmeticException var16) {
            throw new InvalidDurationException("Specified decimal fraction duration cannot maintain precision", var16);
          }
        }
      }

      return time;
    }
  }

  private static long getUnit(String unit, long secondsPerDay, long secondsPerWeek) throws InvalidDurationException {
    long time;
    switch (unit.charAt(0)) {
      case 'd':
        validateDurationUnit(unit.substring(0), DateUtils.Duration.DAY);
        time = secondsPerDay;
        break;
      case 'h':
        validateDurationUnit(unit.substring(0), DateUtils.Duration.HOUR);
        time = DateUtils.Duration.HOUR.getSeconds();
        break;
      case 'm':
        validateDurationUnit(unit.substring(0), DateUtils.Duration.MINUTE);
        time = DateUtils.Duration.MINUTE.getSeconds();
        break;
      case 'w':
        validateDurationUnit(unit.substring(0), DateUtils.Duration.WEEK);
        time = secondsPerWeek;
        break;
      default:
        throw new InvalidDurationException("Not a valid duration string");
    }

    return time;
  }

  private static String validateDurationUnit(String durationString, DateUtils.Duration duration) throws InvalidDurationException {
    if (durationString.length() > 1) {
      String singular = duration.name().toLowerCase();
      String plural = duration.name().toLowerCase() + "s";
      if (durationString.indexOf(plural) != -1) {
        return durationString.substring(durationString.indexOf(plural));
      } else if (durationString.indexOf(singular) != -1) {
        return durationString.substring(durationString.indexOf(singular));
      } else {
        throw new InvalidDurationException("Not a valid durationString string");
      }
    } else {
      return durationString.substring(1);
    }
  }

  public static String getDurationString(long seconds) {
    return getDurationStringSeconds(seconds, DateUtils.Duration.DAY.getSeconds(), DateUtils.Duration.WEEK.getSeconds());
  }

  public static String getDurationStringWithNegative(long seconds) {
    return seconds < 0L ? "-" + getDurationString(-seconds) : getDurationString(seconds);
  }

  public static String getDurationString(long l, int hoursPerDay, int daysPerWeek) {
    long secondsInDay = (long) hoursPerDay * DateUtils.Duration.HOUR.getSeconds();
    long secondsPerWeek = (long) daysPerWeek * secondsInDay;
    return getDurationStringSeconds(l, secondsInDay, secondsPerWeek);
  }

  public static String getDurationStringSeconds(long l, long secondsPerDay, long secondsPerWeek) {
    if (l == 0L) {
      return "0m";
    } else {
      StringBuffer result = new StringBuffer();
      if (l >= secondsPerWeek) {
        result.append(l / secondsPerWeek);
        result.append("w ");
        l %= secondsPerWeek;
      }

      if (l >= secondsPerDay) {
        result.append(l / secondsPerDay);
        result.append("d ");
        l %= secondsPerDay;
      }

      if (l >= DateUtils.Duration.HOUR.getSeconds()) {
        result.append(l / DateUtils.Duration.HOUR.getSeconds());
        result.append("h ");
        l %= DateUtils.Duration.HOUR.getSeconds();
      }

      if (l >= DateUtils.Duration.MINUTE.getSeconds()) {
        result.append(l / DateUtils.Duration.MINUTE.getSeconds());
        result.append("m ");
      }

      return result.toString().trim();
    }
  }

  public static String getDurationPretty(long numSecs, ResourceBundle resourceBundle) {
    return getDurationPrettySeconds(numSecs, DateUtils.Duration.DAY.getSeconds(), DateUtils.Duration.WEEK.getSeconds(), resourceBundle, false);
  }

  public static String getDurationPretty(long numSecs, int hoursPerDay, int daysPerWeek, ResourceBundle resourceBundle) {
    long secondsInDay = (long) hoursPerDay * DateUtils.Duration.HOUR.getSeconds();
    long secondsPerWeek = (long) daysPerWeek * secondsInDay;
    return getDurationPrettySeconds(numSecs, secondsInDay, secondsPerWeek, resourceBundle, false);
  }

  public static String getDurationPrettySecondsResolution(long numSecs, ResourceBundle resourceBundle) {
    return getDurationPrettySeconds(numSecs, DateUtils.Duration.DAY.getSeconds(), DateUtils.Duration.WEEK.getSeconds(), resourceBundle, true);
  }

  public static String getDurationPrettySecondsResolution(long numSecs, int hoursPerDay, int daysPerWeek, ResourceBundle resourceBundle) {
    long secondsInDay = (long) hoursPerDay * DateUtils.Duration.HOUR.getSeconds();
    long secondsPerWeek = (long) daysPerWeek * secondsInDay;
    return getDurationPrettySeconds(numSecs, secondsInDay, secondsPerWeek, resourceBundle, true);
  }

  private static String getDurationPrettySeconds(long numSecs, long secondsPerDay, long secondsPerWeek, ResourceBundle resourceBundle, boolean secondsDuration) {
    long secondsPerYear = secondsPerWeek * 52L;
    return getDurationPrettySeconds(numSecs, secondsPerYear, secondsPerDay, secondsPerWeek, resourceBundle, secondsDuration);
  }

  public static String getDurationPrettySeconds(long numSecs, long secondsPerDay, long secondsPerWeek, ResourceBundle resourceBundle) {
    return getDurationPrettySeconds(numSecs, secondsPerDay, secondsPerWeek, resourceBundle, false);
  }

  private static String getDurationPrettySeconds(long numSecs, long secondsPerYear, long secondsPerDay, long secondsPerWeek, ResourceBundle resourceBundle, boolean secondResolution) {
    if (numSecs == 0L) {
      return secondResolution ? "0 " + getText(resourceBundle, "core.dateutils.seconds") : "0 " + getText(resourceBundle, "core.dateutils.minutes");
    } else {
      StringBuffer result = new StringBuffer();
      long minute;
      if (numSecs >= secondsPerYear) {
        minute = numSecs / secondsPerYear;
        result.append(minute).append(' ');
        if (minute > 1L) {
          result.append(getText(resourceBundle, "core.dateutils.years"));
        } else {
          result.append(getText(resourceBundle, "core.dateutils.year"));
        }

        result.append(", ");
        numSecs %= secondsPerYear;
      }

      if (numSecs >= secondsPerWeek) {
        minute = numSecs / secondsPerWeek;
        result.append(minute).append(' ');
        if (minute > 1L) {
          result.append(getText(resourceBundle, "core.dateutils.weeks"));
        } else {
          result.append(getText(resourceBundle, "core.dateutils.week"));
        }

        result.append(", ");
        numSecs %= secondsPerWeek;
      }

      if (numSecs >= secondsPerDay) {
        minute = numSecs / secondsPerDay;
        result.append(minute).append(' ');
        if (minute > 1L) {
          result.append(getText(resourceBundle, "core.dateutils.days"));
        } else {
          result.append(getText(resourceBundle, "core.dateutils.day"));
        }

        result.append(", ");
        numSecs %= secondsPerDay;
      }

      if (numSecs >= DateUtils.Duration.HOUR.getSeconds()) {
        minute = numSecs / DateUtils.Duration.HOUR.getSeconds();
        result.append(minute).append(' ');
        if (minute > 1L) {
          result.append(getText(resourceBundle, "core.dateutils.hours"));
        } else {
          result.append(getText(resourceBundle, "core.dateutils.hour"));
        }

        result.append(", ");
        numSecs %= DateUtils.Duration.HOUR.getSeconds();
      }

      if (numSecs >= DateUtils.Duration.MINUTE.getSeconds()) {
        minute = numSecs / DateUtils.Duration.MINUTE.getSeconds();
        result.append(minute).append(' ');
        if (minute > 1L) {
          result.append(getText(resourceBundle, "core.dateutils.minutes"));
        } else {
          result.append(getText(resourceBundle, "core.dateutils.minute"));
        }

        result.append(", ");
        if (secondResolution) {
          numSecs %= DateUtils.Duration.MINUTE.getSeconds();
        }
      }

      if (numSecs >= 1L && numSecs < DateUtils.Duration.MINUTE.getSeconds()) {
        result.append(numSecs).append(' ');
        if (numSecs > 1L) {
          result.append(getText(resourceBundle, "core.dateutils.seconds"));
        } else {
          result.append(getText(resourceBundle, "core.dateutils.second"));
        }

        result.append(", ");
      }

      return result.length() > 2 ? result.substring(0, result.length() - 2) : result.toString();
    }
  }

  public String formatDurationPretty(long l) {
    return getDurationPretty(l, this.resourceBundle);
  }

  public String formatDurationPretty(String seconds) {
    return getDurationPretty(Long.parseLong(seconds), this.resourceBundle);
  }

  /**
   * @deprecated
   */
  public String formatDurationString(long l) {
    return getDurationPretty(l, this.resourceBundle);
  }

  private static String getText(ResourceBundle resourceBundle, String key) {
    try {
      return resourceBundle.getString(key);
    } catch (MissingResourceException var3) {
      log.error(var3);
      return "";
    }
  }

  public static Calendar toEndOfPeriod(Calendar calendar, int period) {
    boolean zero = false;
    int[] arr$ = CALENDAR_PERIODS;
    int len$ = arr$.length;

    for (int i$ = 0; i$ < len$; ++i$) {
      int calendarPeriod = arr$[i$];
      if (zero) {
        calendar.set(calendarPeriod, calendar.getMaximum(calendarPeriod));
      }

      if (calendarPeriod == period) {
        zero = true;
      }
    }

    if (!zero) {
      throw new IllegalArgumentException("unknown Calendar period: " + period);
    } else {
      return calendar;
    }
  }

  public static Calendar toStartOfPeriod(Calendar calendar, int period) {
    boolean zero = false;
    int[] arr$ = CALENDAR_PERIODS;
    int len$ = arr$.length;

    for (int i$ = 0; i$ < len$; ++i$) {
      int calendarPeriod = arr$[i$];
      if (zero) {
        if (calendarPeriod == 5) {
          calendar.set(5, 1);
        } else {
          calendar.set(calendarPeriod, 0);
        }
      }

      if (calendarPeriod == period) {
        zero = true;
      }
    }

    if (!zero) {
      throw new IllegalArgumentException("unknown Calendar period: " + period);
    } else {
      return calendar;
    }
  }

  public static DateUtils.DateRange toDateRange(Calendar date, int period) {
    Calendar cal = (Calendar) date.clone();
    toStartOfPeriod(cal, period);
    Date startDate = new Date(cal.getTimeInMillis());
    cal.add(period, 1);
    Date endDate = new Date(cal.getTimeInMillis());
    return new DateUtils.DateRange(startDate, endDate);
  }

  public static Calendar getCalendarDay(int year, int month, int day) {
    return initCalendar(year, month, day, 0, 0, 0, 0);
  }

  public static Date getDateDay(int year, int month, int day) {
    return getCalendarDay(year, month, day).getTime();
  }

  public static Date getSqlDateDay(int year, int month, int day) {
    return new java.sql.Date(getCalendarDay(year, month, day).getTimeInMillis());
  }

  public static int get24HourTime(String meridianIndicator, int hours) {
    if (hours == 12) {
      if ("am".equalsIgnoreCase(meridianIndicator)) {
        return 0;
      }

      if ("pm".equalsIgnoreCase(meridianIndicator)) {
        return 12;
      }
    }

    int onceMeridianAdjustment = "pm".equalsIgnoreCase(meridianIndicator) ? 12 : 0;
    return hours + onceMeridianAdjustment;
  }

  public static Date tomorrow() {
    Calendar cal = Calendar.getInstance();
    cal.add(5, 1);
    return cal.getTime();
  }

  public static Date yesterday() {
    Calendar cal = Calendar.getInstance();
    cal.add(5, -1);
    return cal.getTime();
  }

  private static Calendar initCalendar(int year, int month, int day, int hour, int minute, int second, int millis) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, day, hour, minute, second);
    calendar.set(14, millis);
    return calendar;
  }

  static {
    SECOND_MILLIS = DateUtils.Duration.SECOND.getMilliseconds();
    MINUTE_MILLIS = DateUtils.Duration.MINUTE.getMilliseconds();
    HOUR_MILLIS = DateUtils.Duration.HOUR.getMilliseconds();
    DAY_MILLIS = DateUtils.Duration.DAY.getMilliseconds();
    MONTH_MILLIS = DateUtils.Duration.MONTH.getMilliseconds();
    YEAR_MILLIS = DateUtils.Duration.YEAR.getMilliseconds();
    CALENDAR_PERIODS = new int[]{1, 2, 5, 11, 12, 13, 14};
    ISO8601DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
  }

  public static class DateRange {
    public final Date startDate;
    public final Date endDate;

    public DateRange(Date startDate, Date endDate) {
      this.startDate = startDate;
      this.endDate = endDate;
    }
  }

  public static enum Duration {
    SECOND(1L),
    MINUTE(60L),
    HOUR(60L * MINUTE.getSeconds()),
    DAY(24L * HOUR.getSeconds()) {
      public long getModifiedSeconds(long secondsPerDay, long secondsPerWeek) {
        return secondsPerDay;
      }
    },
    WEEK(7L * DAY.getSeconds()) {
      public long getModifiedSeconds(long secondsPerDay, long secondsPerWeek) {
        return secondsPerWeek;
      }
    },
    MONTH(31L * DAY.getSeconds()) {
      public long getModifiedSeconds(long secondsPerDay, long secondsPerWeek) {
        return 31L * secondsPerDay;
      }
    },
    YEAR(52L * WEEK.getSeconds()) {
      public long getModifiedSeconds(long secondsPerDay, long secondsPerWeek) {
        return 52L * secondsPerWeek;
      }
    };

    private final long seconds;

    public long getSeconds() {
      return this.seconds;
    }

    public long getMilliseconds() {
      return 1000L * this.getSeconds();
    }

    public long getModifiedSeconds(long secondsPerDay, long secondsPerWeek) {
      return this.getSeconds();
    }

    private Duration(long seconds) {
      this.seconds = seconds;
    }
  }
}
