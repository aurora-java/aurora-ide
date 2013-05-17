package aurora.ide.meta.gef.editors.property;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import aurora.ide.meta.gef.editors.models.commands.ChangePropertyCommand;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class PropertyManager {

	private CommandStack commandStack;

	public PropertyManager(CommandStack commandStack) {
		this.commandStack = commandStack;
	}

	public IPropertySource createIPropertySource(ComponentPart part) {
		return new DefaultPropertySource(part, this);
	}

	public void setPropertyValue(Object id, Object value,
			AuroraComponent component) {
		ChangePropertyCommand command = new ChangePropertyCommand(component, ""
				+ id, value);
		commandStack.execute(command);
	}

	public IPropertyDescriptor[] getPropertyDescriptors(ComponentPart object) {

		EditablePropertyFactory editablePropertyFactory = new EditablePropertyFactory();
		IPropertyDescriptor[] pd = editablePropertyFactory
				.createPropertyDescriptors(object.getComponent());
		return pd;
	}
}
