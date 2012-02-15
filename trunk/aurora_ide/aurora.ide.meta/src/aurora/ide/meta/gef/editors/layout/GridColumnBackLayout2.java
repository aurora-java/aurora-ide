package aurora.ide.meta.gef.editors.layout;

import aurora.ide.meta.gef.editors.parts.ComponentPart;

import java.util.List;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

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
				rr.setHeight(selfRectangle.height - 25);
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
		selfRectangle = parent.getComponent().getBounds();
		return selfRectangle;
	}

}
