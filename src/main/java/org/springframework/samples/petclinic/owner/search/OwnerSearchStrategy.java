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
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.owner.Owner;

/**
 * Strategy interface for owner search.
 *
 * PATTERN: Strategy Pattern WHY: - Defines a family of search algorithms - Makes
 * algorithms interchangeable - Encapsulates search behavior - Allows adding new search
 * strategies without modifying existing code
 *
 * BENEFITS: - Open/Closed Principle: Open for extension, closed for modification - Single
 * Responsibility: Each strategy handles one search algorithm - Easy to test: Each
 * strategy can be tested independently - Flexible: Can switch strategies at runtime
 *
 * EXAMPLE STRATEGIES: - Exact match search - Partial match search (current
 * implementation) - Fuzzy search - Phonetic search (Soundex) - Full-text search
 *
 * @author Refactored for Design Patterns
 */
public interface OwnerSearchStrategy {

	/**
	 * Search for owners using this strategy.
	 * @param searchTerm the search term
	 * @param pageable pagination information
	 * @return page of matching owners
	 */
	Page<Owner> search(String searchTerm, Pageable pageable);

	/**
	 * Get the name of this search strategy. WHY: For logging, debugging, and UI selection
	 * @return strategy name
	 */
	String getStrategyName();

}
