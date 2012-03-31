package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

import aurora.ide.AuroraProjectNature;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.gef.editors.wizard.dialog.SelectModelDialog;
import aurora.ide.meta.gef.i18n.Messages;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public class SettingWizardPage extends WizardPage {

	private Template template;
	private Composite container;

	public SettingWizardPage() {
		super("aurora.wizard.setting.Page"); //$NON-NLS-1$
		setTitle(Messages.SettingWizardPage_Title);
		setDescription(Messages.SettingWizardPage_Model_Bind);
		setPageComplete(false);
	}

	private IPath getBMPath() {
		AuroraMetaProject metaPro = new AuroraMetaProject(((NewWizardPage) getPreviousPage()).getMetaProject());
		IPath bmPath = null;
		try {
			if (metaPro == null || metaPro.getAuroraProject() == null) {
				return bmPath;
			}
			IProject auroraPro = metaPro.getAuroraProject();
			if (auroraPro.hasNature(AuroraProjectNature.ID)) {
				bmPath = new Path(auroraPro.getPersistentProperty(ProjectPropertyPage.BMQN));
			}
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return bmPath;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		setControl(composite);

		container = new Composite(composite, SWT.BORDER);
		container.setLayout(new GridLayout(3, false));
		GridData data = new GridData(GridData.FILL_BOTH);
		container.setLayoutData(data);

		Link setting = new Link(composite, SWT.NONE);
		setting.setText(Messages.SettingWizardPage_Detail_Setting);
		// setting.setEnabled(false);

		setting.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void createDynamicComponents(Template template) {
		this.template = template;
		for (Control c : container.getChildren()) {
			if (!c.isDisposed()) {
				c.dispose();
			}
		}
		IPath bmPath = getBMPath();
		for (BMReference bm : template.getBms()) {
			createTextField(bm, bmPath);
		}
		container.layout(true);
	}

	private void createTextField(final BMReference bm, final IPath bmPath) {
		Label lbl = new Label(container, SWT.None);
		lbl.setText(bm.getName());
		final Text txt = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		txt.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				setBm(bm, bmPath, txt);
			}
		});
		
		final Button btn = new Button(container, SWT.None);
		btn.setText(Messages.SettingWizardPage_Select_model);
		if (bmPath == null) {
			btn.setEnabled(false);
		}
		
		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setBm(bm, bmPath, txt);
			}
		});
	}

	private void setBm(final BMReference bm, final IPath bmPath, final Text txt) {
		IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(bmPath);
		SelectModelDialog dialog = new SelectModelDialog(getShell(), folder);
		if ((dialog.open() == Dialog.OK) && (dialog.getResult() instanceof IFile)) {
			txt.setText(((IFile) dialog.getResult()).getFullPath().toString());
			bm.setModel((IFile) dialog.getResult());
			for (BMReference b : template.getBms()) {
				if (b.getModel() == null) {
					setPageComplete(false);
					break;
				} else {
					setPageComplete(true);
				}
			}
		}
	};

	public void setTemplate(Template template) {
		this.template = template;
	}

	public Template getTemplate() {
		return template;
	}
}
