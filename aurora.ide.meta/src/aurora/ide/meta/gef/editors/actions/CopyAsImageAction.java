package aurora.ide.meta.gef.editors.actions;

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
			IFigure figure = rootEditPart
					.getLayer(LayerConstants.PRINTABLE_LAYERS);

			Rectangle rectangle = figure.getBounds();

			Image image = new Image(Display.getDefault(), rectangle.width + 50,
					rectangle.height + 50);
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

}
