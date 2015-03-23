/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class SimpleTreeIterator<T extends Object>  implements Iterable<T> {

	public SimpleTreeIterator() {
	}
	
	public abstract Iterator<T> iterator();
	
	public class TreeIterator implements Iterator<T> {
		int iteratorIndex = -1;
		int childIndex = 0;
		List<T> children;
		List<Iterator<T>> iterators;
		
		public TreeIterator(List<T> children) {
			this.children = children;
		}
		
		public boolean hasChildren() {
			return children.size()>0;
		}
		
		@Override
		public boolean hasNext() {
			if (!hasChildren())
				return false;
			
			if (iteratorIndex==-1) {
				iteratorIndex = 0;
				iterators = new ArrayList<Iterator<T>>();
				for (T child : children) {
					iterators.add(((Iterable)child).iterator());
				}
			}
			return iteratorIndex<iterators.size() || childIndex<children.size();
		}

		@Override
		public T next() {
			if (!hasChildren())
				return null;
			while (iteratorIndex<iterators.size()) {
				Iterator<T> iter = iterators.get(iteratorIndex);
				if (iter.hasNext())
					return iter.next();
				++iteratorIndex;
			}
			if (childIndex<children.size()) {
				T child = children.get(childIndex);
				++childIndex;
				return child;
			}
			return null;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
