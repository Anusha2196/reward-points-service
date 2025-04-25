package com.charter.rewardpointsservice.service;

import java.time.LocalDate;

import com.charter.rewardpointsservice.dto.RewardPointsDto;

public interface RewardService {
	RewardPointsDto getRewardPoints(Long customerId, LocalDate startDate, LocalDate endDate);
}
