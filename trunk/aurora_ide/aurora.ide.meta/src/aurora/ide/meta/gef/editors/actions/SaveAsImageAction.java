package aurora.ide.meta.gef.editors.actions;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import aurora.ide.meta.gef.editors.parts.ViewDiagramPart;

public class SaveAsImageAction extends SelectionAction {

	public static final String ID = "aurora.ide.meta.gef.editors.actions.SaveAsImageAction";

	private GraphicalEditor editor;

	public SaveAsImageAction(GraphicalEditor part) {
		super(part);
		editor = part;
		this.setId(ID);
		this.setText("Save AS Image");
	}

	// public void update(IStructuredSelection sel){
	// }

	public void run() {

		ScalableRootEditPart rootEditPart = (ScalableRootEditPart) getViewer()
				.getRootEditPart();
		double zoom = rootEditPart.getZoomManager().getZoom();

		try {
			FileDialog dialog = new FileDialog(Display.getCurrent()
					.getActiveShell(), SWT.SAVE);
			dialog.setFileName(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor().getTitle()
					+ ".png");
			dialog.setOverwrite(true);
			dialog.setFilterExtensions(new String[] { "*.png", "*.bmp",
					"*.jpg", "*.jpeg" });
			String file = dialog.open();
			if (file != null) {
				IFigure figure =getRootFigure(rootEditPart);
				Rectangle rectangle = calBounds(figure);

				Image image = new Image(Display.getDefault(),
						rectangle.width, rectangle.height);
				GC gc = new GC(image);
				SWTGraphics graphics = new SWTGraphics(gc);
				figure.paint(graphics);
				ImageLoader loader = new ImageLoader();
				loader.data = new ImageData[] { image.getImageData() };

				if (file.endsWith(".bmp")) {
					loader.save(file, SWT.IMAGE_BMP);
				}
				// else if (file.endsWith(".gif")) {
				// loader.save(file, SWT.IMAGE_GIF);
				// }
				else if (file.endsWith(".jpg") || file.endsWith(".jpeg")) {
					loader.save(file, SWT.IMAGE_JPEG);
				} else if (file.endsWith(".png")) {
					loader.save(file, SWT.IMAGE_PNG);
				}
				// else if (file.endsWith(".tiff")) {
				// loader.save(file, SWT.IMAGE_TIFF);
				// }
				else {
					file = file + ".bmp";
					loader.save(file, SWT.IMAGE_BMP);
				}

				image.dispose();
				gc.dispose();
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
		} finally {
			rootEditPart.getZoomManager().setZoom(zoom);
		}
	}

	public IFigure getRootFigure(ScalableRootEditPart rootEditPart) {
		List children = rootEditPart.getChildren();
		if(children.size()>0){
			Object object = children.get(0);
			if(object instanceof ViewDiagramPart){
			return	((ViewDiagramPart) object).getFigure();
			}
		}
		return rootEditPart.getLayer(LayerConstants.PRINTABLE_LAYERS);
	}

	public Rectangle calBounds(IFigure figure) {
		Rectangle r = new Rectangle(0,0,0,0);
		List children = figure.getChildren();
		for (Object object : children) {
			if(object instanceof IFigure){
				Rectangle b = ((IFigure) object).getBounds();
				r.union(b);
			}
		}
		return r.expand(10, 10);
	}

	protected GraphicalViewer getViewer() {
		return (GraphicalViewer) editor.getAdapter(GraphicalViewer.class);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

}
