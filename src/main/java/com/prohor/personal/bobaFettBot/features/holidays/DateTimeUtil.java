package com.prohor.personal.bobaFettBot.features.holidays;

import java.time.*;
import java.time.format.*;
import java.util.*;

public final class DateTimeUtil {
    private static final ZoneId MOSCOW_ZONE = ZoneId.of("Europe/Moscow");
    private static final Locale RUSSIAN_LOCALE = Locale.forLanguageTag("ru-RU");

    private DateTimeUtil() {
    }

    public static String getRussianMonthNameInDate(LocalDate date) {
        return date.getMonth().getDisplayName(TextStyle.FULL, RUSSIAN_LOCALE);
    }

    public static String getRussianMonthNameInDate(short shortDateRepresentation) {
        return Month.of(shortDateRepresentation / 100).getDisplayName(TextStyle.FULL, RUSSIAN_LOCALE);
    }

    public static String getRussianDate(LocalDate date) {
        return date.getDayOfMonth() + " " + getRussianMonthNameInDate(date);
    }

    public static String getRussianDate(short shortDateRepresentation) {
        return shortDateRepresentation % 100 + " " + getRussianMonthNameInDate(shortDateRepresentation);
    }

    public static LocalDate getNow() {
        return LocalDate.now(MOSCOW_ZONE);
    }

    public static LocalTime getTimeNow() {
        return LocalTime.now(MOSCOW_ZONE);
    }

    public static short getShortDateRepresentation(LocalDate date) {
        return (short) (date.getMonthValue() * 100 + date.getDayOfMonth());
    }
}
