package com.prohor.personal.bobaFettBot.bot.commands;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.objects.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class CancelCommand extends BotCommand {
    public CancelCommand() {
        super("/cancel", "отменить действие");
    }

    @Override
    public void executeCommand(Message message, Bot bot) throws Exception {
        bot.sendMessage(SendMessage.builder().text("Отменять нечего").chatId(message.getChatId()).build());
    }
}
