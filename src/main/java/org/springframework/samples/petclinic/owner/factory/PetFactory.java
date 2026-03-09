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

import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.PetTypeRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Factory for creating Pet instances.
 *
 * PATTERN: Factory Pattern + Abstract Factory Pattern WHY: - Encapsulates pet creation
 * logic - Handles PetType lookup - Provides type-specific factory methods (dog, cat,
 * etc.) - Ensures pets are created with valid types
 *
 * DEMONSTRATES: - Factory Pattern: createPet methods - Abstract Factory Pattern:
 * createDog, createCat methods (family of related objects)
 *
 * @author Refactored for Design Patterns
 */
@Component
public class PetFactory {

	private final PetTypeRepository petTypeRepository;

	public PetFactory(PetTypeRepository petTypeRepository) {
		this.petTypeRepository = petTypeRepository;
	}

	/**
	 * Create a pet with specific details. PATTERN: Factory Method
	 * @param name pet name
	 * @param birthDate pet birth date
	 * @param petType pet type
	 * @return new Pet instance
	 */
	public Pet createPet(String name, LocalDate birthDate, PetType petType) {
		Pet pet = new Pet();
		pet.setName(name);
		pet.setBirthDate(birthDate);
		pet.setType(petType);
		return pet;
	}

	/**
	 * Create a pet with type name lookup. PATTERN: Factory Method with dependency
	 * resolution WHY: Client doesn't need to know how to lookup PetType
	 * @param name pet name
	 * @param birthDate pet birth date
	 * @param typeName type name (e.g., "dog", "cat")
	 * @return new Pet instance
	 */
	public Pet createPet(String name, LocalDate birthDate, String typeName) {
		PetType petType = findPetTypeByName(typeName);
		return createPet(name, birthDate, petType);
	}

	/**
	 * Create a dog. PATTERN: Abstract Factory - specific product creation WHY:
	 * Convenience method for common pet type
	 * @param name dog name
	 * @param birthDate dog birth date
	 * @return new Dog Pet instance
	 */
	public Pet createDog(String name, LocalDate birthDate) {
		return createPet(name, birthDate, "dog");
	}

	/**
	 * Create a cat. PATTERN: Abstract Factory - specific product creation
	 * @param name cat name
	 * @param birthDate cat birth date
	 * @return new Cat Pet instance
	 */
	public Pet createCat(String name, LocalDate birthDate) {
		return createPet(name, birthDate, "cat");
	}

	/**
	 * Create a bird. PATTERN: Abstract Factory - specific product creation
	 * @param name bird name
	 * @param birthDate bird birth date
	 * @return new Bird Pet instance
	 */
	public Pet createBird(String name, LocalDate birthDate) {
		return createPet(name, birthDate, "bird");
	}

	/**
	 * Create a default test pet. PATTERN: Factory Method for testing
	 * @return Pet with default test data
	 */
	public Pet createDefaultPet() {
		PetType dogType = findPetTypeByName("dog");
		return createPet("Max", LocalDate.now().minusYears(2), dogType);
	}

	/**
	 * Create a pet using builder pattern. PATTERN: Builder Pattern
	 * @return PetBuilder instance
	 */
	public PetBuilder builder() {
		return new PetBuilder(petTypeRepository);
	}

	/**
	 * Helper method to find pet type by name. WHY: Encapsulates lookup logic
	 */
	private PetType findPetTypeByName(String typeName) {
		return petTypeRepository.findPetTypes()
			.stream()
			.filter(type -> type.getName().equalsIgnoreCase(typeName))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Unknown pet type: " + typeName));
	}

	/**
	 * Builder class for Pet.
	 *
	 * PATTERN: Builder Pattern WHY: Fluent API for constructing pets with validation
	 *
	 * EXAMPLE: Pet pet = petFactory.builder() .withName("Buddy")
	 * .withBirthDate(LocalDate.of(2020, 5, 15)) .withType("dog") .build();
	 */
	public static class PetBuilder {

		private final Pet pet;

		private final PetTypeRepository petTypeRepository;

		public PetBuilder(PetTypeRepository petTypeRepository) {
			this.pet = new Pet();
			this.petTypeRepository = petTypeRepository;
		}

		public PetBuilder withName(String name) {
			pet.setName(name);
			return this;
		}

		public PetBuilder withBirthDate(LocalDate birthDate) {
			pet.setBirthDate(birthDate);
			return this;
		}

		public PetBuilder withType(PetType petType) {
			pet.setType(petType);
			return this;
		}

		public PetBuilder withType(String typeName) {
			PetType petType = petTypeRepository.findPetTypes()
				.stream()
				.filter(type -> type.getName().equalsIgnoreCase(typeName))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unknown pet type: " + typeName));
			return withType(petType);
		}

		public Pet build() {
			validateRequiredFields();
			return pet;
		}

		private void validateRequiredFields() {
			if (pet.getName() == null || pet.getName().isBlank()) {
				throw new IllegalStateException("Pet name is required");
			}
			if (pet.getType() == null) {
				throw new IllegalStateException("Pet type is required");
			}
			if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(LocalDate.now())) {
				throw new IllegalStateException("Pet birth date cannot be in the future");
			}
		}

	}

}
