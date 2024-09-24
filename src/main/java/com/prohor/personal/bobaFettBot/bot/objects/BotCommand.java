package com.prohor.personal.bobaFettBot.bot.objects;

import com.prohor.personal.bobaFettBot.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Message;

public abstract class BotCommand implements Identifiable<String> {
    private final String command;
    private final String description;

    public BotCommand(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public abstract void executeCommand(Message message, Bot bot) throws Exception;

    public final String getDescription() {
        return description;
    }

    @Override
    public final String getIdentifier() {
        return command;
    }
}
