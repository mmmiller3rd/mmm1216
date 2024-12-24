package com.tool.rental.service;

import com.tool.rental.model.ETool;
import com.tool.rental.model.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Slf4j
@Component
public class ToolService {
    public String activeProfile;
    private final DynamoDbClient client;

    @Autowired
    public ToolService(DynamoDbClient client, @Value("${spring.profiles.active:local}") String activeProfile) {
        this.client = client;
        this.activeProfile = activeProfile;
    }

    public Map<String, Tool> loadTools() {
        Map<String, Tool> toolMap = new HashMap<>();
        if (activeProfile.contains("cloud")) {
            ScanRequest scanRequest = ScanRequest.builder().tableName("Tool").build();
            ScanResponse toolResponse = client.scan(scanRequest);
            for (Map<String, AttributeValue> item : toolResponse.items()) {
                toolMap.put(item.get("toolCode").s(), new Tool(item.get("toolCode").s(), item.get("toolType").s(), item.get("brand").s()));
            }
        } else {
            for (ETool tool: ETool.values()) {
                toolMap.put(tool.getToolCode(), new Tool(tool.getToolCode(), tool.getToolType(), tool.getBrand()));
            }
        }
        return toolMap;
    }

    public void upsertTool(Tool tool) {
        HashMap<String, AttributeValue> itemKey = new HashMap<>();
        itemKey.put("toolCode", AttributeValue.fromS(tool.getToolCode()));

        GetItemRequest getRequest = GetItemRequest.builder()
                .tableName("Tool")
                .key(itemKey)
                .build();
        GetItemResponse getResponse = client.getItem(getRequest);

        if (getResponse.hasItem()) {
            // update item
            HashMap<String, AttributeValueUpdate> item = new HashMap<>();
            item.put("toolType", AttributeValueUpdate.builder().value(AttributeValue.fromS(tool.getToolType())).build());
            item.put("brand", AttributeValueUpdate.builder().value(AttributeValue.fromS(tool.getBrand())).build());
            UpdateItemRequest request = UpdateItemRequest.builder()
                    .tableName("Tool").key(itemKey).attributeUpdates(item).build();
            client.updateItem(request);
        } else {
            HashMap<String, AttributeValue> item = new HashMap<>();
            item.put("toolCode", AttributeValue.fromS(tool.getToolCode()));
            item.put("toolType", AttributeValue.fromS(tool.getToolType()));
            item.put("brand", AttributeValue.fromS(tool.getBrand()));
            PutItemRequest request = PutItemRequest.builder()
                    .tableName("Tool")
                    .item(item)
                    .build();

            client.putItem(request);
        }
    }
}
