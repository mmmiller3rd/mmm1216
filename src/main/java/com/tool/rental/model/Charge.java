package com.tool.rental.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Charge {
    private String toolType;
    private BigDecimal dailyCharge;
    public String weekdayCharge;
    public String weekendCharge;
    public String holidayCharge;

    public Charge(ECharge eCharge) {
        this.toolType = eCharge.name();
        this.dailyCharge = eCharge.getDailyCharge();
        this.weekdayCharge = eCharge.getWeekdayCharge();
        this.weekendCharge = eCharge.getWeekendCharge();
        this.holidayCharge = eCharge.getHolidayCharge();
    }
}
