package com.prohor.personal.bobaFettBot.features.holidays.callbacks;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.objects.BotCallback;
import com.prohor.personal.bobaFettBot.data.entities.UserStatus;
import com.prohor.personal.bobaFettBot.features.holidays.statuses.WaitImportChatId;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public class ImportHolidaysInitCallback extends BotCallback {
    public ImportHolidaysInitCallback() {
        super("holidays.init-import");
    }

    private static ImportHolidaysInitCallback instance;

    public static ImportHolidaysInitCallback getInstance() {
        if (instance == null)
            instance = new ImportHolidaysInitCallback();
        return instance;
    }

    @Override
    public void callbackReceived(CallbackQuery callbackQuery, Bot bot) throws Exception {
        long chatId = callbackQuery.getMessage().getChatId();
        bot.usersWithStatus.add(chatId);
        bot.storage.create(new UserStatus(chatId, WaitImportChatId.getInstance().getIdentifier()));
        bot.editMessageText(EditMessageText.builder()
                .text("Напишите ID пользователя, у которого вы хотите импортировать " +
                        "праздники (узнать ID можно командой /id)")
                .messageId(callbackQuery.getMessage().getMessageId())
                .chatId(chatId)
                .build());
    }
}
