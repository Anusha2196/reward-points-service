package com.charter.rewardpointsservice.dto;

import lombok.Data;

@Data
public class ResponseDto {

	private String status = "success";
	private String error;
	private RewardPointsDto rewardPointsDto;
}
