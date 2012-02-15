package aurora.ide.meta.gef.editors.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.RowCol;
import aurora.ide.meta.gef.editors.parts.ComponentPart;

public class RowColBackLayout extends BackLayout {

	private Insets padding = new Insets(0, 0, 0, 0);
	protected Rectangle zero = new Rectangle(0, 0, 0, 0);
	protected Rectangle selfRectangle = new Rectangle();
	protected Point location = new Point();
	protected RowCol rowCol;
	protected int col;
	protected int row;
	protected int titleHight;

	protected ComponentPart host;
	protected int t_col = 0;
	protected int t_row = 0;
	protected ComponentPart[][] childs;
	protected int[] maxColWidths;
	protected int[] maxRowHights;
	protected int realRow;

	protected Map<ComponentPart, Rectangle> partMap;

	protected void init(ComponentPart parent) {
		if (parent.getComponent() instanceof RowCol) {
			host = parent;
			rowCol = (RowCol) parent.getComponent();
			col = rowCol.getCol();
			if (col == 0)
				return;
			row = rowCol.getRow();
			Rectangle fBounds = parent.getFigure().getBounds();
			selfRectangle = fBounds.isEmpty() ? rowCol.getBounds() : fBounds;
			// selfRectangle = rowCol.getBounds() ;
			titleHight = rowCol.getHeadHight();
			location.x = location.x + getPadding().left;
			location.y = location.y + titleHight + getPadding().top;
			location.translate(selfRectangle.getTopLeft());
			t_col = 0;
			t_row = 0;
			maxColWidths = new int[col];
			for (int i = 0; i < col; i++) {
				maxColWidths[i] = 0;
			}
			partMap = new HashMap<ComponentPart, Rectangle>();

			List children = this.getChildren();
			realRow = children.size() / col
					+ ((children.size() % col) == 0 ? 0 : 1);
			if (realRow == 0)
				return;

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
				t_col++;
			}
		}
	}

	public Rectangle layout(ComponentPart parent) {
		init(parent);
		if (getRealRow() == 0 || this.col == 0) {
			return calculateRectangle(parent);
		}
		calculateMaxWidthHight();
		calculateChildLocation();
		applyToChildren();
		Rectangle calculateRectangle = calculateRectangle(parent);
		return calculateRectangle;
	}

	protected void applyToChildren() {
		List children = this.getChildren();
		for (int i = 0; i < children.size(); i++) {
			ComponentPart ep = (ComponentPart) children.get(i);
			Rectangle layout = this.partMap.get(ep);
			applyToFigure(ep, layout);
		}
	}

	public List getChildren() {
		return this.getHost().getChildren();
	}

	protected void calculateMaxWidthHight() {
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

	protected void calculateChildLocation() {
		for (int i = 0; i < realRow; i++) {
			for (int j = 0; j < col; j++) {
				ComponentPart rp = childs[i][j];
				if (rp == null)
					return;
				Rectangle rr = this.partMap.get(rp);
				rr.setLocation(location);
				location.x += maxColWidths[j] + getPadding().left;
			}
			location.x = getPadding().left + selfRectangle.getTopLeft().x;
			location.y = location.y + maxRowHights[i] + getPadding().top;
		}
	}

	protected Rectangle calculateRectangle(ComponentPart parent) {
		if (parent.getComponent() instanceof Form) {
			return this.calculateFormRectangle(parent);
		}
		Rectangle selfRectangle = zero.getCopy().setLocation(
				parent.getFigure().getBounds().getLocation());
		List children = parent.getChildren();
		for (int i = 0; i < children.size(); i++) {
			ComponentPart cp = (ComponentPart) children.get(i);
			selfRectangle.union(cp.getFigure().getBounds().getCopy());
		}
		if (!selfRectangle.isEmpty()) {
			return selfRectangle.expand(5, 5);
		}
		selfRectangle = parent.getComponent().getBounds();
		return selfRectangle;
	}

	private Rectangle calculateFormRectangle(ComponentPart parent) {
		Rectangle selfRectangle = zero.getCopy().setLocation(
				parent.getFigure().getBounds().getLocation());
		List children = parent.getChildren();
		for (int i = 0; i < children.size(); i++) {
			ComponentPart cp = (ComponentPart) children.get(i);
			selfRectangle.union(cp.getFigure().getBounds().getCopy());
		}

		if (selfRectangle.width > rowCol.getBounds().width) {
			// return selfRectangle.expand(1, 1);
			return selfRectangle.expand(5, 5);
		}
		// return
		// this.selfRectangle.getCopy().setSize(this.rowCol.getSize().width,selfRectangle.height).expand(0,
		// 5);
		int nw = Math.max(selfRectangle.width, rowCol.getBounds().width);
		int nh = Math.max(selfRectangle.height, rowCol.getBounds().height);
		return this.selfRectangle.getCopy().setSize(nw, nh).expand(0, 5);

	}

	public Insets getPadding() {
		return padding;
	}

	public void setPadding(Insets padding) {
		this.padding = padding;
	}

	public ComponentPart getHost() {
		return host;
	}

	public void setHost(ComponentPart host) {
		this.host = host;
	}

	protected int getRealRow() {
		return realRow;
	}

	protected Rectangle getSelfRectangle() {
		return selfRectangle;
	}

}
