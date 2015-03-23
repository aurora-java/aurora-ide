package aurora.ide.search.core;

import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.search.condition.SearchForCondition;
import aurora.ide.search.reference.MapFinderResult;

public class SearchForMapIteator extends CompositeMapIteator {

	private SearchForCondition searchFor;

	public int process(CompositeMap map) {
		try {
			List matchs = getMatch(map);
			if (matchs != null) {
				for (Object match : matchs) {
					if (!this.getResult().contains(match)) {
						this.getResult()
								.add(
										new MapFinderResult(
												(CompositeMap) match, null));
					}
				}
			}
		} catch (ApplicationException e) {
		}
		return IterationHandle.IT_CONTINUE;
	}

	protected List getMatch(CompositeMap map) throws ApplicationException {
		List<CompositeMap> maps = searchFor.getSearchFor(map);
		return maps;
	}

	public SearchForMapIteator(SearchForCondition searchFor) {
		this.searchFor = searchFor;
	}

}
