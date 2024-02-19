package org.reports.customer_reports.request;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import org.reports.customer_reports.enums.Gender;

@Data
public class CustomerFilterCriteria {
    private String firstName;
    private String lastName;
    private Gender gender;
    private int age;
    private String email;
    private String phone;
}
