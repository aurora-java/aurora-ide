package aurora.ide.meta.project.prototype;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import aurora.ide.meta.gef.editors.template.parse.TemplateHelper;
import aurora.ide.meta.gef.editors.wizard.dialog.TComposite;

public class UIPrototypePage extends PreferencePage implements IWorkbenchPreferencePage {

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		TComposite tComposite = new TComposite(container, SWT.BORDER, TemplateHelper.getInstance().getTemplates());
		tComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		return container;
	}
}
