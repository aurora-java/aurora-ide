package aurora.ide.meta.gef.editors.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import aurora.ide.search.core.Util;

public class FileSystemSelector implements ModifyListener, KeyListener,
		SelectionListener, ITreeContentProvider {
	private IContainer root = null;
	private IResource selection = null;
	private Combo text;
	private int maxHis = 10;
	private TreeViewer viewer;
	private String filter = "";
	private Job job = null;

	public FileSystemSelector(Composite parent, int style) {
		super();
		Composite com = new Composite(parent, style);
		com.setLayout(new GridLayout(1, true));
		Label l = new Label(com, SWT.NONE);
		l.setText("? = any character , * = any string");
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text = new Combo(com, SWT.BORDER);
		text.addModifyListener(this);
		text.addKeyListener(this);
		text.addSelectionListener(this);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		viewer = new TreeViewer(com, SWT.BORDER);
		viewer.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		viewer.setContentProvider(this);
		viewer.setLabelProvider(new WorkbenchLabelProvider());
	}

	public void setInput(IContainer iContainer) {
		this.root = iContainer;
		refresh();
	}

	private void refresh() {
		if (job != null)
			job.cancel();
		job = new Job("refresh") {
			protected IStatus run(IProgressMonitor monitor) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						viewer.setInput(root);
						// viewer.expandToLevel(1);
					}
				});
				job = null;
				return Status.OK_STATUS;
			}
		};
		job.schedule(300);
	}

	public void modifyText(ModifyEvent e) {
		updateTreeViewer();
	}

	private void updateTreeViewer() {
		filter = text.getText();
		refresh();
	}

	public void keyPressed(KeyEvent e) {
		if (e.character == 13) {
			updateHis();
		}
	}

	private void updateHis() {
		String str = text.getText();
		if (str.length() == 0)
			return;
		String[] oldItems = text.getItems();
		String[] newItems = oldItems;
		boolean isNew = true;
		for (int i = 0; i < oldItems.length; i++) {
			if (oldItems[i].equals(str)) {
				isNew = false;
				System.arraycopy(oldItems, 0, newItems, 1, i);
				break;
			}
		}
		if (isNew) {
			int newLength = oldItems.length;
			if (oldItems.length < maxHis)
				newLength++;
			newItems = new String[newLength];
			System.arraycopy(oldItems, 0, newItems, 1, newLength - 1);
		}
		newItems[0] = str;
		text.setItems(newItems);
		text.select(0);
	}

	public void keyReleased(KeyEvent e) {

	}

	public void widgetSelected(SelectionEvent e) {
		updateHis();
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		System.out.println(oldInput + "  ----   " + newInput);
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IContainer) {
			IContainer folder = (IContainer) parentElement;
			try {
				ArrayList<IResource> als = new ArrayList<IResource>();
				for (IResource ir : folder.members()) {
					if (accept(ir))
						als.add(ir);
				}
				IResource[] result = als.toArray(new IResource[als.size()]);
				Arrays.sort(result, new ResourceComparator());
				return result;
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private boolean accept(IResource ir) {
		if (ir.getName().startsWith("."))
			return false;
		if (filter == null || filter.length() == 0)
			return true;
		String path = ir.getFullPath().toString();
		if (Util.stringMatch(filter, path, false, false))
			return true;
		else if (ir instanceof IContainer) {
			if (hasChildren(ir)) {
				return true;
			}
		}
		return false;
	}

	public Object getParent(Object element) {
		if (element instanceof IResource) {
			IResource res = (IResource) element;
			return res.getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IContainer) {
			IContainer folder = (IContainer) element;
			return getChildren(folder).length > 0;
		}
		return false;
	}

	/**
	 * 
	 * Comparator to make sure the folder be listed before file ,and they are
	 * all in alphabetical order
	 * 
	 */
	private class ResourceComparator implements Comparator<IResource> {

		public int compare(IResource o1, IResource o2) {
			if (o1 instanceof IContainer) {
				if (!(o2 instanceof IContainer))
					return -1;
			} else {
				if (o2 instanceof IContainer)
					return 1;
			}
			return o1.getName().compareTo(o2.getName());
		}
	}
}
