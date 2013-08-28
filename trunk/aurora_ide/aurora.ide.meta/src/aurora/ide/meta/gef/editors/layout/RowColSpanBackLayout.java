package aurora.ide.meta.gef.editors.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.plugin.source.gen.screen.model.RowCol;

public class RowColSpanBackLayout extends BackLayout {

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
	// protected ComponentPart[][] childs;
	protected int[] maxColWidths;
	protected int[] maxRowHights;
	protected int realRow;

	protected Map<ComponentPart, Rectangle> partMap;

	protected Map<Integer, Row> rowcol = new HashMap<Integer, Row>();

	private class Row {
		int l;
//		List<Node> nodes = new ArrayList<Node>(1000);
		Node[] nodes ;
		void add(Node n, int i) {
//			Object node = nodes.get(i);
//			if(node == null){
//				nodes.remove(i);
//			}
//			nodes.add(i, n);
			nodes[i] = n;
		}

		public int nowCol() {
			int size = this.nodes.length;
			for (int i = 0; i <size; i++) {
				Node node = nodes[i];
				if (node == null)
					return i ;

			}
			return size;
		}
	}

	private class Node {
		int row;
		int col;
		ComponentPart part;
		int rowspan;
		int colspan;

		Node(ComponentPart part, int row, int col) {
			this.part = part;
			this.row = row;
			this.col = col;
			this.rowspan = part.getComponent().getRowspan();
			this.colspan = part.getComponent().getColspan();
		}
	}

	protected void init(ComponentPart parent) {
		if (parent.getComponent() instanceof RowCol) {
			host = parent;
			rowCol = (RowCol) parent.getComponent();
			col = rowCol.getCol();
			if (col == 0)
				return;
			row = rowCol.getRow();
			Rectangle fBounds = parent.getFigure().getBounds();
			selfRectangle = fBounds.isEmpty() ? toDraw2d(rowCol.getBoundsCopy())
					: fBounds;
			// selfRectangle = rowCol.getBounds() ;
			titleHight = rowCol.getHeadHight();
			location.x = location.x + getPadding().left;
			location.y = location.y + titleHight + getPadding().top;
			location.translate(selfRectangle.getTopLeft());
			t_col = 0;
			t_row = 0;
			maxColWidths = new int[col];
			for (int i = 0; i < col; i++) {
				maxColWidths[i] = rowCol.getMinColWidth();
			}
			partMap = new HashMap<ComponentPart, Rectangle>();

			List children = this.getChildren();
			for (int i = 0; i < children.size(); i++) {
				ComponentPart ep = (ComponentPart) children.get(i);
				// if (t_col >= col) {
				nextRow(); 
				// }
				this.fillRowCol(ep, t_row, t_col);
				Rectangle layout = GraphLayoutManager.layout(ep);
				this.partMap.put(ep, layout);
				int cs = ep.getComponent().getColspan();
//				t_col += cs;
				t_col = getRow(t_row).nowCol();
			}
			// realRow = children.size() / col
			// + ((children.size() % col) == 0 ? 0 : 1);
			realRow = this.rowcol.size();
			if (realRow == 0)
				return;
			// childs = new ComponentPart[realRow][col];
			maxRowHights = new int[realRow];
			for (int i = 0; i < realRow; i++) {
				maxRowHights[i] = rowCol.getMinRowHeight();
			}

			// fillRowCol();
			// for (int i = 0; i < children.size(); i++) {
			// ComponentPart ep = (ComponentPart) children.get(i);
			// if (t_col == col) {
			// t_row++;
			// t_col = 0;
			// }
			// childs[t_row][t_col] = ep;
			// Rectangle layout = GraphLayoutManager.layout(ep);
			// this.partMap.put(ep, layout);
			// t_col++;
			// }
		}
	}

	private void nextRow() {
		if (t_col >= col) {
			t_row++;
			t_col = this.getRow(t_row).nowCol();
			nextRow();
		}
	}

	private Row getRow(int i) {
		Row r = this.rowcol.get(i);
		if (r == null) {
			r = new Row();
			r.l = i;
//			for (int j = 0; j < col; j++) {
//				r.nodes.add(null);
//			}
			r.nodes = new Node[col];
			rowcol.put(i, r);
		}
		return r;
	}

	private void fillRowCol(ComponentPart ep, int _row, int _col) {
		int cs = ep.getComponent().getColspan();
		int rs = ep.getComponent().getRowspan();
		Node n = new Node(ep, _row, _col);
		for (int k = _row; k < _row + rs; k++) {
			fillCol(n, k, _col, cs);
		}
		// if (_col >= col) {
		// _row++;
		// _col = 0;
		// }
		// Rectangle layout = GraphLayoutManager.layout(ep);
		// this.partMap.put(ep, layout);
		// _col += cs;
	}

	private void fillCol(Node n, int _row, int _col, int colspan) {
		Row r = this.getRow(_row);
		for (int j = _col; j < _col + colspan && j < col; j++) {
			r.add(n, j);
		}
	}

//	private void fillRowCol2() {
//		List children = this.getChildren();
//		int _col = 0;
//		int _row = 0;
//		for (int i = 0; i < children.size(); i++) {
//			ComponentPart ep = (ComponentPart) children.get(i);
//			int cs = ep.getComponent().getColspan();
//			int rs = ep.getComponent().getRowspan();
//			Row r = this.getRow(_row);
//			Node n = new Node(ep, _row, _col);
//			for (int k = _row; k < _row + rs - 1; k++) {
//				Row _r = this.getRow(k);
//				for (int j = _col; j < _col + cs && j < col; j++) {
//					_r.add(n, j);
//				}
//			}
//			for (int j = _col; j < _col + cs && j < col; j++) {
//				r.add(n, j);
//			}
//			if (_col >= col) {
//				_row++;
//				_col = 0;
//			}
//			Rectangle layout = GraphLayoutManager.layout(ep);
//			this.partMap.put(ep, layout);
//			_col += cs;
//		}
//	}

//	private int calRealRow() {
//		int row = 0;
//		int _col = col;
//		List children = this.getChildren();
//		for (Object object : children) {
//			if (object instanceof ComponentPart) {
//				int cs = ((ComponentPart) object).getComponent().getColspan();
//				_col = _col - cs;
//				if (_col <= 0) {
//					_col = col;
//					row++;
//				}
//			}
//		}
//		return row;
//	}

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

	private int getRowNodeHight(Node n, int row) {
		if (n == null)
			return rowCol.getMinRowHeight();
		int h = rowCol.getMinRowHeight();
		if (row == n.row + n.rowspan - 1) {
			Rectangle rr = this.partMap.get(n.part);
			int mmh = 0;
			for (int i = n.row; i < row; i++) {
				mmh += maxRowHights[i] + getPadding().top;
			}
			h = rr.height - mmh;
		}
		return h;
	}

	private int getColNodeWidth(Node n, int col) {
		if (n == null)
			return rowCol.getMinColWidth();
		int w = rowCol.getMinColWidth();
		if (col == n.col + n.colspan - 1) {
			Rectangle rr = this.partMap.get(n.part);
			int mmw = 0;
			for (int i = n.col; i < col; i++) {
				mmw += maxColWidths[i] + getPadding().left;
			}
			w = rr.width - mmw;
		}
		return w;
	}

	protected void calculateMaxWidthHight() {
		for (int i = 0; i < realRow; i++) {
			Row r = this.rowcol.get(i);
			for (int k = 0; k < r.nodes.length; k++) {
				Node n = r.nodes[k];
				maxRowHights[i] = Math.max(maxRowHights[i],
						getRowNodeHight(n, i));
			}
		}
		for (int j = 0; j < col; j++) {
			for (int k = 0; k < realRow; k++) {
				Row r = this.rowcol.get(k);
				if (r.nodes.length <= j) {
					break;
				}
				Node n = r.nodes[j];
				maxColWidths[j] = Math.max(maxColWidths[j],
						getColNodeWidth(n, j));
			}
		}
		// for (int i = 0; i < realRow; i++) {
		// for (int j = 0; j < col; j++) {
		// ComponentPart rp = childs[i][j];
		// if (rp == null)
		// break;
		// Rectangle rr = this.partMap.get(rp);
		// maxRowHights[i] = Math.max(maxRowHights[i], rr.height);
		// }
		// }
		// for (int j = 0; j < col; j++) {
		// for (int k = 0; k < realRow; k++) {
		// ComponentPart cp = childs[k][j];
		// if (cp == null)
		// break;
		// Rectangle cr = this.partMap.get(cp);
		// maxColWidths[j] = Math.max(maxColWidths[j], cr.width);
		// }
		// }
	}

	protected void calculateChildLocation() {
		for (int i = 0; i < realRow; i++) {
			for (int j = 0; j < col; j++) {
				Row r = this.rowcol.get(i);
				if (r.nodes.length <= j) {
					break;
				}
				Node n = r.nodes[j];
				if (n == null)
					continue;
				ComponentPart rp = n.part;
				if (rp == null)
					return;
				if (n.row == i && n.col == j) {
					Rectangle rr = this.partMap.get(rp);
					rr.setLocation(location);
				}
				location.x += maxColWidths[j] + getPadding().left;
			}
			location.x = getPadding().left + selfRectangle.getTopLeft().x;
			location.y = location.y + maxRowHights[i] + getPadding().top;
		}
	}

	protected Rectangle calculateRectangle(ComponentPart parent) {
		Rectangle selfRectangle = zero.getCopy().setLocation(
				parent.getFigure().getBounds().getLocation());
		List children = parent.getChildren();
		Rectangle modelRectangle = toDraw2d(parent.getComponent()
				.getBoundsCopy());
		if (children.size() == 0
				|| /* don't layout self yet */selfRectangle.equals(zero))
			return modelRectangle;
		for (int i = 0; i < children.size(); i++) {
			ComponentPart cp = (ComponentPart) children.get(i);
			selfRectangle.union(this.partMap.get(cp).getCopy());
		}
		Rectangle expand = selfRectangle.expand(5, 5);
		return new Rectangle(expand.x, expand.y, Math.max(expand.width,
				modelRectangle.width), Math.max(expand.height,
				modelRectangle.height));
	}

	private Rectangle calculateFormRectangle(ComponentPart parent) {
		Rectangle selfRectangle = zero.getCopy().setLocation(
				parent.getFigure().getBounds().getLocation());
		List children = parent.getChildren();
		for (int i = 0; i < children.size(); i++) {
			ComponentPart cp = (ComponentPart) children.get(i);
			selfRectangle.union(cp.getFigure().getBounds().getCopy());
		}

		if (selfRectangle.width > rowCol.getBoundsCopy().width) {
			// return selfRectangle.expand(1, 1);
			return selfRectangle.expand(5, 5);
		}
		// return
		// this.selfRectangle.getCopy().setSize(this.rowCol.getSize().width,selfRectangle.height).expand(0,
		// 5);
		int nw = Math.max(selfRectangle.width, rowCol.getBoundsCopy().width);
		int nh = Math.max(selfRectangle.height, rowCol.getBoundsCopy().height);
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
