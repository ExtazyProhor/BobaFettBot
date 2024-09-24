package com.prohor.personal.bobaFettBot.bot.commands;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.objects.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandsList extends BotCommand {
    public CommandsList() {
        super("/commands", null);
    }

    @Override
    public void executeCommand(Update update, Bot bot) throws Exception {
        StringBuilder builder = new StringBuilder();
        for (BotCommand s : bot.commandService) {
            if (s.getIdentifier().equals("/start") || s.getIdentifier().equals("/commands"))
                continue;
            builder.append(s.getIdentifier()).append(" - ").append(s.getDescription()).append("\n");
        }
        bot.execute(SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(builder.toString())
                .build());
    }
}