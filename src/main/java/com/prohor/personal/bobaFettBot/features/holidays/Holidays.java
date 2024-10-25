package com.prohor.personal.bobaFettBot.features.holidays;

import com.prohor.personal.bobaFettBot.distribution.DateTimeUtil;
import com.prohor.personal.bobaFettBot.system.ExceptionWriter;
import org.json.*;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

public final class Holidays {
    public static void init(File directory, ExceptionWriter exceptionWriter) {
        if (DIRECTORY != null)
            return;
        WRITER = exceptionWriter;
        DIRECTORY = new File(directory, "holidays");
        try {
            CURRENT_YEAR_HOLIDAYS = loadHolidays(DateTimeUtil.getToday().getYear());
            NEXT_YEAR_HOLIDAYS = loadHolidays(DateTimeUtil.getToday().getYear() + 1);
        } catch (IOException e) {
            WRITER.writeException(e);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private Holidays() {
    }

    private static ExceptionWriter WRITER;
    private static File DIRECTORY;
    private static JSONArray CURRENT_YEAR_HOLIDAYS;
    private static JSONArray NEXT_YEAR_HOLIDAYS;

    static void updateYear(LocalDate today) {
        if (today.getDayOfYear() != 1)
            return;
        CURRENT_YEAR_HOLIDAYS = NEXT_YEAR_HOLIDAYS;
        try {
            NEXT_YEAR_HOLIDAYS = loadHolidays(today.getYear());
        } catch (IOException e) {
            WRITER.writeException(e);
            System.exit(-1);
        }
    }

    private record HolidayData(String header, String holidays) {
    }

    private static HolidayData getHolidays(LocalDate date) {
        JSONArray holidays;
        if (date.getYear() == DateTimeUtil.getToday().getYear())
            holidays = CURRENT_YEAR_HOLIDAYS;
        else
            holidays = NEXT_YEAR_HOLIDAYS;
        holidays = holidays
                .getJSONArray(date.getMonthValue() - 1)
                .getJSONObject(date.getDayOfMonth() - 1)
                .getJSONArray("holidays");
        return new HolidayData(formatHeader(DateTimeUtil.getRussianDate(date) + " " + date.getYear()),
                formatHolidays(holidays.toList().stream().map(Object::toString).toList()));
    }

    public static String getHolidaysMessage(List<String> customHolidays, LocalDate date) {
        HolidayData holidayData = getHolidays(date);
        if (customHolidays == null || customHolidays.isEmpty())
            return holidayData.header + holidayData.holidays;
        return holidayData.header + formatHolidays(customHolidays) + "\n\n" + holidayData.holidays;
    }

    private static String formatHeader(String header) {
        return header + "\n\n";
    }

    private static String formatHolidays(List<String> holidays) {
        return holidays.stream().map(x -> "– " + x).collect(Collectors.joining("\n"));
    }

    private static JSONArray loadHolidays(int year) throws IOException {
        return new JSONArray(Files.readString(Path.of(DIRECTORY.toURI()).resolve(year + ".json")));
    }
}
