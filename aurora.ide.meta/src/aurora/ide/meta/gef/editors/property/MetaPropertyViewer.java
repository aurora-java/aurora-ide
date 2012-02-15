package aurora.ide.meta.gef.editors.property;

import org.eclipse.gef.commands.CommandStack;
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
	private CommandStack commandStack;

	public MetaPropertyViewer(Composite c, IWorkbenchPart vse) {
		this.wpart = vse;
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
		if (object instanceof ComponentPart)
			return PropertySourceUtil
					.translate(((ComponentPart) object).getPropertySource2(),
							commandStack);
		return null;
	}

	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}
}
