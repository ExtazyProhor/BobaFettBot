package com.prohor.personal.bobaFettBot.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Keyboard {
    public static InlineKeyboardMarkup getInlineKeyboard(List<List<String>> btnText, List<List<String>> btnCallback) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = 0; i < btnText.size(); ++i) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < btnText.get(i).size(); ++j) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(btnText.get(i).get(j));
                button.setCallbackData(btnCallback.get(i).get(j));
                row.add(button);
            }
            keyboard.add(row);
        }

        markup.setKeyboard(keyboard);
        return markup;
    }

    public static InlineKeyboardMarkup getColumnInlineKeyboard(List<String> btnText, List<String> btnCallback) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = 0; i < btnText.size(); ++i) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(btnText.get(i));
            button.setCallbackData(btnCallback.get(i));
            row.add(button);
            keyboard.add(row);
        }

        markup.setKeyboard(keyboard);
        return markup;
    }
}
