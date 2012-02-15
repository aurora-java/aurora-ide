package aurora.ide.meta.gef.editors.layout;

import java.util.List;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.ContainerPart;

public class ScreenGraphLayout extends BackLayout {
	private static final Insets PADDING = new Insets(8, 16, 8, 6);// 8,6,8,6
	private ContainerPart diagram;

	private Rectangle last = new Rectangle(0, 0, 0, 0);

	public ScreenGraphLayout(ContainerPart diagram) {
		this.diagram = diagram;
		last = diagram.getFigure().getBounds().getCopy().setSize(0, 0);
		PADDING.left = last.x + 16;
	}

	public void layout() {
		List children = getSortChildren();
		for (int i = 0; i < children.size(); i++) {
			ComponentPart ep = (ComponentPart) children.get(i);
			Rectangle layout = GraphLayoutManager.layout(ep);
			layout = newChildLocation(ep, layout);
			applyToFigure(ep, layout);
		}
	}

	private List getSortChildren() {
		List children = diagram.getChildren();
		// List sortChildren = new ArrayList(children);
		//
		// Collections.sort(sortChildren, new Comparator() {
		//
		// public int compare(Object o1, Object o2) {
		// ComponentPart ep1 = (ComponentPart) o1;
		// ComponentPart ep2 = (ComponentPart) o2;
		// Rectangle bound1 = ep1.getFigure().getBounds();
		// Rectangle bound2 = ep2.getFigure().getBounds();
		// Rectangle bounds1M = ep1.getComponent().getBounds();
		// Rectangle bounds2M = ep2.getComponent().getBounds();
		// Rectangle epl1 = bound1.isEmpty() ? bounds1M : bound1;
		// Rectangle epl2 = bound2.isEmpty() ? bounds2M : bound2;
		// return epl1.y - epl2.y;
		// }
		//
		// });
		return children;
	}

	public Rectangle layout(ComponentPart ep) {
		return null;
	}

	protected Rectangle newChildLocation(ComponentPart ep, Rectangle layout) {
		layout.x = PADDING.left;
		layout.y = last.y + last.height + PADDING.top;
		last = layout.getCopy();
		return layout.getCopy();
	}
}
