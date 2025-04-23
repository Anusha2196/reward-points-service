package com.charter.rewardpointsservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.charter.rewardpointsservice.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
