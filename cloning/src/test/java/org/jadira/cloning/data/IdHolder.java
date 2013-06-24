package org.jadira.cloning.data;


public class IdHolder {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof IdHolder) {
			if (this.id == null && ((IdHolder) that).id == null) {
				return true;
			} else {
				return this.id.equals(((IdHolder) that).id);
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
		return hash;
	}
}
