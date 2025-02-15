package com.prohor.personal.bobaFettBot.data.entities;

import com.prohor.personal.bobaFettBot.data.mapping.Entity;
import com.prohor.personal.bobaFettBot.data.mapping.EntityField;
import com.prohor.personal.bobaFettBot.data.mapping.PrimaryKey;
import com.prohor.personal.bobaFettBot.data.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;

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
}
