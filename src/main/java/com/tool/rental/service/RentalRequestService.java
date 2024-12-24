package com.tool.rental.service;

import com.tool.rental.model.RentalRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDate;
import java.util.*;

@Component
public class RentalRequestService {
    public String activeProfile;
    private final DynamoDbClient client;

    @Autowired
    public RentalRequestService(DynamoDbClient client, @Value("${spring.profiles.active:local}") String activeProfile) {
        this.client = client;
        this.activeProfile = activeProfile;
    }

    public void saveRentalRequest(List<RentalRequest> requestList) {
        if (!activeProfile.contains("cloud")) {
            return;
        }
        Map<String, List<RentalRequest>> requestMap = new HashMap<>();
        requestList.forEach(request -> {
            if (requestMap.containsKey(request.getToolCode())) {
                requestMap.get(request.getToolCode()).add(request);
            } else {
                requestMap.put(request.getToolCode(), List.of(request));
            }
        });
        requestMap.forEach((toolCode, requests) -> {
            HashMap<String, AttributeValue> requestKey = new HashMap<>();
            requestKey.put("toolCode", AttributeValue.fromS(toolCode));

            GetItemRequest getRequest = GetItemRequest.builder()
                    .tableName("RentalRequest")
                    .key(requestKey)
                    .build();
            GetItemResponse getResponse = client.getItem(getRequest);
            List<String> requestString = requests.stream().map(this::stringifyRequest).toList();
            String requestsAttr = requestString.toString().substring(1, requestString.toString().length() - 1);
            if (getResponse.hasItem()) {
                HashMap<String, AttributeValueUpdate> item = new HashMap<>();
                item.put("requests", AttributeValueUpdate.builder().value(AttributeValue.fromS(getResponse.item().get("requests").s() + "," + requestsAttr)).build());
                UpdateItemRequest request = UpdateItemRequest.builder()
                        .tableName("RentalRequest").key(requestKey).attributeUpdates(item).build();
                client.updateItem(request);
            } else {
                HashMap<String, AttributeValue> item = new HashMap<>();
                item.put("toolCode", AttributeValue.fromS(toolCode));
                item.put("requests", AttributeValue.fromS(requestsAttr));
                PutItemRequest request = PutItemRequest.builder()
                        .tableName("RentalRequest")
                        .item(item)
                        .build();

                client.putItem(request);
            }
        });
    }

    public List<RentalRequest> getRequests() {
        if (!activeProfile.contains("cloud")) {
            return new ArrayList<>();
        }
        ScanRequest scanRequest = ScanRequest.builder().tableName("RentalRequest").build();
        ScanResponse chargeResponse = client.scan(scanRequest);
        List<RentalRequest> requestList = new ArrayList<>();
        for (Map<String, AttributeValue> item : chargeResponse.items()) {
            Arrays.stream(item.get("requests").s().split(",")).forEach(r -> {
                String[] attributes = r.split("\\|");
                requestList.add(new RentalRequest(attributes[0], LocalDate.parse(attributes[1]), Integer.parseInt(attributes[2]), Double.parseDouble(attributes[3]), Integer.parseInt(attributes[4])));
            });
        }
        return requestList;
    }

    public String stringifyRequest(RentalRequest request) {
        return request.getToolCode() + "|" + request.getCheckoutDate() + "|" + request.getRentalDays() + "|" + request.getDiscountPercent() + "|" + request.getQuantity();
    }

    public void delete(String toolCode) {
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("toolCode", AttributeValue.builder()
                .s(toolCode)
                .build());

        DeleteItemRequest deleteReq = DeleteItemRequest.builder()
                .tableName("RentalRequest")
                .key(keyToGet)
                .build();
        client.deleteItem(deleteReq);
    }
}
