package com.prohor.personal.bobaFettBot.features.holidays.callbacks;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.Keyboard;
import com.prohor.personal.bobaFettBot.bot.objects.BotCallback;
import com.prohor.personal.bobaFettBot.data.entities.CustomHoliday;
import com.prohor.personal.bobaFettBot.distribution.DateTimeUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.prohor.personal.bobaFettBot.distribution.DateTimeUtil.*;

import java.time.LocalDate;
import java.util.*;

public class CustomHolidayCallback extends BotCallback {
    private CustomHolidayCallback() {
        super("holidays.custom-holiday.");
    }

    @Override
    public void callbackReceived(CallbackQuery callbackQuery, Bot bot) throws Exception {
        String callback = getSuffix(callbackQuery);
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        switch (callback) {
            case "create" -> createCustomHoliday(chatId, messageId, bot);
            case "delete" -> deleteCustomHoliday(0, chatId, messageId, bot);
            case "list" -> listCustomHoliday(0, chatId, messageId, bot);
            case "cancel" -> cancelDeletingCustomHoliday(chatId, messageId, bot);
            default -> {
                String metadata = callback.substring(callback.indexOf('.') + 1);
                callback = callback.substring(0, callback.indexOf('.'));
                switch (callback) {
                    case "del-page<" -> deleteCustomHoliday(Integer.parseInt(metadata) - 1, chatId, messageId, bot);
                    case "del-page>" -> deleteCustomHoliday(Integer.parseInt(metadata) + 1, chatId, messageId, bot);
                    case "del-by-id" -> {
                        CustomHoliday customHoliday = bot.storage.get(CustomHoliday.class, Long.parseLong(metadata));
                        bot.storage.delete(CustomHoliday.class, Long.parseLong(metadata));
                        bot.editMessageText(EditMessageText.builder()
                                .text("Праздник " + getCustomHolidayDescription(customHoliday) + " удален")
                                .chatId(chatId)
                                .messageId(messageId)
                                .build());
                    }
                    case "read-page<" -> listCustomHoliday(Integer.parseInt(metadata) - 1, chatId, messageId, bot);
                    case "read-page>" -> listCustomHoliday(Integer.parseInt(metadata) + 1, chatId, messageId, bot);
                }
            }
        }
    }

    private static String getCustomHolidayDescription(CustomHoliday holiday) {
        return getRussianDate(holiday.getHolidayDate()) + " " + holiday.getHolidayName();
    }

    private static CustomHolidayCallback instance;

    public static CustomHolidayCallback getInstance() {
        if (instance == null)
            instance = new CustomHolidayCallback();
        return instance;
    }

    public void createCustomHoliday(long chatId, Bot bot) throws Exception {
        LocalDate now = DateTimeUtil.getToday();
        bot.sendMessage(SendMessage.builder()
                .chatId(chatId)
                .text(ChooseCustomHolidayDateCallback.getMessageForDate(now))
                .replyMarkup(ChooseCustomHolidayDateCallback.getKeyboardForDateChoose(now))
                .build());
    }

    private void createCustomHoliday(long chatId, int messageId, Bot bot) throws Exception {
        LocalDate now = DateTimeUtil.getToday();
        bot.editMessageText(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(ChooseCustomHolidayDateCallback.getMessageForDate(now))
                .replyMarkup(ChooseCustomHolidayDateCallback.getKeyboardForDateChoose(now))
                .build());
    }

    private static final int PER_PAGE = 7;

    private void deleteCustomHoliday(int page, long chatId, int messageId, Bot bot) throws Exception {
        CustomHoliday holiday = new CustomHoliday();
        holiday.setChatId(chatId);
        List<CustomHoliday> customHolidays = bot.storage.getAllByFields(holiday);
        if (customHolidays.isEmpty()) {
            hasNotHolidays(chatId, messageId, bot);
            return;
        }
        customHolidays.sort(Comparator.comparingInt(CustomHoliday::getHolidayDate)
                .thenComparingLong(CustomHoliday::getCustomHolidayId));
        int totalPages = customHolidays.size() / PER_PAGE + (customHolidays.size() % PER_PAGE == 0 ? 0 : 1);
        if (page >= totalPages)
            page = 0;
        else if (page < 0)
            page = totalPages - 1;

        List<List<String>> buttonsText = new ArrayList<>();
        List<List<String>> buttonsCallback = new ArrayList<>();
        String prefix = getIdentifier();
        for (int i = 0; i < PER_PAGE; ++i) {
            int current = page * PER_PAGE + i;
            if (current >= customHolidays.size())
                break;
            holiday = customHolidays.get(current);
            String fullHoliday = getCustomHolidayDescription(holiday);
            buttonsText.add(List.of(fullHoliday.length() < 60 ? fullHoliday : fullHoliday.substring(0, 60) + "..."));
            buttonsCallback.add(List.of(prefix + "del-by-id" + '.' + holiday.getCustomHolidayId()));
        }
        buttonsText.add(List.of("❌ Отменить удаление ❌"));
        buttonsCallback.add(List.of(prefix + "cancel"));
        if (totalPages > 1) {
            buttonsText.add(List.of("❮", (page + 1) + "/" + totalPages, "❯"));
            buttonsCallback.add(List.of(prefix + "del-page<" + '.' + page, "-", prefix + "del-page>" + '.' + page));
        }

        bot.editMessageText(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text("Выберите праздник, который нужно удалить")
                .replyMarkup(Keyboard.getInlineKeyboard(buttonsText, buttonsCallback))
                .build());
    }

    private void listCustomHoliday(int page, long chatId, int messageId, Bot bot) throws Exception {
        CustomHoliday holiday = new CustomHoliday();
        holiday.setChatId(chatId);
        List<CustomHoliday> customHolidays = bot.storage.getAllByFields(holiday);
        if (customHolidays.isEmpty()) {
            hasNotHolidays(chatId, messageId, bot);
            return;
        }
        customHolidays.sort(Comparator.comparingInt(CustomHoliday::getHolidayDate)
                .thenComparingLong(CustomHoliday::getCustomHolidayId));
        int totalPages = customHolidays.size() / PER_PAGE + (customHolidays.size() % PER_PAGE == 0 ? 0 : 1);
        if (page >= totalPages)
            page = 0;
        else if (page < 0)
            page = totalPages - 1;

        StringBuilder message = new StringBuilder();
        for (int i = 0; i < PER_PAGE; ++i) {
            int current = page * PER_PAGE + i;
            if (current >= customHolidays.size())
                break;
            holiday = customHolidays.get(current);
            message.append(getCustomHolidayDescription(holiday)).append("\n");
        }

        if (totalPages == 1) {
            bot.editMessageText(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(message.toString())
                    .build());
            return;
        }
        String prefix = getIdentifier();
        List<List<String>> buttonsText = List.of(List.of("❮", (page + 1) + "/" + totalPages, "❯"));
        List<List<String>> buttonsCallback = List.of(List.of(
                prefix + "read-page<" + '.' + page,
                "-",
                prefix + "read-page>" + '.' + page));

        bot.editMessageText(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(message.toString())
                .replyMarkup(Keyboard.getInlineKeyboard(buttonsText, buttonsCallback))
                .build());
    }

    private void hasNotHolidays(long chatId, int messageId, Bot bot) throws Exception {
        bot.editMessageText(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text("У вас нет собственных праздников")
                .build());
    }

    private void cancelDeletingCustomHoliday(long chatId, int messageId, Bot bot) throws Exception {
        bot.editMessageText(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text("Вы отменили удаление праздника")
                .build());
    }
}
