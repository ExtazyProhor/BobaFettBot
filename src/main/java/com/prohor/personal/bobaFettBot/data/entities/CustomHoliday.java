package com.prohor.personal.bobaFettBot.data.entities;

import com.prohor.personal.bobaFettBot.data.mapping.*;
import lombok.*;

import java.time.*;

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
    private LocalDate holidayDate;
    @Setter
    @EntityField(name = "holiday_name")
    private String holidayName;

    public CustomHoliday(long chatId, LocalDate holidayDate, String holidayName) {
        this.chatId = chatId;
        this.holidayDate = holidayDate;
        this.holidayName = holidayName;
    }
}
