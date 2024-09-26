package com.prohor.personal.bobaFettBot.data.entities;

import com.prohor.personal.bobaFettBot.data.mapping.*;
import lombok.*;

import java.time.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
@Table(name = "holidays")
public class HolidaysSubscriber implements Entity {
    @PrimaryKey
    @EntityField(name = "chat_id")
    private Long chatId;
    @Setter
    @EntityField(name = "daily_distribution_time")
    private LocalTime dailyDistributionTime;
    @Setter
    @EntityField(name = "indentation_of_days")
    private Short indentationOfDays;
    @Setter
    @EntityField(name = "subscription_is_active")
    private Boolean subscriptionIsActive;

    public HolidaysSubscriber(long chatId, LocalTime time, short indentation) {
        this.chatId = chatId;
        this.dailyDistributionTime = time;
        this.indentationOfDays = indentation;
        this.subscriptionIsActive = true;
    }
}
