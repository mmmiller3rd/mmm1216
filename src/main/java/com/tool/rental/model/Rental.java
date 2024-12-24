package com.tool.rental.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Rental {
    private String toolCode;
    private String toolType;
    private String brand;
    private BigDecimal dailyCharge;
    public String weekdayCharge;
    public String weekendCharge;
    public String holidayCharge;
}
