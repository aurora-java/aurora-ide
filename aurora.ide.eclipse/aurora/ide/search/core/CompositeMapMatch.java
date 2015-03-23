package aurora.ide.search.core;

import uncertain.composite.CompositeMap;
import aurora.ide.search.ui.LineElement;

public class CompositeMapMatch extends AbstractMatch {

	private CompositeMap map;

	private LineElement l;

	public CompositeMap getMap() {
		return map;
	}

	public void setMap(CompositeMap map) {
		this.map = map;
	}

	public CompositeMapMatch(Object element, int offset, int length,
			CompositeMap map, LineElement l) {
		super(element, offset, length);
		this.map = map;
		this.l = l;
	}

	@Override
	public LineElement getLineElement() {

		return l;
	}

}
