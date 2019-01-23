package com.atos.service.customer.integration.controller.util;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.test.web.servlet.ResultMatcher;

import com.atos.service.customer.model.Customer;

public class CustomerControllerTestUtils {

	public static ResultMatcher customerAtIndexIsCorrect(int index, Customer expected) {
		return new CompositeResultMatcher()
			.addMatcher(jsonPath("$.[" + index + "].id").value(expected.getId()))
			.addMatcher(jsonPath("$.[" + index + "].firstName").value(expected.getFirstName()))
			.addMatcher(jsonPath("$.[" + index + "].surName").value(expected.getSurName()));
	}
	
	public static ResultMatcher customerIsCorrect(Customer expected) {
		return customerIsCorrect(expected.getId(), expected);
	}
	
	private static ResultMatcher customerIsCorrect(Long expectedId, Customer expected) {
		return new CompositeResultMatcher().addMatcher(jsonPath("$.id").value(expectedId))
			.addMatcher(jsonPath("$.firstName").value(expected.getFirstName()))
			.addMatcher(jsonPath("$.surName").value(expected.getSurName()));
	}
	
	public static ResultMatcher updatedCustomerIsCorrect(Long originalId, Customer expected) {
		return customerIsCorrect(originalId, expected);
	}
	
	
}
