package aurora.ide.meta.gef.editors.source.gen.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;

public class MapFinder {

	class RelationFinder implements IterationHandle {
		String localFieldName = "";
		CompositeMap refMap;

		public RelationFinder(String localFieldName) {
			this.localFieldName = localFieldName;
		}

		public int process(CompositeMap map) {
			if ("reference".equalsIgnoreCase(map.getName())) {
				Set keySet = map.keySet();
				for (Object object : keySet) {
					if ("localField".equalsIgnoreCase(object.toString())) {
						String string = map.getString(object);
						if (localFieldName.equals(string)) {
							refMap = map;
							return IterationHandle.IT_BREAK;
						}
					}
				}
			}
			return IterationHandle.IT_CONTINUE;
		}

		CompositeMap getRelationMap() {
			return refMap == null ? null : refMap.getParent();
		}
	}

	class LovFieldFinder implements IterationHandle {
		String rName = "";
		List<CompositeMap> maps = new ArrayList<CompositeMap>();

		public LovFieldFinder(String rName) {
			this.rName = rName;
		}

		public int process(CompositeMap map) {
			if ("ref-field".equalsIgnoreCase(map.getName())) {
				Set keySet = map.keySet();
				for (Object object : keySet) {
					if ("relationName".equalsIgnoreCase(object.toString())) {
						String string = map.getString(object);
						if (rName.equals(string)) {
							maps.add(map);
							break;
						}
					}
				}
			}
			return IterationHandle.IT_CONTINUE;
		}

		List<CompositeMap> geMaps() {
			return maps;
		}
	}

	public CompositeMap lookupRelation(String localFieldName, CompositeMap bmMap) {
		RelationFinder rf = new RelationFinder(localFieldName);
		if(bmMap == null)
			return null;
		bmMap.iterate(rf, false);
		return rf.getRelationMap();
	}

	public List<CompositeMap> lookupLovFields(String rName, CompositeMap bmMap) {
		LovFieldFinder rf = new LovFieldFinder(rName);
		bmMap.iterate(rf, false);
		return rf.geMaps();
	}
}
