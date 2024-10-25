package com.prohor.personal.bobaFettBot.features.holidays.callbacks;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.Keyboard;
import com.prohor.personal.bobaFettBot.bot.objects.BotCallback;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

public class GetHolidaysInitCallback extends BotCallback {
    private GetHolidaysInitCallback() {
        super("holidays.init-get");
    }

    private static GetHolidaysInitCallback instance;

    public static GetHolidaysInitCallback getInstance() {
        if (instance == null)
            instance = new GetHolidaysInitCallback();
        return instance;
    }

    private static final String START_MESSAGE = "Выберите день, праздники которого хотите узнать";
    private static final InlineKeyboardMarkup KEYBOARD = Keyboard.getColumnInlineKeyboard(
            List.of("Сегодня", "Завтра", "Послезавтра", "Другой день"),
            List.of(GetHolidaysCallback.getInstance().getIdentifier() + "tod",
                    GetHolidaysCallback.getInstance().getIdentifier() + "tom",
                    GetHolidaysCallback.getInstance().getIdentifier() + "aft",
                    GetHolidaysCallback.getInstance().getIdentifier() + "oth"));

    @Override
    public void callbackReceived(CallbackQuery callbackQuery, Bot bot) throws Exception {
        bot.editMessageText(EditMessageText.builder()
                .text(START_MESSAGE)
                .messageId(callbackQuery.getMessage().getMessageId())
                .chatId(callbackQuery.getMessage().getChatId())
                .replyMarkup(KEYBOARD)
                .build());
    }
}
