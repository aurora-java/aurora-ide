package aurora.ide.meta.gef.editors.models.commands;

import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.Util;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;

public class BindDropModelCommand extends DropBMCommand {

	private Container container;
	private Object data;

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
			String object = f.getString("defaultEditor");
			if (Util.supportEditor(object) != null)
				gc.setEditor(Util.getType(f));
			container.addChild(gc);
		}

	}

	private void fillForm(List<CompositeMap> fields) {
		for (CompositeMap field : fields) {
			String name = (String) field.get("field");
			name = name == null ? field.getString("name") : name;
			name = name == null ? "" : name;
			Input input = new Input();
			input.setName(name);
			input.setPrompt(getPrompt(field));
			input.setType(Util.getType(field));
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

}
