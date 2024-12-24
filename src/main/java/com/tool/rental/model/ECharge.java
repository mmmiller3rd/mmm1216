package com.tool.rental.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum ECharge {
    Ladder (BigDecimal.valueOf(1.99), "Yes", "Yes", "No"),
    Chainsaw (BigDecimal.valueOf(1.49), "Yes", "No", "Yes"),
    Jackhammer (BigDecimal.valueOf(2.99), "Yes", "No", "No"),
    UNKNOWN (BigDecimal.ZERO, "No", "No", "No");

    private final BigDecimal dailyCharge;
    public final String weekdayCharge;
    public final String weekendCharge;
    public final String holidayCharge;

    ECharge(BigDecimal dailyCharge, String weekdayCharge, String weekendCharge, String holidayCharge) {
        this.dailyCharge = dailyCharge;
        this.weekdayCharge = weekdayCharge;
        this.weekendCharge = weekendCharge;
        this.holidayCharge = holidayCharge;
    }

    public static ECharge getCharge(String toolType) {
        return switch (toolType) {
            case "Ladder" -> Ladder;
            case "Chainsaw" -> Chainsaw;
            case "Jackhammer" -> Jackhammer;
            default -> UNKNOWN;
        };
    }
}
