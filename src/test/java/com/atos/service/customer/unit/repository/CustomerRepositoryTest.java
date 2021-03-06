package com.atos.service.customer.unit.repository;

import static com.atos.service.customer.util.CustomerTestUtils.assertAllButIdsMatchBetweenCustomers;
import static com.atos.service.customer.util.CustomerTestUtils.generateTestCustomer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.atos.service.customer.model.Customer;
import com.atos.service.customer.repository.CustomerRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerRepositoryTest {
	
	private static final long NONEXISTENT_ID = 1000;

	@Autowired
	private CustomerRepository repository;
	
	@Before
	public void setUp() {
		repository.clear();
	}
	
	private void assertNoExistingCustomers() {
		assertExistingCustomerCountIs(0);
	}
	
	private void assertExistingCustomerCountIs(int count) {
		Assert.assertEquals(count, repository.getCount());
	}

	
	private Customer injectCustomer() {
		Customer createdCustomer = repository.create(generateTestCustomer());
		return createdCustomer;
	}
	
	@Test
	public void testFindExistingCustomerEnsureCorrectCustomerValues() throws Exception {
		Customer injectedCustomer = injectCustomer();
		Optional<Customer> foundCustomer = repository.findById(injectedCustomer.getId());
		assertCustomersMatch(injectedCustomer, foundCustomer.get());
	}
	
	private static void assertCustomersMatch(Customer expected, Customer actual) {
		Assert.assertEquals(expected.getId(), actual.getId());
		assertAllButIdsMatchBetweenCustomers(expected, actual);
	}
	
	@Test
	public void testFindAllWithNoExistingCustomersEnsureNoCustomersFound() throws Exception {
		assertFindAllIsCorrectWithCustomerCount(0);
	}
	
	private void assertFindAllIsCorrectWithCustomerCount(int count) {
		injectGivenNumberOfCustomers(count);
		assertExistingCustomerCountIs(count);
		List<Customer> customersFound = repository.findAll();
		Assert.assertEquals(count, customersFound.size());
	}
	
	private List<Customer> injectGivenNumberOfCustomers(int count) {
		
		List<Customer> injectedCustomers = new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			injectedCustomers.add(injectCustomer());
		}
		
		return injectedCustomers;
	}
	
	@Test
	public void testFindAllWithOneExistingCustomerEnsureOneCustomerFound() throws Exception {
		assertFindAllIsCorrectWithCustomerCount(1);
	}
	
	@Test
	public void testFindAllWithTwoExistingCustomersEnsureTwoCustomersFound() throws Exception {
		assertFindAllIsCorrectWithCustomerCount(2);
	}
	
	@Test
	public void testFindAllWithTwoExistingCustomersEnsureFirstCustomerIsCorrect() throws Exception {
		List<Customer> injectedCustomers = injectGivenNumberOfCustomers(2);
		List<Customer> customersFound = repository.findAll();
		assertCustomersMatch(injectedCustomers.get(0), customersFound.get(0));
	}

	@Test
	public void testFindAllWithTwoExistingCustomersEnsureSecondCustomerIsCorrect() throws Exception {
		List<Customer> injectedCustomers = injectGivenNumberOfCustomers(2);
		List<Customer> customersFound = repository.findAll();
		assertCustomersMatch(injectedCustomers.get(1), customersFound.get(1));
	}
	
	@Test
	public void testDeleteNonexistentCustomerEnsureNoCustomerDeleted() throws Exception {
		assertNoExistingCustomers();
		boolean wasDeleted = repository.delete(NONEXISTENT_ID);
		Assert.assertFalse(wasDeleted);
	}

	@Test
	public void testDeleteExistingCustomerEnsureCustomerDeleted() throws Exception {
		Customer injectedCustomer = injectCustomer();
		assertExistingCustomerCountIs(1);
		boolean wasDeleted = repository.delete(injectedCustomer.getId());
		Assert.assertTrue(wasDeleted);
		assertNoExistingCustomers();
	}
	
}
