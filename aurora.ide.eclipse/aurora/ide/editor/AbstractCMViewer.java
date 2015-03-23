/**
 * 
 */
package aurora.ide.editor;


import org.eclipse.swt.SWT;

import uncertain.composite.CompositeMap;
import uncertain.schema.Element;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.editor.core.IViewer;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LocaleMessage;

public abstract class AbstractCMViewer implements IViewer{
	
	protected CompositeMap selectedData;
	protected CompositeMap focusData;
	
	public void copyElement() {
		CompositeMap child = new CommentCompositeMap(getFocus());
		child.setParent(getFocus().getParent());
		setSelection(child);
	}
	
	
	public void pasteElement() {
		CompositeMap selectedCm = getSelection();
		if (selectedCm == null)
			return;
		CompositeMap parentComp = getFocus();
		if (!CompositeMapUtil.validNextNodeLegalWithAction(parentComp, selectedCm)) {
			return;
		}
		CompositeMap child = new CommentCompositeMap(selectedCm);
		if (child != null) {
			parentComp.addChild(child);
			selectedCm.getParent().removeChild(selectedCm);
			CompositeMapUtil.addArrayNode(parentComp);
		}
		selectedCm = null;
		refresh(true);
	}



	public void cutElement() {
		setSelection(getFocus());
	}

	public void removeElement() {
		CompositeMap comp = getFocus();
		if (comp != null) {
//			Element em = LoadSchemaManager.getSchemaManager().getElement(comp);
			Element em =CompositeMapUtil.getElement(comp);
			if (em != null && em.isArray()) {
				if (comp.getChildsNotNull().size() > 0) {
					int buttonID = DialogUtil.showConfirmDialogBox(LocaleMessage.getString("clear.array.question"));
					switch (buttonID) {
					case SWT.OK:
						if (comp != null) {
							comp.getChildsNotNull().clear();
							refresh(true);
							return;
						}
						refresh(true);
					case SWT.CANCEL:
						return;
					}
				}
				DialogUtil.showWarningMessageBox(LocaleMessage.getString("can.not.delete.array.hint"));
				return;
			}
		}
		int buttonID = DialogUtil.showConfirmDialogBox(LocaleMessage.getString("delete.element.confirm"));
		switch (buttonID) {
		case SWT.OK:
			if (comp != null) {
				CompositeMap parentCM = comp.getParent();
//				Element element = LoadSchemaManager.getSchemaManager().getElement(
//						parentCM);
				Element element = CompositeMapUtil.getElement(parentCM);
				
				if (element != null&&element.isArray()) {
					comp.getParent().removeChild(comp);
					if (parentCM.getChilds() == null
							|| parentCM.getChilds().size() == 0) {
						parentCM.getParent().removeChild(parentCM);
					}
				} else {
					parentCM.removeChild(comp);
				}
			}
			refresh(true);
		case SWT.CANCEL:
			break;
		}
	}
	
	
	
	
	public CompositeMap getSelection(){
		return selectedData;
	}
	public void setSelection(CompositeMap data){
		selectedData = data;
	}
	public void setFocus(CompositeMap data){
		focusData = data;
	}
	public CompositeMap getFocus(){
		return focusData;
	}
	public abstract CompositeMap getInput();
}
