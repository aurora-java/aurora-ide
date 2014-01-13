package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TextCellEditor;

import aurora.ide.meta.gef.editors.figures.GridColumnCellEditorLocator;
import aurora.ide.meta.gef.editors.figures.GridColumnFigure;
import aurora.ide.meta.gef.editors.layout.GridColumnBackLayout2;
import aurora.ide.meta.gef.editors.models.commands.ChangeTextStyleCommand;
import aurora.ide.meta.gef.editors.policies.ComponentDirectEditPolicy;
import aurora.ide.meta.gef.editors.policies.GridLayoutEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeDirectEditManager;
import aurora.ide.meta.gef.editors.wizard.dialog.TextEditDialog;
import aurora.ide.prototype.consultant.demonstrate.DemonstrateEditorMode;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.StyledStringText;
import aurora.plugin.source.gen.screen.model.properties.ComponentFSDProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class SysLovGridColumnPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		GridColumnFigure figure = new GridColumnFigure();
		figure.setModel(getModel());
		return figure;
	}

	public GridColumn getModel() {
		return (GridColumn) super.getModel();
	}

	public GridColumnFigure getFigure() {
		return (GridColumnFigure) super.getFigure();
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new GridLayoutEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new ComponentDirectEditPolicy());
	}

	protected void refreshVisuals() {
		super.refreshVisuals();

		String stringPropertyValue = getModel().getStringPropertyValue(
				ComponentFSDProperties.FSD_MEANING);
		if ("".equals(stringPropertyValue) || null == stringPropertyValue) {
			getFigure().setToolTip(null);
		} else {
			getFigure().setToolTip(new Label(stringPropertyValue));
		}
	}

	@Override
	public int getResizeDirection() {
		return EAST_WEST;
	}

	public Rectangle layout() {
		GridColumnBackLayout2 layout = new GridColumnBackLayout2();
		return layout.layout(this);
	}

	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType())
				&& req instanceof LocationRequest) {
			int idx = getEditIndex((LocationRequest) req);
			String value = this.getModel().getStringPropertyValue(
					ComponentInnerProperties.GRID_COLUMN_SIMPLE_DATA + idx);
			if (this.getEditorMode() instanceof DemonstrateEditorMode) {
				((DemonstrateEditorMode) getEditorMode()).getDemonstratingDialog()
						.applyValue(value);
			}
			// if (idx == 0) {
			// performEditStyledStringText(ComponentProperties.prompt);
			// } else {
			// performEditStyledStringText(ComponentInnerProperties.GRID_COLUMN_SIMPLE_DATA
			// + idx);
			// }
		}
		if (req.getType().equals(RequestConstants.REQ_DIRECT_EDIT)
				&& req instanceof DirectEditRequest) {
			GridColumnFigure figure = this.getFigure();
			int idx = getEditIndex((LocationRequest) req);
			if (idx == 0) {
				performPromptDirectEditRequest(figure);
			} else {
				performSimpleDataDirectEditRequest(figure, idx);
			}
		} else
			super.performRequest(req);
	}

	protected int getEditIndex(LocationRequest req) {
		GridColumnFigure figure = this.getFigure();
		Point location = req.getLocation();
		Rectangle bounds = figure.getBounds().getCopy();
		figure.translateToAbsolute(bounds);
		int columnHight = figure.getColumnHight();
		int _y = location.y - bounds.y - columnHight;
		int idx = _y <= 0 ? 0 : _y / GridColumnFigure.ROW_HEIGHT + 1;
		return idx;
	}

	protected void performSimpleDataDirectEditRequest(GridColumnFigure figure,
			int idx) {
		NodeDirectEditManager manager = new aurora.ide.meta.gef.editors.policies.NodeDirectEditManager(
				this, TextCellEditor.class, new GridColumnCellEditorLocator(
						figure, idx),
				ComponentInnerProperties.GRID_COLUMN_SIMPLE_DATA + idx);
		manager.show();
	}

	protected void performPromptDirectEditRequest(GridColumnFigure figure) {
		NodeDirectEditManager manager = new aurora.ide.meta.gef.editors.policies.NodeDirectEditManager(
				this, TextCellEditor.class, new GridColumnCellEditorLocator(
						figure, 0), ComponentProperties.prompt);
		manager.show();
	}

	// private void performEditStyledStringText(String propertyID) {
	// TextEditDialog ted = new TextEditDialog(this.getViewer().getControl()
	// .getShell());
	// StyledStringText sst = new StyledStringText();
	// Object obj = this.getModel().getPropertyValue(
	// propertyID + ComponentInnerProperties.TEXT_STYLE);
	// if (obj instanceof StyledStringText)
	// sst = (StyledStringText) obj;
	// sst.setText(this.getModel().getStringPropertyValue(propertyID));
	// ted.setStyledStringText(sst);
	// if (Dialog.OK == ted.open()) {
	// sst = ted.getStyledStringText();
	// ChangeTextStyleCommand command = new ChangeTextStyleCommand(
	// getModel(), propertyID, sst.getText(), sst);
	// this.getViewer().getEditDomain().getCommandStack().execute(command);
	// }
	// }

}
