package com.prohor.personal.bobaFettBot.bot.objects;

import com.prohor.personal.bobaFettBot.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public abstract class BotCallback implements Identifiable<String> {
    private final String callbackPrefix;

    public BotCallback(String callbackPrefix) {
        this.callbackPrefix = callbackPrefix;
    }

    public abstract void callbackReceived(CallbackQuery callbackQuery, Bot bot) throws Exception;

    protected final String getSuffix(CallbackQuery callbackQuery) {
        return callbackQuery.getData().substring(getIdentifier().length());
    }

    @Override
    public final String getIdentifier() {
        return callbackPrefix;
    }
}
