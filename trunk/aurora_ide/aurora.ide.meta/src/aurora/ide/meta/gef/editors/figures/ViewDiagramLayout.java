package aurora.ide.meta.gef.editors.figures;

import java.util.List;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.layout.ScreenGraphLayout;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.ContainerPart;
import aurora.ide.meta.gef.editors.parts.GraphAnimation;

public class ViewDiagramLayout extends AbstractLayout {

	private ComponentPart diagram;

	public ViewDiagramLayout(boolean b, ComponentPart diagram) {
		this.diagram = diagram;
	}

	public void layout(IFigure parent) {

		GraphAnimation.recordInitialState(parent);
		if (GraphAnimation.playbackState(parent))
			return;
		ScreenGraphLayout ly = new ScreenGraphLayout((ContainerPart) diagram);
		ly.layout();
		parent.repaint();
	}

	@Override
	protected Dimension calculatePreferredSize(IFigure container, int wHint,
			int hHint) {
		container.validate();
		List children = container.getChildren();
		Rectangle result = new Rectangle().setLocation(container
				.getClientArea().getLocation());
		for (int i = 0; i < children.size(); i++)
			result.union(((IFigure) children.get(i)).getBounds());
		result.resize(container.getInsets().getWidth(), container.getInsets()
				.getHeight());
		return result.getSize();
	}
}
