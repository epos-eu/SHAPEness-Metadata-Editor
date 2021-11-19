/*******************************************************************************
 * <one line to give the program's name and a brief idea of what it does.>
 *     
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.epos.metadata.editor.engine.converters.beans;

import java.util.ArrayList;

public class Pair<L,R> {

	private final L left;
	private final R right;

	public Pair(L left, R right) {
		assert left != null;
		assert right != null;

		this.left = left;
		this.right = right;
	}
	
	public L getLeft() { return left; }
	public R getRight() { return right; }

	@Override
	public int hashCode() { return left.hashCode() ^ right.hashCode(); }

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair)) return false;
		Pair<?, ?> pairo = (Pair<?, ?>) o;
		return this.left.equals(pairo.getLeft()) &&
				this.right.equals(pairo.getRight());
	}

	@Override
	public String toString() {
		return "Pair [left=" + left + ", right=" + right + "]";
	}


}