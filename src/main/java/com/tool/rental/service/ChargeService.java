package com.tool.rental.service;

import com.tool.rental.model.Charge;
import com.tool.rental.model.ECharge;
import com.tool.rental.model.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ChargeService {
    public String activeProfile;
    private final DynamoDbClient client;

    @Autowired
    public ChargeService(DynamoDbClient client, @Value("${spring.profiles.active:local}") String activeProfile) {
        this.client = client;
        this.activeProfile = activeProfile;
    }

    public Map<String, Charge> loadCharges() {
        Map<String, Charge> chargeMap = new HashMap<>();
        if (activeProfile.contains("cloud")) {
            ScanRequest scanRequest = ScanRequest.builder().tableName("Charge").build();
            ScanResponse chargeResponse = client.scan(scanRequest);
            for (Map<String, AttributeValue> item : chargeResponse.items()) {
                chargeMap.put(item.get("toolType").s(), new Charge(item.get("toolType").s(), BigDecimal.valueOf(Double.parseDouble(item.get("dailyCharge").n())), item.get("weekdayCharge").s(), item.get("weekendCharge").s(), item.get("holidayCharge").s()));
            }
        } else {
            for (ECharge charge : ECharge.values()) {
                chargeMap.put(charge.name(), new Charge(charge));
            }
        }
        return chargeMap;
    }

    public void upsertCharge(Charge charge) {
        HashMap<String, AttributeValue> itemKey = new HashMap<>();
        itemKey.put("toolType", AttributeValue.fromS(charge.getToolType()));

        GetItemRequest getRequest = GetItemRequest.builder()
                .tableName("Charge")
                .key(itemKey)
                .build();
        GetItemResponse getResponse = client.getItem(getRequest);

        if (getResponse.hasItem()) {
            // update item
            HashMap<String, AttributeValueUpdate> item = new HashMap<>();
            item.put("dailyCharge", AttributeValueUpdate.builder().value(AttributeValue.fromN(charge.getDailyCharge().toString())).build());
            item.put("weekdayCharge", AttributeValueUpdate.builder().value(AttributeValue.fromS(charge.getWeekdayCharge())).build());
            item.put("weekendCharge", AttributeValueUpdate.builder().value(AttributeValue.fromS(charge.getWeekendCharge())).build());
            item.put("holidayCharge", AttributeValueUpdate.builder().value(AttributeValue.fromS(charge.getHolidayCharge())).build());
            UpdateItemRequest request = UpdateItemRequest.builder()
                    .tableName("Charge").key(itemKey).attributeUpdates(item).build();
            client.updateItem(request);
        } else {
            HashMap<String, AttributeValue> item = new HashMap<>();
            item.put("toolType", AttributeValue.fromS(charge.getToolType()));
            item.put("dailyCharge", AttributeValue.fromN(charge.getDailyCharge().toString()));
            item.put("weekdayCharge", AttributeValue.fromS(charge.getWeekdayCharge()));
            item.put("weekendCharge", AttributeValue.fromS(charge.getWeekendCharge()));
            item.put("holidayCharge", AttributeValue.fromS(charge.getHolidayCharge()));
            PutItemRequest request = PutItemRequest.builder()
                    .tableName("Charge")
                    .item(item)
                    .build();

            client.putItem(request);
        }
    }
}
