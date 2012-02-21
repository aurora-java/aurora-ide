package aurora.ide.meta.gef.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.util.DelegatingDragAdapter;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.IWorkbenchAdapter;

import uncertain.composite.CompositeMap;
import aurora.ide.bm.BMUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.dnd.BMTransfer;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.project.propertypage.ProjectPropertyPage;
import aurora.ide.search.cache.CacheManager;

public class BMViewer {

	private VScreenEditor vse;
	private IProject project;
	private TreeViewer viewer;

	private class ModelField {
		public IFile parent;
		public CompositeMap fieldMap;
		public String editor;

		public String getName() {
			return fieldMap.get("name").toString();
		}
	}

	private class ContentProvider implements ITreeContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof ViewDiagram) {
				List<IFile> modelFiles = getModelFiles((ViewDiagram) inputElement);
				return modelFiles.toArray(new IFile[modelFiles.size()]);
			}
			return null;
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof IFile) {
				return createFields((IFile) parentElement);
			}
			return null;
		}

		public Object getParent(Object element) {
			if (element instanceof ModelField) {
				return ((ModelField) element).parent;
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			return element instanceof IFile;
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
			Class adapterType = IWorkbenchAdapter.class;
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

			if (element instanceof IFile) {
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
			if (element instanceof ModelField) {
				return getImage((ModelField) element);
			}
			return null;
		}

		private Image getImage(ModelField model) {
			String type = model.editor;
			if (Input.Combo.equalsIgnoreCase(type))
				return ImagesUtils.getImage("palette/itembar_01.png");
			if (Input.CAL.equalsIgnoreCase(type)
					|| Input.DATETIMEPICKER.equalsIgnoreCase(type))
				return ImagesUtils.getImage("palette/itembar_02.png");
			if (Input.LOV.equalsIgnoreCase(type))
				return ImagesUtils.getImage("palette/itembar_03.png");
			if (Input.TEXT.equalsIgnoreCase(type))
				return ImagesUtils.getImage("palette/itembar_04.png");
			if (Input.NUMBER.equalsIgnoreCase(type))
				return ImagesUtils.getImage("palette/itembar_05.png");
			return null;
		}

		public final String getText(Object element) {
			if (element instanceof IFile) {
				return ((IFile) element).getFullPath().removeFileExtension()
						.lastSegment();
			}
			if (element instanceof ModelField) {
				return ((ModelField) element).getName();
			}
			return null;
		}
	}

	public BMViewer(Composite c, VScreenEditor vScreenEditor) {
		vse = vScreenEditor;
		init();
		configrueTreeViewer(c);
	}

	private void init() {
		Object file = vse.getEditorInput().getAdapter(IFile.class);
		if (file instanceof IFile) {
			project = ((IFile) file).getProject();
		}
	}

	public String getBMHome() {
		AuroraMetaProject amp = new AuroraMetaProject(project);
		IProject auroraProject;
		try {
			auroraProject = amp.getAuroraProject();
			return auroraProject
					.getPersistentProperty(ProjectPropertyPage.BMQN);
		} catch (ResourceNotFoundException e) {
		} catch (CoreException e) {
		}
		return null;
	}

	public IProject getAuroraProject() {
		AuroraMetaProject amp = new AuroraMetaProject(project);
		try {
			return amp.getAuroraProject();
		} catch (ResourceNotFoundException e) {
		}
		return null;
	}

	private void configrueTreeViewer(Composite c) {
		viewer = new TreeViewer(c, SWT.NONE);
		DelegatingDragAdapter dragAdapter = new DelegatingDragAdapter();
		dragAdapter.addDragSourceListener(new TransferDragSourceListener() {
			public Transfer getTransfer() {
				return BMTransfer.getInstance();
			}

			public void dragStart(DragSourceEvent event) {
				// enable drag listener if there is a viewer selection

				List<CompositeMap> data = getData();
				if (data == null)
					return;
				event.detail = DND.DROP_COPY;
				event.doit = true;
				BMTransfer.getInstance().setObject(data);
			}

			protected List<CompositeMap> getFields(Object data)
					throws CoreException, ApplicationException {
				List<CompositeMap> fs = new ArrayList<CompositeMap>();
				if (data instanceof IFile) {
					CompositeMap model = CacheManager
							.getCompositeMap((IFile) data);
					CompositeMap fields = model.getChild("fields");
					if (fields != null) {
						Iterator childIterator = fields.getChildIterator();
						while (childIterator != null && childIterator.hasNext()) {
							CompositeMap qf = (CompositeMap) childIterator
									.next();
							if ("field".equals(qf.getName())) {
								fs.add(qf);
							}
						}
					}
				}
				if (data instanceof BMViewer.ModelField) {
					fs.add(((BMViewer.ModelField) data).fieldMap);
				}
				return fs;
			}

			private List<CompositeMap> getData() {
				TreeSelection selection = (TreeSelection) viewer.getSelection();
				Object obj = selection.getFirstElement();
				try {
					return getFields(obj);
				} catch (CoreException e) {
				} catch (ApplicationException e) {
				}
				return null;
			}

			public void dragSetData(DragSourceEvent event) {
				event.data = getData();
			}

			public void dragFinished(DragSourceEvent event) {
				BMTransfer.getInstance().setObject(null);
			}
		});
		viewer.addDragSupport(DND.DROP_COPY, dragAdapter.getTransfers(),
				dragAdapter);
		viewer.setContentProvider(new ContentProvider());
		viewer.setLabelProvider(new VLabelProvider());
		refreshInput();
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void refreshInput() {
		viewer.setInput(getViewDiagram());
	}

	public List<IFile> getModelFiles(ViewDiagram viewDiagram) {
		List<IFile> files = new ArrayList<IFile>();
		if (viewDiagram.isBindTemplate()) {
			List<String> models = viewDiagram.getModels();
			for (String classPath : models) {
				try {
					IResource bm = BMUtil.getBMResourceFromClassPath(
							getAuroraProject(), classPath);
					if (bm.exists() && bm instanceof IFile) {
						files.add((IFile) bm);
					}
				} catch (ApplicationException e) {
				}
			}
		}
		try {
			IResource bm = BMUtil.getBMResourceFromClassPath(
					getAuroraProject(), "demo.hand_test");
			files.add((IFile) bm);
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return files;
	}

	public IFolder getModelFolder() {
		String bmHome = this.getBMHome();
		IProject auroraProject = this.getAuroraProject();
		if (bmHome != null && auroraProject != null) {
			return auroraProject.getWorkspace().getRoot()
					.getFolder(new Path(bmHome));
		}
		return null;
	}

	private ViewDiagram getViewDiagram() {
		Object model = vse.getDiagram();
		if (model instanceof ViewDiagram) {
			return (ViewDiagram) model;
		}
		return null;
	}

	private ModelField[] createFields(IFile model) {
		List<ModelField> result = new ArrayList<ModelField>();
		try {
			CompositeMap modelMap = CacheManager.getCompositeMap((IFile) model);
			List<ModelField> fs = new ArrayList<ModelField>();
			CompositeMap fields = modelMap.getChild("fields");
			if (fields != null) {
				Iterator childIterator = fields.getChildIterator();
				while (childIterator != null && childIterator.hasNext()) {
					CompositeMap qf = (CompositeMap) childIterator.next();
					if ("field".equals(qf.getName()) && qf.get("name") != null) {
						ModelField field = new ModelField();
						Object de = qf.get("defaultEditor");
						if (de != null)
							field.editor = de.toString();
						field.parent = model;
						field.fieldMap = qf;
						result.add(field);
					}
				}
			}
		} catch (CoreException e) {
		} catch (ApplicationException e) {
		}
		return result.toArray(new ModelField[result.size()]);
	}
}
