package aurora.ide.editor.widgets.core;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import uncertain.composite.CompositeMap;

public class DataFilter extends ViewerFilter {
	
	String filterColumn ;
	String filterString ="";
	public DataFilter(String filterColumn){
		this.filterColumn = filterColumn;
	}
	
	
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		CompositeMap p = (CompositeMap) element;
		return p.getString(filterColumn).startsWith(filterString);
	}
	public void setFilterString(String filterString){
		this.filterString = filterString;
	}
}