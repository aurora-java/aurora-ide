package aurora.ide.meta.gef.editors.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.figures.GridColumnFigure;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.GridPart;
import aurora.ide.meta.gef.editors.parts.NavbarPart;
import aurora.ide.meta.gef.editors.parts.ToolbarPart;

public class GridBackLayout2 extends RowColBackLayout {

	
	private int maxDepth = 0;
	private Map<ComponentPart, Integer> depthMap = new HashMap<ComponentPart, Integer>();

	final static private Insets padding = new Insets(-1, 0, 0, 0);

	private GridPart gridPart;
	private ToolbarPart toolbarPart;
	private NavbarPart navbarPart;
	private int navbarHight;

	public GridBackLayout2(){
		location.x = 1;
//		location.y = -1;
		this.setPadding(padding);
	}
	
	
	
	public Rectangle layout(ComponentPart parent) {
		
		init(parent);
		gridPart = (GridPart)host;
		navbarPart = getNavbarPart();
		toolbarPart = getToolbarPart();
		if(!this.hasToolbar()){
			location.y = location.y-25;
		}
		titleHight = this.hasToolbar() ? 25 : 0;
		navbarHight = navbarPart == null ? 0 : 25;
		
		if (getRealRow() == 0) {
			Rectangle calculateRectangle = calculateRectangle(parent);
			layoutToolbar(calculateRectangle);
			layoutNavbar(calculateRectangle);
			return calculateRectangle;
		}
		calculateMaxWidthHight();
		calculateChildLocation();
		applyToChildren();
		calculateChildDepth(parent, 1);
		applyToALlChildCH(parent);
		Rectangle calculateRectangle = calculateRectangle(parent);
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
	public List getChildren() {
		return this.getColumns(this.getHost());
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

	private void applyToALlChildCH(ComponentPart parent) {
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
			applyToALlChildCH(cp);
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


	protected void calculateChildLocation() {
		for (int i = 0; i < realRow; i++) {
			for (int j = 0; j < col; j++) {
				ComponentPart rp = childs[i][j];
				if (rp == null)
					return;
				Rectangle rr = this.partMap.get(rp);
				rr.setLocation(location);
				rr.setHeight(selfRectangle.height - titleHight - navbarHight);
				location.x += maxColWidths[j] + 0 - 1;
			}
			location.x = 0 + selfRectangle.getTopLeft().x;
			location.y = location.y + maxRowHights[i] + 0;
		}
	}

	protected Rectangle calculateRectangle(ComponentPart parent) {
		Rectangle selfRectangle = zero.getCopy().setLocation(
				parent.getFigure().getBounds().getLocation());
		List children = getColumns(parent);
		for (int i = 0; i < children.size(); i++) {
			ComponentPart cp = (ComponentPart) children.get(i);
			selfRectangle.union(cp.getFigure().getBounds().getCopy());
		}
		if (selfRectangle.width > this.selfRectangle.width) {
			// return selfRectangle.expand(1, 1);
			return this.selfRectangle.getCopy().setWidth(selfRectangle.width+50);
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
