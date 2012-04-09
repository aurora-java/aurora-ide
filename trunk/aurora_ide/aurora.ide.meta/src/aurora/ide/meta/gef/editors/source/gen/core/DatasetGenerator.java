package aurora.ide.meta.gef.editors.source.gen.core;

import java.util.HashMap;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.DatasetBinder;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.QueryContainer;
import aurora.ide.meta.gef.editors.models.QueryDataSet;
import aurora.ide.meta.gef.editors.models.ResultDataSet;

class DatasetGenerator {
	private ScreenGenerator sg;
	private Map<Dataset, String> datasetMaper = new HashMap<Dataset, String>();

	DatasetGenerator(ScreenGenerator sg) {
		this.sg = sg;
	}

	public CompositeMap fillDatasets(Container ac) {
		Dataset dataset = findDataset(ac);
		return fillDatasetsMap(dataset);
	}

	CompositeMap fillDatasetsMap(Dataset dataset) {
		CompositeMap datasets = sg.getDatasetsMap();
		if (dataset == null)
			return null;
		String dsID = getOrCreateDSID(dataset);
		CompositeMap dsMap = datasets.getChildByAttrib("id", dsID);
		if (dsMap == null) {
			CompositeMap rds = createDatasetMap(dsID, dataset);
			datasets.addChild(rds);
			return rds;
		}
		return dsMap;
	}

	public String getOrCreateDSID(Dataset dataset) {
		String dsID = this.datasetMaper.get(dataset);
		if (dsID == null) {
			dsID = sg.getIdGenerator().genDatasetID(dataset);
			datasetMaper.put(dataset, dsID);
		}
		return dsID;
	}

	public CompositeMap createDatasetMap(String id, Dataset dataset) {
		CompositeMap rds = sg.getA2Map().toCompositMap(dataset);
		rds.put("id", id);
		if (dataset.isUse4Query()) {
			rds.put("autoCreate", true);
		} else {
			rds.put("model", dataset.getModel());
		}
		QueryContainer qs = (QueryContainer) dataset
				.getPropertyValue(ResultDataSet.QUERY_CONTAINER);
		if (qs != null) {
			Container target = qs.getTarget();
			if (target != null) {
				Dataset ds = this.findDataset(target);
				String qds = this.fillDatasetsMap(ds).getString("id", "");
				if (ds instanceof QueryDataSet) {
					rds.put(ResultDataSet.QUERY_DATASET, qds.toString());
				}
				if (ds instanceof ResultDataSet) {
					rds.put("bindName", rds.getString("id", ""));
					rds.put("bindTarget", qds);
				}
			} else {
				rds.put("loadData", true);
			}
		}
		return rds;
	}

	public void bindDatasetMap(Container parent, AuroraComponent ac,
			CompositeMap child) {
		if (ac instanceof DatasetBinder) {
			Dataset dataset = null;
			if (ac instanceof Grid) {
				dataset = findDataset((Grid) ac);
			} else {
				dataset = findDataset(parent);
			}
			if (dataset != null) {
				CompositeMap ds = this.fillDatasetsMap(dataset);
				child.put("bindTarget", ds.get("id"));
			}
		}
	}

	public void fillDatasetMap(Dataset dataset, AuroraComponent ac) {

		if (ac.getName() == null || "".equals(ac.getName()))
			return;
		CompositeMap dsMap = fillDatasetsMap(dataset);
		if (dsMap == null) {
			return;
		}
		CompositeMap fields = dsMap.getChild("fields");
		if (fields == null) {
			fields = sg.createCompositeMap("fields");
			dsMap.addChild(fields);
		}

		CompositeMap field = fields.getChildByAttrib(AuroraComponent.NAME,
				ac.getPropertyValue(AuroraComponent.NAME));
		if (field == null) {
			field = sg.createCompositeMap("field");
			fields.addChild(field);
			field.put(AuroraComponent.NAME,
					ac.getPropertyValue(AuroraComponent.NAME));
		}
		this.sg.getA2Map().bindDatasetField(field, dataset, ac);
	}

	public Dataset findDataset(Container container) {
		if (container == null)
			return null;
		boolean useParentBM = isUseParentBM(container);
		if (useParentBM) {
			return findDataset(container.getParent());
		}
		Dataset dataset = container.getDataset();
		return dataset;
	}

	public String findDatasetId(Container container) {
		Dataset findDataset = this.findDataset(container);
		return this.datasetMaper.get(findDataset);
	}

	private boolean isUseParentBM(Container container) {
		if (Container.SECTION_TYPE_QUERY.equals(container.getSectionType())
				|| Container.SECTION_TYPE_RESULT.equals(container
						.getSectionType())) {
			return false;
		}
		return true;
	}
}
