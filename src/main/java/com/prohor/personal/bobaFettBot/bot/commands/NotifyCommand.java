package com.prohor.personal.bobaFettBot.bot.commands;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.objects.BotCommand;
import com.prohor.personal.bobaFettBot.bot.statuses.WaitNotifyMessage;
import com.prohor.personal.bobaFettBot.data.entities.UserStatus;
import com.prohor.personal.bobaFettBot.util.AdminUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

public class NotifyCommand extends BotCommand {
    public NotifyCommand() {
        super("/notify", null);
    }

    @Override
    public void executeCommand(Message message, Bot bot) throws Exception {
        Chat chat = message.getChat();
        long chatId = chat.getId();
        if (!AdminUtils.isAdmin(chatId))
            return;
        bot.storage.create(new UserStatus(chatId, WaitNotifyMessage.getInstance().getIdentifier()));
        bot.usersWithStatus.add(chatId);
        bot.sendMessage(SendMessage.builder()
                .chatId(chatId)
                .text("На первой строчке chatId через запятую, все остальное - сообщение")
                .build());
    }
}
