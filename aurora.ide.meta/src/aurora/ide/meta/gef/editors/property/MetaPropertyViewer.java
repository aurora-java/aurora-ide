package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

import aurora.ide.meta.gef.editors.parts.ComponentPart;

public class MetaPropertyViewer implements ISelectionChangedListener,
		IPropertySourceProvider {

	private PropertySheetPage psp = new PropertySheetPage();
	private IWorkbenchPart wpart;
	private PropertyManager propertyManager;

	public MetaPropertyViewer(Composite c, IWorkbenchPart vse,PropertyManager propertyManager) {
		this.wpart = vse;
		this.propertyManager = propertyManager;
		psp.setPropertySourceProvider(this);
		psp.createControl(c);
		Control ctrl = psp.getControl();
		ctrl.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void selectionChanged(SelectionChangedEvent event) {
		ISelection s = event.getSelection();
		psp.selectionChanged(wpart, s);
	}

	public IPropertySource getPropertySource(Object object) {
		// if (object instanceof ComponentPart)
		// return PropertySourceUtil
		// .translate(((ComponentPart) object).getComponent(),
		// commandStack);
		// return null;
		if (object instanceof ComponentPart)
			return propertyManager
					.createIPropertySource((ComponentPart) object);
		return null;
	}

//	public void setCommandStack(CommandStack commandStack) {
//		this.commandStack = commandStack;
//	}

	public PropertyManager getPropertyManager() {
		return propertyManager;
	}

	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}
}
