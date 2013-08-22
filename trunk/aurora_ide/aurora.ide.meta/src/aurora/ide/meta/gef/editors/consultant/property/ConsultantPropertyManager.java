package aurora.ide.meta.gef.editors.consultant.property;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.property.PropertyManager;

public class ConsultantPropertyManager extends PropertyManager {

	public ConsultantPropertyManager(CommandStack commandStack) {
		super(commandStack);
	}

	public IPropertyDescriptor[] getPropertyDescriptors(ComponentPart object) {
		ConsultantPropertyFactory editablePropertyFactory = new ConsultantPropertyFactory();
		IPropertyDescriptor[] pd = editablePropertyFactory
				.createPropertyDescriptors(object.getComponent());
		return pd;
	}

	public IPropertyDescriptor[] getFSDPropertyDescriptors(ComponentPart object) {
		FSDPropertyFactory editablePropertyFactory = new FSDPropertyFactory();
		IPropertyDescriptor[] pd = editablePropertyFactory
				.createPropertyDescriptors(object.getComponent());
		return pd;
	}
}
