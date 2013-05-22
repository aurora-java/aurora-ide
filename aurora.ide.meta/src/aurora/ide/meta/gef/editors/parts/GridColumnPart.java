package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.jface.viewers.TextCellEditor;

import aurora.ide.meta.gef.editors.figures.GridColumnCellEditorLocator;
import aurora.ide.meta.gef.editors.figures.GridColumnFigure;
import aurora.ide.meta.gef.editors.layout.GridColumnBackLayout2;
import aurora.ide.meta.gef.editors.policies.ComponentDirectEditPolicy;
import aurora.ide.meta.gef.editors.policies.GridLayoutEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeDirectEditManager;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class GridColumnPart extends ContainerPart {

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
		if (req.getType().equals(RequestConstants.REQ_DIRECT_EDIT)
				&& req instanceof DirectEditRequest) {
			Point location = ((DirectEditRequest) req).getLocation();
			GridColumnFigure figure = this.getFigure();
			Rectangle bounds = figure.getBounds().getCopy();
			figure.translateToAbsolute(bounds);
			int columnHight = figure.getColumnHight();
			int _y = location.y - bounds.y - columnHight;
			int idx = _y <= 0 ? 0 : _y / GridColumnFigure.ROW_HEIGHT+1;
			if (idx == 0) {
				performPromptDirectEditRequest(figure);
			} else {
				performSimpleDataDirectEditRequest(figure, idx);
			}
		} else
			super.performRequest(req);
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

}
