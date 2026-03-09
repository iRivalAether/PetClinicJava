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

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Implementation of VisitService.
 *
 * PATTERN: Service Layer Pattern + Facade Pattern WHY: - Service Layer: Encapsulates
 * business logic for visit management - Facade: Provides simplified interface for complex
 * operations involving multiple entities (Owner, Pet, Visit)
 *
 * This service demonstrates: - Complex entity relationship management - Transaction
 * coordination - Business rule enforcement
 *
 * @author Refactored for Design Patterns
 */
@Service
@Transactional(readOnly = true)
public class VisitServiceImpl implements VisitService {

	private final OwnerRepository ownerRepository;

	/**
	 * Constructor injection. PATTERN: Dependency Injection WHY: Promotes loose coupling
	 * and testability
	 */
	public VisitServiceImpl(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}

	@Override
	@Transactional
	public Owner addVisit(Integer ownerId, Integer petId, Visit visit) {
		// PATTERN: Facade Pattern
		// WHY: Hides the complexity of:
		// 1. Finding owner
		// 2. Finding pet within owner
		// 3. Validating visit
		// 4. Adding visit to pet
		// 5. Saving changes
		// All of this is hidden behind a simple method call

		// Step 1: Find owner
		Owner owner = ownerRepository.findById(ownerId)
			.orElseThrow(() -> new IllegalArgumentException(
					"Owner not found with id: " + ownerId + ". Please ensure the ID is correct"));

		// Step 2: Find pet
		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new IllegalArgumentException("Pet with id " + petId + " not found for owner with id " + ownerId);
		}

		// Step 3: Validate visit
		validateVisit(visit);

		// Step 4: Add visit to owner (which internally adds to pet)
		owner.addVisit(petId, visit);

		// Step 5: Save owner (cascades to pet and visit)
		return ownerRepository.save(owner);
	}

	@Override
	public void validateVisit(Visit visit) {
		// PATTERN: Validation Logic Centralization
		// WHY: Keeps all visit validation rules in one place
		// Ensures consistency across the application

		if (visit == null) {
			throw new IllegalArgumentException("Visit cannot be null");
		}

		// Validate description is not empty
		if (!StringUtils.hasText(visit.getDescription())) {
			throw new IllegalArgumentException("Visit description is required");
		}

		// Validate date is set
		if (visit.getDate() == null) {
			throw new IllegalArgumentException("Visit date is required");
		}

		// Additional validations can be added here:
		// - Visit date not too far in the future
		// - Description length limits
		// - Check for duplicate visits on same day
		// etc.
	}

}
