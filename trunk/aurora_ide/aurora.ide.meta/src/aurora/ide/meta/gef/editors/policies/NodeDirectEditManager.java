package aurora.ide.meta.gef.editors.policies;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Text;

import aurora.plugin.source.gen.screen.model.AuroraComponent;

/**
 */
public class NodeDirectEditManager extends DirectEditManager {

	Font scaledFont;

	protected VerifyListener verifyListener;

	protected IFigure nodeFigure;

	private String feature;

	/**
	 * @see org.eclipse.gef.tools.DirectEditManager#bringDown()
	 */
	// protected void bringDown() {
	// //This method might be re-entered when super.bringDown() is called.
	// Font disposeFont = scaledFont;
	// scaledFont = null;
	// super.bringDown();
	// if (disposeFont != null)
	// disposeFont.dispose();
	// }

	public NodeDirectEditManager(GraphicalEditPart source, Class<?> editorType,
			CellEditorLocator locator, String feature) {
		super(source, editorType, locator, feature);
		this.nodeFigure = source.getFigure();
		this.feature = feature;
	}

	/**
	 * @see org.eclipse.gef.tools.DirectEditManager#initCellEditor()
	 */
	protected void initCellEditor() {
		Text text = (Text) getCellEditor().getControl();
		// verifyListener = new VerifyListener() {
		// public void verifyText(VerifyEvent event) {
		// Text text = (Text) getCellEditor().getControl();
		// String oldText = text.getText();
		// String leftText = oldText.substring(0, event.start);
		// String rightText = oldText.substring(event.end, oldText.length());
		// GC gc = new GC(text);
		// String s = leftText + event.text + rightText;
		// Point size = gc.textExtent(leftText + event.text + rightText);
		// gc.dispose();
		// if (size.x != 0)
		// size = text.computeSize(size.x, SWT.DEFAULT);
		// getCellEditor().getControl().setSize(size.x, size.y);
		// }
		// };
		// text.addVerifyListener(verifyListener);

		// String initialLabelText = nodeFigure.getText();
		String propertyValue = ((AuroraComponent) getEditPart().getModel())
				.getStringPropertyValue(feature);
		getCellEditor().setValue(propertyValue);
		IFigure figure = ((GraphicalEditPart) getEditPart()).getFigure();
		scaledFont = figure.getFont();
		FontData data = scaledFont.getFontData()[0];
		Dimension fontSize = new Dimension(0, data.getHeight());
		nodeFigure.translateToAbsolute(fontSize);
		data.setHeight(fontSize.height);
//		scaledFont = new Font(null, data);
		text.setFont(scaledFont);
		text.selectAll();
	}

	/**
	 * @see org.eclipse.gef.tools.DirectEditManager#unhookListeners()
	 */
	// protected void unhookListeners() {
	// super.unhookListeners();
	// Text text = (Text) getCellEditor().getControl();
	// text.removeVerifyListener(verifyListener);
	// verifyListener = null;
	// }
}