package com.prohor.personal.bobaFettBot.distribution;

import com.prohor.personal.bobaFettBot.bot.Bot;
import org.slf4j.*;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;

public class Distributor implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Distributor.class);
    private static final int SECS_IN_15_MINUTES = 15 * 60;

    private final List<DistributionTask> tasksList;
    private final Bot bot;

    public Distributor(Bot bot, DistributionTask... tasks) {
        this.bot = bot;
        this.tasksList = Arrays.asList(tasks);

        LocalTime now = DateTimeUtil.getTimeNow();
        int initialDelay = SECS_IN_15_MINUTES - (now.getMinute() * 60 + now.getSecond()) % SECS_IN_15_MINUTES;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this, initialDelay, SECS_IN_15_MINUTES, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        LocalTime roundedNow = getRoundedTime();
        if (roundedNow.getHour() == 0 && roundedNow.getMinute() == 0)
            DateTimeUtil.setToday(DateTimeUtil.getDateNow());
        for (var task : tasksList) {
            try {
                task.distribute(bot, roundedNow);
            } catch (Exception e) {
                log.error("error with distribution task", e);
            }
        }
    }

    private static LocalTime getRoundedTime() {
        LocalTime now = DateTimeUtil.getTimeNow();
        int minutes = now.getMinute();
        int roundedMinutes = (int) (Math.round(minutes / 15.0) * 15) % 60;
        int newHour = now.getHour();
        if (roundedMinutes == 0 && minutes > 30)
            newHour = (newHour + 1) % 24;
        return LocalTime.of(newHour, roundedMinutes);
    }
}
