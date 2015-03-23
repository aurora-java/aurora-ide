/**
 * 
 */
package aurora.ide.editor;


import org.eclipse.swt.SWT;

import aurora.ide.editor.core.IViewer;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LocaleMessage;


import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;

public abstract class PropertyViewer implements IViewer{
	
	public void removePropertyAction() {
		Attribute attribute = getSelection();
		if(attribute == null){
			DialogUtil.showWarningMessageBox("Please select an attribute first!");
			return;
		}
		int buttonID = DialogUtil.showConfirmDialogBox(LocaleMessage.getString("delete.attribute.confirm"));
		switch (buttonID) {
		case SWT.OK:
			CompositeMap data = getInput();
			String propertyName = attribute.getLocalName();
			data.remove(propertyName);
			refresh(true);
			
		case SWT.CANCEL:
			break;
		}
	}
	public abstract CompositeMap getInput();
	public abstract Attribute getSelection();
}
