package com.prohor.personal.bobaFettBot.bot.commands;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.objects.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class CommandsList extends BotCommand {
    public CommandsList() {
        super("/commands", null);
    }

    @Override
    public void executeCommand(Message message, Bot bot) throws Exception {
        StringBuilder builder = new StringBuilder();
        for (BotCommand s : bot.commandService) {
            if (s.getIdentifier().equals("/start") || s.getIdentifier().equals("/commands"))
                continue;
            builder.append(s.getIdentifier()).append(" - ").append(s.getDescription()).append("\n");
        }
        if (builder.isEmpty())
            return;
        bot.sendMessage(SendMessage.builder()
                .chatId(message.getChatId())
                .text(builder.toString())
                .build());
    }
}
