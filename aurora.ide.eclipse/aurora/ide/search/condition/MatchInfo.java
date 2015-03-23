package aurora.ide.search.condition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;

import uncertain.composite.CompositeMap;

public class MatchInfo {

	public static final MatchInfo Not_Match = new MatchInfo();

	private List<IRegion> regions = new ArrayList<IRegion>();

	private CompositeMap map;

	public MatchInfo() {
	}

	public List<IRegion> getRegions() {
		return regions;
	}

	public void addRegion(IRegion region) {
		if (region != null && !regions.contains(region))
			regions.add(region);
	}

	public void addAllRegion(List<IRegion> _regions) {
		for (IRegion r : _regions) {
			this.addRegion(r);
		}
	}

	public CompositeMap getMap() {
		return map;
	}

	public void setMap(CompositeMap map) {
		this.map = map;
	}

	public boolean isMatch() {
		return !regions.isEmpty();
	}

}
