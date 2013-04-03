package aurora.ide.meta.gef.editors.models.commands;

import org.eclipse.gef.commands.Command;

import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class ChangePropertyCommand extends Command {
	private AuroraComponent component;
	private String id;
	private Object value_new;
	private Object value_old;

	public ChangePropertyCommand(AuroraComponent component, String id,
			Object value_new) {
		this.component = component;
		this.id = id;
		this.value_new = value_new;
	} 

	@Override
	public void execute() {
		this.value_old = component.getPropertyValue(id);
		component.setPropertyValue(id, value_new);
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
		component.setPropertyValue(id, value_old);
	}
}