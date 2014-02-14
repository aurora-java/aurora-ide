package aurora.ide.prototype.consultant.product.demonstrate;

import java.util.List;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;

import aurora.ide.meta.gef.editors.models.commands.ChangePropertyCommand;
import aurora.ide.swt.util.UWizard;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.Combox;
import aurora.plugin.source.gen.screen.model.DemonstrateBind;
import aurora.plugin.source.gen.screen.model.DemonstrateDS;
import aurora.plugin.source.gen.screen.model.DemonstrateData;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.Input;
import aurora.plugin.source.gen.screen.model.LOV;
import aurora.plugin.source.gen.screen.model.ToolbarButton;
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
		String componentType = ac.getComponentType();
		if (GridColumn.GRIDCOLUMN.equals(componentType)) {
			GridColumn gc = (GridColumn) ac;
			if (Input.Combo.equals(gc.getEditor())) {
				return new DemonstrateDSWizard(shell, this);
			}
			if (Input.LOV.equals(gc.getEditor())) {
				return new DemonstrateLOVWizard(shell, this);
			}
		}
		if (ToolbarButton.TOOLBAR_BUTTON.equals(componentType)
				|| Button.BUTTON.equals(componentType)
				|| GridColumn.GRIDCOLUMN.equals(componentType)) {
			return new DemonstrateOpeningWizard(shell, this);
		}
		if (Input.LOV.equals(componentType)) {
			return new DemonstrateLOVWizard(shell, this);
		}
		if (Input.Combo.equals(componentType)) {
			return new DemonstrateDSWizard(shell, this);
		}
		return new DemonstrateDSWizard(shell, this);
	}

	public boolean isWillDemonstrate() {
		String ct = ac.getComponentType();
		return ToolbarButton.TOOLBAR_BUTTON.equals(ct)
				|| Button.BUTTON.equals(ct) || GridColumn.GRIDCOLUMN.equals(ct)
				|| LOV.LOV.equals(ct) || Combox.Combo.equals(ct);
	}

	@SuppressWarnings("unchecked")
	public DemonstrateData getDemonstrateData() {
		Object propertyValue = ac
				.getPropertyValue(DemonstrateData.DEMONSTRATE_DATA);
		if (propertyValue instanceof DemonstrateData) {
			AuroraComponent cloneObject = cloneObject((AuroraComponent) propertyValue);

			List<DemonstrateBind> inputs = (List<DemonstrateBind>) ((AuroraComponent) propertyValue)
					.getPropertyValue(DemonstrateBind.BIND_COMPONENT);
			List<DemonstrateBind> cloneInputs = (List<DemonstrateBind>) ((AuroraComponent) cloneObject)
					.getPropertyValue(DemonstrateBind.BIND_COMPONENT);
			if (inputs != null)
				for (int i = 0; i < inputs.size(); i++) {
					DemonstrateBind demonstrateBind = inputs.get(i);
					AuroraComponent bindModel = demonstrateBind.getBindModel();
					if (bindModel != null) {
						cloneInputs.get(i).setBindModel(bindModel);
					}
				}
			return (DemonstrateData) cloneObject;

		} else {
			return getDefaultData();
		}
	}

	private DemonstrateData getDefaultData() {
		DemonstrateData demonstrateData = new DemonstrateData();
		String ct = ac.getComponentType();
		if (LOV.LOV.equals(ct)) {
			DemonstrateDS ds = lovDemonstrateDS();
			demonstrateData.setDemonstrateData(ds.getData());
			demonstrateData.setDemonstrateDSName("");
		}
		if (Combox.Combo.equals(ct)) {
			DemonstrateDS ds = comboxDemonstrateDS();
			demonstrateData.setDemonstrateData(ds.getData());
			demonstrateData.setDemonstrateDSName("");
		}
		return demonstrateData;
	}

	private AuroraComponent cloneObject(AuroraComponent ac) {
		Object2CompositeMap o2c = new Object2CompositeMap();
		CompositeMap map = o2c.createCompositeMap(ac);
		CompositeMap2Object c2o = new CompositeMap2Object();
		AuroraComponent createObject = c2o.createObject(map);
		return createObject;
	}

	static public DemonstrateDS comboxDemonstrateDS() {
		DemonstrateDS dds = new DemonstrateDS(
				Messages.DemonstrateSettingManager_0,
				Messages.DemonstrateSettingManager_1);
		return dds;
	}

	static final String s = /*
							 * Messages.DemonstrateSettingManager_2 + "\n" +
							 */Messages.DemonstrateSettingManager_4
			+ "\n" //$NON-NLS-2$ //$NON-NLS-4$
			+ Messages.DemonstrateSettingManager_6
			+ "\n" + Messages.DemonstrateSettingManager_8 + "\n" + Messages.DemonstrateSettingManager_10; //$NON-NLS-2$ //$NON-NLS-4$

	static public DemonstrateDS lovDemonstrateDS() {
		DemonstrateDS dds = new DemonstrateDS(
				Messages.DemonstrateSettingManager_11, s);
		return dds;

	}

	public void applyDemonData(DemonstrateData data) {
		ChangePropertyCommand command = new ChangePropertyCommand(ac, "" //$NON-NLS-1$
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

	public AuroraComponent getAuroraComponent() {
		return ac;
	}

}
