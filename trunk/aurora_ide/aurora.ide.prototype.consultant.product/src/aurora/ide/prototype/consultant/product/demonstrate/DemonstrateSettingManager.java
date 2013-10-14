package aurora.ide.prototype.consultant.product.demonstrate;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;

import aurora.ide.meta.gef.editors.models.commands.ChangePropertyCommand;
import aurora.ide.swt.util.UWizard;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.Combox;
import aurora.plugin.source.gen.screen.model.DemonstrateData;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.LOV;
import aurora.plugin.source.gen.screen.model.io.CompositeMap2Object;
import aurora.plugin.source.gen.screen.model.io.Object2CompositeMap;

public class DemonstrateSettingManager {

	private AuroraComponent ac;
	private CommandStack commandStack;

	public DemonstrateSettingManager(AuroraComponent ac) {
		this.ac = ac;
	}

	public int openSettingWizard(Shell shell) {
		UWizard wd = createWizard(shell);
		int open = wd.open();
		return open;
	}

	private UWizard createWizard(Shell shell) {
		if (Button.BUTTON.equals(ac.getComponentType())
				|| GridColumn.GRIDCOLUMN.equals(ac.getComponentType())) {
			return new DemonstrateOpeningWizard(shell, this);
		} else {
			return new DemonstrateDSWizard(shell, this);
		}

	}

	public boolean isWillDemonstrate() {
		String ct = ac.getComponentType();
		return Button.BUTTON.equals(ct) || GridColumn.GRIDCOLUMN.equals(ct)
				|| LOV.LOV.equals(ct) || Combox.Combo.equals(ct);
	}

	public DemonstrateData getDemonstrateData() {
		Object propertyValue = ac
				.getPropertyValue(DemonstrateData.DEMONSTRATE_DATA);
		return propertyValue instanceof DemonstrateData ? (DemonstrateData) cloneObject((AuroraComponent) propertyValue)
				: new DemonstrateData();
	}

	private AuroraComponent cloneObject(AuroraComponent ac) {
		Object2CompositeMap o2c = new Object2CompositeMap();
		CompositeMap map = o2c.createCompositeMap(ac);
		CompositeMap2Object c2o = new CompositeMap2Object();
		AuroraComponent createObject = c2o.createObject(map);
		return createObject;
	}

	public void applyDemonData(DemonstrateData data) {
		ChangePropertyCommand command = new ChangePropertyCommand(ac, ""
				+ DemonstrateData.DEMONSTRATE_DATA, data);
		if (commandStack != null)
			getCommandStack().execute(command);
	}

	public CommandStack getCommandStack() {
		return commandStack;
	}

	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}
}
