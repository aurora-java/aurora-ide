package aurora.ide.meta.gef.editors.models.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import aurora.ide.meta.gef.editors.property.IPropertySource2;

public class ChangePropertyCommand extends Command implements IPropertySource {
	private IPropertySource2 propertySource2;
	private CommandStack cmdStack;
	private Object id;
	private Object value_new;
	private Object value_old;

	public ChangePropertyCommand(IPropertySource2 ps2, CommandStack cmdStack) {
		this.propertySource2 = ps2;
		this.cmdStack = cmdStack;
	}

	@Override
	public void execute() {
		this.value_old = getPropertyValue(id);
		propertySource2.setPropertyValue(id, value_new);

	}

	@Override
	public String getLabel() {
		return "Change Property";
	}

	@Override
	public void redo() {
		execute();
	}

	@Override
	public void undo() {
		propertySource2.setPropertyValue(id, value_old);
	}

	public void setPropertyValue(Object id, Object value) {
		this.id = id;
		this.value_new = value;
		cmdStack.execute(this);
	}

	public void resetPropertyValue(Object id) {
		propertySource2.resetPropertyValue(id);
	}

	public boolean isPropertySet(Object id) {
		return propertySource2.isPropertySet(id);
	}

	public Object getPropertyValue(Object id) {
		return propertySource2.getPropertyValue(id);
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return propertySource2.getPropertyDescriptors();
	}

	public Object getEditableValue() {
		return propertySource2.getEditableValue();
	}

}