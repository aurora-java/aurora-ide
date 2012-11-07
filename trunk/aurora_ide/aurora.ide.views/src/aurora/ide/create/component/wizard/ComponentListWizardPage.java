package aurora.ide.create.component.wizard;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchContentProvider;

import uncertain.composite.CompositeMap;
import aurora.ide.create.component.ComponentListFactory;

public class ComponentListWizardPage extends WizardPage implements
		ISelectionChangedListener {

	private class ContentProvider extends WorkbenchContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof CompositeMap) {
				@SuppressWarnings("unchecked")
				List<CompositeMap> childsNotNull = ((CompositeMap) inputElement)
						.getChildsNotNull();
				return childsNotNull.toArray(new CompositeMap[childsNotNull
						.size()]);
			}
			return null;
		}

		public Object[] getChildren(Object parentElement) {
			return null;
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return false;
		}
	}

	private class VLabelProvider extends LabelProvider {


		public final Image getImage(Object element) {
			if (element instanceof CompositeMap) {
				return ComponentListFactory.getImage((CompositeMap)element);
			}
			return null;
		}

		public final String getText(Object element) {
			if (element instanceof CompositeMap) {
				return ((CompositeMap) element).getName();
			}
			return null;
		}

	}

	private TreeViewer viewer;

	private CompositeMap currentSelectionObject;

	public ComponentListWizardPage(String pageName) {
		super(pageName);
	}
	

	private void init(CompositeMap createInput) {
		this.viewer.setSelection(new StructuredSelection(createInput
				.getChilds().get(0)));
	}

	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout());
		FilteredTree ff = new FilteredTree(control, SWT.SINGLE,
				new PatternFilter(), true);
		viewer = ff.getViewer();
		viewer.setContentProvider(new ContentProvider());

		viewer.setLabelProvider(new VLabelProvider());

		// refreshInput();
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		viewer.addSelectionChangedListener(this);
		this.setControl(control);
		CompositeMap createInput = ComponentListFactory.createInput();
		setInput(createInput);
		init(createInput);

	}

	public void setInput(Object input) {
		viewer.setInput(input);
	}

	

	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) selection)
					.getFirstElement();
			if (firstElement instanceof CompositeMap) {
				this.setCurrentSelectionObject((CompositeMap) firstElement);
				return;
			}
		}
		this.setCurrentSelectionObject(null);
	}

	public CompositeMap getCurrentSelectionObject() {
		return currentSelectionObject;
	}

	public void setCurrentSelectionObject(CompositeMap currentSelectionObject) {
		this.currentSelectionObject = currentSelectionObject;
	}
}
