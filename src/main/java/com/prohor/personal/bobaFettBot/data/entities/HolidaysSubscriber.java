package com.prohor.personal.bobaFettBot.data.entities;

import com.prohor.personal.bobaFettBot.data.mapping.Entity;
import lombok.*;

import java.time.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class HolidaysSubscriber extends Entity {
    private long chatId;
    @Setter
    private LocalTime dailyDistributionTime;
    @Setter
    private short indentationOfDays;
    @Setter
    private boolean subscriptionIsActive;
    @Setter
    private boolean canShareCustomHolidays;

    public HolidaysSubscriber(long chatId, LocalTime time, short indentation) {
        this.chatId = chatId;
        this.dailyDistributionTime = time;
        this.indentationOfDays = indentation;
        this.subscriptionIsActive = true;
        this.canShareCustomHolidays = true;
    }
}
