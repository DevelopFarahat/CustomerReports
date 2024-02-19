package org.reports.customer_reports.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gender {
    MALE("male"),
    FEMALE("female");
    private String value;
}
