package aurora.ide.search.condition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;

public class SearchForCondition extends SearchCondition {

	public static final String Parent = "parent";
	public static final String Child = "child";
	public static final String Element = "element";

	private String searchForType;

	public SearchForCondition(String searchForType) {
		super();
		this.searchForType = searchForType;
	}

	public String getSearchForType() {
		return searchForType;
	}

	public void setSearchForType(String searchForType) {
		this.searchForType = searchForType;
	}

	@Override
	public SearchCondition read(IDialogSettings s) {
		return super.read(s);
	}

	public MatchInfo match(CompositeMap map, Attribute attrib) {
		return MatchInfo.Not_Match;
	}

	public List<CompositeMap> getSearchFor(CompositeMap map) {
		List<CompositeMap> result = new ArrayList<CompositeMap>();
		if (Parent.equals(this.getSearchForType())) {
			CompositeMap parent = map.getParent();
			if (parent != null)
				result.add(parent);
		}

		if (Child.equals(this.getSearchForType())) {
			return map.getChilds();
		}
		if (Element.equals(this.getSearchForType())) {
			if (map != null)
				result.add(map);
		}
		return result;
	}

}
