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
package org.springframework.samples.petclinic.owner.dto;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Mapper for Owner entity to OwnerDTO.
 *
 * PATTERN: Mapper Pattern (also known as Converter Pattern) WHY: - Centralizes conversion
 * logic between entities and DTOs - Keeps entities and DTOs decoupled - Makes it easy to
 * maintain mapping logic - Allows for complex transformations
 *
 * BENEFITS: - Single Responsibility: Only handles entity <-> DTO conversion - Testable:
 * Easy to unit test mapping logic - Reusable: Can be used across different
 * services/controllers - Maintainable: All mapping logic in one place
 *
 * ALTERNATIVE: Could use libraries like MapStruct or ModelMapper, but this manual
 * approach provides more control and is more explicit.
 *
 * @author Refactored for Design Patterns
 */
@Component
public class OwnerMapper {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
	 * Convert Owner entity to OwnerDTO. PATTERN: Entity to DTO mapping
	 * @param owner the owner entity
	 * @return OwnerDTO with computed fields
	 */
	public OwnerDTO toDTO(Owner owner) {
		if (owner == null) {
			return null;
		}

		OwnerDTO dto = new OwnerDTO();
		dto.setId(owner.getId());
		dto.setFirstName(owner.getFirstName());
		dto.setLastName(owner.getLastName());
		dto.setAddress(owner.getAddress());
		dto.setCity(owner.getCity());
		dto.setTelephone(owner.getTelephone());

		// Computed field - full name
		dto.setFullName(owner.getFirstName() + " " + owner.getLastName());

		// Map pets to summary DTOs (prevents over-fetching)
		if (owner.getPets() != null) {
			dto.setPets(owner.getPets().stream().map(this::toPetSummaryDTO).collect(Collectors.toList()));
		}

		return dto;
	}

	/**
	 * Convert OwnerDTO to Owner entity. PATTERN: DTO to Entity mapping
	 * @param dto the owner DTO
	 * @return Owner entity
	 */
	public Owner toEntity(OwnerDTO dto) {
		if (dto == null) {
			return null;
		}

		Owner owner = new Owner();
		owner.setId(dto.getId());
		owner.setFirstName(dto.getFirstName());
		owner.setLastName(dto.getLastName());
		owner.setAddress(dto.getAddress());
		owner.setCity(dto.getCity());
		owner.setTelephone(dto.getTelephone());

		// Note: Pets are not mapped here to avoid complexity
		// Pet management should be done through dedicated endpoints

		return owner;
	}

	/**
	 * Update existing Owner entity with DTO data. PATTERN: Partial update mapping WHY:
	 * Prevents creating new entities when updating existing ones
	 * @param owner existing owner entity
	 * @param dto DTO with new data
	 */
	public void updateEntityFromDTO(Owner owner, OwnerDTO dto) {
		if (owner == null || dto == null) {
			return;
		}

		owner.setFirstName(dto.getFirstName());
		owner.setLastName(dto.getLastName());
		owner.setAddress(dto.getAddress());
		owner.setCity(dto.getCity());
		owner.setTelephone(dto.getTelephone());

		// Note: ID and Pets are not updated to maintain referential integrity
	}

	/**
	 * Convert Pet entity to PetSummaryDTO. PATTERN: Nested entity mapping WHY: Prevents
	 * circular references and reduces payload size
	 * @param pet the pet entity
	 * @return PetSummaryDTO with minimal information
	 */
	private OwnerDTO.PetSummaryDTO toPetSummaryDTO(Pet pet) {
		if (pet == null) {
			return null;
		}

		OwnerDTO.PetSummaryDTO dto = new OwnerDTO.PetSummaryDTO();
		dto.setId(pet.getId());
		dto.setName(pet.getName());

		if (pet.getType() != null) {
			dto.setType(pet.getType().getName());
		}

		if (pet.getBirthDate() != null) {
			dto.setBirthDate(pet.getBirthDate().format(DATE_FORMATTER));
		}

		// Computed field - visit count
		if (pet.getVisits() != null) {
			dto.setVisitCount(pet.getVisits().size());
		}

		return dto;
	}

}
