package aurora.ide.meta.gef.editors.source.gen;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.DatasetField;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.source.gen.core.AuroraComponent2CompositMap;

public class InputMap extends AbstractComponentMap {

	private Input input;

	public InputMap(Input c) {
		this.input = c;
	}

	@Override
	public CompositeMap toCompositMap() {
		String type = input.getType();
		CompositeMap map = AuroraComponent2CompositMap.createChild(type);
		IPropertyDescriptor[] propertyDescriptors = input
				.getPropertyDescriptors();
		for (IPropertyDescriptor iPropertyDescriptor : propertyDescriptors) {
			Object id = iPropertyDescriptor.getId();

			boolean isKey = this.isCompositMapKey(id.toString());
			if (isKey) {
				Object value = "";
				if (Input.TYPECASE.equals(id))
					value = input.getTypeCase();
				else if (Input.ENABLE_BESIDE_DAYS.equals(id))
					value = input.getEnableBesideDays();
				else if (Input.ENABLE_MONTH_BTN.equals(id))
					value = input.getEnableMonthBtn();
				else {
					value = input.getPropertyValue(id);
				}
				if (value != null && !("".equals(value)))
					map.putString(id, value.toString());
			}
		}

		if (isCombo()) {
			map.putString("name", input.getName() + "_display");
		}
		return map;
	}

	private boolean isCombo() {
		if (input instanceof Input) {
			return Input.Combo.equals(input.getType());
		}
		return false;
	}



	@Override
	public boolean isCompositMapKey(String key) {
		if (Input.READONLY.equals(key) || Input.REQUIRED.equals(key)
				|| DatasetField.DISPLAY_FIELD.equals(key)
				|| DatasetField.VALUE_FIELD.equals(key)
				|| DatasetField.LOV_SERVICE.equals(key)
				|| DatasetField.OPTIONS.equals(key)
				|| DatasetField.CHECKED_VALUE.equals(key)
				|| DatasetField.UNCHECKED_VALUE.equals(key)
				|| DatasetField.DEFAULT_VALUE.equals(key)
				|| DatasetField.RETURN_FIELD.equals(key)) {
			return false;
		}
		return true;
	}

}
