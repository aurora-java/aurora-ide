package aurora.ide.meta.gef.editors.models.commands;

import java.util.List;

import org.eclipse.gef.commands.Command;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class DropBMCommand extends Command {
	private ViewDiagram diagram;

	public ViewDiagram getDiagram() {
		return diagram;
	}

	public void setDiagram(ViewDiagram diagram) {
		this.diagram = diagram;
	}

	protected String getPrompt(CompositeMap field) {
		return field != null ? field.getString("prompt") : "prompt:";
	}

	protected String getType(CompositeMap field) {
		String object = field.getString("defaultEditor");
		if (supportEditor(object) != null) {
			return object;
		} else {

			if ("java.lang.Long".equals(field.getString("datatype"))) {
				return Input.NUMBER;
			}
			if ("java.lang.String".equals(field.getString("datatype"))) {
				return Input.TEXT;
			}
			if ("java.util.Date".equals(field.getString("datatype"))) {
				return Input.CAL;
			}
		}
		return Input.TEXT;
	}

	protected String supportEditor(String object) {
		for (String t : Input.INPUT_TYPES) {
			if (t.equalsIgnoreCase(object))
				return t;
		}
		return null;
	}

	protected CompositeMap getField(String name, List<CompositeMap> fs) {
		for (CompositeMap f : fs) {
			if (name.equals(f.getString("name"))) {
				return f;
			}
		}
		return null;
	}

	public void redo() {
		this.execute();
	}

	public void undo() {
		// TODO
	}

}
