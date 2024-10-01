package com.prohor.personal.bobaFettBot.features.holidays.commands;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.Keyboard;
import com.prohor.personal.bobaFettBot.bot.objects.BotCommand;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.GetHolidaysCallback;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

public class GetHolidaysCommand extends BotCommand {
    public GetHolidaysCommand() {
        super("/get_holidays", "узнать какие праздники отмечают в определенный день этого года");
    }

    private static final String START_MESSAGE = "Выберите день, праздники которого хотите узнать";
    private final InlineKeyboardMarkup KEYBOARD = Keyboard.getColumnInlineKeyboard(
            List.of("Сегодня", "Завтра", "Послезавтра", "Другой день"),
            List.of(GetHolidaysCallback.getInstance().getIdentifier() + "tod",
                    GetHolidaysCallback.getInstance().getIdentifier() + "tom",
                    GetHolidaysCallback.getInstance().getIdentifier() + "aft",
                    GetHolidaysCallback.getInstance().getIdentifier() + "oth"));

    @Override
    public void executeCommand(Message message, Bot bot) throws Exception {
        bot.sendMessage(SendMessage.builder()
                .text(START_MESSAGE)
                .chatId(message.getChatId())
                .replyMarkup(KEYBOARD)
                .build());
    }
}
