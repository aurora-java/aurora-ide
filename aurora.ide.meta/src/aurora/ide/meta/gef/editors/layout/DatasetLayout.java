package aurora.ide.meta.gef.editors.layout;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.meta.gef.editors.parts.DatasetDiagramPart;

public class DatasetLayout extends AbstractLayout {
	private static final Insets PADDING = new Insets(5, 5, 0, 2);
	private Point lastLocation = new Point();
	private DatasetDiagramPart dsDiagram;

	public DatasetLayout(DatasetDiagramPart datasetDiagramPart) {
		dsDiagram = datasetDiagramPart;
	}

	public void layout(IFigure container) {
		int line = 1;
		lastLocation = newLine(0, line);
		boolean reLayout = true;
		List children = container.getChildren();
		// Rectangle bounds = container.getBounds();
		for (Iterator iterator = children.iterator(); iterator.hasNext();) {
			IFigure f = (IFigure) iterator.next();
			lastLocation.translate(PADDING.left, 0);

			Dimension size = f.getPreferredSize();
			if (lastLocation.x + size.width >= container.getSize().width) {
				lastLocation = newLine(f.getSize().height+lastLocation.y, line);
				line++;
				lastLocation.translate(PADDING.left, 0);
			}
			f.setLocation(lastLocation);
			lastLocation.translate(size.width, 0);
		}
		if (reLayout) {
			Composite parent = dsDiagram.getViewer().getControl().getParent();
			GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
			layoutData.heightHint = line * 25;
			parent.setLayoutData(layoutData);
			parent.getParent().layout();
		}
	}

	private Point newLine(int y, int line) {
		return new Point().translate(0, y + PADDING.top );
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
