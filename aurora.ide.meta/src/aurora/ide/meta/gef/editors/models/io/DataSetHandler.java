package aurora.ide.meta.gef.editors.models.io;

import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.ContainerHolder;
import aurora.ide.meta.gef.editors.models.QueryDataSet;
import aurora.ide.meta.gef.editors.models.ResultDataSet;

public class DataSetHandler extends DefaultIOHandler {
	public static final String COMMENT_TARGET = "target";
	public static final String COMMENT_OWNER = "owner";

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeSimpleAttribute(map, ac);
		map.setName(Dataset.class.getSimpleName());
		map.put("class", ac.getClass().getName());
		Dataset ds = (Dataset) ac;
		map.put(Dataset.MODEL, ds.getModel());
		if (ds instanceof QueryDataSet) {
			// TODO
		} else if (ds instanceof ResultDataSet) {
			ResultDataSet rds = (ResultDataSet) ds;
			map.put(ResultDataSet.SELECTION_MODE, rds.getSelectionMode());
			map.put(ResultDataSet.PAGE_SIZE, rds.getPageSize());
			map.put(ResultDataSet.AUTO_QUERY, rds.isAutoQuery());
		}
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		super.storeComplexAttribute(map, ac);
		if (ac instanceof ResultDataSet) {
			ResultDataSet rds = (ResultDataSet) ac;
			ReferenceHandler roh = new ReferenceHandler();
			ContainerHolder qc = (ContainerHolder) rds.getQueryContainer();
			if (qc != null) {
				Container cont = qc.getTarget();
				if (cont != null) {
					CompositeMap tMap = roh.toCompositeMap(cont, mic);
					tMap.put(ReferenceHandler.COMMENT, COMMENT_TARGET);
					map.addChild(tMap);
				}
			}
			// owner
			AuroraComponent owner = rds.getOwner();
			if (owner != null) {
				CompositeMap oMap = roh.toCompositeMap(owner, mic);
				oMap.put(ReferenceHandler.COMMENT, COMMENT_OWNER);
				map.addChild(oMap);
			}
		}
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreSimpleAttribute(ac, map);
		Dataset ds = (Dataset) ac;
		ds.setModel(map.getString(Dataset.MODEL));
		if (ds instanceof QueryDataSet) {
			// TODO
		} else if (ds instanceof ResultDataSet) {
			ResultDataSet rds = (ResultDataSet) ds;
			rds.setSelectionMode(map.getString(ResultDataSet.SELECTION_MODE));
			rds.setPageSize(map.getInt(ResultDataSet.PAGE_SIZE));
			rds.setAutoQuery(map.getBoolean(ResultDataSet.AUTO_QUERY));
		}
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		super.restoreComplexAttribute(ac, map);
		if (ac instanceof ResultDataSet) {
			ResultDataSet rds = (ResultDataSet) ac;
			@SuppressWarnings("unchecked")
			List<CompositeMap> list = map.getChildsNotNull();
			for (CompositeMap m : list) {
				if (!ReferenceHandler.NS_PREFIX.equals(m.getPrefix()))
					continue;
				String comment = m.getString(ReferenceHandler.COMMENT);
				String refId = m.getString(ReferenceHandler.REF_ID);
				ContainerHolder qc = rds.getQueryContainer();
				AuroraComponent a = mic.markMap.get(refId);
				if (COMMENT_TARGET.equals(comment)) {
					if (a != null) {
						qc.setTarget((Container) a);
						continue;
					}
					ReferenceDecl rd = new ReferenceDecl(refId, qc,
							"setTarget", Container.class);
					mic.refDeclList.add(rd);
				} else if (COMMENT_OWNER.equals(comment)) {
					if (a != null) {
						rds.setOwner(a);
						continue;
					}
					ReferenceDecl rd = new ReferenceDecl(refId, rds,
							"setOwner", AuroraComponent.class);
					mic.refDeclList.add(rd);
				}
			}
		}
	}

	@Override
	protected Dataset getNewObject(CompositeMap map) {
		String cls = map.getString("class");
		Object obj = null;
		try {
			obj = Class.forName(cls).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (Dataset) obj;
	}

}
