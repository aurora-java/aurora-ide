/**
 * 
 */
package aurora.ide.celleditor;


import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author linjinxiao
 *
 */
public interface ICellEditor {
	public void init();
	public void createCellEditor(Composite parent);
	public String getSelection();
	public Object valueToShow(String value);
	public void SetSelection(String value);
	public Control getCellControl();
	public void dispose();
	public boolean validValue(String value);
	public String getErrorMessage();
	public CellEditor getCellEditor();
}
