package com.tool.rental.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tool.rental.exception.InvalidRentalRequestException;
import com.tool.rental.model.Rental;
import com.tool.rental.model.RentalAgreement;
import com.tool.rental.model.RentalRequest;
import com.tool.rental.service.RentalRequestService;
import com.tool.rental.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RentalController {

    private final RentalService rentalService;
    private final RentalRequestService rentalRequestService;
    private final ObjectMapper mapper;
    private final String activeProfile;

    @Autowired
    public RentalController(RentalService rentalService, RentalRequestService rentalRequestService, @Value("${spring.profiles.active:local}") String activeProfile) {
        this.rentalService = rentalService;
        this.rentalRequestService = rentalRequestService;
        this.activeProfile = activeProfile;
        this.mapper = new ObjectMapper();
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/getToolsAndCharges", produces = {"application/json"})
    public ResponseEntity<String> getToolsAndCharges() throws JsonProcessingException {
        if (!activeProfile.contains("cloud")) {
            return ResponseEntity.ok("Database unavailable");
        }
        return ResponseEntity.ok(mapper.writeValueAsString(rentalService.getToolsAndCharges()));
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/getRentalAgreements", produces = {"application/json"})
    public ResponseEntity<String> getRentalAgreements() throws JsonProcessingException {
        try {
            List<RentalAgreement> rentalAgreements = rentalRequestService.getRequests().stream().parallel().map(rentalService::rent).toList();
            return ResponseEntity.ok(mapper.writeValueAsString(rentalAgreements));
        } catch (InvalidRentalRequestException irre) {
            return ResponseEntity.badRequest().body(irre.getMessage());
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/rent", produces = {"application/json"})
    public ResponseEntity<String> rent(@RequestBody List<RentalRequest> requestList) throws JsonProcessingException {
        try {
            List<RentalAgreement> rentalAgreements = requestList.stream().parallel().map(rentalService::rent).toList();
            rentalRequestService.saveRentalRequest(requestList);
            return ResponseEntity.ok(mapper.writeValueAsString(rentalAgreements));
        } catch (InvalidRentalRequestException irre) {
            return ResponseEntity.badRequest().body(irre.getMessage());
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/upsertToolAndCharge")
    public ResponseEntity<String> upsertToolAndCharge(@RequestBody Rental rental) {
        if (!activeProfile.contains("cloud")) {
            return ResponseEntity.ok("Database unavailable");
        }
        try {
            return ResponseEntity.ok(mapper.writeValueAsString(rentalService.upsertRental(rental)));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(ex.getMessage());
        }
    }
}
