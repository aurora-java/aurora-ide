package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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

import aurora.ide.builder.ResourceUtil;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.property.MutilInputResourceSelector;
import aurora.ide.meta.gef.editors.template.Component;
import aurora.ide.meta.gef.editors.template.TabRef;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.gef.editors.wizard.dialog.StyleSettingDialog;
import aurora.ide.meta.gef.i18n.Messages;
import aurora.ide.meta.project.AuroraMetaProject;

public class SettingWizardPage extends WizardPage {

	private Composite composite;

	public SettingWizardPage() {
		super("aurora.wizard.setting.Page"); //$NON-NLS-1$
		setTitle(Messages.SettingWizardPage_Title);
		setDescription("Setting");
		setPageComplete(true);
	}

	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		setControl(composite);
	}

	public void createCustom(Template template) {
		for (Control c : composite.getChildren()) {
			if (!c.isDisposed()) {
				c.dispose();
			}
		}

//		if (template.getLink().size() > 0) {
//			Group gl = new Group(composite, SWT.None);
//			gl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//			gl.setText("Add Link");
//			gl.setLayout(new GridLayout());
//			for (Component cp : template.getLink()) {
//				if ("grid".equals(cp.getComponentType())) {
//					Button btn =new Button(gl,SWT.None);
//
//				} else if ("button".equals(cp.getComponentType())) {
//					Label lbl = new Label(gl, SWT.None);
//					lbl.setText(cp.getName());
//				}
//			}
//		}

		if (template.getRef().size() > 0) {
			Group gr = new Group(composite, SWT.None);
			gr.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			gr.setText("Set tab Ref");
			gr.setLayout(new GridLayout(4, false));
			for (Component cp : template.getRef()) {
				createRefField((TabRef) cp, gr);
			}
		}
		composite.layout();
	}

	private void createRefField(final TabRef cp, Group gr) {
		Label lbl = new Label(gr, SWT.None);
		lbl.setText("Select:");
		final Text txt = new Text(gr, SWT.BORDER);
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button btnSelect = new Button(gr, SWT.None);
		btnSelect.setText("选择文件");

		Button btnParam = new Button(gr, SWT.None);
		btnParam.setText("添加参数");
		btnParam.setEnabled(true);

		btnSelect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MutilInputResourceSelector fss = new MutilInputResourceSelector(getShell());
				String webHome = ResourceUtil.getWebHome(getAuroraProject());
				IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(webHome);
				fss.setExtFilter(new String[] { "screen", "uip" });
				IContainer uipFolder = getMetaProject().getFolder("ui_prototype");
				fss.setInputs(new IContainer[] { (IContainer) res, uipFolder });
				Object obj = fss.getSelection();
				if (!(obj instanceof IFile)) {
					return;
				}
				txt.setText(((IFile) obj).getFullPath().toString());
				cp.setUrl(((IFile) obj).getFullPath().toString());
			}
		});

		btnParam.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				StyleSettingDialog dialog = new StyleSettingDialog(getShell(), cp.getParas());
				if (dialog.open() == Dialog.OK) {
					cp.setParas(dialog.getResult());
				}
			}
		});
	}

	private IProject getMetaProject() {
		for (IWizardPage page = this; page.getPreviousPage() != null; page = page.getPreviousPage()) {
			if (page instanceof NewWizardPage) {
				return ((NewWizardPage) page).getMetaProject();
			}
		}
		return null;
	}

	private IProject getAuroraProject() {
		try {
			return new AuroraMetaProject(getMetaProject()).getAuroraProject();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
