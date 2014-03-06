package org.jadira.reflection.core.identity;

import java.util.Objects;

public final class Tuple<L, R> {

	// TODO Add toString and Comparable

	public final L left;
	public final R right;

	public static <L, R> Tuple<L, R> of(L left, R right) {
		return new Tuple<L, R>(left, right);
	}

	public Tuple(L left, R right) {
		this.left = left;
		this.right = right;
	}

	public L getLeft() {
		return left;
	}

	public R getRight() {
		return right;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Tuple<?, ?>) {
			Tuple<?, ?> other = (Tuple<?, ?>) obj;
			return Objects.equals(getLeft(), other.getLeft())
					&& Objects.equals(getRight(), other.getRight());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (getLeft() == null ? 0 : getLeft().hashCode())
				^ (getRight() == null ? 0 : getRight().hashCode());
	}
}