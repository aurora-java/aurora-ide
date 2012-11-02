package aurora.ide.create.component.wizard;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
		private ResourceManager resourceManager;

		private ResourceManager getResourceManager() {
			if (resourceManager == null) {
				resourceManager = new LocalResourceManager(
						JFaceResources.getResources());
			}

			return resourceManager;
		}

		protected final Object getAdapter(Object sourceObject) {
			Class<?> adapterType = IWorkbenchAdapter.class;
			if (sourceObject == null) {
				return null;
			}
			if (adapterType.isInstance(sourceObject)) {
				return sourceObject;
			}

			if (sourceObject instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) sourceObject;

				Object result = adaptable.getAdapter(adapterType);
				if (result != null) {
					// Sanity-check
					Assert.isTrue(adapterType.isInstance(result));
					return result;
				}
			}

			if (!(sourceObject instanceof PlatformObject)) {
				Object result = Platform.getAdapterManager().getAdapter(
						sourceObject, adapterType);
				if (result != null) {
					return result;
				}
			}
			return null;
		}

		public final Image getImage(Object element) {
			if (element instanceof IResource) {
				// obtain the base image by querying the element
				IWorkbenchAdapter adapter = (IWorkbenchAdapter) getAdapter(element);
				if (adapter == null) {
					return null;
				}
				ImageDescriptor descriptor = adapter
						.getImageDescriptor(element);
				if (descriptor == null) {
					return null;
				}

				return (Image) getResourceManager().get(descriptor);
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

	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent,SWT.NONE);
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
		setInput(createInput());

	}

	public void setInput(Object input) {
		viewer.setInput(input);
	}

	public Object createInput() {
		CompositeMap form = new CompositeMap("Form");
		form.put("id", "form");
		CompositeMap grid = new CompositeMap("Grid");
		grid.put("id", "grid");
		CompositeMap form_grid = new CompositeMap("Form + Grid");
		grid.put("id", "form_grid");
		
		CompositeMap input = new CompositeMap();
		input.addChild(form);
		input.addChild(grid);
		input.addChild(form_grid);
		return input;
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
