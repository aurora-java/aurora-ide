package aurora.ide.editor.widgets;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import uncertain.composite.CompositeMap;

public class PlainCompositeMapContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
	        CompositeMap data = (CompositeMap)inputElement;
	        List childs = data.getChilds();
	        if(childs!=null){
	            return childs.toArray();
	        }else
	            return new Object[]{};
		}
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}