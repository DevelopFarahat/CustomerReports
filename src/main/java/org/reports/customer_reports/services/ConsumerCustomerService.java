package org.reports.customer_reports.services;

import org.reports.customer_reports.entity.Customer;
import org.reports.customer_reports.repository.CustomerRepository;
import org.reports.customer_reports.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class ConsumerCustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getCustomerPage(int pageNumber, int pageSize) {
        Pageable pages = PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending());
        return customerRepository.findAll(pages).getContent();
    }

    public long getTotalCustomers() {
        return customerRepository.count();
    }

    public double getTotalPages(int pageSize) {
        long totalCustomers = getTotalCustomers();
        double totalPages = Math.ceil((double) totalCustomers / pageSize);
        return totalPages;
    }

    public CompletableFuture<List<List<String[]>>> getCustomerPageData(int pageSize) {
        double totalPages = getTotalPages(pageSize);
        ExecutorService customerDataExecutorThreadPool = Executors.newFixedThreadPool(8);
        List<CompletableFuture<List<Customer>>> completableFuturesList = new ArrayList<>();
        for (int i = 0; i < totalPages; i++) {
            int finalI = i;
            CompletableFuture<List<Customer>> customerDataFuture = CompletableFuture.supplyAsync(() -> getCustomerPage(finalI, pageSize),customerDataExecutorThreadPool);
            completableFuturesList.add(customerDataFuture);
        }
        CompletableFuture<Void> allFuture = CompletableFuture.allOf(completableFuturesList.toArray(new CompletableFuture[completableFuturesList.size()]));
        CompletableFuture<List<List<Customer>>> allFutureResults = allFuture.thenApply(t -> completableFuturesList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        CompletableFuture<List<List<String[]>>> csvCustomerData = allFutureResults.thenApply(future -> future.stream().map(Utils::toStringArray).collect(Collectors.toList()));
        return csvCustomerData;


    }

}
