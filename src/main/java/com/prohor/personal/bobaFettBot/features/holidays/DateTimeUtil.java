package com.prohor.personal.bobaFettBot.features.holidays;

import java.time.*;
import java.time.format.*;
import java.util.*;

public abstract class DateTimeUtil {
    private static final ZoneId MOSCOW_ZONE = ZoneId.of("Europe/Moscow");
    private static final Locale RUSSIAN_LOCALE = Locale.forLanguageTag("ru-RU");

    private DateTimeUtil() {
    }

    public static String getRussianMonthName(LocalDate date) {
        return date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, RUSSIAN_LOCALE);
    }

    public static String getRussianMonthNameInDate(LocalDate date) {
        return date.getMonth().getDisplayName(TextStyle.FULL, RUSSIAN_LOCALE);
    }

    public static String getRussianDate(LocalDate date) {
        return date.getDayOfMonth() + " " + getRussianMonthNameInDate(date);
    }

    public static LocalDate getNow() {
        return LocalDate.now(MOSCOW_ZONE);
    }
}
