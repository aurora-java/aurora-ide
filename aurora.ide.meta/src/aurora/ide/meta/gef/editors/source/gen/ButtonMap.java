package aurora.ide.meta.gef.editors.source.gen;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.Button;

public class ButtonMap extends AbstractComponentMap {

	private Button c;

	public ButtonMap(Button c) {
		this.c = c;
	}

	public CompositeMap toCompositMap() {
		String type = c.getType();
		CompositeMap map = AuroraComponent2CompositMap.createChild(type);
		if (c.isOnToolBar()) {
			String buttonType = c.getButtonType();
			if (Button.DEFAULT.equals(buttonType)) {
				map.put(Button.BUTTON_TEXT,
						c.getPropertyValue(Button.BUTTON_TEXT));
			} else {
				map.put(Button.BUTTON_TYPE, buttonType);
			}

		} else {
			IPropertyDescriptor[] propertyDescriptors = c
					.getPropertyDescriptors();
			for (IPropertyDescriptor iPropertyDescriptor : propertyDescriptors) {
				Object id = iPropertyDescriptor.getId();
				boolean isKey = this.isCompositMapKey(id.toString());
				if (isKey) {
					Object value = c.getPropertyValue(id).toString();
					
					if (value != null && !("".equals(value)))
						map.putString(id, value.toString());
				}
			}
		}

		return map;
	}

	public boolean isCompositMapKey(String key) {
		boolean equals = Button.BUTTON_CLICKER.equals(key);
		return equals == false;
	}

}
