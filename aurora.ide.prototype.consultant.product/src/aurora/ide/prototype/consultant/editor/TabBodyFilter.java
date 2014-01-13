package aurora.ide.prototype.consultant.editor;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.TabBody;
import aurora.plugin.source.gen.screen.model.TabItem;

public class TabBodyFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		String componentType = ((AuroraComponent) element).getComponentType();
		if (TabBody.TAB_BODY.equals(componentType)) {
			return TabItem.TAB.equals(((AuroraComponent) parentElement).getComponentType());
		}
		return true;
	}

}
