package aurora.ide.prototype.consultant.demonstrate;

import org.eclipse.swt.widgets.Shell;

import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.Combox;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.LOV;

public class Demonstrating {
	private ComponentPart part;

	public Demonstrating(ComponentPart part) {
		this.part = part;
	}

	public void demonstrating(Shell shell) {
		if (LOV.LOV.equals(getComponentType())) {
			new LOVDemonstrating(part).demonstrating(shell);
		}
		if (Combox.Combo.equals(getComponentType())) {
			new ComboDemonstrating(part).demonstrating(shell);
		}
		if (Button.BUTTON.equals(getComponentType())) {
			new ButtonDemonstrating(part).demonstrating(shell);
		}
		if (GridColumn.GRIDCOLUMN.equals(getComponentType())) {
			new ButtonDemonstrating(part).demonstrating(shell);
		}
	}

	private String getComponentType() {
		return getModel().getComponentType();
	}

	private AuroraComponent getModel() {
		return part.getComponent();
	}
}
