package com.tool.rental.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tool {
    private String toolCode;
    private String toolType;
    private String brand;
}
