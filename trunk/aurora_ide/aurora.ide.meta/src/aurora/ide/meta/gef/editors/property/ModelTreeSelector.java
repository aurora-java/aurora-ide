package aurora.ide.meta.gef.editors.property;

import java.util.List;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.TabBody;
import aurora.ide.meta.gef.editors.models.TabFolder;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class ModelTreeSelector extends Composite implements
		ITreeContentProvider, ILabelProvider, IColorProvider {

	public static final ViewerFilter CONTAINER_FILTER = new ViewerFilter() {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			return element instanceof Container;
		}

	};

	public static final Color invalid_color = new Color(null, 200, 200, 200);
	private ViewDiagram root = null;
	private AuroraComponent selection = null;
	private TreeViewer viewer = null;

	public ModelTreeSelector(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
		viewer = new TreeViewer(this, SWT.SINGLE);
		viewer.setLabelProvider(this);
		viewer.setContentProvider(this);
	}

	public void refreshTree() {
		viewer.setInput(root);
		viewer.expandAll();
		if (selection != null)
			viewer.setSelection(new StructuredSelection(selection));
	}

	public ViewDiagram getRoot() {
		return root;
	}

	public TreeViewer getTreeViewer() {
		return viewer;
	}

	public void setRoot(ViewDiagram root) {
		this.root = root;
	}

	public AuroraComponent getSelection() {
		ISelection s = viewer.getSelection();
		if (s instanceof IStructuredSelection) {
			return (AuroraComponent) ((IStructuredSelection) s)
					.getFirstElement();
		}
		return null;
	}

	public void setSelection(AuroraComponent selection) {
		this.selection = selection;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Container) {
			List<AuroraComponent> list = ((Container) parentElement)
					.getChildren();
			int size = list.size();
			if (parentElement instanceof TabFolder) {// returns TabItem only
				size >>= 1;
			}
			Object[] objs = new Object[size];
			for (int i = 0; i < size; i++)
				objs[i] = list.get(i);
			return objs;
		}
		return null;
	}

	public Object getParent(Object element) {
		if (element instanceof AuroraComponent) {
			Container cont = ((AuroraComponent) element).getParent();
			if (cont instanceof TabBody) {
				return ((TabBody) cont).getTabItem();
			}
			return cont;
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (!(element instanceof Container))
			return false;
		return ((Container) element).getChildren().size() > 0;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public Image getImage(Object element) {
		return PropertySourceUtil.getImageOf((AuroraComponent) element);
	}

	public String getText(Object element) {
		AuroraComponent ac = (AuroraComponent) element;
		String prop = ac.getPrompt();
		String aType = ac.getType();
		if (prop == null || prop.length() == 0)
			return aType;
		return aType + " [" + prop + "]";
	}

	public Color getForeground(Object element) {
		if (element instanceof TabFolder || element instanceof TabItem)
			return invalid_color;
		return null;
	}

	public Color getBackground(Object element) {
		return null;
	}

	public static ViewerFilter getSectionFilter(final String secType) {
		return new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof TabFolder || element instanceof TabItem)
					return true;
				if (element instanceof Container) {
					return secType.equals(((Container) element)
							.getSectionType());
				}
				return true;
			}
		};
	}
}
