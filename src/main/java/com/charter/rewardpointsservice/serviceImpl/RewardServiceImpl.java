package com.charter.rewardpointsservice.serviceImpl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.charter.rewardpointsservice.dao.TransactionRepository;
import com.charter.rewardpointsservice.model.Transaction;
import com.charter.rewardpointsservice.service.RewardService;


/**
 * Implementation of the RewardService interface. Provides methods to calculate
 * reward points based on transactions.
 */
@Service
public class RewardServiceImpl implements RewardService {

	@Autowired
	private TransactionRepository transactionRepository;


	/**
	 * Calculates reward points based on the transaction amount.
	 * 
	 * @param amount the transaction amount
	 * @return the calculated reward points
	 */
	@Override
	public int calculatePoints(Double amount) {
		int points = 0;
		if (amount > 100) {
			points += (amount - 100) * 2;
			amount = (double) 100;
		}
		if (amount >= 50) {
			points += (amount - 50);
		}
		return points;
	}


	/**
	 * Calculates monthly and total reward points for a specific customer within a
	 * date range.
	 * 
	 * @param customerId the ID of the customer
	 * @param startDate  the start date of the range
	 * @param endDate    the end date of the range
	 * @return a map containing monthly points and total points
	 */
	@Override
	public Map<String, Object> calculateMonthlyPoints(Long customerId, LocalDate startDate, LocalDate endDate) {
		List<Transaction> transactions = transactionRepository.findByCustomerIdAndDateBetween(customerId, startDate,
				endDate);
		Map<String, Integer> monthlyPoints = new HashMap<>();
		int totalPoints = 0;
		for (Transaction transaction : transactions) {
			String month = transaction.getDate().getMonth().toString();
			int points = calculatePoints(transaction.getAmount());
			monthlyPoints.put(month, monthlyPoints.getOrDefault(month, 0) + points);
			totalPoints += points;
		}
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("monthlyPoints", monthlyPoints);
		result.put("totalPoints", totalPoints);
		return result;
	}


	/**
	 * Calculates monthly and total reward points for all customers within a date
	 * range.
	 * 
	 * @param startDate the start date of the range
	 * @param endDate   the end date of the range
	 * @return a map containing customer IDs mapped to their monthly and total
	 *         points
	 */
	@Override
	public Map<Long, Map<String, Object>> calculateAllCustomersPoints(LocalDate startDate, LocalDate endDate) {
		List<Transaction> transactions = transactionRepository.findByDateBetween(startDate, endDate);
		Map<Long, Map<String, Object>> allCustomersPoints = new LinkedHashMap<>();

		for (Transaction transaction : transactions) {
			Long customerId = transaction.getCustomerId();
			String month = transaction.getDate().getMonth().toString();
			int points = calculatePoints(transaction.getAmount());

			allCustomersPoints.putIfAbsent(customerId, new LinkedHashMap<>());
			Map<String, Integer> monthlyPoints = (Map<String, Integer>) allCustomersPoints.get(customerId)
					.getOrDefault("monthlyPoints", new HashMap<>());
			int totalPoints = (int) allCustomersPoints.get(customerId).getOrDefault("totalPoints", 0);

			monthlyPoints.put(month, monthlyPoints.getOrDefault(month, 0) + points);
			totalPoints += points;

			allCustomersPoints.get(customerId).put("monthlyPoints", monthlyPoints);
			allCustomersPoints.get(customerId).put("totalPoints", totalPoints);
		}

		return allCustomersPoints;
	}

}
