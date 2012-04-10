package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import aurora.ide.meta.gef.editors.template.Component;
import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.gef.i18n.Messages;

public class SettingWizardPage extends WizardPage {

	private Composite composite;

	public SettingWizardPage() {
		super("aurora.wizard.setting.Page"); //$NON-NLS-1$
		setTitle(Messages.SettingWizardPage_Title);
		setDescription("Setting");
		setPageComplete(false);
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

		if (template.getLink().size() > 0) {
			Group gl = new Group(composite, SWT.None);
			gl.setLayout(new GridLayout());
			for (Component cp : template.getLink()) {
				if ("grid".equals(cp.getComponentType())) {
					Label lbl=new Label(gl,SWT.None);
					lbl.setText("添加Grid Link");
				}else if("button".equals(cp.getComponentType())){
					Label lbl=new Label(gl,SWT.None);
					lbl.setText("添加Button Link");
				}
			}
		}

		if (template.getRef().size() > 0) {
			Group gr = new Group(composite, SWT.None);
			gr.setLayout(new GridLayout());
			for (Component cp : template.getRef()) {
				Label lbl=new Label(gr,SWT.None);
				lbl.setText("选择ref");
			}
		}
		composite.layout();
	}
}
