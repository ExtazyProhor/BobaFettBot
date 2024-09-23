package com.prohor.personal.bobaFettBot.data.entities;

import lombok.*;

import java.time.*;

@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class HolidaysSubscriber {
    private final long chatId;
    private LocalTime dailyDistributionTime;
    private short indentationOfDays;
    private boolean subscriptionIsActive;
    private boolean canShareCustomHolidays;

    public HolidaysSubscriber(long chatId, LocalTime time, short indentation) {
        this.chatId = chatId;
        this.dailyDistributionTime = time;
        this.indentationOfDays = indentation;
        this.subscriptionIsActive = true;
        this.canShareCustomHolidays = true;
    }
}
