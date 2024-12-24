package com.tool.rental.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tool.rental.exception.InvalidRentalRequestException;
import com.tool.rental.model.*;
import com.tool.rental.util.ChargeableDaysUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RentalService {
    private final ToolService toolService;
    private final ChargeService chargeService;
    private Map<String, Tool> toolMap;
    private Map<String, Charge> chargeMap;

    @Autowired
    public RentalService(ToolService toolService, ChargeService chargeService) {
        this.toolService = toolService;
        this.chargeService = chargeService;
        this.toolMap = new HashMap<>();
        this.chargeMap = new HashMap<>();
    }

    public RentalAgreement rent(RentalRequest request) {
        verifyRequest(request);
        if (toolMap.isEmpty()) {
            getToolsAndCharges();
        }
        Tool tool = toolMap.get(request.getToolCode());
        Charge charge = chargeMap.get(tool.getToolType());
        BigDecimal chargeableDays = ChargeableDaysUtil.calculateChargeableDays(request.getCheckoutDate(), request.getRentalDays(), charge);
        BigDecimal pdc = charge.getDailyCharge().multiply(chargeableDays).multiply(BigDecimal.valueOf(request.getQuantity()));
        BigDecimal da = pdc.multiply(BigDecimal.valueOf(request.getDiscountPercent()/100));
        RentalAgreement agreement = new RentalAgreement.Builder()
                .withToolCode(request.getToolCode())
                .withToolType(tool.getToolType())
                .withBrand(tool.getBrand())
                .withRentalDays(request.getRentalDays())
                .withCheckoutDate(request.getCheckoutDate())
                .withDueDate(request.getCheckoutDate().plusDays(request.getRentalDays()))
                .withDailyRentalCharge(charge.getDailyCharge())
                .withChargeDays(chargeableDays.intValue())
                .withPreDiscountCharge(pdc)
                .withDiscountPercent(BigDecimal.valueOf(request.getDiscountPercent()))
                .withDiscountAmount(da)
                .withFinalCharge(pdc.subtract(da))
                .withQuantity(request.getQuantity())
                .build();
        log.info(agreement.print());
        return agreement;
    }

    public List<Rental> getToolsAndCharges() {
        toolMap = toolService.loadTools();
        chargeMap = chargeService.loadCharges();
        List<Rental> rentals = new ArrayList<>();
        toolMap.forEach((key, tool) -> {
            Charge charge = chargeMap.get(tool.getToolType());
            rentals.add(new Rental(tool.getToolCode(), tool.getToolType(), tool.getBrand(), charge.getDailyCharge(), charge.getWeekdayCharge(), charge.getWeekendCharge(), charge.getHolidayCharge()));
        });
        return rentals;
    }

    public List<Rental> upsertRental(Rental rental) {
        Tool tool = new Tool(rental.getToolCode(), rental.getToolType(), rental.getBrand());
        Charge charge = new Charge(rental.getToolType(), rental.getDailyCharge(), rental.getWeekdayCharge(), rental.getWeekendCharge(), rental.getHolidayCharge());
        toolService.upsertTool(tool);
        chargeService.upsertCharge(charge);
        toolMap.put(tool.getToolCode(), tool);
        chargeMap.put(charge.getToolType(), charge);
        List<Rental> rentals = new ArrayList<>();
        toolMap.forEach((key, t) -> {
            Charge c = chargeMap.get(t.getToolType());
            rentals.add(new Rental(t.getToolCode(), t.getToolType(), t.getBrand(), c.getDailyCharge(), c.getWeekdayCharge(), c.getWeekendCharge(), c.getHolidayCharge()));
        });
        return rentals;
    }

    public void verifyRequest(RentalRequest request) {
        String message = null;
        if (request.getRentalDays() < 1) {
            message = "Rental days should be >= 1";
        }
        if (request.getQuantity() < 1) {
            message = "Rental quantity should be >= 1";
        }
        if (request.getDiscountPercent() < 0 || request.getDiscountPercent() > 100) {
            message = "Discount percent should be in the range 0 - 100";
        }
        if (message != null) {
            try {
                log.error("{}\n{}", message, new ObjectMapper().writeValueAsString(request));
            } catch (JsonProcessingException jpe) {
                log.error(jpe.getMessage());
            }
            throw new InvalidRentalRequestException(message);
        }
    }
}
