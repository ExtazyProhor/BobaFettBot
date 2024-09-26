package com.prohor.personal.bobaFettBot.bot.objects;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.data.entities.UserStatus;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class BotStatus implements Identifiable<String> {
    private final String statusPrefix;

    public BotStatus(String statusPrefix) {
        this.statusPrefix = statusPrefix;
    }

    public abstract void hasStatus(Update update, long chatId, Bot bot) throws Exception;

    protected final String getSuffix(String status) {
        return status.substring(getIdentifier().length());
    }

    @Override
    public final String getIdentifier() {
        return statusPrefix;
    }

    public static void deleteStatus(Bot bot, long chatId) throws Exception {
        bot.usersWithStatus.remove(chatId);
        bot.storage.delete(UserStatus.class, chatId);
    }
}
