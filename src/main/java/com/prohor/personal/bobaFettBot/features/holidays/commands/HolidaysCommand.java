package com.prohor.personal.bobaFettBot.features.holidays.commands;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.Keyboard;
import com.prohor.personal.bobaFettBot.bot.objects.BotCommand;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.CustomHolidayInitCallback;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.GetHolidaysInitCallback;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.ImportHolidaysInitCallback;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.SubscribeHolidaysInitCallback;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

public class HolidaysCommand extends BotCommand {
    public HolidaysCommand() {
        super("/holidays", "работа с праздниками");
    }

    private static final String START_MESSAGE = "Выберите, что именно вы хотите сделать";
    private static final InlineKeyboardMarkup KEYBOARD = Keyboard.getColumnInlineKeyboard(
            List.of("узнать какие праздники отмечают в определенный день",
                    "управление собственными праздниками",
                    "импортировать праздники из другого чата",
                    "ежедневная рассылка праздников"),
            List.of(GetHolidaysInitCallback.getInstance().getIdentifier(),
                    CustomHolidayInitCallback.getInstance().getIdentifier(),
                    ImportHolidaysInitCallback.getInstance().getIdentifier(),
                    SubscribeHolidaysInitCallback.getInstance().getIdentifier()));

    @Override
    public void executeCommand(Message message, Bot bot) throws Exception {
        bot.sendMessage(SendMessage.builder()
                .text(START_MESSAGE)
                .replyMarkup(KEYBOARD)
                .chatId(message.getChatId())
                .build());
    }
}
