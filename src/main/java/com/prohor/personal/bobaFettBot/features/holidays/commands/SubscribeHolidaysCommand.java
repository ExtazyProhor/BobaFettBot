package com.prohor.personal.bobaFettBot.features.holidays.commands;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.objects.BotCommand;
import com.prohor.personal.bobaFettBot.data.entities.HolidaysSubscriber;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.SubscribeHolidaysCallback;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalTime;

public class SubscribeHolidaysCommand extends BotCommand {
    public SubscribeHolidaysCommand() {
        super("/sub_holiday", "ежедневная рассылка праздников");
    }

    @Override
    public void executeCommand(Message message, Bot bot) throws Exception {
        long chatId = message.getChatId();
        if (!bot.storage.contains(HolidaysSubscriber.class, chatId))
            SubscribeHolidaysCallback.getInstance().settingSubscription(LocalTime.of(12, 0), chatId, bot);
        else
            SubscribeHolidaysCallback.getInstance().sendMenu(chatId, bot);
    }
}
