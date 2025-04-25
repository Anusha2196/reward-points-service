package com.charter.rewardpointsservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.charter.rewardpointsservice.dao.TransactionRepository;
import com.charter.rewardpointsservice.dto.RewardPointsDto;
import com.charter.rewardpointsservice.model.Transaction;
import com.charter.rewardpointsservice.serviceImpl.RewardServiceImpl;

class RewardServiceImplTest {

	@Mock
	private TransactionRepository transactionRepository;

	@InjectMocks
	private RewardServiceImpl rewardServiceImpl;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCalculatePoints() {
		assertEquals(90, rewardServiceImpl.calculatePoints(120.0));
		assertEquals(50, rewardServiceImpl.calculatePoints(100.0));
		assertEquals(0, rewardServiceImpl.calculatePoints(40.0));
	}

	@Test
	void testCalculateMonthlyPoints() {
		Long customerId = 1L;
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		LocalDate endDate = LocalDate.of(2025, 3, 31);

		List<Transaction> transactions = List.of(new Transaction(1L, customerId, 120.0, LocalDate.of(2025, 1, 15)),
				new Transaction(2L, customerId, 80.0, LocalDate.of(2025, 2, 20)),
				new Transaction(3L, customerId, 40.0, LocalDate.of(2025, 3, 10)));

		when(transactionRepository.findByCustomerIdAndDateBetween(customerId, startDate, endDate))
				.thenReturn(transactions);

		RewardPointsDto result = rewardServiceImpl.calculateMonthlyPoints(customerId, startDate, endDate);

		assertNotNull(result);
		assertEquals(2, result.getMonthlyPoints().size());
		assertEquals(90, result.getMonthlyPoints().get("JANUARY"));
		assertEquals(30, result.getMonthlyPoints().get("FEBRUARY"));
		assertEquals(120, result.getTotalPoints());
	}

	@Test
	void testGetRewardPoints_InvalidDateRange() {
		Long customerId = 1L;
		LocalDate startDate = LocalDate.of(2025, 4, 1);
		LocalDate endDate = LocalDate.of(2025, 1, 1);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			rewardServiceImpl.getRewardPoints(customerId, startDate, endDate);
		});

		assertEquals("start date must be earlier than the end date.", exception.getMessage());
	}
}
