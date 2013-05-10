package aurora.ide.meta.gef.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.util.DelegatingDragAdapter;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;

import uncertain.composite.CompositeMap;
import aurora.ide.bm.BMUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.Util;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.editors.dnd.BMTransfer;
import aurora.ide.meta.gef.editors.property.ResourceSelector;
import aurora.ide.meta.gef.i18n.Messages;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.project.propertypage.ProjectPropertyPage;
import aurora.ide.search.cache.CacheManager;
import aurora.plugin.source.gen.screen.model.CheckBox;
import aurora.plugin.source.gen.screen.model.Input;
import aurora.plugin.source.gen.screen.model.ScreenBody;

public class BMViewer {

	private VScreenEditor vse;
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
				prompt = Util.getRefFieldSourcePrompt(getAuroraProject(),
						fieldMap,"");
			} else {
				prompt = Util.getPrompt(fieldMap,"");
			}
			return prompt == null ? fieldMap.getString("prompt", "") : prompt;
		}

		public String getName() {
			String fname = fieldMap.getString("name", "");
			return "[" + fname + "]";
		}
	}

	private class ContentProvider implements ITreeContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			IProject auroraProject = getAuroraProject();
			if (auroraProject == null) {
				return new String[] { Messages.BMViewer_No_aurora_project };
			}

			if (inputElement instanceof ScreenBody) {
				if (!((ScreenBody) inputElement).isBindTemplate()) {
					// return new String[] { Messages.BMViewer_No_template };
				}

				List<IFile> modelFiles = getModelFiles((ScreenBody) inputElement);

				if (modelFiles.size() == 0) {
					return new String[] { Messages.BMViewer_No_model };
				}
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
				return PrototypeImagesUtils.getImage("palette/itembar_01.png"); //$NON-NLS-1$
			if (Input.DATE_PICKER.equalsIgnoreCase(type)
					|| Input.DATETIMEPICKER.equalsIgnoreCase(type))
				return PrototypeImagesUtils.getImage("palette/itembar_02.png"); //$NON-NLS-1$
			if (Input.LOV.equalsIgnoreCase(type))
				return PrototypeImagesUtils.getImage("palette/itembar_03.png"); //$NON-NLS-1$
			if (Input.TEXT.equalsIgnoreCase(type))
				return PrototypeImagesUtils.getImage("palette/itembar_04.png"); //$NON-NLS-1$
			if (Input.NUMBER.equalsIgnoreCase(type))
				return PrototypeImagesUtils.getImage("palette/itembar_05.png");
			if (CheckBox.CHECKBOX.equalsIgnoreCase(type))
				return PrototypeImagesUtils.getImage("palette/checkbox_01.png");
			if (ModelField.REF_FIELD.equalsIgnoreCase(type))
				return PrototypeImagesUtils.getImage("palette/ref.png");
			if (ModelField.QUERY_FIELD.equalsIgnoreCase(type))
				return PrototypeImagesUtils.getImage("palette/search.png");//$NON-NLS-1$
			return PrototypeImagesUtils.getImage("palette/itembar_04.png"); //$NON-NLS-1$
		}

		public final String getText(Object element) {
			return getStyledText(element).getString();
		}

		public StyledString getStyledText(Object element) {
			StyledString s = new StyledString();
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

	public BMViewer(Composite c, VScreenEditor vScreenEditor) {
		vse = vScreenEditor;
		init();
		configrueTreeViewer(c);
	}

	private void init() {
		IEditorInput editorInput = vse.getEditorInput();
		if (editorInput == null)
			return;
		Object file = editorInput.getAdapter(IFile.class);
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
					ModelField[] createFields = createFields((IFile) data);
					// CompositeMap model = CacheManager
					// .getCompositeMap((IFile) data);
					//					CompositeMap fields = model.getChild("fields"); //$NON-NLS-1$
					// if (fields != null) {
					// Iterator childIterator = fields.getChildIterator();
					// while (childIterator != null && childIterator.hasNext())
					// {
					// CompositeMap qf = (CompositeMap) childIterator
					// .next();
					//							if ("field".equals(qf.getName())) { //$NON-NLS-1$
					// qf = (CompositeMap) qf.clone();
					// qf.put("model", aurora.ide.search.core.Util
					// .toBMPKG((IFile) data));
					// fs.add(qf);
					// }
					// }
					// }
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
		viewer.setLabelProvider(new DecoratingStyledCellLabelProvider(
				new VLabelProvider(), PlatformUI.getWorkbench()
						.getDecoratorManager().getLabelDecorator(), null));
		refreshInput();
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		hookContextMenu();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				Action addBM = new Action() {
					public void run() {
						addFile();
						vse.markDirty();
					}
				};
				addBM.setText("增加");
				addBM.setImageDescriptor(PrototypeImagesUtils
						.getImageDescriptor("palette/toolbar_btn_01.png"));
				manager.add(addBM);
				Action delBM = new Action() {
					public void run() {
						Object selectObject = getSelectObject();
						if (selectObject instanceof IFile) {
							String bmpkg = aurora.ide.search.core.Util
									.toBMPKG((IFile) selectObject);
							getViewDiagram().getUnBindModels().remove(bmpkg);
							vse.markDirty();
							refreshInput();
						}
					}
				};
				delBM.setText("删除");
				delBM.setImageDescriptor(PrototypeImagesUtils
						.getImageDescriptor("delete.gif"));
				Object selectObject = getSelectObject();
				if (selectObject instanceof IFile)
					manager.add(delBM);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}

	public IFile openResourceSelector(Shell shell, String[] exts,
			IContainer root) {
		ResourceSelector fss = new ResourceSelector(shell);
		fss.setExtFilter(exts);
		fss.setInput((IContainer) root);
		Object obj = fss.getSelection();
		if (!(obj instanceof IFile)) {
			return null;
		}
		return (IFile) obj;
	}

	protected void addFile() {
		String[] as = { "bm" };
		IContainer bmHome = getModelFolder();
		if (bmHome == null || !bmHome.exists()) {
			DialogUtil.showWarningMessageBox("找不到BM主目录，需要配置关联工程。");
			return;
		}
		IFile file = openResourceSelector(this.viewer.getControl().getShell(),
				as, bmHome);
		if (file == null || !file.exists())
			return;
		String bmpkg = aurora.ide.search.core.Util.toBMPKG(file);
		getViewDiagram().addUnBindModel(bmpkg);
		refreshInput();
	}

	public void refreshInput() {
		viewer.setInput(getViewDiagram());
	}

	public List<IFile> getModelFiles(ScreenBody viewDiagram) {
		List<IFile> files = new ArrayList<IFile>();
		// if (viewDiagram.isBindTemplate()) {
		List<String> models = viewDiagram.getModels();
		for (String classPath : models) {
			try {
				IResource bm = BMUtil.getBMResourceFromClassPath(
						getAuroraProject(), classPath);
				if (bm instanceof IFile && bm.exists()
						&& !files.contains((IFile) bm)) {
					files.add((IFile) bm);
				}
			} catch (ApplicationException e) {
			}
			// }
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

	private ScreenBody getViewDiagram() {
		Object model = vse.getDiagram();
		if (model instanceof ScreenBody) {
			return (ScreenBody) model;
		}
		return null;
	}

	private ModelField[] createFields(IFile model) {
		List<ModelField> result = new ArrayList<ModelField>();
		try {
			CompositeMap modelMap = CacheManager
					.getWholeBMCompositeMap((IFile) model);

			BMCompositeMap bmMap = new BMCompositeMap(modelMap);
			List<CompositeMap> fields = bmMap.getFields();
			for (CompositeMap qf : fields) {
				if ("field".equals(qf.getName()) && qf.get("name") != null) { //$NON-NLS-1$ //$NON-NLS-2$
					ModelField field = new ModelField(model, qf,
							Util.getType(qf));
					result.add(field);

					// lookupfield
					String lookupcode = Util.getValueIgnoreCase(qf,
							"lookupcode");
					String lookupfield = Util.getValueIgnoreCase(qf,
							"lookupfield");
					if (lookupcode != null && lookupfield != null) {
						CompositeMap clone = (CompositeMap) qf.clone();
						clone.remove("defaulteditor");
						clone.remove("defaultEditor");
						clone.put("name", lookupfield);
						field = new ModelField(model, clone,
								ModelField.REF_FIELD);
						result.add(field);
					}
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
				if ("query-field".equals(qf.getName()) && qf.get("name") != null) { //$NON-NLS-1$ //$NON-NLS-2$
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

	private Object getSelectObject() {
		TreeItem[] items = viewer.getTree().getSelection();
		if (items.length > 0) {
			return items[0].getData();
		}
		return null;
	}
}
