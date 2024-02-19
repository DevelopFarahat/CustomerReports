package org.reports.customer_reports.utils;

import org.reports.customer_reports.entity.Customer;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<String[]>  toStringArray(List<Customer> customerList) {
        List<String[]> records = new ArrayList<>();
        customerList.stream().forEach(customer -> {
            records.add(new String[]{String.valueOf(customer.getId()), customer.getFirstname(), customer.getLastname(), customer.getEmail(), customer.getPhone(),String.valueOf(customer.getGender()), String.valueOf(customer.getAge())});
        });
        return records;
    }
}
