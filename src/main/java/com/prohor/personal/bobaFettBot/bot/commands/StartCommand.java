package com.prohor.personal.bobaFettBot.bot.commands;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.objects.BotCommand;
import com.prohor.personal.bobaFettBot.data.entities.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;

public class StartCommand extends BotCommand {
    public StartCommand() {
        super("/start", null);
    }

    @Override
    public void executeCommand(Message message, Bot bot) throws Exception {
        Chat chat = message.getChat();
        long chatId = chat.getId();
        String name = chat.isUserChat() ? chat.getFirstName() : chat.getTitle();
        if (!bot.storage.contains(User.class, chatId))
            if (chat.isUserChat())
                bot.storage.create(new User(chatId, chat.getType(), name, "@" + chat.getUserName()));
            else
                bot.storage.create(new User(chatId, chat.getType(), name));
        bot.execute(SendMessage.builder()
                .chatId(chatId)
                .text("Привет, " + name +
                        "! Чтобы узнать что я могу, используй команду /commands или меню слева от поля ввода")
                .build());
    }
}
