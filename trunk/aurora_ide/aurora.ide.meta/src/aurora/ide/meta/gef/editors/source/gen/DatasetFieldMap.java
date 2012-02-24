package aurora.ide.meta.gef.editors.source.gen;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
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
			field = field.getParent().createChild("field");
			field.put("name", ac.getName() + "_display");
		}
		String[]  keys = DatasetField.keys;
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

	@Override
	public boolean isCompositMapKey(String key) {
		if (DatasetField.DISPLAY_FIELD.equals(key)
				|| DatasetField.VALUE_FIELD.equals(key)
				|| DatasetField.LOV_SERVICE.equals(key)
				|| DatasetField.OPTIONS.equals(key)
				|| DatasetField.RETURN_FIELD.equals(key)
				|| DatasetField.TITLE.equals(key)) {
			return false;
		}
		return true;
	}

}
