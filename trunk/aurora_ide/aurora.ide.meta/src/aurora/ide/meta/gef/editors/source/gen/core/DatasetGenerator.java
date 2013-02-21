package aurora.ide.meta.gef.editors.source.gen.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.Util;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.DatasetBinder;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.ContainerHolder;
import aurora.ide.meta.gef.editors.models.QueryDataSet;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.source.gen.ComboDataset;

class DatasetGenerator {
	private ScreenGenerator sg;
	private Map<Dataset, String> datasetMaper = new HashMap<Dataset, String>();

	private List<ComboDataset> createdComboDatasets = new ArrayList<ComboDataset>();

	DatasetGenerator(ScreenGenerator sg) {
		this.sg = sg;
	}

	public CompositeMap fillDatasets(Container ac) {
		Dataset dataset = findDataset(ac);
		return fillDatasetsMap(dataset);
	}

	CompositeMap fillDatasetsMap(Dataset dataset) {
		if (dataset instanceof ComboDataset) {
			return fillComboDatasetMap((ComboDataset) dataset);
		}
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

	private CompositeMap fillComboDatasetMap(ComboDataset dataset) {
		CompositeMap datasets = sg.getDatasetsMap();
		if (dataset == null)
			return null;
		dataset = getCreateComboDataset(dataset);
		String dsID = getOrCreateDSID(dataset);
		CompositeMap dsMap = datasets.getChildByAttrib("id", dsID);
		if (dsMap == null) {
			CompositeMap rds = createComboDatasetMap(dsID, dataset);
			datasets.addChild(rds);
			return rds;
		}
		return dsMap;
	}

	private ComboDataset getCreateComboDataset(ComboDataset ds) {
		for (ComboDataset cds : createdComboDatasets) {
			if (ds.getModel().equals(cds.getModel())) {
				if (ds.getLookupCode() == cds.getLookupCode()) {
					// null
					return cds;
				}
				if (ds.getLookupCode() != null
						&& ds.getLookupCode().equals(cds.getLookupCode())) {
					return cds;
				}
			}
		}
		createdComboDatasets.add(ds);
		return ds;
	}

	private CompositeMap createComboDatasetMap(String id, ComboDataset dataset) {
		CompositeMap rds = sg.getA2Map().toCompositMap(dataset);
		rds.put("id", id);
		rds.put("autoCreate", true);
		rds.put("model", dataset.getModel());
		rds.put("loadData", true);
		String lookupCode = dataset.getLookupCode();
		if (lookupCode != null)
			rds.put("lookupCode", lookupCode);
		rds.remove(ResultDataSet.PAGE_SIZE);
		return rds;
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
		ContainerHolder qs = (ContainerHolder) dataset
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
				rds.put("autoQuery", true);
				rds.put(ResultDataSet.PAGE_SIZE, null);
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
		this.sg.getA2Map().bindDatasetField(dsMap, dataset, ac);
	}

	public Dataset findDataset(Container container) {
		return Util.findDataset(container);
	}

	public String findDatasetId(Container container) {
		Dataset findDataset = this.findDataset(container);
		return this.datasetMaper.get(findDataset);
	}
}
