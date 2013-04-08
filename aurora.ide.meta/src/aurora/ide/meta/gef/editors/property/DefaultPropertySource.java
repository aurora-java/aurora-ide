package aurora.ide.meta.gef.editors.property;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import aurora.ide.meta.gef.editors.models.commands.ChangePropertyCommand;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.IDatasetDelegate;
import aurora.plugin.source.gen.screen.model.IDatasetFieldDelegate;

public class DefaultPropertySource implements IPropertySource {
	// private Object id;
	// private Object value_new;
	// private Object value_old;
	private AuroraComponent component;
	private CommandStack cmdStack;
	private IPropertyDescriptor[] defaultPD = EditablePropertyFactory.NONE_PROPS;

	public DefaultPropertySource(AuroraComponent component,
			CommandStack cmdStack) {
		this.component = component;
		this.cmdStack = cmdStack;
		EditablePropertyFactory editablePropertyFactory = new EditablePropertyFactory();
		defaultPD = editablePropertyFactory
				.createPropertyDescriptors(component);
	}

	public void setPropertyValue(Object id, Object value) {
		// this.id = id;
		// this.value_new = value;
		// cmdStack.execute(this);
		ChangePropertyCommand command = new ChangePropertyCommand(
				getRealComponent(id), "" + id, value);
		cmdStack.execute(command);
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
