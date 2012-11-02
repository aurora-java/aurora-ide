package aurora.ide.create.component.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.meta.gef.control.PrototpyeComposite;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class PrototpyeViewWizardPage extends WizardPage {

	public PrototpyeViewWizardPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout());

		PrototpyeComposite vsEditor = new PrototpyeComposite();

		ViewDiagram viewDiagram = new ViewDiagram();
		Form f = new Form();
		Input child = new Input();
		child.setType(Input.TEXT);
		f.addChild(child);
		child = new Input();
		child.setType(Input.LOV);
		f.addChild(child);
		child = new Input();
		child.setType(Input.Combo);
		f.addChild(child);

		viewDiagram.addChild(f);
		viewDiagram.addChild(new Grid());
		
		vsEditor.setInput(viewDiagram);

		vsEditor.createPartControl(control);
		vsEditor.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		this.setControl(control);
	}

}
