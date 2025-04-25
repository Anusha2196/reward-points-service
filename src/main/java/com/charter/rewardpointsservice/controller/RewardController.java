package com.charter.rewardpointsservice.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.charter.rewardpointsservice.dto.ResponseDto;
import com.charter.rewardpointsservice.dto.RewardPointsDto;
import com.charter.rewardpointsservice.service.RewardService;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller for handling reward points related requests. Provides endpoints to
 * points for a specific customer or all customers within a date
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
    public ResponseEntity<ResponseDto> getRewardPoints(@PathVariable Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    	ResponseDto responseDto = new ResponseDto();
        try {
        	
            RewardPointsDto points = rewardService.getRewardPoints(customerId, startDate, endDate);
            responseDto.setRewardPointsDto(points);
            
        } catch (Exception e) {
        	responseDto.setStatus("failed");
            responseDto.setError(e.getMessage());
        }
        
        return ResponseEntity.ok(responseDto);
    }
}
