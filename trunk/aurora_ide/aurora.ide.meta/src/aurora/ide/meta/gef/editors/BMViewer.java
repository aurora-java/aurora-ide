package aurora.ide.meta.gef.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.util.DelegatingDragAdapter;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import aurora.ide.AuroraPlugin;
import aurora.ide.meta.gef.editors.dnd.BMTransfer;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public class BMViewer {

	private VScreenEditor vse;
	private String bmHome;
	private IProject project;

	public BMViewer(Composite c, VScreenEditor vScreenEditor) {
		vse = vScreenEditor;
		init();
		if (bmHome != null)
			configrueTreeViewer(c);
	}

	private void init() {
		Object file = vse.getEditorInput().getAdapter(IFile.class);
		if (file instanceof IFile) {
			project = ((IFile) file).getProject();
			try {
				bmHome = project
						.getPersistentProperty(ProjectPropertyPage.BMQN);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	// protected void configureTableViewer(TableViewer viewer) {
	// SearchLabelProvider lp = new SearchLabelProvider(this,
	// SearchLabelProvider.SHOW_LABEL_PATH);
	// viewer.setLabelProvider(new DecoratingSearchLabelProvider(lp));
	// viewer.setComparator(new DecoratorIgnoringViewerSorter(lp));
	// TableContentProvider provider = new TableContentProvider(this, viewer);
	// viewer.setContentProvider(provider);
	// this.provider = provider;
	// }
	private void configrueTreeViewer(Composite c) {
		final TreeViewer tv = new TreeViewer(c, SWT.NONE);
		DelegatingDragAdapter dragAdapter = new DelegatingDragAdapter();
		dragAdapter.addDragSourceListener(new TransferDragSourceListener() {
			public Transfer getTransfer() {
				return BMTransfer.getInstance();
			}

			public void dragStart(DragSourceEvent event) {
				// enable drag listener if there is a viewer selection
				IFile bm = getBM();
				if (bm == null)
					return;
				event.detail = DND.DROP_COPY;
				event.doit = true;
				BMTransfer.getInstance().setBM(bm);
			}

			private IFile getBM() {
				TreeSelection selection = (TreeSelection) tv.getSelection();
				Object obj = selection.getFirstElement();
				if (obj instanceof IFile)
					return (IFile) obj;
				return null;
			}

			public void dragSetData(DragSourceEvent event) {
				event.data = getBM();
			}

			public void dragFinished(DragSourceEvent event) {
				BMTransfer.getInstance().setBM(null);
			}
		});
		tv.addDragSupport(DND.DROP_COPY, dragAdapter.getTransfers(),
				dragAdapter);
		tv.setContentProvider(new WorkbenchContentProvider());
		tv.setLabelProvider(new WorkbenchLabelProvider());
		IFolder folder = AuroraPlugin.getWorkspace().getRoot()
				.getFolder(new Path(bmHome));
		if (!folder.exists())
			return;
		tv.setInput(folder);
		tv.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		tv.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof IResource) {
					IResource resource = (IResource) element;
					boolean sameProject = isSameProject(resource);
					if (sameProject && bmHome != null) {

						Path path = new Path(bmHome);
						IPath fullPath = resource.getFullPath();
						if (resource instanceof IFile) {
							return "bm".equalsIgnoreCase(resource
									.getFileExtension())
									&& path.isPrefixOf(fullPath);
						}
						return fullPath.isPrefixOf(path)
								|| path.isPrefixOf(fullPath);
						// return true;
					}
				}
				return false;
			}

			protected boolean isSameProject(IResource resource) {
				return project != null && project.exists()
						&& project.equals(resource.getProject());
			}

		});
	}

}
