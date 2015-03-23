package aurora.ide.search.condition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;

import uncertain.composite.CompositeMap;
import aurora.ide.search.ui.LineElement;

public class AuroraSearchPattern {
	private SearchForCondition searchFor;

	private List<SearchCondition> conditions = new ArrayList<SearchCondition>();

	public AuroraSearchPattern(SearchForCondition searchFor) {
		super();
		this.searchFor = searchFor;
	}

	public SearchForCondition getSearchFor() {
		return searchFor;
	}

	public void setSearchFor(SearchForCondition searchFor) {
		this.searchFor = searchFor;
	}

	public List<SearchCondition> getConditions() {
		return conditions;
	}

	public void addCondition(SearchCondition condition) {
		this.conditions.add(condition);
	}

	public MatchInfo match(CompositeMap map, IDocument document, LineElement l) {
		if (conditions == null)
			return MatchInfo.Not_Match;
		MatchInfo info = new MatchInfo();
		info.setMap(map);
		for (SearchCondition condition : conditions) {
			MatchInfo match = condition.match(map, document, l);
			if (match.isMatch()) {
				info.addAllRegion(match.getRegions());
			} else {
				return MatchInfo.Not_Match;
			}
		}
		return info;
	}
}
