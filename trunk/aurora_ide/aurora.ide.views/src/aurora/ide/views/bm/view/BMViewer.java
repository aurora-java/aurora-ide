package aurora.ide.views.bm.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.util.DelegatingDragAdapter;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.StyledString;
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
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchContentProvider;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.gef.Util;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.views.bm.BMTransfer;

public class BMViewer {

	private IProject project;
	private TreeViewer viewer;

	private class ModelField {
		public IFile parent;
		public CompositeMap fieldMap;
		public String editor;
		static final String REF_FIELD = "ref-field";
		static final String QUERY_FIELD = "query-field";

		public ModelField(IFile parent, CompositeMap fieldMap, String editor) {
			super();
			this.parent = parent;
			this.fieldMap = fieldMap;
			this.editor = editor;
		}

		public String getPrompt() {
			String prompt = null;
			if (REF_FIELD.equals(editor)) {
				if (project != null) {
					prompt = Util
							.getRefFieldSourcePrompt(project, fieldMap, "");
				}
			} else {
				prompt = Util.getPrompt(fieldMap, "");
			}
			return prompt == null ? fieldMap.getString("prompt", "") : prompt;
		}

		public String getName() {
			String fname = fieldMap.getString("name", "");
			return "[" + fname + "]";
		}
	}

	private class ContentProvider extends WorkbenchContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			IProject auroraProject = project;
			if (auroraProject == null) {
				return new String[] { "请选择Aurora工程" };
			}
			if (inputElement instanceof IFolder) {

				List<IFile> modelFiles = getModelFiles((IFolder) inputElement);
				if (modelFiles.size() == 0) {
					return new String[] {  "Model不存在"};
				}
				if (modelFiles.size() > 500) {
					try {
						return ((IFolder) inputElement).members();
					} catch (CoreException e) {
						DialogUtil.logErrorException(e);
						e.printStackTrace();
					}
				}
				return modelFiles.toArray(new IFile[modelFiles.size()]);
			}
			return null;
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof IFile) {
				// return createFields((IFile) parentElement);
			}
			if (parentElement instanceof IContainer) {
				IContainer c = (IContainer) parentElement;
				try {
					return c.members();
				} catch (CoreException e) {
					DialogUtil.logErrorException(e);
					e.printStackTrace();
				}
			}
			return null;
		}

		public Object getParent(Object element) {
			if (element instanceof ModelField) {
				return ((ModelField) element).parent;
			}
			if (element instanceof IFile) {
				return ((IFile) element).getParent();
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			return element instanceof IContainer;
		}
	}

	private class VLabelProvider extends LabelProvider implements
			IStyledLabelProvider {
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
			if (element instanceof ModelField) {
				return getImage((ModelField) element);
			}
			return null;
		}

		private Image getImage(ModelField model) {
			return null;
			// String type = model.editor;
			// if (Input.Combo.equalsIgnoreCase(type))
			//				return ImagesUtils.getImage("palette/itembar_01.png"); //$NON-NLS-1$
			// if (Input.CAL.equalsIgnoreCase(type)
			// || Input.DATETIMEPICKER.equalsIgnoreCase(type))
			//				return ImagesUtils.getImage("palette/itembar_02.png"); //$NON-NLS-1$
			// if (Input.LOV.equalsIgnoreCase(type))
			//				return ImagesUtils.getImage("palette/itembar_03.png"); //$NON-NLS-1$
			// if (Input.TEXT.equalsIgnoreCase(type))
			//				return ImagesUtils.getImage("palette/itembar_04.png"); //$NON-NLS-1$
			// if (Input.NUMBER.equalsIgnoreCase(type))
			// return ImagesUtils.getImage("palette/itembar_05.png");
			// if (CheckBox.CHECKBOX.equalsIgnoreCase(type))
			// return ImagesUtils.getImage("palette/checkbox_01.png");
			// if (ModelField.REF_FIELD.equalsIgnoreCase(type))
			// return ImagesUtils.getImage("palette/ref.png");
			// if (ModelField.QUERY_FIELD.equalsIgnoreCase(type))
			//				return ImagesUtils.getImage("palette/search.png");//$NON-NLS-1$
			//			return ImagesUtils.getImage("palette/itembar_04.png"); //$NON-NLS-1$
		}

		public final String getText(Object element) {
			if (element instanceof IFolder)
				return ((IFolder) element).getName();
			if(element instanceof String)
				return element.toString();
			return getStyledText(element).getString();
		}

		public StyledString getStyledText(Object element) {
			StyledString s = new StyledString();
			if (element instanceof IFolder) {
				s.append(((IFolder) element).getName());
				return s;
			}

			if (element instanceof IFile) {
				s.append(((IFile) element).getFullPath().removeFileExtension()
						.lastSegment());
				s.append(" ");
				String pkg = aurora.ide.search.core.Util
						.getPKG(((IFile) element).getProjectRelativePath());
				s.append("[" + pkg + "]", StyledString.DECORATIONS_STYLER);
				return s;
			}
			if (element instanceof ModelField) {
				s.append(((ModelField) element).getPrompt());
				s.append(" ");
				s.append(((ModelField) element).getName(),
						StyledString.QUALIFIER_STYLER);
				return s;
			}
			if (element instanceof String) {
				return s.append(element.toString());
			}

			return s;
		}
	}

	private class CustomPatternFilter extends PatternFilter {

		public CustomPatternFilter() {
			super();
		}

		@Override
		public boolean isLeafMatch(Viewer viewer, Object element) {
			if (!(element instanceof IFile)) {
				return false;
			}
			String labelText = ((DecoratingStyledCellLabelProvider) ((StructuredViewer) viewer)
					.getLabelProvider()).getStyledStringProvider()
					.getStyledText(element).getString();
			if (labelText == null) {
				return false;
			}
			return wordMatches(labelText);
		}

	}

	public BMViewer(Composite c) {
		configrueTreeViewer(c);
	}

	private void configrueTreeViewer(Composite c) {
		FilteredTree ff = new FilteredTree(c, SWT.SINGLE,
				new CustomPatternFilter(), true);
		setViewer(ff.getViewer());
		DelegatingDragAdapter dragAdapter = new DelegatingDragAdapter();
		dragAdapter.addDragSourceListener(new TransferDragSourceListener() {
			public Transfer getTransfer() {
				return BMTransfer.getInstance();
			}

			public void dragStart(DragSourceEvent event) {
				// enable drag listener if there is a viewer selection

				List<CompositeMap> data = getData();
				if (data == null){
					event.doit = false;
					return;
				}
				event.detail = DND.DROP_COPY;
				event.doit = true;
				BMTransfer.getInstance().setObject(data);
			}

			protected List<CompositeMap> getFields(Object data)
					throws CoreException, ApplicationException {
				if(data instanceof IContainer){
					return null;
				}
				List<CompositeMap> fs = new ArrayList<CompositeMap>();
				if (data instanceof IFile) {
					ModelField[] createFields = createFields((IFile) data);
					
					for (ModelField mf : createFields) {
						fs.add(getFieldMap(mf));
					}
				}
				if (data instanceof BMViewer.ModelField) {
					CompositeMap fieldMap = getFieldMap((ModelField) data);
					fs.add(fieldMap);
				}
				return fs;
			}

			private CompositeMap getFieldMap(ModelField data) {
				CompositeMap fieldMap = data.fieldMap;
				if (ModelField.REF_FIELD.equals(data.editor)) {
					fieldMap = (CompositeMap) fieldMap.clone();
					fieldMap.put("prompt", data.getPrompt());
				}
				fieldMap.put("model",
						aurora.ide.search.core.Util.toBMPKG(data.parent));
				return fieldMap;
			}

			private List<CompositeMap> getData() {
				TreeSelection selection = (TreeSelection) getViewer().getSelection();
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
		getViewer().addDragSupport(DND.DROP_COPY, dragAdapter.getTransfers(),
				dragAdapter);
		getViewer().setContentProvider(new ContentProvider());

		DecoratingStyledCellLabelProvider labelProvider = new DecoratingStyledCellLabelProvider(
				new VLabelProvider(), PlatformUI.getWorkbench()
						.getDecoratorManager().getLabelDecorator(), null);

		getViewer().setLabelProvider(labelProvider);

		// refreshInput();
		getViewer().getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		// hookContextMenu();
	}

	// private void hookContextMenu() {
	// MenuManager menuMgr = new MenuManager("#PopupMenu");
	// menuMgr.setRemoveAllWhenShown(true);
	// menuMgr.addMenuListener(new IMenuListener() {
	// public void menuAboutToShow(IMenuManager manager) {
	// Action addBM = new Action() {
	// public void run() {
	// addFile();
	// vse.markDirty();
	// }
	// };
	// addBM.setText("增加");
	// addBM.setImageDescriptor(ImagesUtils
	// .getImageDescriptor("palette/toolbar_btn_01.png"));
	// manager.add(addBM);
	// Action delBM = new Action() {
	// public void run() {
	// Object selectObject = getSelectObject();
	// if (selectObject instanceof IFile) {
	// String bmpkg = aurora.ide.search.core.Util
	// .toBMPKG((IFile) selectObject);
	// getViewDiagram().getUnBindModels().remove(bmpkg);
	// vse.markDirty();
	// refreshInput();
	// }
	// }
	// };
	// delBM.setText("删除");
	// delBM.setImageDescriptor(ImagesUtils
	// .getImageDescriptor("delete.gif"));
	// Object selectObject = getSelectObject();
	// if (selectObject instanceof IFile)
	// manager.add(delBM);
	// }
	// });
	// Menu menu = menuMgr.createContextMenu(viewer.getControl());
	// viewer.getControl().setMenu(menu);
	// }

	// public IFile openResourceSelector(Shell shell, String[] exts,
	// IContainer root) {
	// ResourceSelector fss = new ResourceSelector(shell);
	// fss.setExtFilter(exts);
	// fss.setInput((IContainer) root);
	// Object obj = fss.getSelection();
	// if (!(obj instanceof IFile)) {
	// return null;
	// }
	// return (IFile) obj;
	// }
	//
	// protected void addFile() {
	// String[] as = { "bm" };
	// IContainer bmHome = getModelFolder();
	// if (bmHome == null || !bmHome.exists()) {
	// DialogUtil.showWarningMessageBox("找不到BM主目录，需要配置关联工程。");
	// return;
	// }
	// IFile file = openResourceSelector(this.viewer.getControl().getShell(),
	// as, bmHome);
	// if (file == null || !file.exists())
	// return;
	// String bmpkg = aurora.ide.search.core.Util.toBMPKG(file);
	// getViewDiagram().addUnBindModel(bmpkg);
	// refreshInput();
	// }

	public void refreshInput() {
		// viewer.setInput(getViewDiagram());
	}

	public List<IFile> getModelFiles(IFolder folder) {
		final List<IFile> files = new ArrayList<IFile>();
		try {
			folder.accept(new IResourceVisitor() {
				@Override
				public boolean visit(IResource resource) throws CoreException {

					if (resource instanceof IFile
							&& "bm".equalsIgnoreCase(resource
									.getFileExtension())) {
						files.add((IFile) resource);
					}
					return true;
				}
			});
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
			e.printStackTrace();
		}
		return files;
	}

	private ModelField[] createFields(IFile model) {
		List<ModelField> result = new ArrayList<ModelField>();
		try {
			CompositeMap modelMap = CacheManager
					.getWholeBMCompositeMap((IFile) model);

			BMCompositeMap bmMap = new BMCompositeMap(modelMap);
			List<CompositeMap> fields = bmMap.getFields();
			for (CompositeMap qf : fields) {
				if ("field".equals(qf.getName()) && qf.get("name") != null && !isPK(bmMap, qf)) { //$NON-NLS-1$ //$NON-NLS-2$
					ModelField field = new ModelField(model, qf,
							Util.getType(qf));
					result.add(field);
				}
			}
			List<CompositeMap> refFields = bmMap.getRefFields();
			for (CompositeMap qf : refFields) {
				if ("ref-field".equals(qf.getName()) && qf.get("name") != null) { //$NON-NLS-1$ //$NON-NLS-2$
					ModelField field = new ModelField(model, qf,
							ModelField.REF_FIELD);
					result.add(field);
				}
			}
			List<CompositeMap> queryFields = bmMap.getQueryFields();
			for (CompositeMap qf : queryFields) {
				if ("query-field".equals(qf.getName())
						&& qf.get("name") != null) { //$NON-NLS-1$ //$NON-NLS-2$
					ModelField field = new ModelField(model, qf,
							ModelField.QUERY_FIELD);
					result.add(field);
				}
			}
		} catch (CoreException e) {
		} catch (ApplicationException e) {
		}
		return result.toArray(new ModelField[result.size()]);
	}

	private boolean isPK(BMCompositeMap bmMap, CompositeMap qf) {
		List<CompositeMap> primaryKeys = bmMap.getPrimaryKeys();
		for (CompositeMap compositeMap : primaryKeys) {
			if (compositeMap.getString("name", "").equals(qf.get("name"))) {
				return true;
			}
		}
		return qf.getBoolean("isprimarykey", false);
	}

	public void setInput(IFolder web_classes) {
		this.project = web_classes.getProject();
		this.getViewer().setInput(web_classes);
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	private void setViewer(TreeViewer viewer) {
		this.viewer = viewer;
	}
	public Object getSelectObject() {
		TreeItem[] items = viewer.getTree().getSelection();
		if (items.length > 0) {
			return items[0].getData();
		}
		return null;
	}
}
