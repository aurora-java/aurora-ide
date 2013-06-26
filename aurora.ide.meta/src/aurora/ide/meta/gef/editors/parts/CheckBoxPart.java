package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.LocationRequest;
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

	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType())
				&& req instanceof LocationRequest) {
			Point location = ((LocationRequest) req).getLocation();
			// InputField figure = (InputField) getFigure();
			Rectangle dataBounds = getPromptBounds();
			if (dataBounds.contains(location) == false) {
				performEditStyledStringText(ComponentProperties.prompt);
			} else {
				performEditStyledStringText(ComponentProperties.text);
			}
		} else
			super.performRequest(req);
	}
}
