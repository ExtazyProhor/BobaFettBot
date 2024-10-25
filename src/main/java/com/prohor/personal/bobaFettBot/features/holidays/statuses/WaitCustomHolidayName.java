package com.prohor.personal.bobaFettBot.features.holidays.statuses;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.objects.BotStatus;
import com.prohor.personal.bobaFettBot.data.entities.CustomHoliday;
import com.prohor.personal.bobaFettBot.data.entities.UserStatus;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;

import static com.prohor.personal.bobaFettBot.distribution.DateTimeUtil.getShortDateRepresentation;

public class WaitCustomHolidayName extends BotStatus {
    private WaitCustomHolidayName() {
        super("holidays.wait-name.");
    }

    private static WaitCustomHolidayName instance;

    public static WaitCustomHolidayName getInstance() {
        if (instance == null)
            instance = new WaitCustomHolidayName();
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
        String customHolidayName = update.getMessage().getText();
        if (customHolidayName.equals("/cancel")) {
            answer.setText("Создание собственного праздника отменено");
            bot.sendMessage(answer);
            BotStatus.deleteStatus(bot, chatId);
            return;
        }
        String status = getSuffix(bot.storage.get(UserStatus.class, chatId).getStatus());
        if (customHolidayName.length() > 50) {
            answer.setText("Название праздника превышает лимит в 50 символов - оно содержит " +
                    customHolidayName.length() + " знаков. Напишите название заново или отмените " +
                    "добавление с помощью команды /cancel");
            bot.sendMessage(answer);
            return;
        }
        bot.storage.create(new CustomHoliday(chatId,
                getShortDateRepresentation(LocalDate.parse(status)), customHolidayName));
        BotStatus.deleteStatus(bot, chatId);
        answer.setText("Праздник \"" + customHolidayName + "\" успешно добавлен");
        bot.sendMessage(answer);
    }
}
