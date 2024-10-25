package com.prohor.personal.bobaFettBot.distribution;

import com.prohor.personal.bobaFettBot.bot.Bot;

import java.time.LocalTime;

public interface DistributionTask {
    void distribute(Bot bot, LocalTime roundedNow) throws Exception;
}
