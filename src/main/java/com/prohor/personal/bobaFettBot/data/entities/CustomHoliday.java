package com.prohor.personal.bobaFettBot.data.entities;

import com.prohor.personal.bobaFettBot.data.mapping.Entity;
import lombok.*;

import java.time.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class CustomHoliday extends Entity {
    private long customHolidayId;
    private long chatId;
    @Setter
    private LocalDate holidayDate;
    @Setter
    private String holidayName;

    public CustomHoliday(long chatId, LocalDate holidayDate, String holidayName) {
        this.chatId = chatId;
        this.holidayDate = holidayDate;
        this.holidayName = holidayName;
    }
}
