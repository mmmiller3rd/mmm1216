package com.tool.rental.util;

import com.tool.rental.model.Charge;
import com.tool.rental.model.ECharge;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ChargeableDaysUtilTest {

    @Test
    public void calculateChargeableDays() {
        // no excluded days
        assertEquals(BigDecimal.valueOf(5), ChargeableDaysUtil.calculateChargeableDays(LocalDate.parse("2024-12-15"), 5, new Charge(ECharge.Ladder)));
        // no weekdays
        assertEquals(BigDecimal.ZERO, ChargeableDaysUtil.calculateChargeableDays(LocalDate.parse("2024-12-16"), 5, new Charge(ECharge.UNKNOWN)));
        // no weekends
        assertEquals(BigDecimal.valueOf(5), ChargeableDaysUtil.calculateChargeableDays(LocalDate.parse("2024-12-09"), 7, new Charge(ECharge.Chainsaw)));
        // yes holidays
        assertEquals(BigDecimal.valueOf(4), ChargeableDaysUtil.calculateChargeableDays(LocalDate.parse("2024-07-01"), 5, new Charge(ECharge.Chainsaw)));
        // no holidays
        assertEquals(BigDecimal.valueOf(4), ChargeableDaysUtil.calculateChargeableDays(LocalDate.parse("2024-07-01"), 5, new Charge(ECharge.Ladder)));
    }

    @Test
    public void calculateChargeableDays_missingCheckout() {
        assertEquals(BigDecimal.ZERO, ChargeableDaysUtil.calculateChargeableDays(null, 1, new Charge(ECharge.Jackhammer)));
    }

    @Test
    public void calculateChargeableDays_missingCharge() {
        assertEquals(BigDecimal.ZERO, ChargeableDaysUtil.calculateChargeableDays(LocalDate.now(), 1, new Charge(ECharge.UNKNOWN)));
    }

    @Test
    public void testIsWeekday_true() {
        LocalDate date = LocalDate.parse("2024-12-17");
        assertTrue(ChargeableDaysUtil.isWeekDay(date));
    }

    @Test
    public void testIsWeekday_false() {
        LocalDate date = LocalDate.parse("2024-12-15");
        assertFalse(ChargeableDaysUtil.isWeekDay(date));
        assertFalse(ChargeableDaysUtil.isWeekDay(null));
    }

    @Test
    public void testIsWeekend_true() {
        LocalDate date = LocalDate.parse("2024-12-15");
        assertTrue(ChargeableDaysUtil.isWeekend(date));
    }

    @Test
    public void testIsWeekend_false() {
        LocalDate date = LocalDate.parse("2024-12-17");
        assertFalse(ChargeableDaysUtil.isWeekend(date));
        assertFalse(ChargeableDaysUtil.isWeekend(null));
    }

    @Test
    public void testIsHoliday_independence() {
        LocalDate date = LocalDate.parse("2024-07-04");
        assertTrue(ChargeableDaysUtil.isHoliday(date));
    }

    @Test
    public void testIsHoliday_labor() {
        LocalDate date = LocalDate.parse("2024-09-02");
        assertTrue(ChargeableDaysUtil.isHoliday(date));
    }

    @Test
    public void testIsHoliday_false() {
        LocalDate date = LocalDate.parse("2024-12-15");
        assertFalse(ChargeableDaysUtil.isHoliday(date));
        assertFalse(ChargeableDaysUtil.isHoliday(null));
    }
}
