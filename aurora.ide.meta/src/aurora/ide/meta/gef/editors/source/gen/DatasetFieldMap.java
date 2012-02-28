package aurora.ide.meta.gef.editors.source.gen;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.DatasetField;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.IDatasetFieldDelegate;
import aurora.ide.meta.gef.editors.models.Input;

public class DatasetFieldMap extends AbstractComponentMap {

	private CompositeMap field;
	private Dataset dataset;
	private AuroraComponent ac;
	private ScreenGenerator sg;
	private DatasetField datasetField;

	public DatasetFieldMap(CompositeMap field, Dataset dataset,
			AuroraComponent ac, ScreenGenerator sg) {
		super();
		this.field = field;
		this.dataset = dataset;
		this.ac = ac;
		this.sg = sg;
		if (ac instanceof IDatasetFieldDelegate) {
			datasetField = ((IDatasetFieldDelegate) ac).getDatasetField();
		}
	}

	@Override
	public CompositeMap toCompositMap() {
		if (this.isCombo() || this.isLov()) {
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
					if (Boolean.TRUE.equals(value))
						field.put(AuroraComponent.READONLY, value);
					continue;
				}
				if (AuroraComponent.REQUIRED.equals(key)) {
					if (Boolean.TRUE.equals(value))
						field.put(AuroraComponent.REQUIRED, value);
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
						Dataset ds = new Dataset();
						ds.setUse4Query(false);
						ds.setModel(value.toString());
						CompositeMap fillDatasets = sg.fillDatasets(ds);
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
					if (optionsMap != null)
						value = optionsMap.getString("defaultDisplayField");
				}
				if (DatasetField.VALUE_FIELD.equals(key)) {
					CompositeMap optionsMap = dataSetFieldUtil.getOptionsMap();
					if (optionsMap != null)
						value = dataSetFieldUtil.getPK(optionsMap);
				}
			}
			if (isLov()) {
				if (DatasetField.TITLE.equals(key)) {
					value = ac.getPropertyValue(key);
				}
				if (DatasetField.LOV_SERVICE.equals(key)) {
					value = dataSetFieldUtil.getOptions();

					CompositeMap mappingMap = sg.createCompositeMap("mapping");

					CompositeMap idMap = sg.createCompositeMap("map");
					CompositeMap optionsMap = dataSetFieldUtil.getOptionsMap();
					if (optionsMap != null) {
						String pk = dataSetFieldUtil.getPK(optionsMap);
						idMap.put("from", pk);
						idMap.put("to", ac.getName());
					}

					CompositeMap valueMap = sg.createCompositeMap("map");
					if (optionsMap != null) {
						String dsValue = optionsMap
								.getString("defaultDisplayField");
						valueMap.put("from", dsValue);
						valueMap.put("to", ac.getName() + "_display");
					}
					mappingMap.addChild(idMap);
					mappingMap.addChild(valueMap);
					field.addChild(mappingMap);
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
			if (value != null && !("".equals(value)))
				field.putString(key, value.toString());
		}
		return field;
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

}
