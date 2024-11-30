package com.prohor.personal.bobaFettBot.features.holidays.callbacks;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.Keyboard;
import com.prohor.personal.bobaFettBot.bot.objects.BotCallback;
import com.prohor.personal.bobaFettBot.data.entities.HolidaysSubscriber;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalTime;
import java.util.*;

public class SubscribeHolidaysCallback extends BotCallback {
    private SubscribeHolidaysCallback() {
        super("holidays.subscribe.");
    }

    private static SubscribeHolidaysCallback instance;

    public static SubscribeHolidaysCallback getInstance() {
        if (instance == null)
            instance = new SubscribeHolidaysCallback();
        return instance;
    }

    @Override
    public void callbackReceived(CallbackQuery callbackQuery, Bot bot) throws Exception {
        String callback = getSuffix(callbackQuery);
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        switch (callback) {
            case "settings" -> {
                HolidaysSubscriber subscriber = bot.storage.get(HolidaysSubscriber.class, chatId);
                settingSubscription(subscriber.getDailyDistributionTime(), subscriber.getIndentationOfDays(),
                        chatId, messageId, bot);
            }
            case "sub" -> subscribeControl(true, chatId, messageId, bot);
            case "uns" -> subscribeControl(false, chatId, messageId, bot);
            default -> {
                String[] parts = callback.split("\\.");
                callback = parts[0];
                LocalTime time = LocalTime.parse(parts[1]);
                int indent = Integer.parseInt(parts[2]);
                switch (callback) {
                    case "time" -> settingSubscription(time, indent, chatId, messageId, bot);
                    case "confirm" -> {
                        HolidaysSubscriber subscriber = new HolidaysSubscriber(chatId, time, (short) indent, true);
                        bot.storage.update(subscriber);
                        bot.editMessageText(EditMessageText.builder()
                                .text("Настройки успешно применены. В " + time + " каждый день будет приходить " +
                                        "список праздников " + INDENT_TEXT[indent])
                                .messageId(messageId)
                                .chatId(chatId)
                                .build());
                    }
                }
            }
        }
    }

    private static final String TIME_PREFIX = getInstance().getIdentifier() + "time.";
    private static final String SETTINGS_MESSAGE =
            "Выберите время ежедневной рассылки и день праздников, относительно даты рассылки";
    private static final List<String> TIME_TEXT = List.of("-1:00", "-0:15", "+0:15", "+1:00");
    private static final String[] INDENTS = {".0", ".1", ".2"};
    private static final String[] INDENT_TEXT = {"того же дня", "следующего дня", "после-следующего дня"};

    public void settingSubscription(LocalTime time, int indent, long chatId, int messageId, Bot bot)
            throws Exception {

        InlineKeyboardMarkup keyboardMarkup = Keyboard.getInlineKeyboard(
                List.of(
                        List.of("Установить время: " + time),
                        TIME_TEXT,
                        List.of("тот же день " + (indent == 0 ? "✅" : "◽")),
                        List.of("день после " + (indent == 1 ? "✅" : "◽")),
                        List.of("через день " + (indent == 2 ? "✅" : "◽")),
                        List.of("Применить")),
                List.of(
                        List.of("-"),
                        List.of(
                                TIME_PREFIX + time.minusHours(1) + INDENTS[indent],
                                TIME_PREFIX + time.minusMinutes(15) + INDENTS[indent],
                                TIME_PREFIX + time.plusMinutes(15) + INDENTS[indent],
                                TIME_PREFIX + time.plusHours(1) + INDENTS[indent]),
                        List.of(indent == 0 ? "-" : (TIME_PREFIX + time + INDENTS[0])),
                        List.of(indent == 1 ? "-" : (TIME_PREFIX + time + INDENTS[1])),
                        List.of(indent == 2 ? "-" : (TIME_PREFIX + time + INDENTS[2])),
                        List.of(getIdentifier() + "confirm." + time + INDENTS[indent])));

        bot.editMessageText(EditMessageText.builder()
                .text(SETTINGS_MESSAGE)
                .replyMarkup(keyboardMarkup)
                .chatId(chatId)
                .messageId(messageId)
                .build());
    }

    private final InlineKeyboardMarkup MENU_KEYBOARD_SUB = Keyboard.getColumnInlineKeyboard(
            List.of("Настроить время рассылки и дату праздников", "Отписаться от рассылки"),
            List.of(getIdentifier() + "settings", getIdentifier() + "uns"));
    private final InlineKeyboardMarkup MENU_KEYBOARD_UNS = Keyboard.getColumnInlineKeyboard(
            List.of("Подписаться на рассылку"), List.of(getIdentifier() + "sub"));

    public void sendMenu(long chatId, int messageId, Bot bot) throws Exception {
        boolean subscribed = bot.storage.get(HolidaysSubscriber.class, chatId).getSubscriptionIsActive();
        bot.editMessageText(EditMessageText.builder()
                .text("Что вы хотите сделать?")
                .replyMarkup(subscribed ? MENU_KEYBOARD_SUB : MENU_KEYBOARD_UNS)
                .chatId(chatId)
                .messageId(messageId)
                .build());
    }

    private void subscribeControl(boolean isSubscribe, long chatId, int messageId, Bot bot) throws Exception {
        HolidaysSubscriber subscriber = bot.storage.get(HolidaysSubscriber.class, chatId);
        subscriber.setSubscriptionIsActive(isSubscribe);
        bot.storage.update(subscriber);
        bot.editMessageText(EditMessageText.builder()
                .text("Теперь вы " + (isSubscribe ? "" : "не ") + "будете получать рассылку праздников")
                .messageId(messageId)
                .chatId(chatId)
                .build());
    }
}
