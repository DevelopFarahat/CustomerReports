package org.reports.customer_reports.controller;

import org.reports.customer_reports.entity.Customer;
import org.reports.customer_reports.request.CustomerFilterCriteria;
import org.reports.customer_reports.services.ConsumerCustomerService;
import org.reports.customer_reports.services.CustomerReportsService;
import org.reports.customer_reports.services.ProducerCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class CustomerController {

    @Autowired
    private ProducerCustomerService producerCustomerService;
    @Autowired
    private ConsumerCustomerService ConsumerCustomerService;
    @Autowired
    private CustomerReportsService customerReportsService;

    @GetMapping(value = "/customer/saving/database/1million")
    public ResponseEntity<Long> saveCustomerToDatabase() {
        return new ResponseEntity<>(producerCustomerService.saveAllCustomerPerBatch(), HttpStatus.OK);
    }

    @GetMapping(value = "/customer")
    public ResponseEntity<Resource> getCustomers() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customers.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(customerReportsService.generateZipFile());
    }

    @PostMapping("/customer/filter/criteria")
    public ResponseEntity<?> getDesiredCustomer(@RequestParam int pageNumber, @RequestBody CustomerFilterCriteria customerFilterCriteria) {
        return new ResponseEntity<>(ConsumerCustomerService.getCustomerDataBySearchCriteria(pageNumber, customerFilterCriteria), HttpStatus.OK);
    }
}
