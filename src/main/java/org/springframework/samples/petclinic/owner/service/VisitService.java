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
import org.springframework.samples.petclinic.owner.Visit;

/**
 * Service interface for Visit operations.
 *
 * PATTERN: Service Layer Pattern WHY: Encapsulates business logic for visit management.
 * Benefits: - Centralizes visit-related business rules - Coordinates between Visit, Pet,
 * and Owner operations - Manages complex relationships
 *
 * @author Refactored for Design Patterns
 */
public interface VisitService {

	/**
	 * Add a new visit for a pet. PATTERN: Facade Pattern WHY: Simplifies the complex
	 * operation of finding owner, pet, and adding visit
	 * @param ownerId the owner's ID
	 * @param petId the pet's ID
	 * @param visit the visit to add
	 * @return the updated owner with the new visit
	 * @throws IllegalArgumentException if owner or pet not found, or validation fails
	 */
	Owner addVisit(Integer ownerId, Integer petId, Visit visit);

	/**
	 * Validate visit data according to business rules.
	 * @param visit the visit to validate
	 * @throws IllegalArgumentException if validation fails
	 */
	void validateVisit(Visit visit);

}
