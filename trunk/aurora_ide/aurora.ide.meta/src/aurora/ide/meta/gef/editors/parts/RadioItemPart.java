package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.jface.viewers.TextCellEditor;

import aurora.ide.meta.gef.editors.figures.InputField;
import aurora.ide.meta.gef.editors.figures.PromptCellEditorLocator;
import aurora.ide.meta.gef.editors.figures.RadioItemFigure;
import aurora.ide.meta.gef.editors.figures.SimpleDataCellEditorLocator;
import aurora.ide.meta.gef.editors.policies.NodeDirectEditManager;
import aurora.plugin.source.gen.screen.model.RadioItem;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class RadioItemPart extends InputPart {
	protected IFigure createFigure() {
		RadioItemFigure cbf = new RadioItemFigure();
		RadioItem model = (RadioItem) getModel();
		cbf.setModel(model);
		return cbf;
	}

	public RadioItemFigure getFigure() {
		return (RadioItemFigure) super.getFigure();
	}

	public RadioItem getModel() {
		return (RadioItem) super.getModel();
	}

	protected void performSimpleDataDirectEditRequest(InputField figure) {
		NodeDirectEditManager manager = new aurora.ide.meta.gef.editors.policies.NodeDirectEditManager(
				this, TextCellEditor.class, new SimpleDataCellEditorLocator(
						figure), ComponentProperties.text);
		manager.show();
	}

	protected void performPromptDirectEditRequest(InputField figure) {
		NodeDirectEditManager manager = new aurora.ide.meta.gef.editors.policies.NodeDirectEditManager(
				this, TextCellEditor.class,
				new PromptCellEditorLocator(figure), ComponentProperties.text);
		manager.show();
	}

	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType())
				&& req instanceof LocationRequest) {
			performEditStyledStringText(ComponentProperties.text);
		} else
		if (req.getType().equals(RequestConstants.REQ_DIRECT_EDIT)
				&& req instanceof DirectEditRequest) {
			Point location = ((DirectEditRequest) req).getLocation();
			InputField figure = (InputField) getFigure();
			Rectangle dataBounds = getPromptBounds();
			if (dataBounds.contains(location) == false) {
				performPromptDirectEditRequest(figure);
			} else {
				performSimpleDataDirectEditRequest(figure);
			}
		} else
			super.performRequest(req);

	}
}
