package aurora.ide.meta.gef.editors.wizard;

import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.gef.editors.template.parse.TemplateHelper;
import aurora.ide.meta.gef.editors.wizard.dialog.TComposite;
import aurora.ide.meta.gef.i18n.Messages;

public class NewWizardPage extends WizardPage {
	private Text txtPath;
	private Text txtFile;

	private Template template;
	private IProject metaProject;
	private IResource metaFolder;

	public NewWizardPage() {
		super("aurora.wizard.new.Page"); //$NON-NLS-1$
		setTitle(Messages.NewWizardPage_Title);
		setDescription(Messages.NewWizardPage_Desc);
		setPageComplete(false);
		initMetaFolder();
	}

	private void initMetaFolder() {
		IResource r = getMetaFolderBySelection();
		if (r == null) {
			r = getMetaProjectByEditor();
		} else if (r instanceof IFile) {
			r = ((IFile) r).getParent();
		}
		metaFolder = r;
	}

	private IResource getMetaFolderBySelection() {
		IResource r = null;
		try {
			ISelection obj = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
			if (obj instanceof StructuredSelection) {
				StructuredSelection ts = (StructuredSelection) obj;
				if (!ts.isEmpty() && (ts.getFirstElement() instanceof IResource)) {
					r = (IResource) ts.getFirstElement();
					if (TemplateHelper.isMetaProject(r)) { //$NON-NLS-1$ //$NON-NLS-2$
						return r;
					}
				}
			}
		} catch (NullPointerException e1) {
		}
		return null;
	}

	private IProject getMetaProjectByEditor() {
		IResource r = null;
		try {
			r = (IResource) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
		} catch (NullPointerException e) {
			return null;
		}
		if(r==null){
			return null;
		}
		if (TemplateHelper.isMetaProject(r)) {
			return r.getProject();
		}
		return null;
	}

	private void createText(Composite composite) {
		Composite container = new Composite(composite, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lbl = new Label(container, SWT.NONE);
		lbl.setText(Messages.NewWizardPage_Folder);
		txtPath = new Text(container, SWT.BORDER);
		txtPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button btn = new Button(container, SWT.NONE);
		btn.setText(Messages.NewWizardPage_Exploer);
		lbl = new Label(container, SWT.NONE);
		lbl.setText(Messages.NewWizardPage_FileName);
		txtFile = new Text(container, SWT.BORDER);
		txtFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		txtPath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		txtFile.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot().getProject(), true, ""); //$NON-NLS-1$
				dialog.setTitle(Messages.NewWizardPage_folder_4);
				if (dialog.open() == Dialog.OK && dialog.getResult().length != 0) {
					String path = dialog.getResult()[0].toString();
					IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(path));
					if (!setPath(container)) {
						txtPath.setText(path);
					} else {
						txtFile.setFocus();
					}
				}
			}
		});
	}

	private boolean setPath(IResource container) {
		String path = "";
		if (container instanceof IProject) {
			IResource r = ((IProject) container).findMember("ui_prototype");
			if (r != null) {
				path = r.getProject().getName() + "/" + r.getProjectRelativePath().toString();
				txtPath.setText(path);
				return true;
			}
		} else if ((container instanceof IFolder) && TemplateHelper.isMetaProject(container)) {
			if (container.getProjectRelativePath().toString().indexOf("ui_prototype") >= 0) {
				path = container.getProject().getName() + "/" + container.getProjectRelativePath().toString();
				txtPath.setText(path);
				return true;
			}
		}
		return false;
	}

	private void createTemplate(Composite composite, Map<String, java.util.List<Template>> tempMap) {
		TComposite tComposite = new TComposite(composite, SWT.BORDER, tempMap);
		tComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		template = tComposite.getSelection();
		tComposite.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TComposite t = (TComposite) e.getSource();
				template = t.getSelection();
				if (template == null) {
					return;
				}
				setDescription(template.getDescription());
				if (metaProject != null) {
					((SelectModelWizardPage) getNextPage()).createDynamicTextComponents(template);
				}
			}
		});
		composite.layout(true);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		setControl(container);
		createText(container);
		Group composite = new Group(container, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setText(Messages.NewWizardPage_Template);

		createTemplate(composite, TemplateHelper.loadTemplate());

		if (setPath(metaFolder)) {
			txtFile.setFocus();
		}
	}

	public String getPath() {
		return txtPath.getText().trim();
	}

	public String getFileName() {
		String fileName = txtFile.getText().trim();
		if (fileName.length() > 0 && fileName.indexOf(".") == -1) { //$NON-NLS-1$
			fileName = fileName + ".uip"; //$NON-NLS-1$
		}
		return fileName;
	}

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getPath()));
		String fileName = getFileName();
		int dotLoc = fileName.lastIndexOf('.');
		if (getPath().length() == 0) {
			updateStatus(Messages.NewWizardPage_folder);
		} else if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus(Messages.NewWizardPage_folder_2);
		} else if (!container.isAccessible()) {
			updateStatus(Messages.NewWizardPage_Project);
		} else if (!TemplateHelper.isMetaProject(container)) { //$NON-NLS-1$
			updateStatus(Messages.NewWizardPage_Project_2);
		} else if (getPath().lastIndexOf("ui_prototype") == -1) { //$NON-NLS-1$
			updateStatus(Messages.NewWizardPage__folder_3);
		} else {
			setMetaProject(container.getProject());
			if (fileName != null && !fileName.equals("") && ((IContainer) container).getFile(new Path(fileName)).exists()) { //$NON-NLS-1$
				updateStatus(Messages.NewWizardPage_File);
			} else if (fileName.length() == 0) {
				updateStatus(Messages.NewWizardPage_File_1);
			} else if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
				updateStatus(Messages.NewWizardPage_File_2);
			} else if (dotLoc != -1 && (!fileName.substring(dotLoc + 1).equalsIgnoreCase("uip"))) { //$NON-NLS-1$
				updateStatus(Messages.NewWizardPage_File_3);
			} else {
				updateStatus(null);
				setDescription(template.getDescription());
				((SelectModelWizardPage) getNextPage()).createDynamicTextComponents(template);
			}
			return;
		}
		setMetaProject(null);
	}

	public void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public IProject getMetaProject() {
		return metaProject;
	}

	public void setMetaProject(IProject metaProject) {
		this.metaProject = metaProject;
	}
}
