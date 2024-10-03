package com.prohor.personal.bobaFettBot.features.holidays;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.data.entities.CustomHoliday;
import com.prohor.personal.bobaFettBot.data.entities.HolidaysSubscriber;
import com.prohor.personal.bobaFettBot.system.ExceptionWriter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.prohor.personal.bobaFettBot.features.holidays.DateTimeUtil.getShortDateRepresentation;

public class HolidaysDistributor implements Runnable {
    private static final int SECS_IN_15_MINUTES = 15 * 60;

    private final Bot bot;
    private final ExceptionWriter writer;

    public HolidaysDistributor(Bot bot, ExceptionWriter writer) {
        this.bot = bot;
        this.writer = writer;
        LocalTime now = DateTimeUtil.getTimeNow();
        int initialDelay = SECS_IN_15_MINUTES - (now.getMinute() * 60 + now.getSecond()) % SECS_IN_15_MINUTES;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this, initialDelay, SECS_IN_15_MINUTES, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        HolidaysSubscriber atTime = new HolidaysSubscriber();
        LocalTime roundedNow = getRoundedTime();
        LocalDate today = DateTimeUtil.getNow();
        if (roundedNow.getHour() == 0 && roundedNow.getMinute() == 0)
            Holidays.setToday(today);
        atTime.setDailyDistributionTime(roundedNow);
        atTime.setSubscriptionIsActive(true);
        try {
            Map<Long, List<String>> usersCustomHolidays = getUsersCustomHolidays();
            List<HolidaysSubscriber> subscribers = bot.storage.getAllByFields(atTime);
            for (HolidaysSubscriber subscriber : subscribers) {
                long chatId = subscriber.getChatId();
                reminderAboutCustomHolidays(chatId, today, bot);
                int indent = subscriber.getIndentationOfDays();
                bot.sendMessage(SendMessage.builder()
                        .chatId(chatId)
                        .text(Holidays.getHolidaysMessage(usersCustomHolidays.get(subscriber.getChatId()),
                                today.plusDays(indent)))
                        .build());
            }
        } catch (Exception e) {
            writer.writeException(e);
        }
    }

    private Map<Long, List<String>> getUsersCustomHolidays() throws Exception {
        LocalDate today = Holidays.getToday();
        List<CustomHoliday> customHolidays = new ArrayList<>();
        customHolidays.addAll(bot.storage.getAllByFields(
                new CustomHoliday(getShortDateRepresentation(today))));
        customHolidays.addAll(bot.storage.getAllByFields(
                new CustomHoliday(getShortDateRepresentation(today.plusDays(1)))));
        customHolidays.addAll(bot.storage.getAllByFields(
                new CustomHoliday(getShortDateRepresentation(today.plusDays(2)))));

        Map<Long, List<String>> map = new HashMap<>();
        for (CustomHoliday customHoliday : customHolidays) {
            if (!map.containsKey(customHoliday.getChatId()))
                map.put(customHoliday.getChatId(), new ArrayList<>());
            map.get(customHoliday.getChatId()).add(customHoliday.getHolidayName());
        }
        return map;
    }

    private void reminderAboutCustomHolidays(long chatId, LocalDate today, Bot bot) throws Exception {
        CustomHoliday condition = new CustomHoliday(chatId, DateTimeUtil.getShortDateRepresentation(today.plusDays(7)));
        if (!bot.storage.containsByFields(condition))
            return;
        bot.sendMessage(SendMessage.builder()
                .text("Напоминаю, что через неделю будет:\n\n" + bot.storage.getAllByFields(condition).stream()
                        .map(x -> "- " + x.getHolidayName())
                        .collect(Collectors.joining("\n")))
                .chatId(chatId)
                .build());
    }

    private static LocalTime getRoundedTime() {
        LocalTime now = DateTimeUtil.getTimeNow();
        int minutes = now.getMinute();
        int roundedMinutes = (int) (Math.round(minutes / 15.0) * 15) % 60;
        int newHour = now.getHour();
        if (roundedMinutes == 0 && minutes > 30)
            newHour = (newHour + 1) % 24;
        return LocalTime.of(newHour, roundedMinutes);
    }
}
