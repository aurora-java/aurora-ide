package aurora.ide.prototype.freemarker;

import java.util.HashMap;
import java.util.Map;

import uncertain.composite.CompositeMap;

public class UID {

	private Map<String, CompositeMap> idMap = new HashMap<String, CompositeMap>();

	private Map<CompositeMap, String> mapID = new HashMap<CompositeMap, String>();

	public String getID(CompositeMap map) {
		String id = mapID.get(map);
		if (id == null) {
			return newID(map);
		}
		return id;
	}

	private String newID(CompositeMap map) {
		String valueOf = String.valueOf(idMap.size());
		idMap.put(valueOf, map);
		mapID.put(map, valueOf);
		return valueOf;
	}

	public CompositeMap getMap(String id) {
		return idMap.get(id);
	}

}
