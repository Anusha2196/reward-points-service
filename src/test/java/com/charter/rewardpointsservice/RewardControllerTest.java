package com.charter.rewardpointsservice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class RewardControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testGetRewardPointsValidDates() throws Exception {
		mockMvc.perform(get("/rewards/points/1").param("startDate", "2023-01-01").param("endDate", "2023-03-31"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.monthlyPoints.JANUARY").value(90))
				.andExpect(jsonPath("$.monthlyPoints.FEBRUARY").value(30))
				.andExpect(jsonPath("$.monthlyPoints.MARCH").value(250))
				.andExpect(jsonPath("$.totalPoints").value(370));
	}

	@Test
	public void testGetRewardPointsNoDates() throws Exception {
		mockMvc.perform(get("/rewards/points/1")).andExpect(status().isOk())
				.andExpect(jsonPath("$.monthlyPoints").exists()).andExpect(jsonPath("$.totalPoints").exists());
	}

	@Test
	public void testGetRewardPointsInvalidDateRange() throws Exception {
		mockMvc.perform(get("/rewards/points/1").param("startDate", "2023-03-31").param("endDate", "2023-01-01"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("start date must be earlier than the end date."));
	}

	@Test
	public void testGetRewardPointsDateRangeExceedsThreeMonths() throws Exception {
		mockMvc.perform(get("/rewards/points/1").param("startDate", "2023-01-01").param("endDate", "2023-05-01"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Date range should not exceed three months."));
	}

	@Test
	public void testCalculateAllCustomersPointsValidDates() throws Exception {
		mockMvc.perform(get("/rewards/allcustomers").param("startDate", "2023-01-01").param("endDate", "2023-03-31"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.1.monthlyPoints.JANUARY").value(90))
				.andExpect(jsonPath("$.1.monthlyPoints.FEBRUARY").value(30))
				.andExpect(jsonPath("$.1.totalPoints").value(120))
				.andExpect(jsonPath("$.2.monthlyPoints.MARCH").value(250))
				.andExpect(jsonPath("$.2.totalPoints").value(250));
	}

	@Test
	public void testCalculateAllCustomersPointsNoDates() throws Exception {
		mockMvc.perform(get("/rewards/allcustomers")).andExpect(status().isOk()).andExpect(jsonPath("$").exists());
	}

	@Test
	public void testCalculateAllCustomersPointsInvalidDateRange() throws Exception {
		mockMvc.perform(get("/rewards/allcustomers").param("startDate", "2023-03-31").param("endDate", "2023-01-01"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("start date must be earlier than the end date."));
	}

	@Test
	public void testCalculateAllCustomersPointsDateRangeExceedsThreeMonths() throws Exception {
		mockMvc.perform(get("/rewards/allcustomers").param("startDate", "2023-01-01").param("endDate", "2023-05-01"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Date range should not exceed three months."));
	}
}