package com.ty.mid.framework.common.util;

import cn.hutool.core.lang.Assert;
import com.ty.mid.framework.common.lang.NonNull;
import com.ty.mid.framework.common.lang.Nullable;
import com.ty.mid.framework.common.util.collection.MiscUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
public class DateUtils {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static final String DATE_DOT_PATTERN = "yyyy.MM.dd";
    public static final String DATE_MINUTE_DOT_PATTERN = "yyyy.MM.dd HH:mm";
    public static final String DATE_TIME_DOT_PATTERN = "yyyy.MM.dd HH:mm:ss";

    public static final String DATE_PATTERN_ZH = "yyyy年MM月dd日";
    public static final String YEAR_MONTH_PATTERN = "yyyy-MM";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String YEAR_PATTERN = "yyyy";

    public static Map<String, List<Integer>> SEASON_MONTH_MAP = MiscUtils.toMap(
            "1", Arrays.asList(1, 2, 3),
            "2", Arrays.asList(4, 5, 6),
            "3", Arrays.asList(7, 8, 9),
            "4", Arrays.asList(10, 11, 12)
    );


    public static String resolveSeasonName(Date calcDate) {
        return resolveSeasonName(calcDate, 0);
    }

    public static String resolveLastSeasonName(Date calcDate) {
        return resolveSeasonName(calcDate, -1);
    }

    public static String resolveNextSeasonName(Date calcDate) {
        return resolveSeasonName(calcDate, 1);
    }

    public static String resolveSeasonName(Date calcDate, int seasonOffset) {
        DatePeriod period = resolveSeasonRange(calcDate, seasonOffset);
        return resolveSeasonName(period);
    }

    public static String resolveSeasonName(DatePeriod period) {
        Assert.notNull(period);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(period.getFrom());
        Integer month = calendar.get(Calendar.MONTH) + 1;

        String seasonNum = SEASON_MONTH_MAP.entrySet().stream().filter(e -> e.getValue().contains(month)).findFirst().map(e -> e.getKey()).orElse(null);
        String year = new SimpleDateFormat(YEAR_PATTERN).format(period.getFrom());

        return year.concat("-").concat(seasonNum);
    }

    /**
     * 获取当前日期所在季度
     *
     * @param calcDate
     * @return
     */
    public static DatePeriod resolveSeasonRange(Date calcDate) {
        return resolveSeasonRange(calcDate, 0);
    }

    public static DatePeriod resolveNextSeasonRange(Date calcDate) {
        return resolveSeasonRange(calcDate, 1);
    }

    public static DatePeriod resolveLastSeasonRange(Date calcDate) {
        return resolveSeasonRange(calcDate, -1);
    }

    public static DatePeriod resolveSeasonRange(Date calcDate, int seasonOffset) {
        Assert.notNull(calcDate, "参数不能为空");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calcDate);
        Integer month = calendar.get(Calendar.MONTH) + 1;

        String seasonNum = SEASON_MONTH_MAP.entrySet().stream().filter(e -> e.getValue().contains(month)).findFirst().map(e -> e.getKey()).orElse(null);

        int season = Integer.valueOf(seasonNum);
        int yearOffset = 0;

        int step = seasonOffset < 0 ? -1 : 1;
        for (int i = 0; i < Math.abs(seasonOffset); i++) {
            season = season + step;

            if (season == 0 || season == 5) {
                yearOffset++;
            }
            if (season == 0) {
                season = 4;
            }
            if (season == 5) {
                season = 1;
            }
        }

        seasonNum = String.valueOf(season);
        yearOffset = step < 0 ? 0 - yearOffset : yearOffset;

        List<Integer> months = SEASON_MONTH_MAP.get(seasonNum);

        calendar.set(Calendar.MONTH, Integer.valueOf(months.get(0) - 1));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.YEAR, yearOffset);

        Date start = calendar.getTime();

        calendar.set(Calendar.MONTH, months.get(months.size() - 1) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        Date end = calendar.getTime();

        return new DatePeriod(getStartOfDate(start), getEndOfDate(end));
    }

    /**
     * 获取当前时间毫秒数
     *
     * @return
     */
    public static long nowTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间秒数
     *
     * @return
     */
    public static long nowTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static Date now() {
        return new Date();
    }

    public static Date now(int diffSeconds) {
        return now(diffSeconds, Calendar.SECOND);
    }

    public static Date now(int diff, int field) {
        Date now = now();
        return resolveDate(now, field, diff, false);
    }

    /**
     * 获取当前时间戳
     *
     * @return
     */
    public static Timestamp nowTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 格式化时间为 yyyy-MM-dd HH:mm:ss 格式
     *
     * @param date
     * @return
     */
    public static String formatDateTime(@NonNull Date date) {
        Assert.notNull(date, "date不能为空！");
        return new SimpleDateFormat(DATE_TIME_PATTERN).format(date);
    }

    public static String formatDateZh(@NonNull Date date) {
        Assert.notNull(date, "date不能为空！");
        return new SimpleDateFormat(DATE_PATTERN_ZH).format(date);
    }

    /**
     * 格式化时间为 yyyy-MM-dd 格式
     *
     * @param date
     * @return
     */
    public static String formatDate(@NonNull Date date) {
        Assert.notNull(date, "date不能为空！");
        return new SimpleDateFormat(DATE_PATTERN).format(date);
    }

    public static String formatDateDot(@NonNull Date date) {
        Assert.notNull(date, "date不能为空！");
        return new SimpleDateFormat(DATE_DOT_PATTERN).format(date);
    }

    public static String formatDateTimeDot(@NonNull Date date) {
        Assert.notNull(date, "date不能为空！");
        return new SimpleDateFormat(DATE_TIME_DOT_PATTERN).format(date);
    }

    public static String formatMinuteDot(@NonNull Date date) {
        Assert.notNull(date, "date不能为空！");
        return new SimpleDateFormat(DATE_MINUTE_DOT_PATTERN).format(date);
    }

    /**
     * 格式化时间为 yyyy-MM 格式
     *
     * @param date
     * @return
     */
    public static String formatYearMonth(@NonNull Date date) {
        Assert.notNull(date, "date不能为空！");
        return new SimpleDateFormat(YEAR_MONTH_PATTERN).format(date);
    }

    /**
     * 格式化时间为 HH:mm:ss 格式
     *
     * @param date
     * @return
     */
    public static String formatTime(@NonNull Date date) {
        Assert.notNull(date, "date不能为空！");
        return new SimpleDateFormat(TIME_PATTERN).format(date);
    }

    /**
     * 格式化时间为 yyyy-MM-dd HH:mm 格式
     *
     * @param date
     * @return
     */
    public static String formatToMinute(@NonNull Date date) {
        Assert.notNull(date, "date不能为空！");
        return new SimpleDateFormat(DATE_MINUTE_PATTERN).format(date);
    }

    public static String format(Date date, String pattern) {
        Assert.notNull(date, "date不能为空！");
        Assert.notEmpty(pattern, "pattern不能为空");
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 获取日期的开始时间（去掉时分秒）
     *
     * @param date
     * @return
     */
    public static Date getStartOfDate(@NonNull Date date) {
        Assert.notNull(date, "date不能为空！");
        return resolveDate(date, 0, 0, true);
    }

    public static Date getStartOfMonth(@NonNull Date date) {
        Assert.notNull(date, "date不能为空！");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.DATE, 1);
        trimStartOfDate(cal);
        return cal.getTime();
    }

    public static Date getEndOfMonth(@NonNull Date date) {
        Assert.notNull(date, "date不能为空！");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(cal.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * 日期加一
     *
     * @return
     */
    public static Date getNextDate() {
        return getNextDate(1);
    }

    public static Date getNextYear() {
        return getNextYear(1);
    }

    public static Date getNextYear(int years) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.now());
        calendar.add(Calendar.YEAR, years);

        return calendar.getTime();
    }

    public static Date getNextYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, 1);

        return calendar.getTime();
    }

    /**
     * 获取以后的日期
     *
     * @param days 天数
     * @return
     */
    public static Date getNextDate(int days) {
        return getNextDate(now(), days);
    }

    public static Date getNextDate(Date from) {
        return getNextDate(from, 1);
    }

    public static Date getNextDate(Date from, int days) {
        return resolveDate(from, Calendar.DATE, days, false);
    }

    public static Date getNextHour(Date from) {
        return resolveDate(from, Calendar.HOUR_OF_DAY, 1, false);
    }

    public static Date getNextHour(Date from, int hours) {
        return resolveDate(from, Calendar.HOUR_OF_DAY, hours, false);
    }

    public static Date getNextMinute(Date from) {
        return resolveDate(from, Calendar.HOUR_OF_DAY, 1, false);
    }

    public static Date getNextMinute(Date from, int minutes) {
        return resolveDate(from, Calendar.MINUTE, minutes, false);
    }

    public static Date getNextDate(Date from, int days, boolean trimHourToStart) {
        return resolveDate(from, Calendar.DATE, days, trimHourToStart);
    }

    public static Date getEndOfDate() {
        return getEndOfDate(DateUtils.now());
    }

    public static Date getEndOfDate(Date date) {
        Assert.notNull(date, "date can not be null");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 获取昨天的日期
     *
     * @return
     */
    public static Date getLastDate() {
        return getLastDate(1);
    }

    /**
     * 获取之前的时间
     *
     * @param days 天数
     * @return
     */
    public static Date getLastDate(int days) {
        return resolveDate(now(), Calendar.DATE, 0 - days, false);
    }

    /**
     * 获取明天的开始时间
     *
     * @return
     */
    public static Date getNextStartDate() {
        return getNextStartDate(1);
    }

    /**
     * 获取以后的开始时间
     *
     * @param days 天数
     * @return
     */
    public static Date getNextStartDate(int days) {
        return resolveDate(now(), Calendar.DATE, days, true);
    }

    /**
     * 获取昨天的开始时间
     *
     * @return
     */
    public static Date getLastStartDate() {
        return getLastStartDate(1);
    }

    /**
     * 获取之前的开始是时间
     *
     * @param days 天数
     * @return
     */
    public static Date getLastStartDate(int days) {
        return resolveDate(now(), Calendar.DATE, 0 - days, true);
    }

    /**
     * 比较两个日期是否相等
     *
     * @param a
     * @param b
     * @return 0 相等 ， 大于 0：a > b 小于 0： a < b
     */
    public static int isEquals(@NonNull Date a, @NonNull Date b) {
        Assert.notNull(a, "时间1不能为空");
        Assert.notNull(b, "时间2不能为空");

        return (int) (a.getTime() - b.getTime());
    }

    /**
     * 判断当前时间是否在范围内
     *
     * @param fromDate
     * @param thruDate
     * @return
     */
    public static boolean isNowInRange(@NonNull Date fromDate, @Nullable Date thruDate) {
        return isDateInRange(now(), fromDate, thruDate);
    }

    /**
     * 判断给定时间是否在范围内
     *
     * @param date
     * @param fromDate
     * @param thruDate
     * @return
     */
    public static boolean isDateInRange(@NonNull Date date, @NonNull Date fromDate, @Nullable Date thruDate) {
        Assert.notNull(date, "date不能为空！");
        Assert.notNull(fromDate);

        log.info("checking date date[{}] in range:[{}, {}}", date, fromDate, thruDate);
        if (date.getTime() < fromDate.getTime()) {
            return false;
        }

        if (thruDate != null) {

            if (date.getTime() > thruDate.getTime()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 计算月份差
     *
     * @param from
     * @param to
     * @return
     */
    public static long diffYears(Date from, Date to) {
        LocalDateTime fromTime = toLocalDateTime(from);
        LocalDateTime toTime = toLocalDateTime(to);

        return ChronoUnit.YEARS.between(fromTime, toTime);
    }

    /**
     * 计算月份差
     *
     * @param from
     * @param to
     * @return
     */
    public static long diffMonths(Date from, Date to) {
        LocalDateTime fromTime = toLocalDateTime(from);
        LocalDateTime toTime = toLocalDateTime(to);

        return ChronoUnit.MONTHS.between(fromTime, toTime);
    }

    /**
     * 计算天数差
     *
     * @param from
     * @param to
     * @return
     */
    public static long diffDays(Date from, Date to) {
        LocalDateTime fromTime = toLocalDateTime(from);
        LocalDateTime toTime = toLocalDateTime(to);

        return ChronoUnit.DAYS.between(fromTime, toTime);
    }

    /**
     * 计算小时差
     *
     * @param from
     * @param to
     * @return
     */
    public static long diffHours(Date from, Date to) {
        LocalDateTime fromTime = toLocalDateTime(from);
        LocalDateTime toTime = toLocalDateTime(to);

        return ChronoUnit.HOURS.between(fromTime, toTime);
    }

    /**
     * 计算分钟差
     *
     * @param from
     * @param to
     * @return
     */
    public static long diffMinutes(Date from, Date to) {
        LocalDateTime fromTime = toLocalDateTime(from);
        LocalDateTime toTime = toLocalDateTime(to);

        return ChronoUnit.MINUTES.between(fromTime, toTime);
    }

    /**
     * 计算秒数差
     *
     * @param from
     * @param to
     * @return
     */
    public static long diffSeconds(Date from, Date to) {
        LocalDateTime fromTime = toLocalDateTime(from);
        LocalDateTime toTime = toLocalDateTime(to);

        return ChronoUnit.SECONDS.between(fromTime, toTime);
    }

    /**
     * 日期类型转换
     *
     * @param date
     * @return
     */
    public static LocalDateTime toLocalDateTime(@NonNull Date date) {
        Assert.notNull(date, "date不能为空！");

        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        return LocalDateTime.ofInstant(instant, zoneId);
    }

    // private methods
    private static Date resolveDate(@Nullable Date date, int field, int amount, boolean trimStartOfDate) {
        Calendar calendar = Calendar.getInstance();

        if (date != null) {
            calendar.setTime(date);
        }

        if (amount != 0) {
            calendar.add(field, amount);
        }

        if (trimStartOfDate) {
            trimStartOfDate(calendar);
        }

        return calendar.getTime();
    }

    private static void trimStartOfDate(@NonNull Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void trimStartOfHour(@NonNull Calendar calendar) {
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void trimStartOfSeconds(@NonNull Calendar calendar) {
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static class DatePeriod {
        private Date from;
        private Date thru;

        public DatePeriod(Date from) {
            this.from = from;
        }

        public DatePeriod(Date from, Date thru) {
            this.from = from;
            this.thru = thru;
        }

        public Date getFrom() {
            return from;
        }

        public Date getThru() {
            return thru;
        }
    }
}
