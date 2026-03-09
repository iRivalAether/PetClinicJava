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
package org.springframework.samples.petclinic.owner.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.dto.OwnerDTO;
import org.springframework.samples.petclinic.owner.dto.OwnerMapper;
import org.springframework.samples.petclinic.owner.service.OwnerService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API Controller for Owner operations.
 *
 * PATTERN DEMONSTRATION: DTO Pattern + Service Layer Pattern WHY THIS IS BETTER: - Uses
 * DTOs instead of exposing entities directly - Service layer handles business logic -
 * Controller only handles HTTP concerns - Clean API contract independent of database
 * schema
 *
 * BENEFITS: - API versioning: Can create OwnerDTOv2 without changing entities - Security:
 * Controls what data is exposed - Performance: DTOs can be optimized for network transfer
 * - Flexibility: DTOs can aggregate data from multiple entities
 *
 * @author Refactored for Design Patterns
 */
@RestController
@RequestMapping("/api/owners")
public class OwnerRestController {

	private final OwnerService ownerService;

	private final OwnerMapper ownerMapper;

	/**
	 * PATTERN: Constructor Dependency Injection WHY: Immutable dependencies, easier
	 * testing, clear requirements
	 */
	public OwnerRestController(OwnerService ownerService, OwnerMapper ownerMapper) {
		this.ownerService = ownerService;
		this.ownerMapper = ownerMapper;
	}

	/**
	 * Get all owners with pagination. PATTERN: DTO Pattern - Returns DTOs, not entities
	 * @param page page number (default 0)
	 * @param size page size (default 10)
	 * @return Page of OwnerDTOs
	 */
	@GetMapping
	public ResponseEntity<Page<OwnerDTO>> getAllOwners(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<Owner> owners = ownerService.findByLastName("", pageable);

		// PATTERN: Mapper Pattern - Converting entities to DTOs
		// WHY: API clients get optimized, versioned representation
		Page<OwnerDTO> ownerDTOs = owners.map(ownerMapper::toDTO);

		return ResponseEntity.ok(ownerDTOs);
	}

	/**
	 * Search owners by last name.
	 * @param lastName last name to search for
	 * @param page page number
	 * @param size page size
	 * @return List of matching OwnerDTOs
	 */
	@GetMapping("/search")
	public ResponseEntity<List<OwnerDTO>> searchOwners(@RequestParam String lastName,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<Owner> owners = ownerService.findByLastName(lastName, pageable);

		// Converting to DTOs
		List<OwnerDTO> ownerDTOs = owners.getContent().stream().map(ownerMapper::toDTO).collect(Collectors.toList());

		return ResponseEntity.ok(ownerDTOs);
	}

	/**
	 * Get owner by ID.
	 * @param id owner ID
	 * @return OwnerDTO if found
	 */
	@GetMapping("/{id}")
	public ResponseEntity<OwnerDTO> getOwner(@PathVariable Integer id) {
		return ownerService.findById(id)
			.map(ownerMapper::toDTO) // Entity to DTO mapping
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Create a new owner. PATTERN: DTO Pattern - Accepts DTO, converts to entity, returns
	 * DTO WHY: Client sends DTO, backend works with entities, client receives DTO
	 * @param ownerDTO the owner data
	 * @return created OwnerDTO
	 */
	@PostMapping
	public ResponseEntity<OwnerDTO> createOwner(@Valid @RequestBody OwnerDTO ownerDTO) {
		// DTO to Entity mapping
		Owner owner = ownerMapper.toEntity(ownerDTO);

		// Business logic in service layer
		Owner createdOwner = ownerService.createOwner(owner);

		// Entity to DTO mapping
		OwnerDTO responseDTO = ownerMapper.toDTO(createdOwner);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
	}

	/**
	 * Update an existing owner. PATTERN: DTO Pattern for updates
	 * @param id owner ID
	 * @param ownerDTO updated owner data
	 * @return updated OwnerDTO
	 */
	@PutMapping("/{id}")
	public ResponseEntity<OwnerDTO> updateOwner(@PathVariable Integer id, @Valid @RequestBody OwnerDTO ownerDTO) {

		// Set ID from path parameter (prevents ID tampering)
		ownerDTO.setId(id);

		// DTO to Entity mapping
		Owner owner = ownerMapper.toEntity(ownerDTO);

		// Business logic in service layer
		Owner updatedOwner = ownerService.updateOwner(id, owner);

		// Entity to DTO mapping
		OwnerDTO responseDTO = ownerMapper.toDTO(updatedOwner);

		return ResponseEntity.ok(responseDTO);
	}

	/**
	 * Exception handler for IllegalArgumentException. PATTERN: Centralized exception
	 * handling
	 * @param ex the exception
	 * @return error response
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
		ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", ex.getMessage());
		return ResponseEntity.badRequest().body(error);
	}

	/**
	 * Simple error response DTO. PATTERN: DTO for error responses WHY: Consistent error
	 * format across API
	 */
	public static class ErrorResponse {

		private String code;

		private String message;

		public ErrorResponse(String code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}

	}

}
