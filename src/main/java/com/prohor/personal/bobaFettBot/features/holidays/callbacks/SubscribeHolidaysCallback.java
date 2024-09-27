package com.prohor.personal.bobaFettBot.features.holidays.callbacks;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.Keyboard;
import com.prohor.personal.bobaFettBot.bot.objects.BotCallback;
import com.prohor.personal.bobaFettBot.data.entities.HolidaysSubscriber;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
                        HolidaysSubscriber subscriber = new HolidaysSubscriber(chatId, time, (short) indent);
                        if (bot.storage.contains(HolidaysSubscriber.class, chatId))
                            bot.storage.update(subscriber);
                        else
                            bot.storage.create(subscriber);
                        bot.editMessageText(EditMessageText.builder()
                                .text("Настройки успешно применены")
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

    public void settingSubscription(LocalTime time, long chatId, Bot bot) throws Exception {
        settingSubscription(time, 0, chatId, null, bot);
    }

    private void settingSubscription(LocalTime time, int indent, long chatId, Integer messageId, Bot bot)
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
                        List.of(TIME_PREFIX + time + INDENTS[0]),
                        List.of(TIME_PREFIX + time + INDENTS[1]),
                        List.of(TIME_PREFIX + time + INDENTS[2]),
                        List.of(getIdentifier() + "confirm." + time + INDENTS[indent])));

        if (messageId == null)
            bot.sendMessage(SendMessage.builder()
                    .text(SETTINGS_MESSAGE)
                    .replyMarkup(keyboardMarkup)
                    .chatId(chatId)
                    .build());
        else
            bot.editMessageText(EditMessageText.builder()
                    .text(SETTINGS_MESSAGE)
                    .replyMarkup(keyboardMarkup)
                    .chatId(chatId)
                    .messageId(messageId)
                    .build());
    }

    public void sendMenu(long chatId, Bot bot) throws Exception {
        boolean subscribed = bot.storage.get(HolidaysSubscriber.class, chatId).getSubscriptionIsActive();
        String prefix = getIdentifier();
        bot.sendMessage(SendMessage.builder()
                .text("Что вы хотите сделать?")
                .replyMarkup(Keyboard.getColumnInlineKeyboard(
                        List.of(
                                "Настроить время рассылки и дату праздников",
                                subscribed ? "Отписаться от рассылки" : "Подписаться на рассылку"),
                        List.of(
                                prefix + "settings",
                                prefix + (subscribed ? "uns" : "sub"))))
                .chatId(chatId)
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
