package com.prohor.personal.bobaFettBot.features.holidays.callbacks;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.objects.BotCallback;
import com.prohor.personal.bobaFettBot.data.entities.HolidaysSubscriber;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalTime;

public class SubscribeHolidaysInitCallback extends BotCallback {
    public SubscribeHolidaysInitCallback() {
        super("holidays.init-sub");
    }

    private static SubscribeHolidaysInitCallback instance;

    public static SubscribeHolidaysInitCallback getInstance() {
        if (instance == null)
            instance = new SubscribeHolidaysInitCallback();
        return instance;
    }

    @Override
    public void callbackReceived(CallbackQuery callbackQuery, Bot bot) throws Exception {
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        if (!bot.storage.contains(HolidaysSubscriber.class, chatId))
            SubscribeHolidaysCallback.getInstance().settingSubscription(LocalTime.of(12, 0), 0, chatId, messageId, bot);
        else
            SubscribeHolidaysCallback.getInstance().sendMenu(chatId, messageId, bot);
    }
}
