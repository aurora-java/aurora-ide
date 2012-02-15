package aurora.ide.meta.gef.editors.models.commands;

import java.util.List;

import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.TabBody;

/**
 */
public class CreateComponentCommand extends Command {
	protected Container container;

	protected AuroraComponent child;
	private AuroraComponent reference = null;

	public void setTargetContainer(Container container) {
		this.container = container;
	}

	public void setChild(AuroraComponent child) {
		this.child = child;
	}

	public boolean canExecute() {
		if (child instanceof TabBody)
			return false;
		String sType = container.getSectionType();
		if (Container.SECTION_TYPE_QUERY.equals(sType)) {
			if (!(child instanceof Input))
				return false;
		} else if (Container.SECTION_TYPE_BUTTON.equals(sType)) {
			if (!(child instanceof Button))
				return false;
		}
		return true;
	}

	public void execute() {
		if (reference == null)
			container.addChild(child);
		else {
			List<AuroraComponent> list = container.getChildren();
			int idx = list.indexOf(reference);
			container.addChild(child, idx);
		}
	}

	public String getLabel() {
		return "Create Component";
	}

	public void redo() {
		this.execute();
	}

	public void undo() {
		container.removeChild(child);
	}

	public void setReferenceModel(AuroraComponent reference) {
		this.reference = reference;
	}
}