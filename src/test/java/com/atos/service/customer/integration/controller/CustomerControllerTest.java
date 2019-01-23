package com.atos.service.customer.integration.controller;

import static com.atos.service.customer.integration.controller.util.CustomerControllerTestUtils.updatedCustomerIsCorrect;
import static com.atos.service.customer.util.CustomerTestUtils.assertAllButIdsMatchBetweenCustomers;
import static com.atos.service.customer.util.CustomerTestUtils.generateTestCustomer;
import static com.atos.service.customer.util.CustomerTestUtils.generateUpdatedCustomer;
import static com.atos.service.customer.integration.controller.util.CustomerControllerTestUtils.customerAtIndexIsCorrect;
import static com.atos.service.customer.integration.controller.util.CustomerControllerTestUtils.customerIsCorrect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultActions;

import com.atos.service.customer.model.Customer;
import com.atos.service.customer.repository.CustomerRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerControllerTest extends ControllerIntegrationTest {
	
	private static final String INVALID_TEST_CUSTOMER = "";
	private static final String TEST_CUSTOMER = "{\"firstName\": \"test firstname\", \"surname\": \"test surname\"}";
	private static final String TEST_CUSTOMER_MISSING_DATA = "{\"foo\": \"bar\"}";
	
	@Autowired
	private CustomerRepository repository;
	
	
	@Before
	public void setUp() {
		repository.clear();
	}

    @Test
    public void testGetAllEmptyListEnsureCorrectResponse() throws Exception {
    	assertNoCustomers();
        getCustomer()
        	.andExpect(status().isOk())
            .andExpect(content().string(equalTo("[]")));
    }

	private ResultActions getCustomer() throws Exception {
		return get("/customer");
	}
    
    private void assertNoCustomers() {
    	assertCustomerCountIs(0);
    }
    
    private void assertCustomerCountIs(int count) {
    	Assert.assertEquals(count, repository.getCount());
    }
    
    @Test
    public void testGetAllOneCustomerEnsureCorrectResponse() throws Exception {
    	Customer injectedCustomer = injectCustomer();
    	assertCustomerCountIs(1);
        getCustomer()
        	.andExpect(status().isOk())
        	.andExpect(customerAtIndexIsCorrect(0, injectedCustomer));
    }
    
    private Customer injectCustomer() {
    	Customer customer = new Customer();
    	customer.setFirstName("Test FirstName");
    	customer.setSurName("Test SurName");
    	
    	return repository.create(customer);
    }
    
    @Test
    public void testGetAllTwoCustomerEnsureCorrectResponse() throws Exception {
    	Customer injectedCustomer1 = injectCustomer();
    	Customer injectedCustomer2 = injectCustomer();
    	assertCustomerCountIs(2);
        getCustomer()
        	.andExpect(status().isOk())
        	.andExpect(customerAtIndexIsCorrect(0, injectedCustomer1))
        	.andExpect(customerAtIndexIsCorrect(1, injectedCustomer2));
    }
    
    @Test
    public void testGetNonexistentCustomerEnsureNotFoundResponse() throws Exception {
    	assertNoCustomers();
        getCustomer(1)
        	.andExpect(status().isNotFound());
    }

	private ResultActions getCustomer(long id) throws Exception {
		return get("/customer/{id}", id);
	}
    
    @Test
    public void testGetExistingCustomerEnsureCorrectResponse() throws Exception {
    	Customer injectedCustomer = injectCustomer();
    	assertCustomerCountIs(1);
        getCustomer(injectedCustomer.getId())
        	.andExpect(status().isOk())
        	.andExpect(customerIsCorrect(injectedCustomer));
    }
    
  
    @Test
    public void testCreateNewCustomerEnsureCustomerCreated() throws Exception {
    	assertNoCustomers();
    	Customer desiredCustomer = generateTestCustomer();
    	createCustomer(toJsonString(desiredCustomer));
    	assertCustomerCountIs(1);
    	assertAllButIdsMatchBetweenCustomers(desiredCustomer, getCreatedCustomer());
    }
    
    private ResultActions createCustomer(String payload) throws Exception {
    	return post("/customer", payload);
    }

	private Customer getCreatedCustomer() {
		List<Customer> customers = repository.findAll();
		return customers.get(customers.size() - 1);
	}
    
    @Test
    public void testCreateNewCustomerEnsureCorrectResponse() throws Exception {
    	assertNoCustomers();
    	createCustomer(TEST_CUSTOMER)
    		.andExpect(status().isCreated())
    		.andExpect(customerIsCorrect(getCreatedCustomer()));
    }
    
    @Test
    public void testCreateNewCustomerMissingDataEnsureCorrectResponse() throws Exception {
    	assertNoCustomers();
    	createCustomer(TEST_CUSTOMER_MISSING_DATA)
    		.andExpect(status().isCreated())
    		.andExpect(customerIsCorrect(getCreatedCustomer()));
    }
    
    @Test
    public void testCreateInvalidNewCustomerEnsureCorrectResponse() throws Exception {
    	assertNoCustomers();
    	createCustomer(INVALID_TEST_CUSTOMER)
    		.andExpect(status().isBadRequest());
    }
    
    @Test
    public void testDeleteNonexistentCustomerEnsureCorrectResponse() throws Exception {
    	assertNoCustomers();
    	deleteCustomer(1)
    		.andExpect(status().isNotFound());
    }
    
    private ResultActions deleteCustomer(long id) throws Exception {
    	return delete("/customer/{id}", id);
    }

    @Test
    public void testDeleteExistingCustomerEnsureCorrectResponse() throws Exception {
    	Customer injectedCustomer = injectCustomer();
    	assertCustomerCountIs(1);
    	deleteCustomer(injectedCustomer.getId())
    		.andExpect(status().isNoContent());
    }
    
    @Test
    public void testDeleteExistingCustomerEnsureCustomerDeleted() throws Exception {
    	Customer injectedCustomer = injectCustomer();
    	assertCustomerCountIs(1);
    	deleteCustomer(injectedCustomer.getId());
    	assertNoCustomers();
    }
    
    @Test
    public void testUpdateNonexistentCustomerEnsureCorrectResponse() throws Exception {
    	assertNoCustomers();
    	updateCustomer(1, new Customer())
    		.andExpect(status().isNotFound());
    }
    
    private ResultActions updateCustomer(long id, Customer updatedCustomer) throws Exception {
    	return put("/customer/{id}", updatedCustomer, String.valueOf(id));
    }
    
    @Test
    public void testUpdateExistingCustomerEnsureCustomerUpdated() throws Exception {
    	Customer originalCustomer = injectCustomer();
    	assertCustomerCountIs(1);
    	Customer updatedCustomer = generateUpdatedCustomer(originalCustomer);
    	updateCustomer(originalCustomer.getId(), updatedCustomer);
    	assertAllButIdsMatchBetweenCustomers(updatedCustomer, originalCustomer);
    }
    
    @Test
    public void testUpdateExistingCustomerEnsureCorrectResponse() throws Exception {
    	Customer originalCustomer = injectCustomer();
    	assertCustomerCountIs(1);
    	Customer updatedCustomer = generateUpdatedCustomer(originalCustomer);
    	updateCustomer(originalCustomer.getId(), updatedCustomer)
    		.andExpect(status().isOk())
    		.andExpect(updatedCustomerIsCorrect(originalCustomer.getId(), updatedCustomer));
    }
}
