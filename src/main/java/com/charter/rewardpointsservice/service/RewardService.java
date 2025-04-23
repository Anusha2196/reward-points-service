package com.charter.rewardpointsservice.service;

import java.time.LocalDate;
import java.util.Map;

public interface RewardService {
	int calculatePoints(Double amount);

	Map<String, Object> calculateMonthlyPoints(Long customerId, LocalDate startDate, LocalDate endDate);

	Map<Long, Map<String, Object>> calculateAllCustomersPoints(LocalDate startDate, LocalDate endDate);

}
