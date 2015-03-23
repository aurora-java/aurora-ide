package aurora.ide.editor.widgets;


import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import aurora.ide.celleditor.ICellEditor;
import aurora.ide.editor.widgets.core.ICellModifierListener;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.ExceptionUtil;


import uncertain.composite.CompositeMap;

public class GridCellModifier implements ICellModifier {

	/**
	 * @param viewer
	 */
	protected GridViewer viewer;
	private ArrayList listeners = new ArrayList();

	public GridCellModifier(GridViewer viewer) {
		super();
		this.viewer = viewer;
	}

	public boolean canModify(Object element, String property) {
		return true;
	}

	public Object getValue(Object element, String property) {
		if (element == null)
			return "";
		CompositeMap data = (CompositeMap) element;
		String value = data.getString(property);
		if (value == null) {
			value = "";
			data.put(property, value);
		}
		ICellEditor cellEditor = viewer.getCellEditor(property);
		if (cellEditor != null) {
			Object returnValue = cellEditor.valueToShow(value);
			return returnValue;
		}
		return value;
	}

	public void modify(Object element, String property, Object value) {
		ICellEditor cellEditor = viewer.getCellEditor(property);
		if (cellEditor != null) {
			value = cellEditor.getSelection();
		}

		TableItem item = (TableItem) element;
		Object o = item.getData();
		CompositeMap data = (CompositeMap) o;

		Object oldValue = data.get(property);
		
		try {
			noticeListeners(data,property,value);
		} catch (Exception e) {
			DialogUtil.showErrorMessageBox(ExceptionUtil.getExceptionTraceMessage(e));
			return ;
		}
		
		
		if (oldValue == null)
			oldValue = "";
		if (oldValue == null || !oldValue.equals(value)) {
			data.put(property, value);
			viewer.refresh(true);
		}
		if (value == null || value.equals("")) {
			data.remove(property);
		}
	}
	public void addCellModifierListener(ICellModifierListener listener){
		listeners.add(listener);
	}
	
	public void removeCellModifierListener(ICellModifierListener listener){
		if(listeners.contains(listener)){
			listeners.remove(listener);
		}
	}
	
	private void noticeListeners(CompositeMap record, String property, Object value) {
		if(listeners.size()==0){
			return;
		}
		Iterator collection = listeners.iterator();
		for(;collection.hasNext();){
			ICellModifierListener listener = (ICellModifierListener)collection.next();
			String newValue = value==null?null:value.toString();
			listener.modify(record, property, newValue);
		}
	}







}