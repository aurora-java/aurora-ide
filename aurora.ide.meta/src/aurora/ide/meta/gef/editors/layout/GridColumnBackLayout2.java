package aurora.ide.meta.gef.editors.layout;

import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.GridPart;
import aurora.plugin.source.gen.screen.model.Grid;

import java.util.List;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;

public class GridColumnBackLayout2 extends RowColBackLayout {
	final static private Insets padding = new Insets(0, 0, 0, 0);

	public GridColumnBackLayout2() {
		location.x = 0;
		// location.y = -1;
		this.setPadding(padding);
	}

	protected void calculateChildLocation() {
		for (int i = 0; i < realRow; i++) {
			for (int j = 0; j < col; j++) {
				ComponentPart rp = childs[i][j];
				if (rp == null)
					return;
				Rectangle rr = this.partMap.get(rp);
				rr.setLocation(location);
				// rr.setHeight(selfRectangle.height - 25);
				location.x += maxColWidths[j];

			}
			location.x = 0 + selfRectangle.getTopLeft().x;
			location.y = location.y + maxRowHights[i] + 0;
		}
	}

	protected Rectangle calculateRectangle(ComponentPart parent) {
		Rectangle selfRectangle = zero.getCopy().setLocation(
				parent.getFigure().getBounds().getLocation());
		List children = parent.getChildren();
		for (int i = 0; i < children.size(); i++) {
			ComponentPart cp = (ComponentPart) children.get(i);
			selfRectangle.union(cp.getFigure().getBounds().getCopy());
		}

		if (!selfRectangle.isEmpty()) {
			// return selfRectangle.expand(1, 1);
			return selfRectangle;
		}
		selfRectangle = toDraw2d(parent.getComponent().getBoundsCopy());
		selfRectangle.setHeight(calculateHeight(parent));
		return selfRectangle;
	}

	private int _depth = 0;

	private int calculateHeight(ComponentPart cp) {
		EditPart parent = cp.getParent();
		if(parent instanceof ComponentPart == false){
			return _depth;
		}
		if (parent instanceof GridPart) {
			int h = ((GridPart) parent).getFigure().getBounds().height;
			Grid grid = ((GridPart) parent).getGrid();
			h = h == 0 ? grid.getBoundsCopy().height : h;
			int nh = grid.hasNavBar() ? 25 : 0;
			int th = grid.hasToolbar() ? 25 : 0;
			return h - nh - th - _depth;
		} else {
			_depth += 25;
			return calculateHeight((ComponentPart) parent);
		}
	}

}
