package aurora.ide.meta.gef.editors.property;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.IDatasetDelegate;
import aurora.plugin.source.gen.screen.model.IDatasetFieldDelegate;

public class DefaultPropertySource implements IPropertySource {

	private IPropertyDescriptor[] defaultPD = EditablePropertyFactory.NONE_PROPS;
	private PropertyManager manager;
	private AuroraComponent component;

	public DefaultPropertySource(ComponentPart componentPart,
			PropertyManager manager) {
		this.manager = manager;
		component = componentPart.getComponent();
		defaultPD = manager.getPropertyDescriptors(componentPart);
	}

	public void setPropertyValue(Object id, Object value) {
		manager.setPropertyValue(id, value, getRealComponent(id));
	}

	public void resetPropertyValue(Object id) {
		// propertySource2.resetPropertyValue(id);
	}

	public boolean isPropertySet(Object id) {
		return true;
	}

	public Object getPropertyValue(Object id) {
		return getRealComponent(id).getPropertyValue("" + id);
	}

	private AuroraComponent getRealComponent(Object id) {
		for (IPropertyDescriptor pd : defaultPD) {
			if (pd instanceof StylePropertyDescriptor) {
				int style = ((StylePropertyDescriptor) pd).getStyle();
				boolean flag = pd.getId().equals(id);
				if (flag && ((style & StylePropertyDescriptor.dataset) != 0)
						&& component instanceof IDatasetDelegate) {
					return ((IDatasetDelegate) component).getDataset();
				}
				if (flag
						&& ((style & StylePropertyDescriptor.datasetfield) != 0)
						&& component instanceof IDatasetFieldDelegate) {
					return ((IDatasetFieldDelegate) component)
							.getDatasetField();
				}
				if (flag
						&& ((style & StylePropertyDescriptor.component_child) != 0)) {
					AuroraComponent ac = component
							.getAuroraComponentPropertyValue(((StylePropertyDescriptor) pd)
									.getChildPropertyId());
					if (ac != null) {
						return ac;
					}
					return component;
				}
			}
		}
		return component;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return defaultPD;
	}

	public Object getEditableValue() {
		return component;
	}

}
