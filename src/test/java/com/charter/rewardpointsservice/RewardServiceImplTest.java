package com.charter.rewardpointsservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.charter.rewardpointsservice.dao.TransactionRepository;
import com.charter.rewardpointsservice.model.Transaction;
import com.charter.rewardpointsservice.serviceImpl.RewardServiceImpl;

class RewardServiceImplTest {
        @Mock
	    private TransactionRepository transactionRepository;

	    @InjectMocks
	    private RewardServiceImpl rewardService;

	    @BeforeEach
	    public void setUp() {
	        MockitoAnnotations.openMocks(this);
	    }

	    @Test
	    public void testCalculatePoints() {
	        assertEquals(0, rewardService.calculatePoints(50.0));
	        assertEquals(50, rewardService.calculatePoints(100.0));
	        assertEquals(250, rewardService.calculatePoints(200.0));
	    }

	    @Test
	    public void testCalculateMonthlyPoints() {
	        Long customerId = 1L;
	        LocalDate startDate = LocalDate.of(2023, 1, 1);
	        LocalDate endDate = LocalDate.of(2023, 3, 31);

	        List<Transaction> transactions = Arrays.asList(
	                new Transaction(1L, customerId, 120.0, LocalDate.of(2023, 1, 15)),
	                new Transaction(2L, customerId, 80.0, LocalDate.of(2023, 2, 20)),
	                new Transaction(3L, customerId, 200.0, LocalDate.of(2023, 3, 10))
	        );

	        when(transactionRepository.findByCustomerIdAndDateBetween(customerId, startDate, endDate)).thenReturn(transactions);

	        Map<String, Object> result = rewardService.calculateMonthlyPoints(customerId, startDate, endDate);

	        Map<String, Integer> monthlyPoints = (Map<String, Integer>) result.get("monthlyPoints");
	        int totalPoints = (int) result.get("totalPoints");

	        assertEquals(3, monthlyPoints.size());
	        assertEquals(90, monthlyPoints.get("JANUARY"));
	        assertEquals(30, monthlyPoints.get("FEBRUARY"));
	        assertEquals(250, monthlyPoints.get("MARCH"));
	        assertEquals(370, totalPoints);
	    }

	    @Test
	    public void testCalculateAllCustomersPoints() {
	        LocalDate startDate = LocalDate.of(2023, 1, 1);
	        LocalDate endDate = LocalDate.of(2023, 3, 31);

	        List<Transaction> transactions = Arrays.asList(
	                new Transaction(1L, 1L, 120.0, LocalDate.of(2023, 1, 15)),
	                new Transaction(2L, 1L, 80.0, LocalDate.of(2023, 2, 20)),
	                new Transaction(3L, 2L, 200.0, LocalDate.of(2023, 3, 10))
	        );

	        when(transactionRepository.findByDateBetween(startDate, endDate)).thenReturn(transactions);

	        Map<Long, Map<String, Object>> result = rewardService.calculateAllCustomersPoints(startDate, endDate);

	        assertEquals(2, result.size());

	        Map<String, Integer> customer1Points = (Map<String, Integer>) result.get(1L).get("monthlyPoints");
	        int customer1TotalPoints = (int) result.get(1L).get("totalPoints");

	        assertEquals(2, customer1Points.size());
	        assertEquals(90, customer1Points.get("JANUARY"));
	        assertEquals(30, customer1Points.get("FEBRUARY"));
	        assertEquals(120, customer1TotalPoints);

	        Map<String, Integer> customer2Points = (Map<String, Integer>) result.get(2L).get("monthlyPoints");
	        int customer2TotalPoints = (int) result.get(2L).get("totalPoints");

	        assertEquals(1, customer2Points.size());
	        assertEquals(250, customer2Points.get("MARCH"));
	        assertEquals(250, customer2TotalPoints);
	    }
	    
	        @Test
	        public void testCalculateMonthlyPointsInvalidDateRange() {
	            Long customerId = 1L;
	            LocalDate startDate = LocalDate.of(2023, 3, 31);
	            LocalDate endDate = LocalDate.of(2023, 1, 1);

	            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
	                rewardService.calculateMonthlyPoints(customerId, startDate, endDate);
	            });

	            assertEquals("start date must be earlier than the end date.", exception.getMessage());
	        }

	        @Test
	        public void testCalculateMonthlyPointsDateRangeExceedsThreeMonths() {
	            Long customerId = 1L;
	            LocalDate startDate = LocalDate.of(2023, 1, 1);
	            LocalDate endDate = LocalDate.of(2023, 5, 1);

	            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
	                rewardService.calculateMonthlyPoints(customerId, startDate, endDate);
	            });

	            assertEquals("Date range should not exceed three months.", exception.getMessage());
	        }
	    }

