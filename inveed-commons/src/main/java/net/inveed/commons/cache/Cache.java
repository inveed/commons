package net.inveed.commons.cache;

import java.util.Map;
import java.util.Set;

public class Cache<K, V> {
	private LRUStorage<K, V> cache = new LRUStorage<K, V>();
	private int maxSize = 0;
	
	public Cache() {
		CacheManager.INSTANCE.register(this);
	}

	public void put(K key, V obj) {
		this.cache.put(key, obj);
		this.freeSpace();
	}

	private void freeSpace() {
		this.cache.shrink(getMaxSize());
	}

	public long size() {
		return this.cache.size();
	}

	public void remove(K key) {
		this.cache.remove(key);
	}
	
	public V get(K oid) {
		V ret = this.cache.get(oid);
		if (ret != null) {
			this.cache.put(oid, ret);
		}
		
		return ret;
	}

	public void clear() {
		this.cache = new LRUStorage<>();
	}
	
	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		if (maxSize < 1) {
			throw new IllegalArgumentException("Size should be greater then zero");
		}
		this.maxSize = maxSize;
		this.freeSpace();
	}
	
	public boolean containsKey(K key) {
		return this.cache.containsKey(key);
	}
	
	public void putAll(Map<? extends K, ? extends V> m) {
		for (K key : m.keySet()) {
			this.put(key, m.get(key));
		}
	}
	
	public Set<K> keySet() {
		return this.cache.keySet();
	}
}

