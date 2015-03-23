package aurora.ide.search.core;

import org.eclipse.search.ui.text.Match;

import aurora.ide.search.ui.LineElement;

public abstract class AbstractMatch extends Match {

	public AbstractMatch(Object element, int offset, int length) {
		super(element, offset, length);

	}

	abstract public LineElement getLineElement();

	public int getOriginalOffset() {
		return this.getOffset();
	}

	public int getOriginalLength() {
		return this.getLength();
	}

}
