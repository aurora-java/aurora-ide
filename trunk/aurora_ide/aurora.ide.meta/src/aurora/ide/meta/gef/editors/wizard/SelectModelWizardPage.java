package aurora.ide.meta.gef.editors.wizard;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.AuroraProjectNature;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.template.BMReference;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.gef.editors.wizard.dialog.SelectModelDialog;
import aurora.ide.meta.gef.i18n.Messages;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public class SelectModelWizardPage extends WizardPage {

	private Template template;
	private Composite composite;

	public SelectModelWizardPage() {
		super("aurora.wizard.select.Page"); //$NON-NLS-1$
		setTitle(Messages.SettingWizardPage_Title);
		setDescription(Messages.SettingWizardPage_Model_Bind);
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
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		setControl(composite);

	}

	public void createDynamicTextComponents(Template template) {
		this.template = template;
		setPageComplete(false);
		for (Control c : composite.getChildren()) {
			if (!c.isDisposed()) {
				c.dispose();
			}
		}
		IPath bmPath = getBMPath();

		if (template.getBms().size() > 0) {
			Group compoModel = new Group(composite, SWT.NONE);
			compoModel.setLayout(new GridLayout(3, false));
			compoModel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			compoModel.setText("Model");
			for (BMReference bm : template.getBms()) {
				createTextField(compoModel, bm, bmPath);
			}
			compoModel.layout();
		}

		if (template.getInitBms().size() > 0) {
			Group compoInitModel = new Group(composite, SWT.NONE);
			compoInitModel.setLayout(new GridLayout(3, false));
			compoInitModel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			compoInitModel.setText("InitModel");
			for (BMReference bm : template.getInitBms()) {
				createTextField(compoInitModel, bm, bmPath);
			}
			compoInitModel.layout();
		}
		composite.layout();
	}

	private void createTextField(Composite composite, final BMReference bm, final IPath bmPath) {
		Label lbl = new Label(composite, SWT.None);
		lbl.setText(bm.getName());
		final Text txt = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));

		final Button btn = new Button(composite, SWT.None);
		btn.setText(Messages.SettingWizardPage_Select_model);
		if (bmPath == null) {
			btn.setEnabled(false);
		}

		txt.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				setBm(bm, bmPath, txt);
			}
		});

		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setBm(bm, bmPath, txt);
			}
		});
	}

	private void setBm(BMReference bm, IPath bmPath, Text txt) {
		IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(bmPath);
		SelectModelDialog dialog = new SelectModelDialog(getShell(), folder);
		if ((dialog.open() == Dialog.OK) && (dialog.getResult() instanceof IFile)) {
			txt.setText(((IFile) dialog.getResult()).getFullPath().toString());
			bm.setModel((IFile) dialog.getResult());
			if (checkFinish() && template.getLink().size() + template.getRef().size() > 0) {
				setPageComplete(true);
				SettingWizardPage page = (SettingWizardPage) getNextPage();
				page.createCustom(template);
			} else {
				setPageComplete(false);
			}
		}
	}

	public boolean checkFinish() {
		boolean bool = false;
		List<BMReference> bms = new ArrayList<BMReference>();
		bms.addAll(template.getBms());
		bms.addAll(template.getInitBms());
		for (BMReference b : bms) {
			if (b.getModel() == null) {
				return false;
			} else {
				bool = true;
			}
		}
		return bool;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public Template getTemplate() {
		return template;
	}
}
