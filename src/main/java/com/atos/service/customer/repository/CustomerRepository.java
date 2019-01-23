package com.atos.service.customer.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.atos.service.customer.model.Customer;

@Repository
public  class CustomerRepository {

	@Autowired
	private IdGenerator idGenerator;
	
	private List<Customer> customers = Collections.synchronizedList(new ArrayList<>());

	public Customer create(Customer element) {
		customers.add(element);
		element.setId(idGenerator.getNextId());
		return element;
	}

	public boolean delete(Long id) {
		return customers.removeIf(element -> element.getId().equals(id));
	}

	public List<Customer> findAll() {
		return customers;
	}

	public Optional<Customer> findById(Long id) {
		return customers.stream().filter(e -> e.getId().equals(id)).findFirst();
	}

	public int getCount() {
		return customers.size();
	}

	public void clear() {
		customers.clear();
	}

	

}
