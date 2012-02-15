package aurora.ide.meta.gef.editors.source.gen;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.Input;

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
					value = input.getPropertyValue(id).toString();
				}
				if (value != null && !("".equals(value)))
					map.putString(id, value.toString());
			}
		}
		return map;
	}

	@Override
	public boolean isCompositMapKey(String key) {
		if (Input.READONLY.equals(key) || Input.REQUIRED.equals(key)) {
			return false;
		}
		return true;
	}

}
