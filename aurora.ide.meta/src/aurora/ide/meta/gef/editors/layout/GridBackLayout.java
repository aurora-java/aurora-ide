package aurora.ide.meta.gef.editors.layout;

import aurora.ide.meta.gef.editors.figures.GridColumnFigure;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.GridPart;
import aurora.ide.meta.gef.editors.parts.NavbarPart;
import aurora.ide.meta.gef.editors.parts.ToolbarPart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class GridBackLayout extends BackLayout {

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
	private int maxDepth = 0;
	private Map<ComponentPart, Integer> depthMap = new HashMap<ComponentPart, Integer>();

	private Map<ComponentPart, Rectangle> partMap;
	private GridPart gridPart;
	private ToolbarPart toolbarPart;
	private NavbarPart navbarPart;
	private int navbarHight;

	public Rectangle layout(ComponentPart parent) {

		if (parent instanceof GridPart) {
			gridPart = (GridPart) parent;
			box = (Container) parent.getComponent();
			col = 100;
			row = 1;
			Rectangle fBounds = parent.getFigure().getBounds();
			selfRectangle = fBounds.isEmpty() ? box.getBounds() : fBounds;
			titleHight = this.hasToolbar() ? 25 : 0;
			location.x = 0;
			location.y = titleHight;
			location.translate(selfRectangle.getTopLeft());
			t_col = 0;
			t_row = 0;
			maxColWidths = new int[col];
			for (int i = 0; i < col; i++) {
				maxColWidths[i] = 0;
			}
			partMap = new HashMap<ComponentPart, Rectangle>();
			navbarPart = getNavbarPart();
			toolbarPart = getToolbarPart();
			navbarHight = navbarPart == null ? 0 : 25;
		}

		List children = getColumns(parent);

		realRow = children.size() / col
				+ ((children.size() % col) == 0 ? 0 : 1);
		Rectangle calculateRectangle = calculateRectangle(parent);
		if (realRow == 0) {
			layoutToolbar(calculateRectangle);
			layoutNavbar(calculateRectangle);
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
		calculateChildDepth(parent, 1);
		applyToAllChildCH(parent);

		calculateRectangle = calculateRectangle(parent);
		layoutToolbar(calculateRectangle);
		layoutNavbar(calculateRectangle);
		return calculateRectangle;
	}

	private void layoutToolbar(Rectangle calculateRectangle) {
		Point location = new Point();
		location.translate(selfRectangle.getTopLeft());
		if (this.toolbarPart != null) {
			Rectangle layout = GraphLayoutManager.layout(toolbarPart);
			Rectangle setLocation = layout.getCopy().setLocation(location);
			setLocation.setWidth(calculateRectangle.width);
			this.applyToFigure(toolbarPart, setLocation);
		}
	}

	private void layoutNavbar(Rectangle calculateRectangle) {
		Point bottomLeft = selfRectangle.getBottomLeft();
		Point setY = bottomLeft.getCopy().setY(bottomLeft.y - 25);
		if (this.navbarPart != null) {
			Rectangle layout = GraphLayoutManager.layout(navbarPart);
			Rectangle setLocation = layout.getCopy().setLocation(setY);
			setLocation.setWidth(calculateRectangle.width);
			this.applyToFigure(navbarPart, setLocation);
		}
	}

	private ToolbarPart getToolbarPart() {
		List children = this.gridPart.getChildren();
		for (Iterator iterator = children.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			if (object instanceof ToolbarPart) {
				return (ToolbarPart) object;
			}
		}
		return null;
	}

	private NavbarPart getNavbarPart() {
		List children = this.gridPart.getChildren();
		for (Iterator iterator = children.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			if (object instanceof NavbarPart) {
				return (NavbarPart) object;
			}
		}
		return null;
	}

	private List getColumns(ComponentPart parent) {
		List<ComponentPart> columns = new ArrayList<ComponentPart>();
		List children = parent.getChildren();
		for (Iterator iterator = children.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			if (object instanceof NavbarPart || object instanceof ToolbarPart) {
				continue;
			}
			columns.add((ComponentPart) object);
		}
		return columns;
	}

	private void applyToAllChildCH(ComponentPart parent) {
		List children = getColumns(parent);
		int columnHight = 25;
		for (int i = 0; i < children.size(); i++) {
			ComponentPart cp = (ComponentPart) children.get(i);
			if (getColumns(cp).size() > 0) {
				((GridColumnFigure) cp.getFigure()).setColumnHight(columnHight);
			} else {
				Integer depth = depthMap.get(cp);
				int l = this.maxDepth - depth + 1;
				((GridColumnFigure) cp.getFigure())
						.setColumnHight((columnHight * l));
			}
			applyToAllChildCH(cp);
		}
	}

	private boolean hasToolbar() {
		return getToolbarPart() != null;
	}

	private boolean hasNavbar() {
		return this.getNavbarPart() != null;
	}

	private void calculateChildDepth(ComponentPart parent, int depth) {
		List children = getColumns(parent);
		for (int i = 0; i < children.size(); i++) {
			maxDepth = Math.max(maxDepth, depth);
			depthMap.put((ComponentPart) children.get(i), depth);
			int t = depth;
			t++;
			calculateChildDepth((ComponentPart) children.get(i), t);
		}
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
				rr.setHeight(selfRectangle.height - titleHight - navbarHight);
				location.x += maxColWidths[j];
			}
			location.x = 0 + selfRectangle.getTopLeft().x;
			location.y = location.y + maxRowHights[i] + 0;
		}
	}

	private Rectangle calculateRectangle(ComponentPart parent) {
		Rectangle selfRectangle = zero.getCopy().setLocation(
				parent.getFigure().getBounds().getLocation());
		List children = getColumns(parent);
		for (int i = 0; i < children.size(); i++) {
			ComponentPart cp = (ComponentPart) children.get(i);
			selfRectangle.union(cp.getFigure().getBounds().getCopy());
		}
		if (selfRectangle.width > this.selfRectangle.width) {
			// return selfRectangle.expand(1, 1);
			return this.selfRectangle.getCopy().setWidth(
					selfRectangle.width + 50);
		}
		// ScrollBar horizontalScrollBar = ((ScrollPane)
		// parent.getFigure()).getHorizontalScrollBar();
		// horizontalScrollBar.setBounds(
		// new Rectangle(selfRectangle.x,
		// selfRectangle.getBottomLeft().y - 25,
		// selfRectangle.width, 35));
		// horizontalScrollBar.setVisible(true);

		selfRectangle = parent.getComponent().getBounds();
		return this.selfRectangle.setWidth(selfRectangle.width);
	}

}
