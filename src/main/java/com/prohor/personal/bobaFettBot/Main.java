package com.prohor.personal.bobaFettBot;

import com.prohor.personal.bobaFettBot.bot.Bot;
import com.prohor.personal.bobaFettBot.bot.commands.CancelCommand;
import com.prohor.personal.bobaFettBot.bot.commands.CommandsList;
import com.prohor.personal.bobaFettBot.bot.commands.GetIdCommand;
import com.prohor.personal.bobaFettBot.bot.commands.NotifyCommand;
import com.prohor.personal.bobaFettBot.bot.commands.StartCommand;
import com.prohor.personal.bobaFettBot.bot.objects.BotPrefixService;
import com.prohor.personal.bobaFettBot.bot.objects.BotService;
import com.prohor.personal.bobaFettBot.bot.statuses.WaitNotifyMessage;
import com.prohor.personal.bobaFettBot.data.PostgresDataStorage;
import com.prohor.personal.bobaFettBot.distribution.Distributor;
import com.prohor.personal.bobaFettBot.features.holidays.Holidays;
import com.prohor.personal.bobaFettBot.features.holidays.HolidaysDistributor;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.ChooseCustomHolidayDateCallback;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.CustomHolidayCallback;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.CustomHolidayInitCallback;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.GetHolidaysCallback;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.GetHolidaysInitCallback;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.ImportHolidaysInitCallback;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.SubscribeHolidaysCallback;
import com.prohor.personal.bobaFettBot.features.holidays.callbacks.SubscribeHolidaysInitCallback;
import com.prohor.personal.bobaFettBot.features.holidays.commands.HolidaysCommand;
import com.prohor.personal.bobaFettBot.features.holidays.statuses.WaitCustomHolidayName;
import com.prohor.personal.bobaFettBot.features.holidays.statuses.WaitImportChatId;
import com.prohor.personal.bobaFettBot.util.AdminUtils;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.util.List;
import java.util.Properties;

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
            Properties properties = new Properties();
            properties.load(Main.class.getClassLoader().getResourceAsStream("application.properties"));
            List.of(properties.getProperty("admin.admins-ids").split(",")).forEach(
                    id -> AdminUtils.addAdmin(Long.parseLong(id))
            );

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            Bot bot = new Bot(
                    properties.getProperty("bot.token"),
                    properties.getProperty("bot.username"),
                    new BotService<>(
                            new StartCommand(),
                            new CommandsList(),
                            new CancelCommand(),
                            new GetIdCommand(),
                            new NotifyCommand(),
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
                            WaitNotifyMessage.getInstance(),
                            WaitCustomHolidayName.getInstance(),
                            WaitImportChatId.getInstance()),
                    new PostgresDataStorage(
                            properties.getProperty("database.url"),
                            properties.getProperty("database.username"),
                            properties.getProperty("database.password")));
            telegramBotsApi.registerBot(bot);

            Holidays.init(directory);
            new Distributor(bot, new HolidaysDistributor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
