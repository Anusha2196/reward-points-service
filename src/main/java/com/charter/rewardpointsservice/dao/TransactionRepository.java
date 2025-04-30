package com.charter.rewardpointsservice.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.charter.rewardpointsservice.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	public List<Transaction> findByCustomerIdAndDateBetween(Long customerId, LocalDate startDate, LocalDate endDate);
}
