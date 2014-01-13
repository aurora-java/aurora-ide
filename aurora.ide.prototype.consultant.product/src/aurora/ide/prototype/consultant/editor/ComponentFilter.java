package aurora.ide.prototype.consultant.editor;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class ComponentFilter extends ViewerFilter {

	private String[] types;

	public ComponentFilter(String[] types) {
		this.types = types;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		String componentType = ((AuroraComponent) element).getComponentType();
		for (String t : types) {
			if (t.equals(componentType))
				return false;
		}
		return true;
	}

}
