package org.reports.customer_reports.services;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.reports.customer_reports.entity.Customer;
import org.reports.customer_reports.request.CustomerFilterCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CustomerSpecification implements Specification<Customer> {
    private CustomerFilterCriteria searchCriteria;

    @Override
    public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (!searchCriteria.getFirstName().isBlank())
            predicates.add(criteriaBuilder.like(root.get("firstname"), searchCriteria.getFirstName()));
        if (!searchCriteria.getLastName().isBlank())
            predicates.add(criteriaBuilder.like(root.get("lastname"), searchCriteria.getLastName()));
        if (!searchCriteria.getEmail().isBlank())
            predicates.add(criteriaBuilder.like(root.get("email"), searchCriteria.getEmail()));
        if (!searchCriteria.getPhone().isBlank())
            predicates.add(criteriaBuilder.like(root.get("phone"), searchCriteria.getPhone()));
        if (searchCriteria.getGender() != null)
            predicates.add(criteriaBuilder.equal(root.get("gender"), searchCriteria.getGender()));
        if (searchCriteria.getAge() != 0)
            predicates.add(criteriaBuilder.equal(root.get("age"), searchCriteria.getAge()));
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
