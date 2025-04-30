package com.charter.rewardpointsservice.dto;

import lombok.Data;

@Data
public class ResponseDto {

	private String status;
	private String error;
	private RewardPointsDto rewardPointsDto;
}
