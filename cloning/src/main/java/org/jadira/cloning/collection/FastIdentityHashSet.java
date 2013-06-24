package org.jadira.cloning.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * A wrapper for IdentityHashMap that resolves object matches quickly for
 * small sets using binary search
 * @param <E>
 */
public class FastIdentityHashSet<E> implements Set<E> {

	private int[] entryKeys = new int[0];
	private static final int ARRAY_SIZE = 12;
	
	private IdentityHashMap<E, Boolean> hashMap = new IdentityHashMap<E, Boolean>(12);
	
	@Override
	public int size() {
		if (entryKeys != null) {
			return entryKeys.length;
		} else {
			return hashMap.size();
		}
	}
	
	@Override
	public boolean isEmpty() {
		if (entryKeys != null) {
			return entryKeys.length == 0;
		} else {
			return hashMap.isEmpty();
		}		
	}
	
	@Override
	public boolean contains(Object key) {
		if (entryKeys != null) {
			return Arrays.binarySearch(entryKeys, System.identityHashCode(key)) >= 0;
		} else {
			return hashMap.containsKey(System.identityHashCode(key));
		}
	}
	
	@Override
	public Iterator<E> iterator() {
		return hashMap.keySet().iterator();
	}
	
	@Override
	public Object[] toArray() {
		return hashMap.keySet().toArray();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		return (T[]) hashMap.keySet().toArray();
	}
	
	@Override
	public boolean add(E e) {
		boolean res = hashMap.put(e, Boolean.TRUE);
		if (res) {
			if (entryKeys == null || (entryKeys.length + 1 <= ARRAY_SIZE)) {
				Object[] keys = hashMap.keySet().toArray(new Object[]{});
				entryKeys = new int[keys.length];
				for (int i = 0; i < keys.length; i++) {
					entryKeys[i] = System.identityHashCode(keys[i]);
				}
				Arrays.sort(entryKeys);
			} else {
				entryKeys = null;
			}
		}
		return res;
	}
	
	@Override
	public boolean remove(Object o) {
		boolean res = hashMap.remove(o);
		if (res) {
			if (hashMap.size() <= ARRAY_SIZE) {
				Object[] keys = hashMap.keySet().toArray(new Object[]{});
		
				entryKeys = new int[keys.length];
				for (int i = 0; i < keys.length; i++) {
					entryKeys[i] = System.identityHashCode(keys[i]);
				}
				Arrays.sort(entryKeys);
			} else {
				entryKeys = null;
			}
		}
		return res;	
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object next : c) {
			if(!contains(next)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		
		boolean res = false;
		for (E next : c) {
			boolean nextRes = add(next);
			if (nextRes) { res = nextRes; }
		}
		return res;
	}
	
	@Override
	public void clear() {
		hashMap.clear();
		entryKeys = new int[]{};
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		
		Set<E> keySet = hashMap.keySet();
		boolean res = keySet.retainAll(c);
		
		hashMap.clear();
		
		for (E next : keySet) {
			hashMap.put(next, Boolean.TRUE);
		}
		
		if (res) {
			Object[] keys = hashMap.keySet().toArray(new Object[]{});
			entryKeys = new int[keys.length];
			for (int i = 0; i < keys.length; i++) {
				entryKeys[i] = System.identityHashCode(keys[i]);
			}
			Arrays.sort(entryKeys);
		}
		return res;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean res = false;
		for (Object next : c) {
			boolean nextRes = remove(next);
			if (nextRes) { res = nextRes; }
		}
		return res;
	}
}
