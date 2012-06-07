package aurora.ide.meta.preference;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
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
import aurora.ide.helpers.FileDeleter;
import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.editors.template.handle.TemplateHelper;
import aurora.ide.meta.gef.editors.wizard.dialog.TComposite;
import aurora.ide.meta.gef.i18n.Messages;

public class UIPrototypePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	@SuppressWarnings("unused")
	private IWorkbench workbench;

	private TComposite tComposite;
	private boolean isDefault = false;

	@Override
	protected void performDefaults() {
		URL ts = FileLocator.find(Platform.getBundle(MetaPlugin.PLUGIN_ID), new Path("template"), null); //$NON-NLS-1$
		try {
			ts = FileLocator.toFileURL(ts);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		tComposite.clear();
		TemplateHelper.getInstance().clearTemplate();
		IPath path = new Path(ts.getPath());
		tComposite.createContent(TemplateHelper.getInstance().getTemplates(path));
		createButton();
		tComposite.getLeftComposite().layout();
		isDefault = true;

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

		IPath path = MetaPlugin.getDefault().getStateLocation().append("template"); //$NON-NLS-1$
		// TemplateHelper.getInstance().clearTemplate();
		tComposite = new TComposite(container, SWT.BORDER, TemplateHelper.getInstance().getTemplates(path));
		createButton();
		return container;
	}

	private void createButton() {
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
		btn.setText(Messages.UIPrototypePreferencePage_Add);
		btn.setLayoutData(formData);

		btn = new Button(buttonComposite, SWT.None);
		formData = new FormData();
		formData.right = new FormAttachment(100, 0);
		formData.height = 25;
		formData.width = 25;
		btn.setText(Messages.UIPrototypePreferencePage_Delete);
		btn.setLayoutData(formData);
	}

	@Override
	protected void performApply() {
		if (isDefault) {
			reloadTemplates();
			isDefault = false;
		}
		super.performApply();
	}

	@Override
	public boolean performOk() {
		if (isDefault) {
			reloadTemplates();
			isDefault = false;
		}
		return super.performOk();
	}

	@Override
	public boolean performCancel() {
		TemplateHelper.getInstance().clearTemplate();
		return super.performCancel();
	}

	private void reloadTemplates() {
		if (SWT.OK == DialogUtil.showConfirmDialogBox(Messages.UIPrototypePreferencePage_Continue)) {
			IPath path = MetaPlugin.getDefault().getStateLocation().append("template"); //$NON-NLS-1$
			File templateDirectory = new File(path.toString());
			FileDeleter.deleteDirectory(templateDirectory);
			MetaPlugin.getDefault().copyTemplateFile();
		}
	}
}
