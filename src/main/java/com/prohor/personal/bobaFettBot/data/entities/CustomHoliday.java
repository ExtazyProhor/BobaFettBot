package com.prohor.personal.bobaFettBot.data.entities;

import lombok.*;

import java.time.*;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class CustomHoliday {
    private long customHolidayId;
    private final long chatId;
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
