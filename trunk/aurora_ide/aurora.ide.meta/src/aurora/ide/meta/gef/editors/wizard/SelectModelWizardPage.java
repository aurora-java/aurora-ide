package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.AuroraProjectNature;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.gef.editors.template.handle.TemplateFactory;
import aurora.ide.meta.gef.editors.template.handle.TemplateHandle;
import aurora.ide.meta.gef.editors.template.handle.TemplateHelper;
import aurora.ide.meta.gef.editors.wizard.dialog.SelectModelDialog;
import aurora.ide.meta.gef.i18n.Messages;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public class SelectModelWizardPage extends WizardPage {

	private ViewDiagram viewDiagram;

	private Composite composite;
	private IPath bmPath;

	public SelectModelWizardPage() {
		super("aurora.wizard.select.Page"); //$NON-NLS-1$
		setTitle(Messages.SettingWizardPage_Title);
		setDescription(Messages.SettingWizardPage_Model_Bind);
	}

	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		setControl(composite);
	}

	public void setBMPath(IProject metaProject) {
		AuroraMetaProject metaPro = new AuroraMetaProject(metaProject);
		try {
			if (metaPro == null || metaPro.getAuroraProject() == null) {
				bmPath = null;
				return;
			}
			IProject auroraPro = metaPro.getAuroraProject();
			if (auroraPro.hasNature(AuroraProjectNature.ID)) {
				bmPath = new Path(auroraPro.getPersistentProperty(ProjectPropertyPage.BMQN));
				return;
			}
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		bmPath = null;
	}

	public void createDynamicTextComponents(Template t) {
		this.viewDiagram = TemplateHelper.getInstance().createView(t);
		setPageComplete(false);
		for (Control c : composite.getChildren()) {
			if (!c.isDisposed()) {
				c.dispose();
			}
		}

		if (TemplateHelper.getInstance().getBms() != null && TemplateHelper.getInstance().getBms().size() > 0) {
			Group compoModel = new Group(composite, SWT.NONE);
			compoModel.setLayout(new GridLayout(3, false));
			compoModel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			compoModel.setText("Model");
			for (BMReference bm : TemplateHelper.getInstance().getBms()) {
				createTextField(compoModel, bm);
			}
			compoModel.layout();
		}

		if (TemplateHelper.getInstance().getInitBms() != null && TemplateHelper.getInstance().getInitBms().size() > 0) {
			Group compoInitModel = new Group(composite, SWT.NONE);
			compoInitModel.setLayout(new GridLayout(3, false));
			compoInitModel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			compoInitModel.setText("InitModel");
			for (BMReference bm : TemplateHelper.getInstance().getInitBms()) {
				createTextField(compoInitModel, bm);
			}
			compoInitModel.layout();
		}
		composite.layout();
	}

	private void createTextField(Composite composite, final BMReference bm) {
		Label lbl = new Label(composite, SWT.None);
		lbl.setText(bm.getName());
		final Text txt = new Text(composite, SWT.BORDER);
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button btn = new Button(composite, SWT.None);
		btn.setText(Messages.SettingWizardPage_Select_model);
		if (bmPath == null) {
			btn.setEnabled(false);
		}

		txt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (null == txt.getText() || "".equals(txt.getText())) {
					setErrorMessage(null);
					setPageComplete(false);
					return;
				}
				IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(txt.getText());
				if (r == null || !r.exists()) {
					updateStatus("文件不存在");
					return;
				} else if (!(r instanceof IFile) || (!r.getFileExtension().equalsIgnoreCase("bm"))) {
					updateStatus("必须选择bm文件");
					return;
				}
				bm.setModel((IFile) r);
				if (checkFinish()) {
					updateStatus(null);
				}
			}
		});

		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IResource folder =ResourcesPlugin.getWorkspace().getRoot().findMember(bmPath);
				SelectModelDialog dialog = new SelectModelDialog(getShell(), folder);
				if ((dialog.open() == Dialog.OK) && (dialog.getResult() instanceof IFile)) {
					txt.setText(((IFile) dialog.getResult()).getFullPath().toString());
				}
			}
		});
	}

	private boolean checkFinish() {
		for (BMReference bm : TemplateHelper.getInstance().getBms()) {
			if (bm.getModel() == null) {
				return false;
			}
		}
		if (true) {
			TemplateHandle handle = TemplateFactory.getTemplateHandle(viewDiagram.getTemplateType());
			if (handle != null) {
				handle.fill(viewDiagram);
			}
		}
		return true;
	}

	public void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public ViewDiagram getViewDiagram() {
		return viewDiagram;
	}
}
