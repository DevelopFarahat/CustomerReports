package org.reports.customer_reports.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.reports.customer_reports.enums.Gender;

@Data
@Builder
@Entity
@Table(name = "CUSTOMER")
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "CUSTOMER_REPORTS_CUSTOMER_GENERATOR")
    @SequenceGenerator(name = "CUSTOMER_REPORTS_CUSTOMER_GENERATOR", sequenceName = "CUSTOMER_REPORTS_CUSTOMER_GENERATOR_S",allocationSize = 100)
    @Column(name = "ID")
    private long id;
    @Column(name = "FIRSTNAME")
    private String firstname;
    @Column(name = "LASTNAME")
    private String lastname;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "GENDER")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name = "AGE")
    private int age;
}
