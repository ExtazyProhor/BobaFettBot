package com.prohor.personal.bobaFettBot.bot.objects;

import com.prohor.personal.bobaFettBot.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class BotCommand implements Identifiable<String> {
    private final String command;

    public BotCommand(String command) {
        this.command = command;
    }

    public abstract void executeCommand(Update update, Bot bot) throws Exception;

    @Override
    public final String getIdentifier() {
        return command;
    }
}
