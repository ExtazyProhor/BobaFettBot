package com.prohor.personal.bobaFettBot.features.holidays.callbacks;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.Keyboard;
import com.prohor.personal.bobaFettBot.bot.objects.BotCallback;
import com.prohor.personal.bobaFettBot.data.entities.CustomHoliday;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

public class CustomHolidayInitCallback extends BotCallback {
    private CustomHolidayInitCallback() {
        super("holidays.init-custom");
    }

    private static CustomHolidayInitCallback instance;

    public static CustomHolidayInitCallback getInstance() {
        if (instance == null)
            instance = new CustomHolidayInitCallback();
        return instance;
    }

    private static final String TEXT =
            "Вы хотите создать новый собственный праздник, удалить существующий или просмотреть их список?";
    private static final List<String> KEYBOARD_BUTTONS = List.of("Создать", "Удалить", "Просмотреть список");

    @Override
    public void callbackReceived(CallbackQuery callbackQuery, Bot bot) throws Exception {
        CustomHoliday customHoliday = new CustomHoliday();
        customHoliday.setChatId(callbackQuery.getMessage().getChatId());
        if (bot.storage.countByFields(customHoliday) == 0) {
            CustomHolidayCallback.getInstance().createCustomHoliday(callbackQuery.getMessage().getChatId(), bot);
            return;
        }
        bot.editMessageText(EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(TEXT)
                .replyMarkup(Keyboard.getColumnInlineKeyboard(KEYBOARD_BUTTONS,
                        List.of(CustomHolidayCallback.getInstance().getIdentifier() + "create",
                                CustomHolidayCallback.getInstance().getIdentifier() + "delete",
                                CustomHolidayCallback.getInstance().getIdentifier() + "list")
                ))
                .build());
    }
}
