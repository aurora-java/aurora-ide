package aurora.ide.meta.gef.editors.models.io;

import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.QueryContainer;
import aurora.ide.meta.gef.editors.models.ResultDataSet;

public class ResultDataSetHandler extends DefaultIOHandler {
	public static final String COMMENT_TARGET = "target";
	public static final String COMMENT_OWNER = "owner";

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		ResultDataSet ds = (ResultDataSet) ac;
		map.put(ResultDataSet.SELECTION_MODE, ds.getSelectionMode());
		map.put(ResultDataSet.PAGE_SIZE, ds.getPageSize());
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		ResultDataSet ds = (ResultDataSet) ac;
		// dataset
		ReferenceHandler roh = new ReferenceHandler();
		QueryContainer qc = (QueryContainer) ds.getQueryContainer();
		if (qc != null) {
			Container cont = qc.getTarget();
			if (cont != null) {
				CompositeMap tMap = roh.toCompositeMap(cont, mic);
				tMap.put(ReferenceHandler.COMMENT, COMMENT_TARGET);
				map.addChild(tMap);
			}
		}
		// owner
		AuroraComponent owner = ds.getOwner();
		if (owner != null) {
			CompositeMap oMap = roh.toCompositeMap(owner, mic);
			oMap.put(ReferenceHandler.COMMENT, COMMENT_OWNER);
			map.addChild(oMap);
		}
	}

	@Override
	protected ResultDataSet getNewObject(CompositeMap map) {
		return new ResultDataSet();
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		ResultDataSet ds = (ResultDataSet) ac;
		ds.setSelectionMode(map.getString(ResultDataSet.SELECTION_MODE));
		ds.setPageSize(map.getInt(ResultDataSet.PAGE_SIZE));
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		ResultDataSet ds = (ResultDataSet) ac;
		@SuppressWarnings("unchecked")
		List<CompositeMap> list = map.getChildsNotNull();
		for (CompositeMap m : list) {
			if (!ReferenceHandler.NS_PREFIX.equals(m.getPrefix()))
				continue;
			String comment = m.getString(ReferenceHandler.COMMENT);
			String refId = m.getString(ReferenceHandler.REF_ID);
			QueryContainer qc = ds.getQueryContainer();
			AuroraComponent a = mic.markMap.get(refId);
			if (COMMENT_TARGET.equals(comment)) {
				if (a != null) {
					qc.setTarget((Container) a);
					continue;
				}
				ReferenceDecl rd = new ReferenceDecl(refId, qc, "setTarget",
						Container.class);
				mic.refDeclList.add(rd);
			} else if (COMMENT_OWNER.equals(comment)) {
				if (a != null) {
					ds.setOwner(a);
					continue;
				}
				ReferenceDecl rd = new ReferenceDecl(refId, ds, "setOwner",
						AuroraComponent.class);
				mic.refDeclList.add(rd);
			}
		}
	}
}
