package com.prohor.personal.bobaFettBot;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.commands.*;
import com.prohor.personal.bobaFettBot.bot.objects.*;
import com.prohor.personal.bobaFettBot.data.*;
import com.prohor.personal.bobaFettBot.distribution.Distributor;
import com.prohor.personal.bobaFettBot.features.holidays.*;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.*;
import com.prohor.personal.bobaFettBot.features.holidays.commands.HolidaysCommand;
import com.prohor.personal.bobaFettBot.features.holidays.statuses.*;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.*;
import java.nio.file.*;

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
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File directory = path.contains(".jar") ?
                new File(path).getParentFile() :
                new File("files/private");

        try {
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
                    new BotService<>(
                            new StartCommand(),
                            new CommandsList(),
                            new CancelCommand(),
                            new GetIdCommand(),

                            new HolidaysCommand()),
                    new BotPrefixService<>(
                            ChooseCustomHolidayDateCallback.getInstance(),
                            CustomHolidayCallback.getInstance(),
                            SubscribeHolidaysCallback.getInstance(),
                            GetHolidaysCallback.getInstance(),
                            SubscribeHolidaysInitCallback.getInstance(),
                            CustomHolidayInitCallback.getInstance(),
                            GetHolidaysInitCallback.getInstance(),
                            ImportHolidaysInitCallback.getInstance()),
                    new BotPrefixService<>(
                            WaitCustomHolidayName.getInstance(),
                            WaitImportChatId.getInstance()),
                    new PostgresDataStorage(connectionPool));
            telegramBotsApi.registerBot(bot);

            Holidays.init(directory);
            new Distributor(bot, new HolidaysDistributor());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
