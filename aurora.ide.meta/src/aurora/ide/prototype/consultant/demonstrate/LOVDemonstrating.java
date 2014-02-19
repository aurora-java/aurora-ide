package aurora.ide.prototype.consultant.demonstrate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import aurora.ide.meta.gef.editors.models.commands.ChangePropertyCommand;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.wizard.dialog.LovDialogInput;
import aurora.ide.meta.gef.editors.wizard.dialog.DemonstratingDialog;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.DemonstrateBind;
import aurora.plugin.source.gen.screen.model.DemonstrateDS;
import aurora.plugin.source.gen.screen.model.DemonstrateData;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

public class LOVDemonstrating {
	private ComponentPart part;
	private String feature = ComponentInnerProperties.INPUT_SIMPLE_DATA;

	public LOVDemonstrating(ComponentPart part) {
		this.part = part;
	}

	public void demonstrating(Shell shell) {
		DemonstratingDialog lov = new DemonstratingDialog(shell, this);
		lov.setInput(parseItems());
		lov.open();
	}

	public void applyValue(String value) {
		applyValue(part.getComponent(), value);
	}

	public void applyValue(AuroraComponent ac, String value) {
		ChangePropertyCommand command = new ChangePropertyCommand(ac, feature,
				value);
		part.getViewer().getEditDomain().getCommandStack().execute(command);
	}

	private LovDialogInput parseItems() {
		DemonstrateData dd = (DemonstrateData) part.getComponent()
				.getPropertyValue(DemonstrateData.DEMONSTRATE_DATA);
		if (dd == null)
			return new LovDialogInput(0, 0);
		String dsName = dd.getDemonstrateDSName();
		if (null != dsName && "".equals(dsName) == false) {
			DemonstrateDS demonstrateDS = DemonstrateDSManager.getInstance()
					.getDemonstrateDS(dsName);
			if (demonstrateDS != null)
				return parseItems(demonstrateDS.getData());
		}
		String data = dd.getDemonstrateData();
		LovDialogInput parseItems = parseItems(data);
		if(parseItems == null)
			return new LovDialogInput(0, 0);
		@SuppressWarnings("unchecked")
		List<DemonstrateBind> inputs = (List<DemonstrateBind>) dd
				.getPropertyValue(DemonstrateBind.BIND_COMPONENT);
		parseItems.setBindModels(inputs);
		return parseItems;
	}

	private LovDialogInput parseItems(String demonstrateDS) {
		if (demonstrateDS == null || "".equals(demonstrateDS))
			return new LovDialogInput(0,0);

		StringReader sr = new StringReader(demonstrateDS);
		BufferedReader br = new BufferedReader(sr);
		String str = "";
		List<String> rows = new ArrayList<String>();
		int col = 0;
		try {
			while ((str = br.readLine()) != null) {
				rows.add(str);
				String[] split = str.split(",");
				col = col > split.length ? col : split.length;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		LovDialogInput input = new LovDialogInput(col, rows.size());

		for (int i = 0; i < rows.size(); i++) {
			String s = rows.get(i);
			String[] split = s.split(",");
			for (int j = 0; j < col; j++) {
				input.add(j, i, j >= split.length ? "" : split[j]);
			}
		}

		// try {
		// while ((str = br.readLine()) != null) {
		// col = 0;
		// String[] split = str.split(",");
		// for (String s : split) {
		// if (row == 1)
		// input.addQueryHead(s);
		// input.addColumn(col, s);
		// col++;
		// }
		// row++;
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		return input;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}
}
