package com.prohor.personal.bobaFettBot.bot;

import com.prohor.personal.bobaFettBot.bot.objects.*;
import com.prohor.personal.bobaFettBot.data.DataStorage;
import com.prohor.personal.bobaFettBot.data.entities.ChatOwner;
import com.prohor.personal.bobaFettBot.data.entities.User;
import com.prohor.personal.bobaFettBot.system.ExceptionWriter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    public final BotService<String, BotCommand> commandService;
    private final BotPrefixService<BotCallback> callbackService;

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

    public final void sendMessage(SendMessage message) throws Exception {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            checkException(e, message.getChatId());
        }
    }

    public final void editMessageReplyMarkup(EditMessageReplyMarkup editMessageReplyMarkup) throws Exception {
        try {
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            checkException(e, editMessageReplyMarkup.getChatId());
        }
    }

    public final void editMessageText(EditMessageText editMessageText) throws Exception {
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
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

        if (newChatMember instanceof ChatMemberLeft || newChatMember instanceof ChatMemberBanned)
            if (storage.contains(User.class, chatId)) {
                storage.delete(User.class, chatId);
                return;
            }
        if (!update.getMyChatMember().getChat().isChannelChat())
            return;
        if (!(newChatMember instanceof ChatMemberAdministrator chatMemberAdministrator))
            return;
        if (!chatMemberAdministrator.getCanPostMessages())
            return;
        if (storage.contains(User.class, chatId))
            return;
        Chat chat = update.getMyChatMember().getChat();
        storage.create(new User(chat.getId(), chat.getType(), chat.getTitle()));

        boolean sent = true;
        long ownerId = update.getMyChatMember().getFrom().getId();
        try {
            execute(SendMessage.builder()
                    .chatId(ownerId)
                    .text("В этом чате вы теперь можете управлять поведением бота в канале \"" + chat.getTitle() +
                            "\". Для этого используйте команду /my_channels. Чтобы поменять управляющего ботом в " +
                            "этом канале на другого пользователя, этот пользователь должен добавить бота в канал " +
                            "сам, после предварительного удаления")
                    .build());
        } catch (TelegramApiException e) {
            sent = false;
        }
        if (sent)
            storage.create(new ChatOwner(chatId, ownerId));
        else
            sendMessage(SendMessage.builder()
                    .chatId(chatId)
                    .text("Личный чат с пользователем " + update.getMyChatMember().getFrom().getFirstName() +
                            ", добавившим бота в этот канал недоступен. Настройка невозможна. Начните диалог с " +
                            "ботом, затем удалите его из канала и добавьте заново")
                    .build());
    }
}
