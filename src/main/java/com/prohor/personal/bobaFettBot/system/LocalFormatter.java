package com.prohor.personal.bobaFettBot.system;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class LocalFormatter {
    private static final ZoneId MOSCOW_ZONE = ZoneId.of("Europe/Moscow");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    private LocalFormatter() {
    }

    public static String getCurrentDateTime() {
        return ZonedDateTime.now(MOSCOW_ZONE).format(FORMATTER);
    }
}

