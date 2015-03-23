package aurora.ide.navigator.action;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.ide.IDE;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.bm.BMUtil;
import aurora.ide.bm.editor.GridDialog;
import aurora.ide.celleditor.CellInfo;
import aurora.ide.celleditor.ComboxCellEditor;
import aurora.ide.celleditor.ICellEditor;
import aurora.ide.editor.widgets.GridViewer;
import aurora.ide.editor.widgets.WizardPageRefreshable;
import aurora.ide.editor.widgets.core.IGridViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.helpers.ProjectUtil;
import aurora.ide.helpers.SystemException;

public class CreateBMByExtendAction implements IObjectActionDelegate {

	ISelection selection;
	private IResource bmResource;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
	public void run(IAction action) {
		if (!(selection instanceof IStructuredSelection)){
			DialogUtil.showErrorMessageBox(selection+"is not a IStructuredSelection!");
			return;
		}
		IStructuredSelection structured = (IStructuredSelection) selection;
		Object firstElment = structured.getFirstElement();
		if (!(firstElment instanceof IResource)){
			DialogUtil.showErrorMessageBox(firstElment+"is not a IResource!");
			return;
		}
		bmResource = (IResource) firstElment;
		if (!bmResource.getName().toLowerCase().endsWith("." + AuroraConstant.BMFileExtension)){
			DialogUtil.showErrorMessageBox(bmResource+"不是bm文件!");
			return;
		}
		CompositeMap bm = null;
		try {
			bm = AuroraResourceUtil.loadFromResource(bmResource);
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
			return;
		}
		if (bm == null)
			return;
		CreateBMByExtendWizard wizard = new CreateBMByExtendWizard(selection);
		WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
		dialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;

	}
	class CreateBMByExtendWizard extends Wizard {
		private ParentBMPage parentBMPage;
		private SelectFieldPage selectFieldPage;
		private FilterFieldPage filterFieldPage;
		private ISelection selection;
		public CreateBMByExtendWizard(ISelection selection) {
			this.selection = selection;
		}
		public void addPages() {
			parentBMPage = new ParentBMPage(selection);
			selectFieldPage = new SelectFieldPage(this);
			filterFieldPage = new FilterFieldPage(this);
			selectFieldPage.setPageComplete(false);
			addPage(parentBMPage);
			addPage(selectFieldPage);
			addPage(filterFieldPage);
		}
		public IWizardPage getNextPage(IWizardPage page) {
			if (selectFieldPage.equals(page)) {
				CompositeMap data = selectFieldPage.getSelection();
				if (data == null || data.getChildsNotNull().size() < 1) {
					selectFieldPage.setErrorMessage("请至少选择一个字段");
					selectFieldPage.setPageComplete(false);
					return null;
				}
			}
			IWizardPage nextPage = super.getNextPage(page);
			if (nextPage instanceof WizardPageRefreshable) {
				((WizardPageRefreshable) nextPage).refreshPage();
			}
			return nextPage;
		}
		public boolean performFinish() {
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(final IProgressMonitor monitor) throws InvocationTargetException {
					getShell().getDisplay().syncExec(new Runnable() {
						public void run() {
							try {
								doFinish(monitor);
							} catch (CoreException e) {
								DialogUtil.logErrorException(e);
							} finally {
								monitor.done();
							}
						}
					});
						
				}
			};
			try {
				getContainer().run(true, false, op);
			} catch (InterruptedException e) {
				DialogUtil.logErrorException(e);
				return false;
			} catch (InvocationTargetException e) {
				DialogUtil.logErrorException(e);
				return false;
			}
			return true;
		}

		/**
		 * The worker method. It will find the container, create the file if
		 * missing or just replace its contents, and open the editor on the
		 * newly created file.
		 */

		private void doFinish(final IProgressMonitor monitor) throws CoreException {
			String fileName = parentBMPage.getFileName();
			if (fileName.indexOf(".") == -1) {
				fileName = fileName + ".bm";
			}
			monitor.beginTask("Creating " + fileName, 2);
			String containerName = parentBMPage.getContainerName();
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(containerName);
			if (!resource.exists() || !(resource instanceof IContainer)) {
				DialogUtil.showErrorMessageBox(LocaleMessage.getString("container") + " \"" + containerName + "\""
						+ LocaleMessage.getString("not.exist"));
				return;
			}
			IContainer container = (IContainer) resource;
			final IFile file = container.getFile(new Path(fileName));
			try {
				InputStream stream = createFileStream();
				if (file.exists()) {
					file.setContents(stream, true, true, monitor);
				} else {
					file.create(stream, true, monitor);
				}
				stream.close();
			} catch (Throwable e) {
				DialogUtil.logErrorException(e);
			} 
			monitor.worked(1);
			monitor.setTaskName("Opening file for editing...");
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					try {
						IDE.openEditor(page, file, true);
					} catch (PartInitException e) {
						DialogUtil.logErrorException(e);
					}
				}
			});
			monitor.worked(1);
		}
		private InputStream createFileStream() throws SystemException {
			CompositeMap fields = selectFieldPage.getSelection();
			String prefix = fields.getPrefix();
			CompositeMap model = new CommentCompositeMap(prefix, AuroraConstant.BMUri, "model");
			model.put("alias", "t1");
			model.put("extendMode", "reference");
			model.put("extend", parentBMPage.getParentBMName());
			fields.setNameSpace(prefix, AuroraConstant.BMUri);
			fields.setName("fields");
			model.addChild(fields);
			CompositeMap filterfields = filterFieldPage.getSelection();
			if (filterfields != null && filterfields.getChilds() != null) {
				filterfields.setNameSpace(prefix, AuroraConstant.BMUri);
				filterfields.setName("query-fields");
				model.addChild(filterfields);
				for (Iterator it = filterfields.getChildIterator(); it.hasNext();) {
					CompositeMap filterfield = (CompositeMap) it.next();
					filterfield.setName("query-field");
					String type = filterfield.getString("type");
					filterfield.remove("type");
					if ((fields.getChildByAttrib("name", filterfield.getString("name")) == null)) {
						CompositeMap field = new CommentCompositeMap(prefix, AuroraConstant.BMUri, "field");
						field.put("name", filterfield.getString("name"));
						fields.addChild(field);
					}
					if ("=".equals(type)) {
						filterfield.put("queryOperator", "=");
					} else if ("description".equals(type)) {
						String queryExpression = "(select 1 from fnd_descriptions fd where t1.description_id=fd.description_id and fd.description_text like ${@"
								+ filterfield.getString("name") + "} and fd.language = ${/session/@lang})";
						filterfield.put("queryExpression", queryExpression);
					} else {
						filterfield.put("queryOperator", "like");
					}
					filterfield.put("field", filterfield.getString("name"));
					filterfield.remove("name");
				}
			}

			CompositeMap descIdField = fields.getChildByAttrib("name", "description_id");
			if (descIdField != null) {
				CompositeMap features = new CommentCompositeMap(BMUtil.BMPrefix, AuroraConstant.BMUri, "features");
				CompositeMap multiLanguage = new CommentCompositeMap(BMUtil.FeaturesPrefix, BMUtil.FeaturesUri,
						"multi-language-storage");
				features.addChild(multiLanguage);
				model.addChild(features);
			}
			String contents = AuroraResourceUtil.xml_decl +AuroraResourceUtil.LineSeparator+AuroraResourceUtil.getSign()+ CommentXMLOutputter.defaultInstance().toXML(model, true);
			try {
				return new ByteArrayInputStream(contents.getBytes(AuroraConstant.ENCODING));
			} catch (UnsupportedEncodingException e) {
				throw new SystemException(e);
			}
		}
		public IResource getParentBM() throws ApplicationException {
			return parentBMPage.getParentBM();
		}
		public void createPageControls(Composite pageContainer) {
		}
	}
	class ParentBMPage extends WizardPageRefreshable {
		public static final String FILE_EXT = "bm";
		private ISelection selection;
		private Text containerText;
		private Text parentBMText;
		private Text fileText;
		public ParentBMPage(ISelection selection) {
			
			super("wizardPage");
			setTitle("创建BM向导");
			setDescription("选择父目录和父BM");
			this.selection = selection;
		}

		public void createControl(Composite parent) {
			Composite container = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			container.setLayout(layout);
			layout.numColumns = 3;
			layout.verticalSpacing = 9;
			Label label = new Label(container, SWT.NULL);
			label.setText(LocaleMessage.getString("container"));

			containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			containerText.setLayoutData(gd);
			containerText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					checkPageValues();
				}
			});

			Button button = new Button(container, SWT.PUSH);
			button.setText(LocaleMessage.getString("openBrowse"));
			button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					handleBrowse();
				}
				/**
				 * Uses the standard container selection dialog to choose the
				 * new value for the container field.
				 */
				private void handleBrowse() {
					ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin
							.getWorkspace().getRoot(), false, LocaleMessage.getString("select.new.file.container"));
					if (dialog.open() == ContainerSelectionDialog.OK) {
						Object[] result = dialog.getResult();
						if (result.length == 1) {
							containerText.setText(((Path) result[0]).toString());
						}
					}
				}
			});
			label = new Label(container, SWT.NULL);
			label.setText("选择父BM");
			parentBMText = new Text(container, SWT.NONE);
			parentBMText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			parentBMText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					checkPageValues();
				}
			});
			Button uncertainProDirButton = new Button(container, SWT.PUSH);
			uncertainProDirButton.setText(LocaleMessage.getString("openBrowse"));
			uncertainProDirButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						pickParentBM();
					} catch (Exception e) {
						DialogUtil.showExceptionMessageBox(e);
					}
				}

				private void pickParentBM() throws ApplicationException {
//					IProject project = ProjectUtil.getIProjectFromSelection();
					IProject project = bmResource.getProject();
					List bmList = ProjectUtil.getBMSFromProject(project);
					CompositeMap bms = new CommentCompositeMap("bms");
					String[] columnProperties = {"name", "fullpath"};
					for (Iterator it = bmList.iterator(); it.hasNext();) {
						IResource bmFile = (IResource) it.next();
						CompositeMap child = new CommentCompositeMap("record");
						child.put("name", bmFile.getName());
						child.put("fullpath", AuroraResourceUtil.getRegisterPath((IFile) bmFile));
						bms.addChild(child);
					}
					GridViewer grid = new GridViewer(null, IGridViewer.filterBar | IGridViewer.NoToolBar);
					grid.setData(bms);
					grid.setFilterColumn("name");
					grid.setColumnNames(columnProperties);
					GridDialog dialog = new GridDialog(new Shell(), grid);
					if (dialog.open() == Window.OK) {
						String value = dialog.getSelected().getString("fullpath");
						if (value != null)
							parentBMText.setText(value);
					}

				}
			});
			label = new Label(container, SWT.NULL);
			label.setText(LocaleMessage.getString("file.name"));

			fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			fileText.setLayoutData(gd);
			fileText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					checkPageValues();
				}
			});
			initPageValues();
			checkPageValues();
			setControl(container);
		}

		/**
		 * Tests if the current workbench selection is a suitable container to
		 * use.
		 */

		public void initPageValues(){
			if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
				IStructuredSelection ssel = (IStructuredSelection) selection;
				if (ssel.size() > 1)
					return;
				Object obj = ssel.getFirstElement();
				if (obj instanceof IResource) {
					IResource bm = (IResource) obj;
					IContainer container = bm.getParent();
					containerText.setText(container.getFullPath().toOSString());
					String registerPath;
					try {
						registerPath = AuroraResourceUtil.getRegisterPath((IFile) bm);
					} catch (ApplicationException e) {
						DialogUtil.showExceptionMessageBox(e);
						return;
					}
					parentBMText.setText(registerPath);
				}
			}
		}
		public void checkPageValues() {
			IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(containerText.getText());
			String containerPath = containerText.getText();
			if (containerPath == null || "".equals(containerPath)) {
				updatePageStatus(LocaleMessage.getString("file.container.must.be.specified"));
				return;
			}
			if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
				updatePageStatus(LocaleMessage.getString("file.container.must.exist"));
				return;
			}
			String parentBMPath = parentBMText.getText();
			if (parentBMPath == null || "".equals(parentBMPath)) {
				updatePageStatus("必须指定父BM!");
				return;
			}
			IResource bmFile = null;
			try {
				bmFile = BMUtil.getBMResourceFromClassPath(parentBMPath);
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
			if (bmFile == null) {
				updatePageStatus("此BM文件不存在!");
				return;
			}
			String fileName = getFileName();
			if (fileName != null && !fileName.equals("") && ((IContainer)container).getFile(new Path(fileName)).exists()) {
				updatePageStatus(LocaleMessage.getString("filename.used"));
				return;
			}
			if (!container.isAccessible()) {
				updatePageStatus(LocaleMessage.getString("project.must.be.writable"));
				return;
			}
			if (fileName.length() == 0) {
				updatePageStatus(LocaleMessage.getString("file.name.must.be.specified"));
				return;
			}

			if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
				updatePageStatus(LocaleMessage.getString("file.name.must.be.valid"));
				return;
			}
			int dotLoc = fileName.lastIndexOf('.');
			if (dotLoc != -1) {
				String ext = fileName.substring(dotLoc + 1);
				if (ext.equalsIgnoreCase("bm") == false) {
					updatePageStatus(LocaleMessage.getString("file.extension.must.be.bm"));
					return;
				}
			}
			updatePageStatus(null);
		}
		public IResource getParentBM() throws ApplicationException {
			String parentBMPath = parentBMText.getText();
			return BMUtil.getBMResourceFromClassPath(parentBMPath);
		}
		public String getParentBMName() {
			return parentBMText.getText();
		}
		public String getFileName() {
			String fileName = fileText.getText();
			if(fileName.indexOf(".")==-1){
				fileName = fileName+"."+FILE_EXT;
			}
			return fileName;
		}
		public String getContainerName() {
			return containerText.getText();
		}
	}
	class SelectFieldPage extends WizardPageRefreshable {
		private GridViewer grid;
		private CreateBMByExtendWizard wizard;
		protected SelectFieldPage(CreateBMByExtendWizard wizard) {
			super("wizardPage");
			setTitle("创建BM向导");
			setDescription("选择查询字段");
			this.wizard = wizard;
		}

		public void createControl(Composite parent) {
			Composite content = new Composite(parent, SWT.NONE);
			content.setLayout(new GridLayout());
			String[] columnProperties = {"name"};
			grid = new GridViewer(columnProperties, IGridViewer.isMulti | IGridViewer.isAllChecked);
			try {
				grid.createViewer(content);
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
			try {
				IResource resource = wizard.getParentBM();
				CompositeMap data = AuroraResourceUtil.loadFromResource(resource);
				if (data == null) {
					DialogUtil.showErrorMessageBox("此BM不存在!");
				}
				grid.setData(getSimpleFields(data));
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
			setPageComplete(true);
			setControl(content);
		}
		public void refreshPage() {
			if(!isInit()){
				return;
			}
			try {
				IResource resource = wizard.getParentBM();
				CompositeMap data = AuroraResourceUtil.loadFromResource(resource);
				if (data == null) {
					DialogUtil.showErrorMessageBox("此BM不存在!");
					return;
				}
				CompositeMap field = data.getChild("fields");
				grid.setData(field);
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
			super.refreshPage();
		}
		public CompositeMap getSelection() {
			return grid.getSelection();
		}

		public void checkPageValues() {
		}

		public void initPageValues() {
		}
	}
	class FilterFieldPage extends WizardPageRefreshable {
		private GridViewer grid;
		private CreateBMByExtendWizard wizard;
		protected FilterFieldPage(CreateBMByExtendWizard wizard) {
			super("wizardPage");
			setTitle("创建BM向导");
			setDescription("选择作为过滤条件的字段");
			this.wizard = wizard;
		}

		public void createControl(Composite parent) {
			Composite content = new Composite(parent, SWT.NONE);
			content.setLayout(new GridLayout());
			String[] columnProperties = {"name", "type"};
			String[] columnTitles= {"字段名", "过滤操作"};
			grid = new GridViewer(columnProperties, IGridViewer.isMulti | IGridViewer.isAllChecked
					| IGridViewer.isOnlyUpdate);
			grid.setColumnTitles(columnTitles);
			try {
				grid.createViewer(content);
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
			CellEditor[] celleditors = new CellEditor[columnProperties.length];
			CellInfo cellProperties = new CellInfo(grid, "type", false);
			cellProperties.setItems(new String[]{"=", "like", "description"});
			ICellEditor cellEditor = new ComboxCellEditor(cellProperties);
			cellEditor.init();
			celleditors[columnProperties.length-1] = cellEditor.getCellEditor();
			grid.addEditor("type", cellEditor);
			grid.setCellEditors(celleditors);
			try {
				IResource resource = wizard.getParentBM();
				CompositeMap data = AuroraResourceUtil.loadFromResource(resource);
				if (data == null) {
					DialogUtil.showErrorMessageBox("此BM不存在!");
				}
				grid.setData(getSimpleFields(data));
			} catch (ApplicationException e) {
				DialogUtil.logErrorException(e);
			}
			setControl(content);
		}
		public void refreshPage() {
			if (getControl() == null)
				return;
			try {
				IResource resource = wizard.getParentBM();
				CompositeMap data = AuroraResourceUtil.loadFromResource(resource);
				if (data == null) {
					DialogUtil.showErrorMessageBox("此BM不存在!");
				}
				CompositeMap field = data.getChild("fields");
				grid.setData(getSimpleFields(field));
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
			super.refreshPage();
		}
		public CompositeMap getSelection() {
			if(grid == null)
				return null;
			return grid.getSelection();
		}

		public void checkPageValues() {}

		public void initPageValues() {}
	}
	public CompositeMap getSimpleFields(CompositeMap bm) {
		CompositeMap fields = bm.getChild("fields");
		CompositeMap simplefiels = new CommentCompositeMap(fields.getPrefix(), fields.getNamespaceURI(), "fields");
		for (Iterator it = fields.getChildIterator(); it.hasNext();) {
			CompositeMap field = (CompositeMap) it.next();
			CompositeMap simplefiel = new CommentCompositeMap(field.getPrefix(), field.getNamespaceURI(), "field");
			simplefiel.put("name", field.getString("name"));
			simplefiels.addChild(simplefiel);
		}
		return simplefiels;
	}
}
