package aurora.ide.search.reference;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.search.core.CompositeMapIteator;

public class NamedMapFinder extends CompositeMapIteator {

	private String mapName;

	public int process(CompositeMap map) {
		try {
			CompositeMap match = getMatch(map);
			if (match != null) {
				this.getResult().add(new MapFinderResult(match, null));
			}
		} catch (ApplicationException e) {
		}
		return IterationHandle.IT_CONTINUE;
	}

	protected CompositeMap getMatch(CompositeMap map)
			throws ApplicationException {
		if (mapName.equalsIgnoreCase(map.getName())) {
			return map;
		}

		return null;
	}

	public NamedMapFinder(String mapName) {
		this.mapName = mapName;
	}

}
