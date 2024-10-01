package com.prohor.personal.bobaFettBot.features.holidays.statuses;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.objects.BotStatus;
import com.prohor.personal.bobaFettBot.data.entities.CustomHoliday;
import com.prohor.personal.bobaFettBot.data.entities.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class WaitImportChatId extends BotStatus {
    private WaitImportChatId() {
        super("holidays.wait-import-id");
    }

    private static WaitImportChatId instance;

    public static WaitImportChatId getInstance() {
        if (instance == null)
            instance = new WaitImportChatId();
        return instance;
    }

    @Override
    public void hasStatus(Update update, long chatId, Bot bot) throws Exception {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            BotStatus.deleteStatus(bot, chatId);
            bot.onUpdateReceived(update);
            return;
        }
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);
        String importChatId = update.getMessage().getText();
        if (importChatId.equals("/cancel")) {
            answer.setText("Импорт праздников отменен");
            bot.sendMessage(answer);
            BotStatus.deleteStatus(bot, chatId);
            return;
        }

        long importChatIdLong;
        try {
            importChatIdLong = Long.parseLong(importChatId);
        } catch (NumberFormatException e) {
            answer.setText("Неправильный формат ID. Это должно быть целое число. Напишите ID заново или отмените " +
                    "импорт праздников с помощью команды /cancel");
            bot.sendMessage(answer);
            return;
        }

        BotStatus.deleteStatus(bot, chatId);
        if (!bot.storage.contains(User.class, importChatIdLong)) {
            answer.setText("Пользователь с таким ID не найден");
            bot.sendMessage(answer);
            return;
        }

        CustomHoliday customHoliday = new CustomHoliday();
        customHoliday.setChatId(importChatIdLong);
        List<CustomHoliday> customHolidays = bot.storage.getAllByFields(customHoliday);
        if (customHolidays.size() == 0)
            answer.setText("У пользователя с указанным ID нет собственных праздников");
        else {
            for (CustomHoliday holiday : customHolidays)
                bot.storage.create(new CustomHoliday(chatId, holiday.getHolidayDate(), holiday.getHolidayName()));
            answer.setText(customHolidays.size() + " праздников успешно импортированы. Для просмотра списка ваших " +
                    "праздников используйте команду /custom_holiday");
        }
        bot.sendMessage(answer);
    }
}
