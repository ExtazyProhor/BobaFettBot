package com.prohor.personal.bobaFettBot.features.holidays.callbacks;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.Keyboard;
import com.prohor.personal.bobaFettBot.bot.objects.BotCallback;
import com.prohor.personal.bobaFettBot.data.entities.CustomHoliday;
import com.prohor.personal.bobaFettBot.distribution.DateTimeUtil;
import com.prohor.personal.bobaFettBot.features.holidays.Holidays;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.util.List;

public class GetHolidaysCallback extends BotCallback {
    public GetHolidaysCallback() {
        super("holidays.get.");
    }

    private static GetHolidaysCallback instance;

    public static GetHolidaysCallback getInstance() {
        if (instance == null)
            instance = new GetHolidaysCallback();
        return instance;
    }

    @Override
    public void callbackReceived(CallbackQuery callbackQuery, Bot bot) throws Exception {
        String callback = getSuffix(callbackQuery);
        long chatId = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        LocalDate today = DateTimeUtil.getToday();
        switch (callback) {
            case "tod" -> sendHolidays(messageId, chatId, today, bot);
            case "tom" -> sendHolidays(messageId, chatId, today.plusDays(1), bot);
            case "aft" -> sendHolidays(messageId, chatId, today.plusDays(2), bot);
            case "oth" -> chooseDate(messageId, chatId, today.withYear(2024), bot);
            default -> {
                LocalDate date = LocalDate.parse(callback.substring(0, callback.indexOf('.')));
                date = fixDate(date);
                callback = callback.substring(callback.indexOf('.') + 1);
                switch (callback) {
                    case "<5D" -> chooseDate(messageId, chatId, date.minusDays(5), bot);
                    case ">5D" -> chooseDate(messageId, chatId, date.plusDays(5), bot);
                    case "<1D" -> chooseDate(messageId, chatId, date.minusDays(1), bot);
                    case ">1D" -> chooseDate(messageId, chatId, date.plusDays(1), bot);
                    case "<3M" -> chooseDate(messageId, chatId, date.minusMonths(3), bot);
                    case ">3M" -> chooseDate(messageId, chatId, date.plusMonths(3), bot);
                    case "<1M" -> chooseDate(messageId, chatId, date.minusMonths(1), bot);
                    case ">1M" -> chooseDate(messageId, chatId, date.plusMonths(1), bot);
                    case "apply" -> bot.editMessageText(EditMessageText.builder()
                            .text(Holidays.getHolidaysMessage(bot.storage.getAllByFields(new CustomHoliday(
                                            chatId, DateTimeUtil.getShortDateRepresentation(date)))
                                    .stream().map(CustomHoliday::getHolidayName).toList(), date))
                            .chatId(chatId)
                            .messageId(messageId)
                            .build());
                }
            }
        }
    }

    private static final String CHOOSE_DATE_MESSAGE = "Выберите дату праздников (этого или следующего года)";
    private static final List<String> ACCEPT_ROW = List.of("Выбрать эту дату");
    private static final List<String> DAY_ROW_TEXT = List.of("-5 дней", "-1 день", "+1 день", "+5 дней");
    private static final List<String> MONTH_ROW_TEXT = List.of("-3 месяца", "-1 месяц", "+1 месяц", "+3 месяца");

    private void chooseDate(Integer messageId, long chatId, LocalDate date, Bot bot) throws Exception {
        date = fixDate(date);
        String prefix = getIdentifier() + date + '.';
        InlineKeyboardMarkup keyboard = Keyboard.getInlineKeyboard(
                List.of(List.of(DateTimeUtil.getRussianDate(date) + " " + date.getYear()),
                        DAY_ROW_TEXT, MONTH_ROW_TEXT, ACCEPT_ROW),
                List.of(List.of("-"),
                        List.of(prefix + "<5D", prefix + "<1D", prefix + ">1D", prefix + ">5D"),
                        List.of(prefix + "<3M", prefix + "<1M", prefix + ">1M", prefix + ">3M"),
                        List.of(prefix + "apply")));

        bot.editMessageText(EditMessageText.builder().text(CHOOSE_DATE_MESSAGE)
                .messageId(messageId).replyMarkup(keyboard).chatId(chatId).build());
    }

    private void sendHolidays(int messageId, long chatId, LocalDate date, Bot bot) throws Exception {
        bot.editMessageText(EditMessageText.builder()
                .text(Holidays.getHolidaysMessage(bot.storage.getAllByFields(
                                new CustomHoliday(chatId, DateTimeUtil.getShortDateRepresentation(date))).stream()
                        .map(CustomHoliday::getHolidayName)
                        .toList(), date))
                .chatId(chatId)
                .messageId(messageId)
                .build());
    }

    private LocalDate fixDate(LocalDate date) {
        int thisYear = DateTimeUtil.getToday().getYear();
        if (date.getYear() > thisYear + 1)
            return date.withYear(thisYear + 1);
        if (date.getYear() < thisYear)
            return date.withYear(thisYear);
        return date;
    }
}
