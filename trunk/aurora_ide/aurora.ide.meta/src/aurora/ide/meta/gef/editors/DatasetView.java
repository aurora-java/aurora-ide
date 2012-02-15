package aurora.ide.meta.gef.editors;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.graphics.Rectangle;

public class DatasetView extends ScrollingGraphicalViewer {
	 protected void hookControl() {
	      super.hookControl();
	      FigureCanvas canvas = getFigureCanvas();
	      Rectangle bounds = canvas.getBounds();
	      System.out.println(bounds);
//	      canvas.getViewport().setContentsTracksWidth(true);
//	      canvas.getViewport().setContentsTracksHeight(false);
	      canvas.setHorizontalScrollBarVisibility(FigureCanvas.NEVER);
	      canvas.setVerticalScrollBarVisibility(FigureCanvas.NEVER);
	   }
	
	
}
