package com.prohor.personal.bobaFettBot.bot.commands;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.objects.BotCommand;
import com.prohor.personal.bobaFettBot.data.entities.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCommand extends BotCommand {
    public StartCommand() {
        super("/start", null);
    }

    @Override
    public void executeCommand(Update update, Bot bot) throws Exception {
        long chatId = update.getMessage().getChatId();
        Chat chat = update.getMessage().getChat();
        if (!bot.storage.contains(User.class, chatId))
            bot.storage.create(new User(
                    chatId,
                    chat.getType(),
                    chat.isUserChat() ? chat.getFirstName() : chat.getTitle()
            ));
        bot.execute(SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Привет, " + update.getMessage().getChat().getTitle() +
                        "! Чтобы узнать что я могу, используй команду /commands или меню слева от поля ввода")
                .build());
    }
}
