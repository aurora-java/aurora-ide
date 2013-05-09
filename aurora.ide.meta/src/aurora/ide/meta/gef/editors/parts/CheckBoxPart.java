package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.TextCellEditor;

import aurora.ide.meta.gef.editors.figures.CheckBoxFigure;
import aurora.ide.meta.gef.editors.figures.InputField;
import aurora.ide.meta.gef.editors.figures.SimpleDataCellEditorLocator;
import aurora.ide.meta.gef.editors.policies.NodeDirectEditManager;
import aurora.plugin.source.gen.screen.model.CheckBox;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class CheckBoxPart extends InputPart {
	protected IFigure createFigure() {
		CheckBoxFigure cbf = new CheckBoxFigure();
		CheckBox model = (CheckBox) getModel();
		cbf.setModel(model);
		return cbf;
	}

	public CheckBoxFigure getFigure() {
		return (CheckBoxFigure) super.getFigure();
	}

	public CheckBox getModel() {
		return (CheckBox) super.getModel();
	}

	protected void performSimpleDataDirectEditRequest(InputField figure) {
		NodeDirectEditManager manager = new aurora.ide.meta.gef.editors.policies.NodeDirectEditManager(
				this, TextCellEditor.class, new SimpleDataCellEditorLocator(
						figure), ComponentProperties.text);
		manager.show();
	}
}
