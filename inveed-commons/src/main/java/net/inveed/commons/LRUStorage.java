package net.inveed.commons;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public final class LRUStorage<K, T> {
	private final class LRUItem {
		public T item;
		public final K key;
		public LRUItem previous;
		public LRUItem next;
		LRUItem(K key, T item) {
			this.key = key;
			this.item = item;
		}
	}
	
	// Least used
	private LRUItem last;
	
	// First (Long-time ago) used. This item will be deleted first when required.
	private LRUItem first;
	
	private final HashMap<K, LRUItem> map = new HashMap<K, LRUItem>();
	ReentrantLock lock = new ReentrantLock();

	
	public T get(K streamId) {
		LRUItem ret = map.get(streamId);
		if (ret != null) {
			return ret.item;
		}
		return null;
	}
	
	/**
	 * Removes tired items to fit required size
	 * @param maxSize
	 */
	public void shrink(int maxSize) {
		if (this.size() < maxSize) {
			return;
		}
		if (maxSize < 1) {
			this.clear();
			return;
		}
		this.lock.lock();
		try {
			LRUItem lastDeleted = null;
			while (this.map.size() > maxSize && this.first != null) {
				lastDeleted = this.first;
				this.map.remove(lastDeleted.key);
				removeFromListUnsafe(lastDeleted);
			}
		} finally {
			this.lock.unlock();
		}
	}
	
	public int size() {
		return this.map.size();
	}

	public void put(K key, T value) {
		LRUItem lruItem = map.get(key);
		if (lruItem != null) {
			lruItem.item = value;
			if (lruItem != this.last) {
				this.lock.lock();
				try {
					this.moveBackUnsafe(lruItem);
				} finally {
					this.lock.unlock();
				}
			}
			
			return;
		}
		
		// Not found in map (without lock).
		this.lock.lock();
		try {
			//Trying again (with lock)
			lruItem = map.get(key);
			if (lruItem != null) {
				lruItem.item = value;
				this.moveBackUnsafe(lruItem);
				return;
			}
			
			lruItem = new LRUItem(key, value) ;
			lruItem.previous = this.last;
			lruItem.next = null;

			if (this.last != null) {
				this.last.next = lruItem;
			} else {
				if (this.first != null) {
					//TODO: LOG, invalid state!
				}
				this.first = lruItem;
			}
			
			this.last = lruItem;

			map.put(key, lruItem);
		} finally {
			this.lock.unlock();
		}
	}
	
	private void removeFromListUnsafe(LRUItem item) {
		if (item == null ) {
			return;
		}
		if (item == this.first) {
			this.first = item.next;
		}
		if (item == this.last) {
			this.last = item.previous;
		}
		if (item.previous != null) {
			item.previous.next = item.next;
		}
		if (item.next != null) {
			item.next.previous = item.previous;
		}
		item.next = null;
		item.previous = null;
	}
	
	private void moveBackUnsafe(LRUItem i) {
		if (i == null) {
			throw new NullPointerException("item is null");
		}
		if (i == this.last) {
			return;
		}
		this.removeFromListUnsafe(i);
		this.last.next = i;
		i.previous = this.last;
		i.next = null;
		this.last = i;
		return;
	}
	
	public void remove(K key) {
		this.lock.lock();
		try {
			if (key == null) {
				return;
			}
			LRUItem i = this.map.get(key);
			if (i == null) {
				return;
			}
			this.removeFromListUnsafe(i);
			this.map.remove(key);
		} finally {
			this.lock.unlock();
		}
	}
	
	public T first() {
		if (this.first != null) 
			return first.item;
		return null;
	}
	
	public T last() {
		if (this.last != null) 
			return this.last.item;
		
		return null;
	}

	public boolean isEmpty() {
		return this.map.isEmpty();
	}
	
	public void clear() {
		this.lock.lock();
		try {
			this.map.clear();
			this.first = null;
			this.last = null;
		} finally {
			this.lock.unlock();
		}
	}

	public boolean containsKey(K key) {
		return this.map.containsKey(key);
	}

	public void putAll(Map<? extends K, ? extends T> m) {
		for (K key : m.keySet()) {
			this.put(key, m.get(key));
		}
	}
	
	public Set<K> keySet() {
		return this.map.keySet();
	}
}

