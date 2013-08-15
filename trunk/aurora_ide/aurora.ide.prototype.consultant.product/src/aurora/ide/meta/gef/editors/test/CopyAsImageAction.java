package aurora.ide.meta.gef.editors.test;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import aurora.ide.meta.gef.editors.parts.ViewDiagramPart;

public class CopyAsImageAction extends SelectionAction {

	public static final String ID = "aurora.ide.meta.gef.editors.actions.CopyAsImageAction";

	private GraphicalEditor editor;

	public CopyAsImageAction(GraphicalEditor part) {
		super(part);
		editor = part;
		this.setId(ID);
		this.setText("Copy As Image");
	}

	public void run() {

		ScalableRootEditPart rootEditPart = (ScalableRootEditPart) getViewer()
				.getRootEditPart();
		double zoom = rootEditPart.getZoomManager().getZoom();

		try {
			// IFigure figure = rootEditPart
			// .getLayer(LayerConstants.PRINTABLE_LAYERS);
			//
			// Rectangle rectangle = figure.getBounds();
			IFigure figure = getRootFigure(rootEditPart);
			Rectangle rectangle = calBounds(figure);

			Image image = new Image(Display.getDefault(), rectangle.width,
					rectangle.height);
			GC gc = new GC(image);
			SWTGraphics graphics = new SWTGraphics(gc);
			figure.paint(graphics);

			Clipboard clipboard = new Clipboard(Display.getDefault());
			clipboard.setContents(new Object[] { image.getImageData() },
					new Transfer[] { ImageTransfer.getInstance() });
			image.dispose();
			gc.dispose();

		} catch (Exception ex) {
			// ex.printStackTrace();
		} finally {
			rootEditPart.getZoomManager().setZoom(zoom);
		}
	}

	protected GraphicalViewer getViewer() {
		return (GraphicalViewer) editor.getAdapter(GraphicalViewer.class);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	public IFigure getRootFigure(ScalableRootEditPart rootEditPart) {
		List children = rootEditPart.getChildren();
		if (children.size() > 0) {
			Object object = children.get(0);
			if (object instanceof ViewDiagramPart) {
				return ((ViewDiagramPart) object).getFigure();
			}
		}
		return rootEditPart.getLayer(LayerConstants.PRINTABLE_LAYERS);
	}

	public Rectangle calBounds(IFigure figure) {
		Rectangle r = new Rectangle(0, 0, 0, 0);
		List children = figure.getChildren();
		for (Object object : children) {
			if (object instanceof IFigure) {
				Rectangle b = ((IFigure) object).getBounds();
				r.union(b);
			}
		}
		return r.expand(10, 10);
	}
}
