package com.tool.rental.model;

import lombok.Getter;

@Getter
public enum ETool {
    CHNS ("CHNS", "Chainsaw", "Stihl"),
    LADW ("LADW", "Ladder", "Werner"),
    JAKD ("JAKD", "Jackhammer", "DeWalt"),
    JAKR ("JAKR", "Jackhammer", "Ridgid"),
    UNKNOWN ("UNKN", "UNKNOWN", "Unknown");

    private final String toolCode;
    private final String toolType;
    private final String brand;

    ETool(String toolCode, String toolType, String brand) {
        this.toolCode = toolCode;
        this.toolType = toolType;
        this.brand = brand;
    }

    public static ETool getTool(String toolCode) {
        return switch (toolCode) {
            case "CHNS" -> CHNS;
            case "LADW" -> LADW;
            case "JAKD" -> JAKD;
            case "JAKR" -> JAKR;
            default -> UNKNOWN;
        };
    }
}
