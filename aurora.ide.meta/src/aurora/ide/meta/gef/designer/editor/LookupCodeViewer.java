package aurora.ide.meta.gef.designer.editor;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.figures.ColorConstants;

public class LookupCodeViewer extends TreeViewer {
	private Tree tree;

	public LookupCodeViewer(Composite parent) {
		super(parent);
		init(parent);
	}

	public LookupCodeViewer(Composite parent, int style) {
		super(parent, style);
		init(parent);
	}

	private void init(Composite parent) {
		tree = getTree();
		setContentProvider(new TreeConentProvider());
		setLabelProvider(new TreeLabelProvider());
		//
	}

	public void select(String code) {
		for (TreeItem ti : tree.getItems()) {
			ti.getStyle();
			if (LookupCodeUtil.getCode(ti.getData()).equalsIgnoreCase(code)) {
				setSelection(new StructuredSelection(ti.getData()));
				break;
			}
		}
	}

	class TreeConentProvider implements ITreeContentProvider {

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		public Object[] getElements(Object inputElement) {
			CompositeMap codemap = (CompositeMap) inputElement;
			return codemap.getChildsNotNull().toArray();
		}

		public Object[] getChildren(Object parentElement) {
			CompositeMap m = (CompositeMap) parentElement;
			return m.getChildsNotNull().toArray();
		}

		public Object getParent(Object element) {
			CompositeMap m = (CompositeMap) element;
			return m.getParent();
		}

		public boolean hasChildren(Object element) {
			if ((tree.getStyle() & SWT.CHECK) != 0)
				return false;
			return LookupCodeUtil.isCode(element);
		}
	}

	class TreeLabelProvider extends LabelProvider implements IColorProvider {

		@Override
		public String getText(Object element) {
			if (LookupCodeUtil.isCode(element)) {
				String code = LookupCodeUtil.getCode(element);
				Boolean b = ((CompositeMap) element).getBoolean("exists");
				if (b != null && b.booleanValue())
					code += "    (exists)";
				return code;
			}
			return LookupCodeUtil.getValueAsString(element);
		}

		public Color getForeground(Object element) {
			String t = getText(element);
			if (t.contains("<"))// <..>means error
				return ColorConstants.red;
			if (LookupCodeUtil.isValue(element)) {
				return ColorConstants.gray;
			}
			return null;
		}

		public Color getBackground(Object element) {
			return null;
		}
	}
}
