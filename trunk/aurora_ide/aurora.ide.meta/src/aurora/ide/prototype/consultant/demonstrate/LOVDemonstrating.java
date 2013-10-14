package aurora.ide.prototype.consultant.demonstrate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.eclipse.swt.widgets.Shell;

import aurora.ide.meta.gef.editors.models.commands.ChangePropertyCommand;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.wizard.dialog.LovDialogInput;
import aurora.ide.meta.gef.editors.wizard.dialog.SysLovDialog;
import aurora.plugin.source.gen.screen.model.DemonstrateDS;
import aurora.plugin.source.gen.screen.model.DemonstrateData;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

public class LOVDemonstrating {
	private ComponentPart part;

	public LOVDemonstrating(ComponentPart part) {
		this.part = part;
	}

	public void demonstrating(Shell shell) {
		SysLovDialog lov = new SysLovDialog(shell,this);
		lov.setInput(parseItems());
		lov.open();
	}

	public void applyValue(String value) {
		ChangePropertyCommand command = new ChangePropertyCommand(
				part.getComponent(),
				ComponentInnerProperties.INPUT_SIMPLE_DATA, value);
		part.getViewer().getEditDomain().getCommandStack().execute(command);

	}

	private LovDialogInput parseItems() {
		DemonstrateData dd = (DemonstrateData) part.getComponent()
				.getPropertyValue(DemonstrateData.DEMONSTRATE_DATA);
		if(dd == null)
			return new LovDialogInput();
		String dsName = dd.getDemonstrateDSName();
		if (null != dsName && "".equals(dsName) == false) {
			DemonstrateDS demonstrateDS = DemonstrateDSManager.getInstance()
					.getDemonstrateDS(dsName);
			if (demonstrateDS != null)
				return parseItems(demonstrateDS.getData());
		}
		String data = dd.getDemonstrateData();
		return parseItems(data);
	}

	private LovDialogInput parseItems(String demonstrateDS) {
		if (demonstrateDS == null || "".equals(demonstrateDS))
			return null;

		StringReader sr = new StringReader(demonstrateDS);
		BufferedReader br = new BufferedReader(sr);
		String str = "";
		int col = 0;
		int row = 0;
		LovDialogInput input = new LovDialogInput();
		try {
			while ((str = br.readLine()) != null) {
				col = 0;
				String[] split = str.split(",");
				for (String s : split) {
					if (row == 1)
						input.addQueryHead(s);
					input.addColumn(col, s);
					col++;
				}
				row++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input;
	}
}
