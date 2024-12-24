package com.tool.rental.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class RentalAgreement {
    private String toolCode;
    private String toolType;
    private String brand;
    private int rentalDays;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "M/d/yy")
    private LocalDate checkoutDate;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "M/d/yy")
    private LocalDate dueDate;
    private BigDecimal dailyRentalCharge;
    private int chargeDays;
    private BigDecimal preDiscountCharge;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal finalCharge;
    private int quantity;
    
    RentalAgreement(Builder builder) {
        this.toolCode = builder.toolCode;
        this.toolType = builder.toolType;
        this.brand = builder.brand;
        this.rentalDays = builder.rentalDays;
        this.checkoutDate = builder.checkoutDate;
        this.dueDate = builder.dueDate;
        this.dailyRentalCharge = builder.dailyRentalCharge;
        this.chargeDays = builder.chargeDays;
        this.preDiscountCharge = builder.preDiscountCharge;
        this.discountPercent = builder.discountPercent;
        this.discountAmount = builder.discountAmount;
        this.finalCharge = builder.finalCharge;
        this.quantity = builder.quantity;
    }
    
    public String print() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
        return "\nTool Code: " + this.toolCode + "\n" +
                "Tool Type: " + this.toolType + "\n" +
                "Brand: " + this.brand + "\n" +
                "Rental Days: " + this.rentalDays + "\n" +
                "Checkout Date: " + this.checkoutDate.format(formatter) + "\n" +
                "Due Date: " + this.dueDate.format(formatter) + "\n" +
                "Daily Rental Charge: $" + this.dailyRentalCharge.setScale(2, RoundingMode.HALF_UP) + "\n" +
                "Charge Days: " + this.chargeDays + "\n" +
                "Pre-discount Charge: $" + this.preDiscountCharge.setScale(2, RoundingMode.HALF_UP) + "\n" +
                "Discount Percent: " + this.discountPercent.setScale(2, RoundingMode.HALF_UP) + "%" + "\n" +
                "Discount Amount: $" + this.discountAmount.setScale(2, RoundingMode.HALF_UP) + "\n" +
                "Final Charge: $" + this.finalCharge.setScale(2, RoundingMode.HALF_UP) + "\n";
    }
    
    public static class Builder {
        private String toolCode;
        private String toolType;
        private String brand;
        private int rentalDays;
        private LocalDate checkoutDate;
        private LocalDate dueDate;
        private BigDecimal dailyRentalCharge;
        private int chargeDays;
        private BigDecimal preDiscountCharge;
        private BigDecimal discountPercent;
        private BigDecimal discountAmount;
        private BigDecimal finalCharge;
        private int quantity;
        
        public Builder withToolCode(String toolcode) {
            this.toolCode = toolcode;
            return this;
        }
        public Builder withToolType(String toolType) {
            this.toolType = toolType;
            return this;
        }
        public Builder withBrand(String brand) {
            this.brand = brand;
            return this;
        }
        public Builder withRentalDays(int rentalDays) {
            this.rentalDays = rentalDays;
            return this;
        }
        public Builder withCheckoutDate(LocalDate checkoutDate) {
            this.checkoutDate = checkoutDate;
            return this;
        }
        public Builder withDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }
        public Builder withDailyRentalCharge(BigDecimal dailyRentalCharge) {
            this.dailyRentalCharge = dailyRentalCharge.setScale(2, RoundingMode.HALF_UP);
            return this;
        }
        public Builder withChargeDays(int chargeDays) {
            this.chargeDays = chargeDays;
            return this;
        }
        public Builder withPreDiscountCharge(BigDecimal preDiscountCharge) {
            this.preDiscountCharge = preDiscountCharge.setScale(2, RoundingMode.HALF_UP);
            return this;
        }
        public Builder withDiscountPercent(BigDecimal discountPercent) {
            this.discountPercent = discountPercent.setScale(2, RoundingMode.HALF_UP);
            return this;
        }
        public Builder withDiscountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount.setScale(2, RoundingMode.HALF_UP);
            return this;
        }
        public Builder withFinalCharge(BigDecimal finalCharge) {
            this.finalCharge = finalCharge.setScale(2, RoundingMode.HALF_UP);
            return this;
        }
        public Builder withQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }
        public RentalAgreement build() {
            return new RentalAgreement(this);
        }
    }
}
