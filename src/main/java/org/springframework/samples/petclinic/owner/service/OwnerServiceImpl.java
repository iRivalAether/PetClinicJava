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
package org.springframework.samples.petclinic.owner.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of OwnerService.
 *
 * PATTERN: Service Layer Pattern WHY: This implementation: - Encapsulates business logic
 * away from controllers - Manages transactions with @Transactional - Provides a single
 * point for owner-related operations - Can be easily tested with mock repositories
 *
 * PATTERN: Facade Pattern WHY: Simplifies complex repository operations and provides a
 * unified interface
 *
 * @author Refactored for Design Patterns
 */
@Service
@Transactional(readOnly = true)
public class OwnerServiceImpl implements OwnerService {

	private final OwnerRepository ownerRepository;

	/**
	 * Constructor injection for better testability and immutability. PATTERN: Dependency
	 * Injection WHY: Promotes loose coupling and makes testing easier
	 */
	public OwnerServiceImpl(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}

	@Override
	@Transactional
	public Owner save(Owner owner) {
		return ownerRepository.save(owner);
	}

	@Override
	public Optional<Owner> findById(Integer id) {
		return ownerRepository.findById(id);
	}

	@Override
	public Page<Owner> findByLastName(String lastName, Pageable pageable) {
		// Normalize search input - defensive programming
		String searchLastName = (lastName == null) ? "" : lastName;
		return ownerRepository.findByLastNameStartingWith(searchLastName, pageable);
	}

	@Override
	@Transactional
	public Owner createOwner(Owner owner) {
		// PATTERN: Template Method Pattern
		// WHY: Defines the skeleton of owner creation: validate -> save

		// Validation step
		validateOwner(owner);

		// Ensure it's a new owner
		if (!owner.isNew()) {
			throw new IllegalArgumentException("Cannot create owner with existing ID");
		}

		// Business logic: could add additional operations here
		// (e.g., send welcome email, create audit log, etc.)

		return ownerRepository.save(owner);
	}

	@Override
	@Transactional
	public Owner updateOwner(Integer ownerId, Owner owner) {
		// PATTERN: Template Method Pattern
		// WHY: Defines the skeleton of owner update: validate -> check exists -> update

		// Validation step
		validateOwner(owner);

		// Check owner exists (will throw exception if not found)
		ownerRepository.findById(ownerId)
			.orElseThrow(() -> new IllegalArgumentException(
					"Owner not found with id: " + ownerId + ". Please ensure the ID is correct"));

		// Verify ID consistency
		if (owner.getId() != null && !Objects.equals(owner.getId(), ownerId)) {
			throw new IllegalArgumentException("Owner ID mismatch");
		}

		// Set the ID to ensure we're updating the correct entity
		owner.setId(ownerId);

		// Business logic: could add additional operations here
		// (e.g., audit logging, notifications, etc.)

		return ownerRepository.save(owner);
	}

	/**
	 * Private method for owner validation. PATTERN: Template Method Pattern (validation
	 * step) WHY: Centralizes validation logic, reusable across create/update operations
	 */
	private void validateOwner(Owner owner) {
		if (owner == null) {
			throw new IllegalArgumentException("Owner cannot be null");
		}

		// Additional business validations can be added here
		// For example: check for duplicate email, phone format, etc.
		// This keeps validation logic in the service layer, not scattered in controllers
	}

}
