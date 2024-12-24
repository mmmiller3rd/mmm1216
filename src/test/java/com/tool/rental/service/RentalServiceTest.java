package com.tool.rental.service;

import com.tool.rental.exception.InvalidRentalRequestException;
import com.tool.rental.model.ECharge;
import com.tool.rental.model.ETool;
import com.tool.rental.model.RentalAgreement;
import com.tool.rental.model.RentalRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {

    @Mock
    private ToolService toolService_mock;

    @Mock
    private ChargeService chargeService_mock;

    RentalService service;
    RentalRequest request;

    @BeforeEach
    public void setup() {
        toolService_mock.activeProfile = "test";
        chargeService_mock.activeProfile = "test";
        service = new RentalService(toolService_mock, chargeService_mock);
    }

    @Test
    public void test_1() {
        request = new RentalRequest("CHNS", LocalDate.parse("2015-09-03"), 5, 101);
        try {
            service.rent(request);
            fail();
        } catch (InvalidRentalRequestException irre) {
            assertEquals("Discount percent should be in the range 0 - 100", irre.getMessage());
        }
    }

    @Test
    public void test_2() {
        when(toolService_mock.loadTools()).thenCallRealMethod();
        when(chargeService_mock.loadCharges()).thenCallRealMethod();
        request = new RentalRequest("LADW", LocalDate.parse("2020-07-02"), 3, 10);
        RentalAgreement agreement = service.rent(request);
        assertEquals(request.getToolCode(), agreement.getToolCode());
        assertEquals(ETool.LADW.getToolType(), agreement.getToolType());
        assertEquals(ETool.LADW.getBrand(), agreement.getBrand());
        assertEquals(request.getRentalDays(), agreement.getRentalDays());
        assertEquals(request.getCheckoutDate(), agreement.getCheckoutDate());
        assertEquals("2020-07-05", agreement.getDueDate().toString());
        assertEquals(ECharge.Ladder.getDailyCharge(), agreement.getDailyRentalCharge());
        assertEquals(2, agreement.getChargeDays());
        assertEquals(BigDecimal.valueOf(3.98), agreement.getPreDiscountCharge());
        assertEquals(BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP), agreement.getDiscountPercent());
        assertEquals(BigDecimal.valueOf(.4).setScale(2, RoundingMode.HALF_UP), agreement.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(3.58), agreement.getFinalCharge());
    }

    @Test
    public void test_3() {
        when(toolService_mock.loadTools()).thenCallRealMethod();
        when(chargeService_mock.loadCharges()).thenCallRealMethod();
        request = new RentalRequest("CHNS", LocalDate.parse("2015-07-02"), 5, 25);
        RentalAgreement agreement = service.rent(request);
        assertEquals(request.getToolCode(), agreement.getToolCode());
        assertEquals(ETool.CHNS.getToolType(), agreement.getToolType());
        assertEquals(ETool.CHNS.getBrand(), agreement.getBrand());
        assertEquals(request.getRentalDays(), agreement.getRentalDays());
        assertEquals(request.getCheckoutDate(), agreement.getCheckoutDate());
        assertEquals("2015-07-07", agreement.getDueDate().toString());
        assertEquals(ECharge.Chainsaw.getDailyCharge(), agreement.getDailyRentalCharge());
        assertEquals(3, agreement.getChargeDays());
        assertEquals(BigDecimal.valueOf(4.47), agreement.getPreDiscountCharge());
        assertEquals(BigDecimal.valueOf(25).setScale(2, RoundingMode.HALF_UP), agreement.getDiscountPercent());
        assertEquals(BigDecimal.valueOf(1.12).setScale(2, RoundingMode.HALF_UP), agreement.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(3.35), agreement.getFinalCharge());
    }

    @Test
    public void test_4() {
        when(toolService_mock.loadTools()).thenCallRealMethod();
        when(chargeService_mock.loadCharges()).thenCallRealMethod();
        request = new RentalRequest("JAKD", LocalDate.parse("2015-09-03"), 6, 0);
        RentalAgreement agreement = service.rent(request);
        assertEquals(request.getToolCode(), agreement.getToolCode());
        assertEquals(ETool.JAKD.getToolType(), agreement.getToolType());
        assertEquals(ETool.JAKD.getBrand(), agreement.getBrand());
        assertEquals(request.getRentalDays(), agreement.getRentalDays());
        assertEquals(request.getCheckoutDate(), agreement.getCheckoutDate());
        assertEquals("2015-09-09", agreement.getDueDate().toString());
        assertEquals(ECharge.Jackhammer.getDailyCharge(), agreement.getDailyRentalCharge());
        assertEquals(3, agreement.getChargeDays());
        assertEquals(BigDecimal.valueOf(8.97), agreement.getPreDiscountCharge());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), agreement.getDiscountPercent());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), agreement.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(8.97), agreement.getFinalCharge());
    }

    @Test
    public void test_5() {
        when(toolService_mock.loadTools()).thenCallRealMethod();
        when(chargeService_mock.loadCharges()).thenCallRealMethod();
        request = new RentalRequest("JAKR", LocalDate.parse("2015-07-02"), 9, 0);
        RentalAgreement agreement = service.rent(request);
        assertEquals(request.getToolCode(), agreement.getToolCode());
        assertEquals(ETool.JAKR.getToolType(), agreement.getToolType());
        assertEquals(ETool.JAKR.getBrand(), agreement.getBrand());
        assertEquals(request.getRentalDays(), agreement.getRentalDays());
        assertEquals(request.getCheckoutDate(), agreement.getCheckoutDate());
        assertEquals("2015-07-11", agreement.getDueDate().toString());
        assertEquals(ECharge.Jackhammer.getDailyCharge(), agreement.getDailyRentalCharge());
        assertEquals(6, agreement.getChargeDays());
        assertEquals(BigDecimal.valueOf(17.94), agreement.getPreDiscountCharge());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), agreement.getDiscountPercent());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), agreement.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(17.94), agreement.getFinalCharge());
    }

    @Test
    public void test_6() {
        when(toolService_mock.loadTools()).thenCallRealMethod();
        when(chargeService_mock.loadCharges()).thenCallRealMethod();
        request = new RentalRequest("JAKR", LocalDate.parse("2020-07-02"), 4, 50);
        RentalAgreement agreement = service.rent(request);
        assertEquals(request.getToolCode(), agreement.getToolCode());
        assertEquals(ETool.JAKR.getToolType(), agreement.getToolType());
        assertEquals(ETool.JAKR.getBrand(), agreement.getBrand());
        assertEquals(request.getRentalDays(), agreement.getRentalDays());
        assertEquals(request.getCheckoutDate(), agreement.getCheckoutDate());
        assertEquals("2020-07-06", agreement.getDueDate().toString());
        assertEquals(ECharge.Jackhammer.getDailyCharge(), agreement.getDailyRentalCharge());
        assertEquals(2, agreement.getChargeDays());
        assertEquals(BigDecimal.valueOf(5.98), agreement.getPreDiscountCharge());
        assertEquals(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_UP), agreement.getDiscountPercent());
        assertEquals(BigDecimal.valueOf(2.99).setScale(2, RoundingMode.HALF_UP), agreement.getDiscountAmount());
        assertEquals(BigDecimal.valueOf(2.99), agreement.getFinalCharge());
    }

    @Test
    public void invalidRentalDays() {
        request = new RentalRequest("CHNS", LocalDate.parse("2015-09-03"), 0, 10);
        try {
            service.rent(request);
            fail();
        } catch (InvalidRentalRequestException irre) {
            assertEquals("Rental days should be >= 1", irre.getMessage());
        }
    }
}
