package aurora.ide.meta.gef.editors.models.commands;

import org.eclipse.gef.commands.Command;

import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class ComponentPropertyEditCommand extends Command {

	private AuroraComponent node;

	private String propertyKey;

	private Object newValue;

	private Object oldValue;

	public ComponentPropertyEditCommand(AuroraComponent node,
			String propertyKey, Object newValue) {
		super();
		this.node = node;
		this.propertyKey = propertyKey;
		this.newValue = newValue;
	}

	public String getPropertyKey() {
		return propertyKey;
	}

	public void setPropertyKey(String propertyKey) {
		this.propertyKey = propertyKey;
	}

	public Object getNewValue() {
		return newValue;
	}

	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}

	public AuroraComponent getNode() {
		return node;
	}

	public void setNode(AuroraComponent node) {
		this.node = node;
	}

	public void execute() {
		oldValue = this.node.getPropertyValue(propertyKey);
		this.node.setPropertyValue(propertyKey, newValue);
	}

	public void redo() {
		this.node.setPropertyValue(propertyKey, newValue);
	}

	public void undo() {
		this.node.setPropertyValue(propertyKey, oldValue);
	}

	public String getLabel() {
		return "Property Edit";
	}
}