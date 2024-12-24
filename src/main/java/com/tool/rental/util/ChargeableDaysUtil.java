package com.tool.rental.util;

import com.tool.rental.model.Charge;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class ChargeableDaysUtil {

    public static BigDecimal calculateChargeableDays(LocalDate checkoutDate, int rentalDays, Charge charge) {
        if (checkoutDate == null || charge.getToolType().equalsIgnoreCase("UNKNOWN")) {
            return BigDecimal.ZERO;
        }
        // checkout date should not be included as a chargeable day
        List<LocalDate> rentalDates = Stream.iterate(checkoutDate.plusDays(1), date -> date.plusDays(1))
                .limit(rentalDays)
                .toList();
        return BigDecimal.valueOf(rentalDates.stream().filter(rentalDate ->
                (!"No".equalsIgnoreCase(charge.getWeekdayCharge()) || !isWeekDay(rentalDate))
                && (!"No".equalsIgnoreCase(charge.getWeekendCharge()) || !isWeekend(rentalDate))
                && (!"No".equalsIgnoreCase(charge.getHolidayCharge()) || !isHoliday(rentalDate))).toList().size());
    }

    public static boolean isWeekDay(LocalDate date) {
        return date != null && !isWeekend(date);
    }

    public static boolean isWeekend(LocalDate date) {
        return date != null && (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
    }

    public static boolean isHoliday(LocalDate date) {
        // return true if the date is July 4th or the first Monday of September (Labor Day)
        return date != null && ((date.getMonth() == Month.JULY && date.getDayOfMonth() == 4) || (date.getMonth() == Month.SEPTEMBER && date.getDayOfWeek() == DayOfWeek.MONDAY && date.getDayOfMonth() < 8));
    }
}
