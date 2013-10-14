package aurora.ide.meta.gef.editors.policies;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import aurora.plugin.source.gen.screen.model.AuroraComponent;

/**
 */
public class NodeDirectEditManager extends DirectEditManager {

	private Font scaledFont;

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
		Control control = getCellEditor().getControl();
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
		if (control instanceof Text) {
			getCellEditor().setValue(propertyValue);
		}
		IFigure figure = ((GraphicalEditPart) getEditPart()).getFigure();
		scaledFont = figure.getFont();
		FontData data = scaledFont.getFontData()[0];
		Dimension fontSize = new Dimension(0, data.getHeight());
		nodeFigure.translateToAbsolute(fontSize);
		data.setHeight(fontSize.height);
		// scaledFont = new Font(null, data);
		if (control instanceof Text) {
			Text text = (Text) control;
			text.setFont(scaledFont);
			text.selectAll();
		}
//		if (control instanceof CCombo) {
//			CCombo text = (CCombo) control;
//			text.setFont(scaledFont);
////			text.setMenu(menu)
//		}
	}
	
	public void show() {
		super.show();
//		Control control = this.getCellEditor().getControl();
//		if (control instanceof CCombo) {
//			CCombo text = (CCombo) control;
//			text.setListVisible(true);
//		}
	}

	protected CellEditor createCellEditorOn(Composite composite) {
//		if (editorType.equals(ComboBoxCellEditor.class)) {
//			return new ComboBoxCellEditor(composite, new String[] { "aa", "bb",
//					"cc" });
//		}

		return super.createCellEditorOn(composite);
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