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
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.PetTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

/**
 * Implementation of PetService.
 *
 * PATTERN: Service Layer Pattern + Facade Pattern WHY: - Service Layer: Encapsulates
 * complex business logic for pet management - Facade: Provides a simplified interface for
 * coordinating Pet and Owner operations
 *
 * This service demonstrates: - Transaction management - Business rule enforcement (name
 * uniqueness, date validation) - Coordination between multiple entities (Pet, Owner,
 * PetType)
 *
 * @author Refactored for Design Patterns
 */
@Service
@Transactional(readOnly = true)
public class PetServiceImpl implements PetService {

	private final OwnerRepository ownerRepository;

	private final PetTypeRepository petTypeRepository;

	public PetServiceImpl(OwnerRepository ownerRepository, PetTypeRepository petTypeRepository) {
		this.ownerRepository = ownerRepository;
		this.petTypeRepository = petTypeRepository;
	}

	@Override
	public Collection<PetType> findPetTypes() {
		return petTypeRepository.findPetTypes();
	}

	@Override
	@Transactional
	public Owner addPetToOwner(Integer ownerId, Pet pet) {
		// PATTERN: Facade Pattern
		// WHY: Simplifies the complex operation of:
		// 1. Finding owner
		// 2. Validating pet
		// 3. Adding pet to owner
		// 4. Saving changes

		// Step 1: Find owner
		Owner owner = ownerRepository.findById(ownerId)
			.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId));

		// Step 2: Validate pet
		validatePet(pet, owner);

		// Step 3: Check for duplicate name
		if (StringUtils.hasText(pet.getName()) && pet.isNew() && owner.getPet(pet.getName(), true) != null) {
			throw new IllegalArgumentException("Pet name '" + pet.getName() + "' already exists for this owner");
		}

		// Step 4: Add pet to owner
		owner.addPet(pet);

		// Step 5: Save owner (cascades to pet)
		return ownerRepository.save(owner);
	}

	@Override
	@Transactional
	public Owner updatePet(Integer ownerId, Integer petId, Pet pet) {
		// PATTERN: Template Method Pattern
		// WHY: Defines the skeleton of pet update operation:
		// validate -> check exists -> check name uniqueness -> update

		Assert.notNull(petId, "Pet ID must not be null");

		// Find owner
		Owner owner = ownerRepository.findById(ownerId)
			.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId));

		// Validate pet
		validatePet(pet, owner);

		// Check for name uniqueness
		String petName = pet.getName();
		if (StringUtils.hasText(petName)) {
			Pet existingPet = owner.getPet(petName, false);
			if (existingPet != null && !Objects.equals(existingPet.getId(), petId)) {
				throw new IllegalArgumentException("Pet name '" + petName + "' already exists for this owner");
			}
		}

		// Update pet details
		Pet existingPet = owner.getPet(petId);
		if (existingPet != null) {
			// PATTERN: Update existing pet properties
			existingPet.setName(pet.getName());
			existingPet.setBirthDate(pet.getBirthDate());
			existingPet.setType(pet.getType());
		}
		else {
			// If pet doesn't exist, add it
			pet.setId(petId);
			owner.addPet(pet);
		}

		return ownerRepository.save(owner);
	}

	@Override
	public void validatePet(Pet pet, Owner owner) {
		// PATTERN: Validation Logic Centralization
		// WHY: Keeps all pet validation rules in one place
		// Makes it easy to add new rules and maintain consistency

		if (pet == null) {
			throw new IllegalArgumentException("Pet cannot be null");
		}

		// Validate birth date is not in the future
		if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("Pet birth date cannot be in the future");
		}

		// Validate pet type is set
		if (pet.getType() == null) {
			throw new IllegalArgumentException("Pet type must be specified");
		}

		// Additional validations can be added here:
		// - Name format validation
		// - Age restrictions
		// - Breed specific rules
		// etc.
	}

}
