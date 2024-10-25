package com.prohor.personal.bobaFettBot.features.holidays;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.data.entities.*;
import com.prohor.personal.bobaFettBot.distribution.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.prohor.personal.bobaFettBot.distribution.DateTimeUtil.getShortDateRepresentation;

public class HolidaysDistributor implements DistributionTask {
    @Override
    public void distribute(Bot bot, LocalTime roundedNow) throws Exception {
        HolidaysSubscriber atTime = new HolidaysSubscriber();
        LocalDate today = DateTimeUtil.getToday();
        if (today.getDayOfYear() == 1 && roundedNow.getHour() == 0 && roundedNow.getMinute() == 0)
            Holidays.updateYear(today);
        atTime.setDailyDistributionTime(roundedNow);
        atTime.setSubscriptionIsActive(true);

        List<Map<Long, List<String>>> usersCustomHolidays = getUsersCustomHolidays(bot);
        List<HolidaysSubscriber> subscribers = bot.storage.getAllByFields(atTime);
        for (HolidaysSubscriber subscriber : subscribers) {
            long chatId = subscriber.getChatId();
            reminderAboutCustomHolidays(chatId, today, bot);
            int indent = subscriber.getIndentationOfDays();
            bot.sendMessage(SendMessage.builder()
                    .chatId(chatId)
                    .text(Holidays.getHolidaysMessage(usersCustomHolidays.get(indent).get(subscriber.getChatId()),
                            today.plusDays(indent)))
                    .build());
        }
    }

    // List by indents < Map < chatId, List of holidays < String > > >
    private List<Map<Long, List<String>>> getUsersCustomHolidays(Bot bot) throws Exception {
        LocalDate today = DateTimeUtil.getToday();
        List<Map<Long, List<String>>> list = new ArrayList<>();

        for (int i = 0; i < 3; ++i) {
            List<CustomHoliday> customHolidays = bot.storage.getAllByFields(
                    new CustomHoliday(getShortDateRepresentation(today.plusDays(i))));
            Map<Long, List<String>> map = new HashMap<>();
            for (CustomHoliday customHoliday : customHolidays) {
                if (!map.containsKey(customHoliday.getChatId()))
                    map.put(customHoliday.getChatId(), new ArrayList<>());
                map.get(customHoliday.getChatId()).add(customHoliday.getHolidayName());
            }
            list.add(map);
        }
        return list;
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
}
