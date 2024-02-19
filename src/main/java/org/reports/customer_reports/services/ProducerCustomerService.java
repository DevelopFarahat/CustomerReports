package org.reports.customer_reports.services;

import com.zaxxer.hikari.HikariDataSource;
import org.reports.customer_reports.entity.Customer;
import org.reports.customer_reports.enums.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ProducerCustomerService {
    @Autowired
    HikariDataSource hikariDataSource;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public List<Customer> constructCustomerList() {
        String[] genderArr = new String[]{"male", "female"};
        List<Customer> customers = new ArrayList<>();

        for (int i = 1; i <= 1000000; i++) {
            Customer customer = Customer.builder()
                    .id(i)
                    .firstname("name:" + " " + i)
                    .lastname("name:" + " " + i)
                    .email("name@gmail.com:" + " " + i)
                    .phone("01232")
                    .gender(Gender.MALE.getValue() == genderArr[(int) Math.floor(Math.random() * genderArr.length)]?Gender.MALE:Gender.FEMALE)
                    .age(29)
                    .build();
            customers.add(customer);
        }
        return customers;
    }

    public List<StringBuilder> generateCustomerBulkInsertStatement() {
        List<Customer> customers = constructCustomerList();
        StringBuilder sql = new StringBuilder("INSERT INTO CUSTOMER(ID,FIRSTNAME,LASTNAME,EMAIL,PHONE,GENDER,AGE)VALUES");
        StringBuilder sqlValues = new StringBuilder();
        List<StringBuilder> bulkCustomerSqlInsertIntoList = new ArrayList<>();
        for(int i = 0 ; i < customers.size();i++){
            Customer customer = customers.get(i);
            sqlValues.append("(").append(customer.getId()).append(",'").append(customer.getFirstname()).append("','").append(customer.getLastname()).append("'\n").append(",'").append(customer.getEmail()).append("','").append(customer.getPhone()).append("','").append(customer.getGender()).append("',").append(customer.getAge()).append("),");
            if( i % batchSize == 0 || (i+1) == customers.size()){
                sqlValues = new StringBuilder(sqlValues.substring(0, sqlValues.lastIndexOf(",")));
                sql.append(sqlValues);
                bulkCustomerSqlInsertIntoList.add(sql);
                sql = new StringBuilder("INSERT INTO CUSTOMER(ID,FIRSTNAME,LASTNAME,EMAIL,PHONE,GENDER,AGE)VALUES");
                sqlValues = new StringBuilder();

            }
        }
        return bulkCustomerSqlInsertIntoList;
    }

    public Long saveAllCustomerPerBatch(){
        List<StringBuilder> customerBulkInsertList = generateCustomerBulkInsertStatement();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ExecutorService executor = Executors.newFixedThreadPool(7);
        CompletableFuture [] databaseSavingThreadsFuture = new CompletableFuture[customerBulkInsertList.size()];
        for (int i = 0 ; i < customerBulkInsertList.size();i++){
            int finalI = i;
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(()->{
                try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
                    String sql = customerBulkInsertList.get(finalI).toString();
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.execute();
                    }
                } catch (SQLException e) {
                    // Handle exception appropriately
                    e.printStackTrace();
                }
            },executor);
            databaseSavingThreadsFuture[i] = completableFuture;
        }
        CompletableFuture<Void> allFutureThreads = CompletableFuture.allOf(databaseSavingThreadsFuture);
        allFutureThreads.join();
        stopWatch.stop();
        return stopWatch.getTotalTimeMillis();
    }




}
