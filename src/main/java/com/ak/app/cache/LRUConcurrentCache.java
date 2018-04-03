package com.ak.app.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * A simple implementation of LRU cache using LinkedHashMap.
 * The cache evicts the Eldest Entry once it reaches its capacity.
 * The cache is populated during the application startup along-with 
 * the index creation and is updated every-time when a getLine 
 * request is made.
 */
public class LRUConcurrentCache {

	private final Map<Integer, String> cache;

	public LRUConcurrentCache(final int capacity) {
		/*
		 * The 3 parameters are : int initialCapacity,float loadFactor,boolean
		 * accessOrder. AccessOrder specifies the ordering mode - true for access-order,
		 * false for insertion-order
		 */
		this.cache = new LinkedHashMap<Integer, String>(capacity, 0.75F, true) {
			@Override
			protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
				return size() > capacity;
			}
		};
	}

	public void put(Integer key, String value) {
		synchronized (cache) {
			cache.put(key, value);
		}
	}

	public String get(Integer key) {
		synchronized (cache) {
			return cache.get(key);
		}
	}
}
