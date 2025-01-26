package com.prohor.personal.bobaFettBot.bot.statuses;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.objects.BotStatus;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class WaitNotifyMessage extends BotStatus {
    private WaitNotifyMessage() {
        super("admin.wait-notify-message");
    }

    private static WaitNotifyMessage instance;

    public static WaitNotifyMessage getInstance() {
        if (instance == null)
            instance = new WaitNotifyMessage();
        return instance;
    }

    @Override
    public void hasStatus(Update update, long chatId, Bot bot) throws Exception {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            BotStatus.deleteStatus(bot, chatId);
            bot.onUpdateReceived(update);
            return;
        }

        String message = update.getMessage().getText();
        if (message.equals("/cancel")) {
            sendAnswer(chatId, "Отправка сообщения отменена", bot, true);
            return;
        }
        int lineIndex = message.indexOf('\n');
        if (lineIndex < 0) {
            sendAnswer(chatId, "Неверный формат, должно быть как минимум 2 строки", bot, false);
            return;
        }
        List<Long> chatIds = new ArrayList<>();
        for (String part : message.substring(0, lineIndex).split(",")) {
            try {
                chatIds.add(Long.parseLong(part));
            } catch (NumberFormatException nfe) {
                sendAnswer(chatId, "Неверный формат chatId: \"" + part + "\"", bot, false);
                return;
            }
        }
        BotStatus.deleteStatus(bot, chatId);
        message = message.substring(lineIndex + 1);
        for (Long chat : chatIds) {
            bot.sendMessage(SendMessage.builder()
                    .chatId(chat)
                    .text(message)
                    .build());
        }
    }

    private static void sendAnswer(long chatId, String text, Bot bot, boolean delStatus) throws Exception {
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);
        answer.setText(text);
        bot.sendMessage(answer);
        if (delStatus)
            BotStatus.deleteStatus(bot, chatId);
    }
}
