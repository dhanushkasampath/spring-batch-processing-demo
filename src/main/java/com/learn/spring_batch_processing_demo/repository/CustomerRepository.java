package com.learn.spring_batch_processing_demo.repository;

import com.learn.spring_batch_processing_demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
