package com.prohor.personal.bobaFettBot.features.holidays.commands;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.Keyboard;
import com.prohor.personal.bobaFettBot.bot.objects.BotCommand;
import com.prohor.personal.bobaFettBot.data.entities.CustomHoliday;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.CustomHolidayCallback;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public class CustomHolidayCommand extends BotCommand {
    public CustomHolidayCommand() {
        super("/custom_holiday", "управление собственными праздниками");
    }

    private static final List<String> KEYBOARD_BUTTONS = List.of("Создать", "Удалить", "Просмотреть список");

    @Override
    public void executeCommand(Message message, Bot bot) throws Exception {
        CustomHoliday customHoliday = new CustomHoliday();
        customHoliday.setChatId(message.getChatId());
        if (bot.storage.countByField(customHoliday) == 0) {
            CustomHolidayCallback.getInstance().createCustomHoliday(message.getChatId(), bot);
            return;
        }
        bot.sendMessage(SendMessage.builder()
                .chatId(message.getChatId())
                .text("Вы хотите создать новый собственный праздник, удалить существующий или просмотреть их список?")
                .replyMarkup(Keyboard.getColumnInlineKeyboard(KEYBOARD_BUTTONS,
                        List.of(CustomHolidayCallback.getInstance().getIdentifier() + "create",
                                CustomHolidayCallback.getInstance().getIdentifier() + "delete",
                                CustomHolidayCallback.getInstance().getIdentifier() + "list")
                ))
                .build());
    }
}
