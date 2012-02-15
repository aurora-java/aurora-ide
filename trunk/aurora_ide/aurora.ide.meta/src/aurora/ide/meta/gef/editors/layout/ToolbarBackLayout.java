package aurora.ide.meta.gef.editors.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.NavbarPart;
import aurora.ide.meta.gef.editors.parts.ToolbarPart;

public class ToolbarBackLayout extends BackLayout {

	private int col;
	private int row;
	private int maxColHight;
	private int titleHight;
	private Rectangle zero = new Rectangle(0, 0, 0, 0);

	private int lastCol = 0;
	private int lastRow = 0;
	private Rectangle selfRectangle = new Rectangle();

	private Point location = new Point();
	private Container box;
	private int t_col = 0;
	private int t_row = 0;
	private ComponentPart[][] childs;
	private int[] maxColWidths;
	private int[] maxRowHights;
	private int realRow;

	private Map<ComponentPart, Rectangle> partMap;

	public Rectangle layout(ComponentPart parent) {

		if (parent instanceof ToolbarPart || parent instanceof NavbarPart) {
			box = (Container) parent.getComponent();
			col = 100;
			row = 1;
			Rectangle fBounds = parent.getFigure().getBounds();
			selfRectangle = fBounds.isEmpty() ? box.getBounds() : fBounds;
			titleHight = 2;
			location.x = 2;
			location.y = titleHight + 0;
			location.translate(selfRectangle.getTopLeft());
			t_col = 0;
			t_row = 0;
			maxColWidths = new int[col];
			for (int i = 0; i < col; i++) {
				maxColWidths[i] = 0;
			}
			partMap = new HashMap<ComponentPart, Rectangle>();
		}

		List children = parent.getChildren();
		realRow = children.size() / col
				+ ((children.size() % col) == 0 ? 0 : 1);
		Rectangle calculateRectangle = calculateRectangle(parent);
		if (realRow == 0) {
			return calculateRectangle;
		}
		childs = new ComponentPart[realRow][col];
		maxRowHights = new int[realRow];
		for (int i = 0; i < realRow; i++) {
			maxRowHights[i] = 0;
		}
		for (int i = 0; i < children.size(); i++) {
			ComponentPart ep = (ComponentPart) children.get(i);
			if (t_col == col) {
				t_row++;
				t_col = 0;
			}
			childs[t_row][t_col] = ep;

			Rectangle layout = GraphLayoutManager.layout(ep);
			this.partMap.put(ep, layout);
			// layout = newChildLocation(layout);
			t_col++;
			// applyToFigure(ep, layout);
		}
		calculateMaxWidthHight();
		calculateChildLocation();
		for (int i = 0; i < children.size(); i++) {
			ComponentPart ep = (ComponentPart) children.get(i);
			Rectangle layout = this.partMap.get(ep);
			applyToFigure(ep, layout);
		}
		calculateRectangle = calculateRectangle(parent);
		return calculateRectangle;
	}

	private void calculateMaxWidthHight() {
		for (int i = 0; i < realRow; i++) {
			for (int j = 0; j < col; j++) {
				ComponentPart rp = childs[i][j];
				if (rp == null)
					break;
				Rectangle rr = this.partMap.get(rp);
				maxRowHights[i] = Math.max(maxRowHights[i], rr.height);
			}
		}
		for (int j = 0; j < col; j++) {
			for (int k = 0; k < realRow; k++) {
				ComponentPart cp = childs[k][j];
				if (cp == null)
					break;
				Rectangle cr = this.partMap.get(cp);
				maxColWidths[j] = Math.max(maxColWidths[j], cr.width);
			}
		}
	}

	private void calculateChildLocation() {
		for (int i = 0; i < realRow; i++) {
			for (int j = 0; j < col; j++) {
				ComponentPart rp = childs[i][j];
				if (rp == null)
					return;
				Rectangle rr = this.partMap.get(rp);
				rr.setLocation(location);
				location.x += maxColWidths[j] + 0;
			}
			location.x = 0 + selfRectangle.getTopLeft().x;
			location.y = location.y + maxRowHights[i] + 0;
		}
	}

	private Rectangle calculateRectangle(ComponentPart parent) {
		return this.selfRectangle.getCopy();
	}

}
