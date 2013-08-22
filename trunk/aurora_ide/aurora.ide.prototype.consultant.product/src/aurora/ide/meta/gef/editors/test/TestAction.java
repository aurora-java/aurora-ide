package aurora.ide.meta.gef.editors.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.palette.DefaultPaletteViewerPreferences;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.parts.ExtAuroraPartFactory;
import aurora.ide.meta.gef.editors.parts.ViewDiagramPart;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.io.CompositeMap2Object;

public class TestAction extends Action implements
		IWorkbenchWindowActionDelegate {

	public static RGB toRGB(String color) {
		String[] split = color.split(",");
		return new RGB(Integer.valueOf(split[0]), Integer.valueOf(split[1]),
				Integer.valueOf(split[2]));
	}

	public static String toString(RGB color) {
		return color.red + "," + color.green + "," + color.blue;
	}

	private IWorkbenchWindow fWindow;

	public TestAction() {
		setEnabled(true);
	}

	public TestAction(IWorkbenchWindow window, String label) {
		this.fWindow = window;
		setText(label);
		setImageDescriptor(aurora.ide.prototype.consultant.product.Activator
				.getImageDescriptor("/icons/open.gif"));
		this.setToolTipText(label);
	}

	public void dispose() {
		fWindow = null;
	}

	public void init(IWorkbenchWindow window) {
		fWindow = window;
	}

	public void run(IAction action) {
		run();
	}

	@Override
	public void run() {

		// ConsultantVScreenEditor editor = new ConsultantVScreenEditor();
		// editor.setDiagram(diagram)
		ScreenBody diagram = null;

		CompositeMap loadFile = CompositeMapUtil.loadFile(new File(
				"/Users/shiliyan/Desktop/Uip_pages/Page1.uip"));
		if (loadFile != null) {
			CompositeMap2Object c2o = new CompositeMap2Object();
			diagram = c2o.createScreenBody(loadFile);
		} else {
			diagram = new ScreenBody();
		}

		GraphicalViewer viewer = new ScrollingGraphicalViewer();
		// Display.getDefault().get
		ScalableRootEditPart root = new ScalableRootEditPart();
		viewer.setRootEditPart(root);
		viewer.setEditPartFactory(new ExtAuroraPartFactory(new EditorMode() {
			public String getMode() {
				return None;
			}

			public boolean isForDisplay() {
				return false;
			}

			public boolean isForCreate() {
				return true;
			}

			public boolean isForUpdate() {
				return true;
			}

			public boolean isForSearch() {
				return false;
			}
		}));
		viewer.setContents(diagram);
		Font font = new Font(Display.getCurrent(),
				new DefaultPaletteViewerPreferences().getFontData());
		IFigure figure = root.getFigure();
		figure.setFont(font);
		figure.validate();

		this.run((ScalableRootEditPart) viewer.getRootEditPart());

		// new SaveAsImageAction((ScalableRootEditPart)
		// viewer.getRootEditPart())
		// .run();
		font.dispose();
	}

	public InputStream ggg() {

		// ConsultantVScreenEditor editor = new ConsultantVScreenEditor();
		// editor.setDiagram(diagram)
		ScreenBody diagram = null;

		CompositeMap loadFile = CompositeMapUtil.loadFile(new File(
				"/Users/shiliyan/Desktop/Uip_pages/Page1.uip"));
		if (loadFile != null) {
			CompositeMap2Object c2o = new CompositeMap2Object();
			diagram = c2o.createScreenBody(loadFile);
		} else {
			diagram = new ScreenBody();
		}

		GraphicalViewer viewer = new ScrollingGraphicalViewer();
		// Display.getDefault().get
		ScalableRootEditPart root = new ScalableRootEditPart();
		viewer.setRootEditPart(root);
		viewer.setEditPartFactory(new ExtAuroraPartFactory(new EditorMode() {
			public String getMode() {
				return None;
			}

			public boolean isForDisplay() {
				return false;
			}

			public boolean isForCreate() {
				return true;
			}

			public boolean isForUpdate() {
				return true;
			}

			public boolean isForSearch() {
				return false;
			}
		}));
		viewer.setContents(diagram);
		Font font = new Font(Display.getCurrent(),
				new DefaultPaletteViewerPreferences().getFontData());
		IFigure figure = root.getFigure();
		figure.setFont(font);
		figure.validate();

		InputStream is = this.run((ScalableRootEditPart) viewer
				.getRootEditPart());

		// new SaveAsImageAction((ScalableRootEditPart)
		// viewer.getRootEditPart())
		// .run();
		font.dispose();
		return is;
	}

	public InputStream run(ScalableRootEditPart rootEditPart) {

		double zoom = rootEditPart.getZoomManager().getZoom();

		try {
			IFigure figure = getRootFigure(rootEditPart);
			Rectangle rectangle = calBounds(figure);

			Image image = new Image(Display.getDefault(), rectangle.width,
					rectangle.height);
			GC gc = new GC(image);
			SWTGraphics graphics = new SWTGraphics(gc);
			figure.paint(graphics);
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] { image.getImageData() };

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			loader.save(baos, SWT.IMAGE_PNG);

			ByteArrayInputStream is = new ByteArrayInputStream(
					baos.toByteArray());
			image.dispose();
			gc.dispose();
			return is;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			rootEditPart.getZoomManager().setZoom(zoom);
		}
		return null;
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

	public void selectionChanged(IAction action, ISelection selection) {
	}

}