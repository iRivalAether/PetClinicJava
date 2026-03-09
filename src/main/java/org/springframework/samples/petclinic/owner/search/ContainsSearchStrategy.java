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
package org.springframework.samples.petclinic.owner.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Case-insensitive contains search strategy.
 *
 * PATTERN: Strategy Pattern Implementation WHY: Alternative search strategy that finds
 * owners whose last name contains the search term anywhere (not just at the start).
 *
 * EXAMPLE: Search "son" would find: "Johnson", "Anderson", "Peterson"
 *
 * @author Refactored for Design Patterns
 */
@Component("containsSearchStrategy")
public class ContainsSearchStrategy implements OwnerSearchStrategy {

	private final OwnerRepository ownerRepository;

	public ContainsSearchStrategy(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}

	@Override
	public Page<Owner> search(String searchTerm, Pageable pageable) {
		// Normalize search term
		String normalizedTerm = (searchTerm == null) ? "" : searchTerm.trim().toLowerCase();

		// Get all owners (in real app, this should be paginated at DB level)
		List<Owner> allOwners = ownerRepository.findByLastNameStartingWith("", Pageable.unpaged()).getContent();

		// Filter owners whose last name contains the search term
		List<Owner> matchingOwners = allOwners.stream()
			.filter(owner -> owner.getLastName().toLowerCase().contains(normalizedTerm))
			.skip(pageable.getOffset())
			.limit(pageable.getPageSize())
			.collect(Collectors.toList());

		// Count total matching owners for pagination
		long total = allOwners.stream()
			.filter(owner -> owner.getLastName().toLowerCase().contains(normalizedTerm))
			.count();

		return new PageImpl<>(matchingOwners, pageable, total);
	}

	@Override
	public String getStrategyName() {
		return "Contains Match (Case-Insensitive)";
	}

}
