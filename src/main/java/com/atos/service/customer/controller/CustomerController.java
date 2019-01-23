package com.atos.service.customer.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.atos.service.customer.model.Customer;
import com.atos.service.customer.repository.CustomerRepository;


@RestController
@ExposesResourceFor(Customer.class)
@RequestMapping(value = "/customer", produces = "application/json")
public class CustomerController {
	
	@Autowired
	private CustomerRepository repository;
	


	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Collection<Customer>> findAllCustomers() {
		List<Customer> customers = repository.findAll();
		return new ResponseEntity<>(customers, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
		Customer createdCustomer = repository.create(customer);
		return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Customer> findCustomerById(@PathVariable Long id) {
		Optional<Customer> customer = repository.findById(id);

		if (customer.isPresent()) {
			return new ResponseEntity<>(customer.get(), HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
		boolean wasDeleted = repository.delete(id);
		HttpStatus responseStatus = wasDeleted ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND;
		return new ResponseEntity<>(responseStatus);
	}
	
	
}
