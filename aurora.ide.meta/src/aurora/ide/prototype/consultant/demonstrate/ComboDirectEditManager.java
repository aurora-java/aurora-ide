package aurora.ide.prototype.consultant.demonstrate;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 */
public class ComboDirectEditManager extends DirectEditManager {

	private Font scaledFont;

	protected VerifyListener verifyListener;

	protected IFigure nodeFigure;


	private String[] items = new String[0];

	public ComboDirectEditManager(GraphicalEditPart source,
			Class<?> editorType, CellEditorLocator locator, String feature) {
		super(source, editorType, locator, feature);
		this.nodeFigure = source.getFigure();
	}

	/**
	 * @see org.eclipse.gef.tools.DirectEditManager#initCellEditor()
	 */
	protected void initCellEditor() {
		Control control = getCellEditor().getControl();
		IFigure figure = ((GraphicalEditPart) getEditPart()).getFigure();
		scaledFont = figure.getFont();
		FontData data = scaledFont.getFontData()[0];
		Dimension fontSize = new Dimension(0, data.getHeight());
		nodeFigure.translateToAbsolute(fontSize);
		data.setHeight(fontSize.height);
		if (control instanceof CCombo) {
			CCombo text = (CCombo) control;
			text.setFont(scaledFont);
		}
	}
	protected boolean isDirty() {
		return getCellEditor().isDirty();
	}

	public void show() {
		super.show();
		Control control = this.getCellEditor().getControl();
		if (control instanceof CCombo) {
			CCombo text = (CCombo) control;
			text.setListVisible(true);
		}
	}

	protected CellEditor createCellEditorOn(Composite composite) {
		return new ComboBoxCellEditor(composite, items){
			protected Object doGetValue() {
//				return items[(Integer) super.doGetValue()];
				return ((CCombo)this.getControl()).getText();
			}
		};
	}

	public void setItem(String[] items) {
		this.items = items;
	}

}