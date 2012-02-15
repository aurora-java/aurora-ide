package aurora.ide.meta.gef.editors.source.gen;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.models.Input;

public class CheckBoxMap extends AbstractComponentMap {

	private CheckBox checkBox;

	public CheckBoxMap(CheckBox c) {
		this.checkBox = c;
	}

	@Override
	public CompositeMap toCompositMap() {
		String type = checkBox.getType();
		CompositeMap map = AuroraComponent2CompositMap.createChild(type);
		IPropertyDescriptor[] propertyDescriptors = checkBox
				.getPropertyDescriptors();
		for (IPropertyDescriptor iPropertyDescriptor : propertyDescriptors) {
			Object id = iPropertyDescriptor.getId();

			boolean isKey = this.isCompositMapKey(id.toString());
			if (isKey) {
				Object value = checkBox.getPropertyValue(id).toString();
				if (value != null && !("".equals(value)))
					map.putString(id, value.toString());
			}
		}
		return map;
	}

	public boolean isCompositMapKey(String key) {
		return true;
	}
}
