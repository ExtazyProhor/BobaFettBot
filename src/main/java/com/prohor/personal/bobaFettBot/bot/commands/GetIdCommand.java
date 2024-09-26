package com.prohor.personal.bobaFettBot.bot.commands;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.objects.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class GetIdCommand extends BotCommand {
    public GetIdCommand() {
        super("/id", "узнать мой id");
    }

    @Override
    public void executeCommand(Message message, Bot bot) throws Exception {
        long chatId = message.getChatId();
        bot.sendMessage(SendMessage.builder().chatId(chatId).text(String.valueOf(chatId)).build());
    }
}
