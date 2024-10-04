package com.prohor.personal.bobaFettBot.bot;

import com.prohor.personal.bobaFettBot.bot.objects.*;
import com.prohor.personal.bobaFettBot.data.DataStorage;
import com.prohor.personal.bobaFettBot.data.entities.User;
import com.prohor.personal.bobaFettBot.data.entities.UserStatus;
import com.prohor.personal.bobaFettBot.system.ExceptionWriter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class Bot extends TelegramLongPollingBot {
    private final BotService<String, BotCommand> commandService;
    private final BotPrefixService<BotCallback> callbackService;
    private final BotPrefixService<BotStatus> statusService;

    public final DataStorage storage;
    public final Set<Long> usersWithStatus;

    private final String username;
    private final ExceptionWriter exceptionWriter;

    public Bot(String token, String username,
               BotService<String, BotCommand> commandService,
               BotPrefixService<BotCallback> callbackService,
               BotPrefixService<BotStatus> statusService,
               DataStorage storage,
               ExceptionWriter exceptionWriter) throws Exception {
        super(token);
        this.username = username;
        this.commandService = commandService;
        this.callbackService = callbackService;
        this.statusService = statusService;
        this.storage = storage;
        this.exceptionWriter = exceptionWriter;

        this.usersWithStatus = new HashSet<>();
        this.usersWithStatus.addAll(storage.getAll(UserStatus.class).stream().map(UserStatus::getChatId).toList());
    }

    @Override
    public final String getBotUsername() {
        return username;
    }

    @Override
    public final void onUpdateReceived(Update update) {
        try {
            Long chatId = null;
            if (update.hasMessage())
                chatId = update.getMessage().getChatId();
            else if (update.hasCallbackQuery())
                chatId = update.getCallbackQuery().getMessage().getChatId();
            if (chatId != null && usersWithStatus.contains(chatId)) {
                String status = storage.get(UserStatus.class, chatId).getStatus();
                if (statusService.hasTask(status))
                    statusService.getTask(status).hasStatus(update, chatId, this);
                return;
            }

            if (update.hasMessage() && update.getMessage().hasText())
                hasMessage(update);
            else if (update.hasCallbackQuery())
                hasCallback(update);
            else if (update.hasMyChatMember())
                hasMyChatMember(update);
        } catch (Exception e) {
            exceptionWriter.writeException(e);
        }
    }

    public final void sendMessage(SendMessage message) throws Exception {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            checkException(e, message.getChatId());
        }
    }

    public final void editMessageText(EditMessageText editMessageText) throws Exception {
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            if (e.getMessage().contains("message is not modified"))
                return;
            checkException(e, editMessageText.getChatId());
        }
    }

    private void checkException(TelegramApiException e, String chatId) throws Exception {
        String exception = e.getMessage();
        if (exception.contains("user is deactivated") ||
                exception.contains("bot was blocked by the user") ||
                exception.contains("group chat was upgraded to a supergroup chat"))
            storage.delete(User.class, Long.parseLong(chatId));
        else
            throw e;
    }

    private void hasMessage(Update update) throws Exception {
        if (update.getMessage().getChat().isChannelChat())
            return;
        String message = update.getMessage().getText().trim();
        if (!message.startsWith("/"))
            return;
        if (message.contains("@")) {
            String username = message.substring(message.indexOf('@') + 1);
            if (!username.equals(getBotUsername()))
                return;
            message = message.substring(0, message.indexOf('@'));
        }
        if (commandService.hasTask(message))
            commandService.getTask(message).executeCommand(update.getMessage(), this);
    }

    private void hasCallback(Update update) throws Exception {
        if (!callbackService.hasTask(update.getCallbackQuery().getData())) return;
        callbackService.getTask(update.getCallbackQuery().getData()).callbackReceived(update.getCallbackQuery(), this);
    }

    private void hasMyChatMember(Update update) throws Exception {
        long chatId = update.getMyChatMember().getChat().getId();
        ChatMember newChatMember = update.getMyChatMember().getNewChatMember();
        Chat chat = update.getMyChatMember().getChat();

        if (!storage.contains(User.class, chatId))
            storage.create(new User(chatId, chat.getType(), chat.getTitle()));

        if (newChatMember instanceof ChatMemberLeft || newChatMember instanceof ChatMemberBanned)
            if (storage.contains(User.class, chatId))
                storage.delete(User.class, chatId);
    }

    public Collection<BotCommand> getAllCommands() {
        return commandService.getAllCommands();
    }
}
