package aurora.ide.meta.gef.editors.models.commands;

import java.util.List;

import org.eclipse.gef.commands.Command;

import uncertain.composite.CompositeMap;
import aurora.plugin.source.gen.screen.model.ScreenBody;

public class DropBMCommand extends Command {
	private ScreenBody diagram;

	public ScreenBody getDiagram() {
		return diagram;
	}

	public void setDiagram(ScreenBody diagram) {
		this.diagram = diagram;
	}

//	protected String getPrompt(CompositeMap field) {
//		return field != null ? field.getString("prompt", "prompt") : "prompt:";
//	}

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
