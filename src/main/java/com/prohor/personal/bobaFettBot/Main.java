package com.prohor.personal.bobaFettBot;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.objects.*;
import com.prohor.personal.bobaFettBot.data.*;
import com.prohor.personal.bobaFettBot.system.ExceptionWriter;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.*;
import java.nio.file.*;
import java.sql.SQLException;

public class Main {
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        ExceptionWriter exceptionWriter = null;
        try {
            exceptionWriter = System.out::println; // TODO: 22.09.2024 DEBUG EXCEPTION WRITER
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            File directory = path.contains(".jar") ?
                    new File(path).getParentFile() :
                    new File("files/private");
            JSONObject tokens = new JSONObject(Files.readString(Paths.get(directory.toURI()).resolve("tokens.json")));
            JSONObject botTokens = tokens.getJSONObject("bot-info");
            JSONObject databaseTokens = tokens.getJSONObject("database-info");

            ConnectionPool connectionPool = new ConnectionPool(
                    databaseTokens.getString("url"),
                    databaseTokens.getString("username"),
                    databaseTokens.getString("password"),
                    5, 1800);

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            Bot bot = new Bot(
                    botTokens.getString("token"),
                    botTokens.getString("username"),
                    new BotService<>(),
                    new BotPrefixService<>(),
                    new PostgresDataStorage(connectionPool),
                    exceptionWriter);
            telegramBotsApi.registerBot(bot);

        } catch (SQLException | TelegramApiException | IOException e) {
            exceptionWriter.writeException(e);
        }
    }
}
