package com.charter.rewardpointsservice.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardPointsDto {
	
	private Map<String, Integer> monthlyPoints;
	private int totalPoints;

}
