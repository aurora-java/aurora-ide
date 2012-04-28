package aurora.ide.meta.preference;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.gef.editors.template.handle.TemplateHelper;
import aurora.ide.meta.gef.editors.wizard.dialog.TComposite;

public class UIPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	@SuppressWarnings("unused")
	private IWorkbench workbench;

	@Override
	protected void performDefaults() {
		if (SWT.OK == DialogUtil.showConfirmDialogBox("设为默认值将丢失自定义模板，是否继续？")) {
			// MetaPlugin.getDefault().copyTemplateFile();
			// TODO Auto-generated method stub
		}
		super.performDefaults();
	}

	public void init(IWorkbench workbench) {
		this.workbench = workbench;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		TComposite tComposite = new TComposite(container, SWT.BORDER, TemplateHelper.getInstance().getTemplates());
		tComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite buttonComposite = new Composite(tComposite.getLeftComposite(), SWT.NONE);
		buttonComposite.setLayout(new FormLayout());
		buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonComposite.setBackground(tComposite.getLeftComposite().getBackground());

		Button btn = new Button(buttonComposite, SWT.None);
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 0);
		formData.height = 25;
		formData.width = 25;
		btn.setText("+");
		btn.setLayoutData(formData);

		btn = new Button(buttonComposite, SWT.None);
		formData = new FormData();
		formData.right = new FormAttachment(100, 0);
		formData.height = 25;
		formData.width = 25;
		btn.setText("-");
		btn.setLayoutData(formData);
		return container;
	}
}
