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
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;

import java.util.Collection;

/**
 * Service interface for Pet operations.
 *
 * PATTERN: Service Layer Pattern WHY: Encapsulates business logic for pet management.
 * Benefits: - Centralizes pet-related business rules - Coordinates between Pet and Owner
 * operations - Provides transaction boundaries
 *
 * @author Refactored for Design Patterns
 */
public interface PetService {

	/**
	 * Get all available pet types.
	 * @return collection of pet types
	 */
	Collection<PetType> findPetTypes();

	/**
	 * Add a new pet to an owner. PATTERN: Facade Pattern - simplifies the complex
	 * operation of adding a pet
	 * @param ownerId the owner's ID
	 * @param pet the pet to add
	 * @return the updated owner with the new pet
	 * @throws IllegalArgumentException if owner not found or validation fails
	 */
	Owner addPetToOwner(Integer ownerId, Pet pet);

	/**
	 * Update an existing pet. PATTERN: Template Method Pattern
	 * @param ownerId the owner's ID
	 * @param petId the pet's ID
	 * @param pet the updated pet data
	 * @return the updated owner
	 * @throws IllegalArgumentException if validation fails
	 */
	Owner updatePet(Integer ownerId, Integer petId, Pet pet);

	/**
	 * Validate pet data according to business rules.
	 * @param pet the pet to validate
	 * @param owner the owner of the pet
	 * @throws IllegalArgumentException if validation fails
	 */
	void validatePet(Pet pet, Owner owner);

}
