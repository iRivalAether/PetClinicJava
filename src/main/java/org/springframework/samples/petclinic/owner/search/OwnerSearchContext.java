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
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Context class for owner search using Strategy Pattern.
 *
 * PATTERN: Strategy Pattern (Context) WHY: - Manages the current search strategy - Allows
 * switching strategies at runtime - Provides a unified interface for searching -
 * Decouples clients from specific search implementations
 *
 * USAGE EXAMPLE: searchContext.setStrategy("contains"); Page<Owner> results =
 * searchContext.search("son", pageable);
 *
 * BENEFITS: - Clients don't need to know about specific strategies - Easy to add new
 * strategies without changing client code - Can switch strategies based on user
 * preference or system configuration
 *
 * @author Refactored for Design Patterns
 */
@Component
public class OwnerSearchContext {

	private final Map<String, OwnerSearchStrategy> strategies;

	private OwnerSearchStrategy currentStrategy;

	/**
	 * Constructor injection of all available strategies. PATTERN: Dependency Injection +
	 * Strategy Pattern WHY: Spring will automatically inject all beans implementing
	 * OwnerSearchStrategy
	 * @param strategies map of strategy beans (key = bean name, value = strategy)
	 */
	public OwnerSearchContext(Map<String, OwnerSearchStrategy> strategies) {
		this.strategies = strategies;
		// Set default strategy
		this.currentStrategy = strategies.get("partialMatchStrategy");
	}

	/**
	 * Set the search strategy by name. PATTERN: Strategy selection
	 * @param strategyName bean name of the strategy
	 * @throws IllegalArgumentException if strategy not found
	 */
	public void setStrategy(String strategyName) {
		OwnerSearchStrategy strategy = strategies.get(strategyName);
		if (strategy == null) {
			throw new IllegalArgumentException(
					"Unknown search strategy: " + strategyName + ". Available strategies: " + strategies.keySet());
		}
		this.currentStrategy = strategy;
	}

	/**
	 * Set the search strategy directly. PATTERN: Strategy injection
	 * @param strategy the strategy to use
	 */
	public void setStrategy(OwnerSearchStrategy strategy) {
		this.currentStrategy = strategy;
	}

	/**
	 * Perform search using current strategy. PATTERN: Strategy execution WHY: Delegates
	 * to current strategy, client doesn't know which one
	 * @param searchTerm search term
	 * @param pageable pagination information
	 * @return page of matching owners
	 */
	public Page<Owner> search(String searchTerm, Pageable pageable) {
		return currentStrategy.search(searchTerm, pageable);
	}

	/**
	 * Get the name of the current strategy.
	 * @return current strategy name
	 */
	public String getCurrentStrategyName() {
		return currentStrategy.getStrategyName();
	}

	/**
	 * Get all available strategy names. WHY: Useful for UI to display available search
	 * options
	 * @return map of strategy names
	 */
	public Map<String, OwnerSearchStrategy> getAvailableStrategies() {
		return strategies;
	}

}
