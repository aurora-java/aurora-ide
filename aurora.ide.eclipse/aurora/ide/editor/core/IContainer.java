/**
 * 
 */
package aurora.ide.editor.core;

import org.eclipse.swt.widgets.Control;

import uncertain.composite.CompositeMap;

public interface IContainer extends IViewer{
	public Object getViewer();
	public Object getSelection();
	public void setSelection(Object data);
	public void setFocus(Object data);
	public Object getFocus();
	public Control getControl();
	public  CompositeMap getInput();
}
