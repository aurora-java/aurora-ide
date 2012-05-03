package aurora.ide.meta.gef.editors.source.gen;

import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.Util;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.DatasetField;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.IDatasetFieldDelegate;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.source.gen.core.MapFinder;
import aurora.ide.meta.gef.editors.source.gen.core.ScreenGenerator;

public class DatasetFieldMap extends AbstractComponentMap {

	private CompositeMap dsMap;
	private Dataset dataset;
	private AuroraComponent ac;
	private ScreenGenerator sg;
	private DatasetField datasetField;

	public DatasetFieldMap(CompositeMap dsMap, Dataset dataset,
			AuroraComponent ac, ScreenGenerator sg) {
		super();
		this.dsMap = dsMap;
		this.dataset = dataset;
		this.ac = ac;
		this.sg = sg;
		if (ac instanceof IDatasetFieldDelegate) {
			datasetField = ((IDatasetFieldDelegate) ac).getDatasetField();
		}
	}

	@Override
	public CompositeMap toCompositMap() {
		CompositeMap field = null;
		if (this.isLov()) {
			return bindLov();
		}
		if (this.isCombo()) {

			field = getOrCreateField();
			CompositeMap fields = field.getParent();
			CompositeMap childByAttrib = fields.getChildByAttrib("name",
					ac.getName() + "_display");
			if (childByAttrib == null) {
				field = fields.createChild("field");
				field.setPrefix(fields.getPrefix());
				field.put("name", ac.getName() + "_display");
			} else {
				field = childByAttrib;
			}
		}
		String[] keys = DatasetField.keys;
		for (String key : keys) {
			Object value = "";
			boolean isKey = this.isCompositMapKey(key);
			if (isKey) {
				value = ac.getPropertyValue(key);
				if (AuroraComponent.READONLY.equals(key)) {
					if (Boolean.TRUE.equals(value)) {
						field = field == null ? getOrCreateField() : field;
						field.put(AuroraComponent.READONLY, value);
					}
					continue;
				}
				if (AuroraComponent.REQUIRED.equals(key)) {
					if (Boolean.TRUE.equals(value)) {
						field = field == null ? getOrCreateField() : field;
						field.put(AuroraComponent.REQUIRED, value);
					}
					continue;
				}
			}
			DataSetFieldUtil dataSetFieldUtil = new DataSetFieldUtil(
					sg.getProject(), ac.getName(), dataset.getModel());
			if (isCombo()) {
				if (DatasetField.RETURN_FIELD.equals(key)) {
					value = ac.getName();
				}
				if (DatasetField.OPTIONS.equals(key)) {
					value = dataSetFieldUtil.getOptions();
					if (value != null) {
						Dataset ds = new ComboDataset();
						ds.setModel(value.toString());
						// ds.setPropertyValue(propName, val)
						CompositeMap fillDatasets = sg.fillDatasetsMap(ds);
						if (fillDatasets != null) {
							value = fillDatasets.get("id");
							CompositeMap dsParent = fillDatasets.getParent();
							dsParent.removeChild(fillDatasets);
							dsParent.addChild(0, fillDatasets);
						}
					}
				}

				if (DatasetField.DISPLAY_FIELD.equals(key)) {
					CompositeMap optionsMap = dataSetFieldUtil.getOptionsMap();
					if (optionsMap != null) {
						value = Util.getValueIgnoreCase(optionsMap,
								"defaultDisplayField");
					}
				}
				if (DatasetField.VALUE_FIELD.equals(key)) {
					CompositeMap optionsMap = dataSetFieldUtil.getOptionsMap();
					if (optionsMap != null)
						value = dataSetFieldUtil.getPK(optionsMap);
				}
			}

			if (isCheckBox()) {
				if (DatasetField.CHECKED_VALUE.equals(key)) {
					value = ac.getPropertyValue(key);
				}
				if (DatasetField.UNCHECKED_VALUE.equals(key)) {
					value = ac.getPropertyValue(key);
				}
			}
			if (value != null && !("".equals(value))) {
				field = field == null ? getOrCreateField() : field;
				field.putString(key, value.toString());
			}
		}
		return field;
	}

	private CompositeMap bindLov() {
		CompositeMap field = getOrCreateField();
		List<CompositeMap> lovMaps = getLovMaps();
		if (lovMaps == null) {
			return bindLov(field);
		} else {
			for (CompositeMap lovMap : lovMaps) {
				CompositeMap fields = field.getParent();
				CompositeMap fieldMap;
				String name = lovMap.getString("name", "");
				CompositeMap childByAttrib = fields.getChildByAttrib("name",
						name);
				if (childByAttrib == null) {
					fieldMap = fields.createChild("field");
					fieldMap.setPrefix(fields.getPrefix());
					fieldMap.put("name", name);
				} else {
					fieldMap = childByAttrib;
				}
				bindLov(fieldMap);
				bindMapping(fieldMap, lovMaps);

			}
		}
		return null;
	}

	private void bindMapping(CompositeMap fieldMap, List<CompositeMap> lovMaps) {
		DataSetFieldUtil dataSetFieldUtil = new DataSetFieldUtil(
				sg.getProject(), ac.getName(), dataset.getModel());
		CompositeMap bmMap = dataSetFieldUtil.getBmMap();
		MapFinder mf = new MapFinder();
		CompositeMap relation = mf.lookupRelation(ac.getName(), bmMap);
		CompositeMap refMap = relation.getChild("reference");
		CompositeMap mappingMap = sg.createCompositeMap("mapping");

		String foreignField = Util.getCompositeValue("foreignField", refMap);
		String localField = Util.getCompositeValue("localField", refMap);
		if (localField != null && foreignField != null) {
			CompositeMap idMap = sg.createCompositeMap("map");
			idMap.put("from", foreignField);
			idMap.put("to", localField);
			mappingMap.addChild(idMap);
		}
		CompositeMap optionsMap = dataSetFieldUtil.getOptionsMap();
		BMCompositeMap opb = new BMCompositeMap(optionsMap);

		for (CompositeMap lovMap : lovMaps) {
			String source = Util.getCompositeValue("sourceField", lovMap);
			String name = Util.getCompositeValue("name", lovMap);

			if (opb.getFieldByName(source) == null)
				continue;
			if (source != null && name != null) {
				CompositeMap m = sg.createCompositeMap("map");
				m.put("from", source);
				m.put("to", name);
				mappingMap.addChild(m);
			}
		}
		fieldMap.addChild(mappingMap);
	}

	private CompositeMap bindLov(CompositeMap field) {
		Object value = "";
		String[] keys = DatasetField.lov_keys;
		DataSetFieldUtil dataSetFieldUtil = new DataSetFieldUtil(
				sg.getProject(), ac.getName(), dataset.getModel());
		for (String key : keys) {

			if (AuroraComponent.READONLY.equals(key)) {
				if (Boolean.TRUE.equals(value))
					field.put(AuroraComponent.READONLY, value);
				continue;
			}
			if (AuroraComponent.REQUIRED.equals(key)) {
				if (Boolean.TRUE.equals(value))
					field.put(AuroraComponent.REQUIRED, value);
				continue;
			}
			if (DatasetField.LOV_SERVICE.equals(key)) {
				value = dataSetFieldUtil.getOptions();
			} else {
				value = ac.getPropertyValue(key);
			}
			if (value != null && !("".equals(value)))
				field.putString(key, value.toString());
		}
		return field;
	}

	public List<CompositeMap> getLovMaps() {
		DataSetFieldUtil dataSetFieldUtil = new DataSetFieldUtil(
				sg.getProject(), ac.getName(), dataset.getModel());
		CompositeMap bmMap = dataSetFieldUtil.getBmMap();
		if (bmMap == null)
			return null;
		MapFinder mf = new MapFinder();
		CompositeMap relation = mf.lookupRelation(ac.getName(), bmMap);
		if (relation != null) {
			String rName = relation.getString("name", "");
			List<CompositeMap> lovFields = mf.lookupLovFields(rName, bmMap);
			return lovFields;
		}
		return null;
	}

	private boolean isCombo() {
		if (ac instanceof Input) {
			return Input.Combo.equals(ac.getType());
		}
		if (ac instanceof GridColumn) {
			return Input.Combo.equals(((GridColumn) ac).getEditor());
		}
		return false;
	}

	private boolean isLov() {
		if (ac instanceof Input) {
			return Input.LOV.equals(ac.getType());
		}
		if (ac instanceof GridColumn) {
			return Input.LOV.equals(((GridColumn) ac).getEditor());
		}
		return false;
	}

	private boolean isCheckBox() {
		if (ac instanceof Input) {
			return CheckBox.CHECKBOX.equals(ac.getType());
		}
		if (ac instanceof GridColumn) {
			return CheckBox.CHECKBOX.equals(((GridColumn) ac).getEditor());
		}
		return false;
	}

	@Override
	public boolean isCompositMapKey(String key) {
		if (DatasetField.DISPLAY_FIELD.equals(key)
				|| DatasetField.VALUE_FIELD.equals(key)
				|| DatasetField.LOV_SERVICE.equals(key)
				|| DatasetField.OPTIONS.equals(key)
				|| DatasetField.RETURN_FIELD.equals(key)
				|| DatasetField.TITLE.equals(key)
				|| DatasetField.CHECKED_VALUE.equals(key)
				|| DatasetField.UNCHECKED_VALUE.equals(key)) {
			return false;
		}
		return true;
	}

	private CompositeMap getOrCreateField() {
		CompositeMap fields = getOrCreateFields();

		CompositeMap field = fields.getChildByAttrib(AuroraComponent.NAME,
				ac.getPropertyValue(AuroraComponent.NAME));
		if (field == null) {
			field = sg.createCompositeMap("field");
			fields.addChild(field);
			field.put(AuroraComponent.NAME,
					ac.getPropertyValue(AuroraComponent.NAME));
		}
		return field;
	}

	private CompositeMap getOrCreateFields() {
		CompositeMap fields = dsMap.getChild("fields");
		if (fields == null) {
			fields = sg.createCompositeMap("fields");
			dsMap.addChild(fields);
		}
		return fields;
	}

}
