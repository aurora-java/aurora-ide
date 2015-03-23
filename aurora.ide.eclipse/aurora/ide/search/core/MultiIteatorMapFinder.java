package aurora.ide.search.core;

import java.util.ArrayList;
import java.util.List;

import aurora.ide.search.reference.IDataFilter;
import aurora.ide.search.reference.MapFinderResult;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;

public class MultiIteatorMapFinder extends CompositeMapIteator {

	private List<CompositeMapIteator> finders = new ArrayList<CompositeMapIteator>();

	private List<MapFinderResult> result = new ArrayList<MapFinderResult>();

	public int process(CompositeMap map) {
		for (CompositeMapIteator f : finders) {
			f.process(map);
		}
		return IterationHandle.IT_CONTINUE;
	}

	public List<MapFinderResult> getResult() {
		for (CompositeMapIteator f : finders) {
			result.addAll(f.getResult());
		}
		return result;
	}

	public MultiIteatorMapFinder() {

	}

	@Override
	public void setFilter(IDataFilter filter) {
		for (CompositeMapIteator f : finders) {
			f.setFilter(filter);
		}
	}

	public MultiIteatorMapFinder addFinder(CompositeMapIteator finder) {
		if (finder != null)
			finders.add(finder);
		return this;
	}

}
