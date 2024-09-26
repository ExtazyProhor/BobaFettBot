package com.prohor.personal.bobaFettBot.features.holidays.commands;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.objects.BotCommand;
import com.prohor.personal.bobaFettBot.data.entities.UserStatus;
import com.prohor.personal.bobaFettBot.features.holidays.statuses.WaitImportChatId;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class ImportHolidaysCommand extends BotCommand {
    public ImportHolidaysCommand() {
        super("/import_holidays", "импортировать праздники из другого чата");
    }

    @Override
    public void executeCommand(Message message, Bot bot) throws Exception {
        long chatId = message.getChatId();
        bot.usersWithStatus.add(chatId);
        bot.storage.create(new UserStatus(chatId, WaitImportChatId.getInstance().getIdentifier()));
        bot.sendMessage(SendMessage.builder().text("Напишите ID пользователя, у которого вы хотите импортировать " +
                "праздники (узнать ID можно командой /id)").chatId(chatId).build());
    }
}
