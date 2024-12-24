package com.tool.rental.controller;

import com.tool.rental.config.AwsDynamoDbCloudConfig;
import com.tool.rental.service.ChargeService;
import com.tool.rental.service.RentalRequestService;
import com.tool.rental.service.RentalService;
import com.tool.rental.service.ToolService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(RentalController.class)
@TestPropertySource(properties = { "spring.profiles.active=test" })
@ContextConfiguration(classes = { AwsDynamoDbCloudConfig.class, ToolService.class, ChargeService.class, RentalService.class, RentalRequestService.class, RentalController.class })
public class RentalControllerTest {
    @Autowired
    MockMvc mvc;

    @Test
    public void rent() throws Exception {
        mvc.perform(post("/rent")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("[{\n" +
                        "    \"toolCode\": \"JAKD\",\n" +
                        "    \"checkoutDate\": \"9/3/15\",\n" +
                        "    \"rentalDays\": 6,\n" +
                        "    \"discountPercent\": 0\n" +
                        "}]"))
                .andExpect(status().isOk())
                .andExpect(content().string("[{" +
                        "\"toolCode\":\"JAKD\"," +
                        "\"toolType\":\"Jackhammer\"," +
                        "\"brand\":\"DeWalt\"," +
                        "\"rentalDays\":6," +
                        "\"checkoutDate\":\"9/3/15\"," +
                        "\"dueDate\":\"9/9/15\"," +
                        "\"dailyRentalCharge\":2.99," +
                        "\"chargeDays\":3," +
                        "\"preDiscountCharge\":8.97," +
                        "\"discountPercent\":0.00," +
                        "\"discountAmount\":0.00," +
                        "\"finalCharge\":8.97," +
                        "\"quantity\":1" +
                        "}]"));
    }

    @Test
    public void rent_invalidRequest_1() throws Exception {
        mvc.perform(post("/rent")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("[{\n" +
                                "    \"toolCode\": \"JAKD\",\n" +
                                "    \"checkoutDate\": \"9/3/15\",\n" +
                                "    \"rentalDays\": 0,\n" +
                                "    \"discountPercent\": 0\n" +
                                "}]"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Rental days should be >= 1"));
    }

    @Test
    public void rent_invalidRequest_2() throws Exception {
        mvc.perform(post("/rent")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("[{\n" +
                                "    \"toolCode\": \"JAKD\",\n" +
                                "    \"checkoutDate\": \"9/3/15\",\n" +
                                "    \"rentalDays\": 2,\n" +
                                "    \"discountPercent\": 101\n" +
                                "}]"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Discount percent should be in the range 0 - 100"));
    }
}
