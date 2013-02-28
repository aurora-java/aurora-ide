package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.ContainerHolder;
import aurora.ide.meta.gef.editors.models.Dataset;
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
			ContainerHolder qc = (ContainerHolder) rds.getQueryContainer();
			if (qc != null) {
				ContainerHolderHandler chh = new ContainerHolderHandler();
				CompositeMap qcMap = chh.toCompositeMap(qc, mic);
				qcMap.setName("QueryContainer");
				map.addChild(qcMap);
			}
			// owner
			AuroraComponent owner = rds.getOwner();
			if (owner != null) {
				map.put(COMMENT_OWNER, owner.markid);
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
			CompositeMap qcMap = map.getChild("QueryContainer");
			if (qcMap != null) {
				ContainerHolder ch = (ContainerHolder) new ContainerHolderHandler()
						.fromCompositeMap(qcMap, mic);
				rds.setQueryContainer(ch);
			}
			String ownerid = map.getString(COMMENT_OWNER);
			if (ownerid != null) {
				rds.setOwner(mic.markMap.get(ownerid));
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
