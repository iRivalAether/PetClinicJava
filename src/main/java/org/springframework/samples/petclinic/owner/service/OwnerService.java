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

import java.util.Optional;

/**
 * Service interface for Owner operations.
 *
 * PATTERN: Service Layer Pattern WHY: Encapsulates business logic and provides a clear
 * contract for owner operations. This interface allows for: - Easy mocking in tests -
 * Multiple implementations if needed - Clear separation of concerns
 *
 * @author Refactored for Design Patterns
 */
public interface OwnerService {

	/**
	 * Save or update an owner in the database.
	 * @param owner the owner to save
	 * @return the saved owner
	 */
	Owner save(Owner owner);

	/**
	 * Find an owner by ID.
	 * @param id the owner ID
	 * @return Optional containing the owner if found
	 */
	Optional<Owner> findById(Integer id);

	/**
	 * Find owners by last name with pagination. PATTERN: Strategy Pattern (internally can
	 * use different search strategies)
	 * @param lastName the last name to search for
	 * @param pageable the pagination information
	 * @return Page of owners matching the criteria
	 */
	Page<Owner> findByLastName(String lastName, Pageable pageable);

	/**
	 * Validate and create a new owner. PATTERN: Template Method Pattern (validation +
	 * save)
	 * @param owner the owner to create
	 * @return the created owner
	 * @throws IllegalArgumentException if validation fails
	 */
	Owner createOwner(Owner owner);

	/**
	 * Validate and update an existing owner. PATTERN: Template Method Pattern (validation
	 * + update)
	 * @param ownerId the ID of the owner to update
	 * @param owner the updated owner data
	 * @return the updated owner
	 * @throws IllegalArgumentException if owner not found or validation fails
	 */
	Owner updateOwner(Integer ownerId, Owner owner);

}
