package aurora.ide.editor.widgets;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.QualifiedName;
import uncertain.schema.Array;
import uncertain.schema.Element;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.helpers.CompositeMapUtil;

public class CompositeMapTreeContentProvider implements ITreeContentProvider {

	
	CompositeMap rootElement;
	public CompositeMapTreeContentProvider(CompositeMap rootElement) {
		super();
		this.rootElement = rootElement;
	}


	public Object[] getChildren(Object parentElement) {
		if (parentElement == null)
			return null;
		CompositeMap map = (CompositeMap) parentElement;
		List childs = new LinkedList(map.getChildsNotNull());

//		Element element = LoadSchemaManager.getSchemaManager().getElement(map);
		Element element =CompositeMapUtil.getElement(map);
		if (element != null) {
			//列示所有数组子节点
			List arrays = element.getAllArrays();
			if (arrays != null) {
				Iterator ite = arrays.iterator();
				while (ite.hasNext()) {
					Array array = (Array) ite.next();
					String name = array.getLocalName();
					CompositeMap newCM = new CommentCompositeMap(map.getPrefix(),
							map.getNamespaceURI(), name);
					QualifiedName nm = newCM.getQName();
					//如果发现不存在此数组节点，则在显示元素上增加此节点，但在核心数据结构中并不添加
					if(CompositeUtil.findChild(map, nm)==null){
						newCM.setParent(map);
						childs.add(newCM);
					}
				}
			}
		}
		if (childs == null)
			return null;
		else{
			return childs.toArray();
		}
	}

	public Object getParent(Object element) {
		if (element == null)
			return null;
		CompositeMap map = (CompositeMap) element;
		CompositeMap parent= map.getParent();
		return parent;
	}

	public boolean hasChildren(Object element) {
		if (element == null)
			return false;
		CompositeMap map = (CompositeMap) element;

		List childs = map.getChilds();
		if(childs != null){
			return true;
		}
		// this element maybe have arrays
		else{
//			Element cm = LoadSchemaManager.getSchemaManager().getElement(map);
			Element cm =CompositeMapUtil.getElement(map);
			if(cm != null &&!cm.getAllArrays().isEmpty()){
				return true;
			}
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement == null)
			return null;
//		return new Object[] { rootElement };
		
		CompositeMap map = (CompositeMap) inputElement;
		if (map.equals(rootElement.getParent())&& !map.equals(rootElement)) {
			return new Object[] { rootElement };
		}
		List childs = new LinkedList(map.getChildsNotNull());
//		Element element = LoadSchemaManager.getSchemaManager().getElement(map);
		Element element =CompositeMapUtil.getElement(map);
		if (element != null) {
			List arrays = element.getAllArrays();
			if (arrays != null) {
				Iterator ite = arrays.iterator();
				while (ite.hasNext()) {
					Array array = (Array) ite.next();
					String name = array.getLocalName();
					QualifiedName qn = array.getQName();
					CompositeMap newCM = new CommentCompositeMap(qn.getPrefix(),
							qn.getNameSpace(), name);
					QualifiedName nm = newCM.getQName();
					if(CompositeUtil.findChild(map, nm)==null){
						newCM.setParent(map);
						childs.add(newCM);
					}
				}
			}
		}
		if (childs == null)
			return null;
		else{
			return childs.toArray();
		}
	}

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

}
