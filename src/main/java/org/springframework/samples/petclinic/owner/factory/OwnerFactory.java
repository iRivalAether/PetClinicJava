/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner.factory;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.stereotype.Component;

/**
 * Factory for creating Owner instances.
 *
 * PATTERN: Factory Pattern WHY: - Encapsulates object creation logic - Provides different
 * methods for different creation scenarios - Ensures objects are created in valid states
 * - Makes testing easier with consistent test data creation
 *
 * BENEFITS: - Single Responsibility: Only handles owner creation - Consistency: All
 * owners created through factory have consistent defaults - Testability: Easy to create
 * test data - Extensibility: Easy to add new creation methods
 *
 * @author Refactored for Design Patterns
 */
@Component
public class OwnerFactory {

	/**
	 * Create a basic owner with required fields. PATTERN: Factory Method
	 * @param firstName owner's first name
	 * @param lastName owner's last name
	 * @param address owner's address
	 * @param city owner's city
	 * @param telephone owner's telephone
	 * @return new Owner instance
	 */
	public Owner createOwner(String firstName, String lastName, String address, String city, String telephone) {
		Owner owner = new Owner();
		owner.setFirstName(firstName);
		owner.setLastName(lastName);
		owner.setAddress(address);
		owner.setCity(city);
		owner.setTelephone(telephone);
		return owner;
	}

	/**
	 * Create an owner with default test values. PATTERN: Factory Method for testing WHY:
	 * Useful for unit tests and demos
	 * @return Owner with default test data
	 */
	public Owner createDefaultOwner() {
		return createOwner("John", "Doe", "123 Main Street", "New York", "1234567890");
	}

	/**
	 * Create an owner using builder pattern. PATTERN: Builder Pattern (fluent API) WHY:
	 * Provides readable, flexible object construction
	 * @return OwnerBuilder instance
	 */
	public OwnerBuilder builder() {
		return new OwnerBuilder();
	}

	/**
	 * Builder class for Owner.
	 *
	 * PATTERN: Builder Pattern WHY: - Provides fluent API for object construction - Makes
	 * code more readable: owner.builder().withFirstName("John").build() - Handles
	 * optional parameters elegantly - Validates before building
	 *
	 * EXAMPLE USAGE: Owner owner = ownerFactory.builder() .withFirstName("John")
	 * .withLastName("Doe") .withAddress("123 Main St") .withCity("New York")
	 * .withTelephone("1234567890") .build();
	 */
	public static class OwnerBuilder {

		private final Owner owner;

		public OwnerBuilder() {
			this.owner = new Owner();
		}

		public OwnerBuilder withFirstName(String firstName) {
			owner.setFirstName(firstName);
			return this;
		}

		public OwnerBuilder withLastName(String lastName) {
			owner.setLastName(lastName);
			return this;
		}

		public OwnerBuilder withAddress(String address) {
			owner.setAddress(address);
			return this;
		}

		public OwnerBuilder withCity(String city) {
			owner.setCity(city);
			return this;
		}

		public OwnerBuilder withTelephone(String telephone) {
			owner.setTelephone(telephone);
			return this;
		}

		/**
		 * Build the Owner instance. PATTERN: Builder validation WHY: Ensures object is in
		 * valid state before returning
		 * @return constructed Owner
		 * @throws IllegalStateException if required fields are missing
		 */
		public Owner build() {
			validateRequiredFields();
			return owner;
		}

		private void validateRequiredFields() {
			if (owner.getFirstName() == null || owner.getFirstName().isBlank()) {
				throw new IllegalStateException("First name is required");
			}
			if (owner.getLastName() == null || owner.getLastName().isBlank()) {
				throw new IllegalStateException("Last name is required");
			}
			if (owner.getAddress() == null || owner.getAddress().isBlank()) {
				throw new IllegalStateException("Address is required");
			}
			if (owner.getCity() == null || owner.getCity().isBlank()) {
				throw new IllegalStateException("City is required");
			}
			if (owner.getTelephone() == null || !owner.getTelephone().matches("\\d{10}")) {
				throw new IllegalStateException("Telephone must be exactly 10 digits");
			}
		}

	}

}
