package com.prohor.personal.bobaFettBot.bot;

import com.prohor.personal.bobaFettBot.bot.objects.*;
import com.prohor.personal.bobaFettBot.data.DataStorage;
import com.prohor.personal.bobaFettBot.system.ExceptionWriter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.*;

public class Bot extends TelegramLongPollingBot {
    protected final BotService<String, BotCommand> commandService;
    protected final BotPrefixService<BotCallback> callbackService;

    public final DataStorage storage;

    private final String username;
    private final ExceptionWriter exceptionWriter;

    public Bot(String token, String username,
               BotService<String, BotCommand> commandService,
               BotPrefixService<BotCallback> callbackService,
               DataStorage storage,
               ExceptionWriter exceptionWriter) {
        super(token);
        this.username = username;
        this.commandService = commandService;
        this.callbackService = callbackService;
        this.storage = storage;
        this.exceptionWriter = exceptionWriter;
    }

    @Override
    public final String getBotUsername() {
        return username;
    }

    @Override
    public final void onUpdateReceived(Update update) {
        try {
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

    private void hasMessage(Update update) throws Exception {
        String message = update.getMessage().getText().trim();
        if (!message.startsWith("/")) return;
        if (message.contains("@")) {
            String username = message.substring(message.indexOf('@') + 1);
            if (!username.equals(getBotUsername())) return;
            message = message.substring(0, message.indexOf('@'));
        }
        if (commandService.hasTask(message))
            commandService.getTask(message).executeCommand(update, this);
    }

    private void hasCallback(Update update) throws Exception {
        if (!callbackService.hasTask(update.getCallbackQuery().getData())) return;
        commandService.getTask(update.getCallbackQuery().getData()).executeCommand(update, this);
    }

    private void hasMyChatMember(Update update) throws Exception {
        long chatId = update.getMyChatMember().getChat().getId();
        ChatMember newChatMember = update.getMyChatMember().getNewChatMember();

        if (newChatMember instanceof ChatMemberLeft || newChatMember instanceof ChatMemberBanned)
            storage.deleteUser(chatId);
        if (!update.getMyChatMember().getChat().isChannelChat())
            return;
        if (!(newChatMember instanceof ChatMemberAdministrator chatMemberAdministrator))
            return;
        if (!chatMemberAdministrator.getCanPostMessages())
            return;
        if (storage.containsUser(chatId))
            return;
        if (commandService.hasTask("/start"))
            commandService.getTask("/start").executeCommand(update, this);
    }
}