package net.inveed.commons;

import java.util.ArrayList;

public class CacheManager {
	public static final CacheManager INSTANCE = new CacheManager();
	private ArrayList<Cache<?, ?>> caches = new ArrayList<>();
	
	private CacheManager() {}
	void register(Cache<?, ?> cache) {
		this.caches.add(cache);
	}
	
	/**
	 * Clear all caches
	 */
	public void flush() {
		for (Cache<?, ?> c : this.caches) {
			c.clear();
		}
	}
}
