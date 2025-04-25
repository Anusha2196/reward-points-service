package com.charter.rewardpointsservice.serviceImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.charter.rewardpointsservice.dao.TransactionRepository;
import com.charter.rewardpointsservice.dto.RewardPointsDto;
import com.charter.rewardpointsservice.model.Transaction;
import com.charter.rewardpointsservice.service.RewardService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the RewardService interface. Provides methods to calculate
 * reward points based on transactions.
 */
@Service
@Slf4j
public class RewardServiceImpl implements RewardService {

	@Autowired
	private TransactionRepository transactionRepository;


	/**
	 * Retrieves reward points for a specific customer within a given date
	 * range.      
	 * If the date range exceeds three months, an exception is thrown.
	 * @param customerId the ID of the customer     
	 * @param startDate  the start date of the range      
	 * @param endDate    the end date of the range      
	 * @return a RewardPointsDto containing the calculated reward points      
	 * @throws IllegalArgumentException if the date range is
	 * invalid or exceeds three months     
	 */
	@Override
	public RewardPointsDto getRewardPoints(Long customerId, LocalDate startDate, LocalDate endDate) {
		Map<String, Object> validationResponse = validateAndAdjustDates(startDate, endDate);
		if (validationResponse.containsKey("error")) {
			throw new IllegalArgumentException((String) validationResponse.get("error"));
		}

		startDate = (LocalDate) validationResponse.get("startDate");
		endDate = (LocalDate) validationResponse.get("endDate");

		return calculateMonthlyPoints(customerId, startDate, endDate);
	}


     /**
     * Calculates the monthly reward points for a customer within a specified date range.
     *
     * @param customerId the ID of the customer
     * @param startDate  the start date of the range
     * @param endDate    the end date of the range
     * @return a RewardPointsDto containing the calculated monthly and total reward points
     */
	public RewardPointsDto calculateMonthlyPoints(Long customerId, LocalDate startDate, LocalDate endDate) {
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
		return new RewardPointsDto(monthlyPoints, totalPoints);
	}

	/**
	 * Calculates reward points based on the transaction amount.
	 * 
	 * @param amount the transaction amount
	 * @return the calculated reward points
	 */
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
	 * Validates and adjusts the provided start and end dates to ensure they form a
	 * valid range. If both dates are null, the range is set to the last three
	 * months from the current date. If only one date is provided, the other date is
	 * adjusted to form a three-month range. If the start date is after the end
	 * date, an error message is returned. If the date range exceeds three months,
	 * an error message is returned.
	 *
	 * @param startDate the start date of the range (can be null)
	 * @param endDate   the end date of the range (can be null)
	 * @return a map containing the adjusted start and end dates, or an error
	 *         message if the range is invalid
	 */
	private Map<String, Object> validateAndAdjustDates(LocalDate startDate, LocalDate endDate) {
		if (startDate == null && endDate == null) {
			endDate = LocalDate.now();
			startDate = endDate.minusMonths(3);
		} else if (startDate != null && endDate == null) {
			endDate = startDate.plusMonths(3);
		} else if (startDate == null && endDate != null) {
			startDate = endDate.minusMonths(3);
		}

		log.info("startDate:" + startDate);
		log.info("endDate:" + endDate);

		if (startDate.isAfter(endDate)) {
			return Map.of("error", "start date must be earlier than the end date.");
		}

		long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);
		LocalDate adjustedDate = startDate.plusMonths(monthsBetween);
		long daysBetween = ChronoUnit.DAYS.between(adjustedDate, endDate);

		log.info("Months between " + startDate + " and " + endDate + ": " + monthsBetween);
		log.info("Extra days between " + adjustedDate + " and " + endDate + ": " + daysBetween);
		log.info("days difference:" + ChronoUnit.DAYS.between(startDate, endDate));
		log.info("months difference:" + ChronoUnit.MONTHS.between(startDate, endDate));

		if (ChronoUnit.MONTHS.between(startDate, endDate) >= 3 && daysBetween > 0) {
			return Map.of("error", "Date range should not exceed three months.");
		}

		return Map.of("startDate", startDate, "endDate", endDate);
	}
}
