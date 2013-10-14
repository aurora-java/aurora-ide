package aurora.ide.prototype.consultant.demonstrate;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.meta.gef.editors.figures.InputField;
import aurora.ide.meta.gef.editors.figures.SimpleDataCellEditorLocator;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.plugin.source.gen.screen.model.DemonstrateDS;
import aurora.plugin.source.gen.screen.model.DemonstrateData;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

public class ComboDemonstrating {
	private ComponentPart part;

	public ComboDemonstrating(ComponentPart part) {
		this.part = part;
	}

	public void demonstrating(Shell shell) {
		ComboDirectEditManager manager = new ComboDirectEditManager(part,
				ComboBoxCellEditor.class, new SimpleDataCellEditorLocator(
						(InputField) part.getFigure()),
				ComponentInnerProperties.INPUT_SIMPLE_DATA);
		manager.setItem(parseItems());
		manager.show();
	}

	private String[] parseItems() {
		DemonstrateData dd = (DemonstrateData) part.getComponent()
				.getPropertyValue(DemonstrateData.DEMONSTRATE_DATA);
		if(dd == null)
			return new String[0];
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

	private String[] parseItems(String demonstrateDS) {
		if (demonstrateDS == null || "".equals(demonstrateDS))
			return new String[0];
		String result = demonstrateDS.replaceAll("\r", "");
		result = result.replaceAll("\n", "");
		return result.split(",");
	}
}
