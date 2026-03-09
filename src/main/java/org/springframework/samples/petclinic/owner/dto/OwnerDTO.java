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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

/**
 * Data Transfer Object for Owner.
 *
 * PATTERN: DTO (Data Transfer Object) Pattern WHY: - Separates API representation from
 * domain model - Controls what data is exposed to clients - Prevents over-fetching (e.g.,
 * we don't expose full pet visit history by default) - Allows versioning of API without
 * changing domain model - Can include computed/aggregated fields not in domain model
 *
 * BENEFITS: - If database schema changes, only mapper needs update, not API contract -
 * Can have different DTOs for different API versions - Prevents accidental exposure of
 * sensitive data - Optimizes network payload
 *
 * @author Refactored for Design Patterns
 */
public class OwnerDTO {

	private Integer id;

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	@NotBlank
	private String address;

	@NotBlank
	private String city;

	@NotBlank
	@Pattern(regexp = "\\d{10}", message = "Telephone must be exactly 10 digits")
	private String telephone;

	// Simplified pet list - only basic info, not full pet entities
	private List<PetSummaryDTO> pets;

	// Computed field - not in domain model
	private String fullName;

	// Computed field - business logic
	private Integer petCount;

	// Constructors
	public OwnerDTO() {
	}

	public OwnerDTO(Integer id, String firstName, String lastName, String address, String city, String telephone) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.telephone = telephone;
	}

	// Getters and Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public List<PetSummaryDTO> getPets() {
		return pets;
	}

	public void setPets(List<PetSummaryDTO> pets) {
		this.pets = pets;
		// Automatically compute petCount when pets are set
		this.petCount = (pets != null) ? pets.size() : 0;
	}

	public String getFullName() {
		if (fullName == null && firstName != null && lastName != null) {
			fullName = firstName + " " + lastName;
		}
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Integer getPetCount() {
		return petCount;
	}

	public void setPetCount(Integer petCount) {
		this.petCount = petCount;
	}

	/**
	 * Nested DTO for Pet summary. WHY: Prevents circular references and over-fetching
	 * Only includes essential pet information
	 */
	public static class PetSummaryDTO {

		private Integer id;

		private String name;

		private String type;

		private String birthDate;

		private Integer visitCount;

		public PetSummaryDTO() {
		}

		public PetSummaryDTO(Integer id, String name, String type, String birthDate) {
			this.id = id;
			this.name = name;
			this.type = type;
			this.birthDate = birthDate;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getBirthDate() {
			return birthDate;
		}

		public void setBirthDate(String birthDate) {
			this.birthDate = birthDate;
		}

		public Integer getVisitCount() {
			return visitCount;
		}

		public void setVisitCount(Integer visitCount) {
			this.visitCount = visitCount;
		}

	}

}
