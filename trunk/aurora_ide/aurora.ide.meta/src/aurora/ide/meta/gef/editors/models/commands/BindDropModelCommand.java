package aurora.ide.meta.gef.editors.models.commands;

import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.Util;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.Label;

public class BindDropModelCommand extends DropBMCommand {

	private Container container;
	private Object data;
	private boolean isDisplay;

	public void execute() {
		List<CompositeMap> fields = (List<CompositeMap>) data;
		if (container instanceof Form) {
			fillForm(fields);
		}
		if (container instanceof GridColumn) {
			fillGrid(fields);
		}
	}

	private void fillGrid(List<CompositeMap> fields) {
		for (CompositeMap f : fields) {
			String string = this.getPrompt(f);
			GridColumn gc = new GridColumn();
			gc.setPrompt(string);
			String name = f.getString("name");
			name = name == null ? "" : name;
			gc.setName(name);
			if (this.isDisplay() == false) {
				String type = Util.getType(f);
				gc.setEditor(type);
			}
			container.addChild(gc);
		}

	}

	private void fillForm(List<CompositeMap> fields) {
		for (CompositeMap field : fields) {
			String name = (String) field.get("field");
			name = name == null ? field.getString("name") : name;
			name = name == null ? "" : name;
			AuroraComponent input = this.isDisplay() ? new Label()
					: new Input();
			String type = this.isDisplay() ? Label.Label : Util.getType(field);
			input.setType(type);
			input.setName(name);
			input.setPrompt(getPrompt(field));
			container.addChild(input);
		}
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public boolean isDisplay() {
		return isDisplay;
	}

	public void setDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}

}
