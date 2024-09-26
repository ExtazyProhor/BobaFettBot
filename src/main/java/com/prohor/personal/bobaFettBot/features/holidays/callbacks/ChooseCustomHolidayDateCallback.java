package com.prohor.personal.bobaFettBot.features.holidays.callbacks;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.Keyboard;
import com.prohor.personal.bobaFettBot.bot.objects.BotCallback;
import com.prohor.personal.bobaFettBot.data.entities.UserStatus;
import com.prohor.personal.bobaFettBot.features.holidays.DateTimeUtil;
import com.prohor.personal.bobaFettBot.features.holidays.statuses.WaitCustomHolidayName;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.util.List;

public class ChooseCustomHolidayDateCallback extends BotCallback {
    private ChooseCustomHolidayDateCallback() {
        super("holidays.choose-date.");
    }

    private static ChooseCustomHolidayDateCallback instance;

    public static ChooseCustomHolidayDateCallback getInstance() {
        if (instance == null)
            instance = new ChooseCustomHolidayDateCallback();
        return instance;
    }

    @Override
    public void callbackReceived(CallbackQuery callbackQuery, Bot bot) throws Exception {
        String callbackSuffix = getSuffix(callbackQuery);
        LocalDate date = LocalDate.parse(callbackSuffix.substring(0, callbackSuffix.indexOf('.')));
        String callback = callbackSuffix.substring(callbackSuffix.indexOf('.') + 1);
        switch (callback) {
            case "apply" -> {
                long chatId = callbackQuery.getMessage().getChatId();
                bot.storage.create(new UserStatus(chatId, WaitCustomHolidayName.getInstance().getIdentifier() + date));
                bot.usersWithStatus.add(chatId);
                bot.sendMessage(SendMessage.builder()
                        .chatId(chatId)
                        .text("Теперь напишите название вашего праздника (не более 50 символов):")
                        .build());
            }
            case "<5D" -> updateMessage(date.minusDays(5), callbackQuery, bot);
            case ">5D" -> updateMessage(date.plusDays(5), callbackQuery, bot);
            case "<1D" -> updateMessage(date.minusDays(1), callbackQuery, bot);
            case ">1D" -> updateMessage(date.plusDays(1), callbackQuery, bot);
            case "<3M" -> updateMessage(date.minusMonths(3), callbackQuery, bot);
            case ">3M" -> updateMessage(date.plusMonths(3), callbackQuery, bot);
            case "<1M" -> updateMessage(date.minusMonths(1), callbackQuery, bot);
            case ">1M" -> updateMessage(date.plusMonths(1), callbackQuery, bot);
        }
    }

    private static void updateMessage(LocalDate date, CallbackQuery callback, Bot bot) throws Exception {
        bot.editMessageText(
                EditMessageText.builder()
                        .chatId(callback.getMessage().getChatId())
                        .messageId(callback.getMessage().getMessageId())
                        .text(getMessageForDate(date))
                        .replyMarkup(getKeyboardForDateChoose(date))
                        .build()
        );
    }

    public static String getMessageForDate(LocalDate date) {
        return "Укажите дату вашего праздника\n\n" + date.getDayOfMonth() + " " +
                DateTimeUtil.getRussianMonthNameInDate(date);
    }

    private static final List<List<String>> KEYBOARD_BUTTON_NAMES = List.of(
            List.of("-5 дней", "-1 день", "+1 день", "+5 дней"),
            List.of("-3 месяца", "-1 месяц", "+1 месяц", "+3 месяца"),
            List.of("Применить"));

    public static InlineKeyboardMarkup getKeyboardForDateChoose(LocalDate date) {
        String prefix = getInstance().getIdentifier() + date + '.';
        return Keyboard.getInlineKeyboard(KEYBOARD_BUTTON_NAMES, List.of(
                List.of(prefix + "<5D", prefix + "<1D", prefix + ">1D", prefix + ">5D"),
                List.of(prefix + "<3M", prefix + "<1M", prefix + ">1M", prefix + ">3M"),
                List.of(prefix + "apply")));
    }
}
