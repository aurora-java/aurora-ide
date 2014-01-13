package aurora.ide.prototype.consultant.editor;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Container;

public class ScreenBodyContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public Object[] getElements(Object inputElement) {
//		if(inputElement instanceof ScreenBody){
//			List<AuroraComponent> children = ((ScreenBody) inputElement).getChildren();
//			return children.toArray(new AuroraComponent[children.size()]);
//		}
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof Container){
			List<AuroraComponent> children = ((Container) parentElement).getChildren();
			return	children.toArray(new AuroraComponent[children.size()]);
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof AuroraComponent)
			return ((AuroraComponent) element).getParent();
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return element instanceof Container;
	}

}
