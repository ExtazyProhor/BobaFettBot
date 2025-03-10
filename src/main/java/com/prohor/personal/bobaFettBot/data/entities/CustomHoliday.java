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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
@Table(name = "custom_holidays")
public class CustomHoliday implements Entity {
    @PrimaryKey
    @EntityField(name = "custom_holiday_id")
    private Integer customHolidayId;
    @Setter
    @EntityField(name = "chat_id")
    private Long chatId;
    @Setter
    @EntityField(name = "holiday_date")
    private Short holidayDate;
    @Setter
    @EntityField(name = "holiday_name")
    private String holidayName;

    public CustomHoliday(long chatId, Short holidayDate, String holidayName) {
        this.chatId = chatId;
        this.holidayDate = holidayDate;
        this.holidayName = holidayName;
    }

    public CustomHoliday(long chatId, Short holidayDate) {
        this.chatId = chatId;
        this.holidayDate = holidayDate;
    }

    public CustomHoliday(Short holidayDate) {
        this.holidayDate = holidayDate;
    }
}
