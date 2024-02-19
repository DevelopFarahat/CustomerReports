package org.reports.customer_reports.repository;

import org.reports.customer_reports.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {


}
