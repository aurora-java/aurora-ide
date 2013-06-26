package aurora.ide.meta.gef.editors.models.commands;

import org.eclipse.gef.commands.Command;

import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.StyledStringText;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

public class ChangeTextStyleCommand extends Command {
	private AuroraComponent component;
	private String id;
	private Object value_new;
	private Object value_old;
	private StyledStringText new_style;
	private StyledStringText old_style;
	private String styleID;

	public ChangeTextStyleCommand(AuroraComponent component, String id,
			Object value_new, StyledStringText new_style) {
		this.component = component;
		this.id = id;
		styleID = id + ComponentInnerProperties.TEXT_STYLE;
		this.value_new = value_new;
		this.new_style = new_style;
	}

	@Override
	public void execute() {
		this.value_old = component.getPropertyValue(id);
		Object propertyValue = component.getPropertyValue(styleID);
		if (propertyValue instanceof StyledStringText)
			old_style = (StyledStringText) propertyValue;
		component.setPropertyValue(id, value_new);
		component.setPropertyValue(styleID, new_style);
	}

	@Override
	public String getLabel() {
		return "Change TextStyle";
	}

	@Override
	public void redo() {
		execute();
	}

	@Override
	public void undo() {
		component.setPropertyValue(id, value_old);
		if (old_style instanceof StyledStringText)
			component.setPropertyValue(styleID, old_style);
	}
}