package com.charter.rewardpointsservice.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.charter.rewardpointsservice.service.RewardService;

import lombok.extern.slf4j.Slf4j;


/**
 * Controller for handling reward points related requests. Provides endpoints to
 * get reward points for a specific customer or all customers within a date
 * range.
 */
@RestController
@RequestMapping("/rewards")
@Slf4j
public class RewardController {

	@Autowired
	private RewardService rewardService;


	/**
	 * Endpoint to get reward points for a specific customer within a date range.
	 *
	 * @param customerId the ID of the customer
	 * @param startDate  the start date of the range (optional)
	 * @param endDate    the end date of the range (optional)
	 * @return a ResponseEntity containing the reward points or an error message
	 */
	@GetMapping("/points/{customerId}")
	public ResponseEntity<Map<String, Object>> getRewardPoints(@PathVariable Long customerId,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
	    try {
	        Map<String, Object> validationResponse = validateAndAdjustDates(startDate, endDate);
	        if (validationResponse.containsKey("error")) {
	            return ResponseEntity.badRequest().body(validationResponse);
	        }

	        startDate = (LocalDate) validationResponse.get("startDate");
	        endDate = (LocalDate) validationResponse.get("endDate");

	        Map<String, Object> points = rewardService.calculateMonthlyPoints(customerId, startDate, endDate);
	        return ResponseEntity.ok(points);
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
	    }
	}


	/**
	 * Endpoint to get reward points for all customers within a date range.
	 *
	 * @param startDate the start date of the range (optional)
	 * @param endDate   the end date of the range (optional)
	 * @return a map containing customer IDs mapped to their reward points
	 */
	@GetMapping("/allcustomers")
	public Map<Long, Map<String, Object>> calculateAllCustomersPoints(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		Map<String, Object> validationResponse = validateAndAdjustDates(startDate, endDate);
		if (validationResponse.containsKey("error")) {
			throw new IllegalArgumentException((String) validationResponse.get("error"));
		}

		startDate = (LocalDate) validationResponse.get("startDate");
		endDate = (LocalDate) validationResponse.get("endDate");

		return rewardService.calculateAllCustomersPoints(startDate, endDate);
	}


	/**
	 * Validates and adjusts the start and end dates. If both dates are null, sets
	 * the date range to the last 3 months. If only one date is provided, adjusts
	 * the other date to maintain a 3-month range. Ensures the start date is not
	 * after the end date and the range does not exceed 3 months.
	 *
	 * @param startDate the start date of the range
	 * @param endDate   the end date of the range
	 * @return a map containing the validated and adjusted start and end dates, or
	 *         an error message
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
	    log.info("months difference:" + ChronoUnit.DAYS.between(startDate, endDate));
	    log.info("months difference:" + ChronoUnit.MONTHS.between(startDate, endDate));

	    if (ChronoUnit.MONTHS.between(startDate, endDate) >= 3 && daysBetween > 0) {
	        return Map.of("error", "Date range should not exceed three months.");
	    }

	    return Map.of("startDate", startDate, "endDate", endDate);
	}

}


