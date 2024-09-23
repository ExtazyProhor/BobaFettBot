package com.prohor.personal.bobaFettBot.bot.objects;

import com.prohor.personal.bobaFettBot.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class BotCallback implements Identifiable<String> {
    private final String callbackPrefix;

    public BotCallback(String callbackPrefix) {
        this.callbackPrefix = callbackPrefix;
    }

    public abstract void callbackReceived(Update update, Bot bot) throws Exception;

    @Override
    public final String getIdentifier() {
        return callbackPrefix;
    }
}
