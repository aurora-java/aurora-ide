package aurora.ide.views.wizard;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;

import aurora.ide.screen.wizard.NewScreenTemplatesWizardPage;

public class OldTemplateWizardPage extends NewScreenTemplatesWizardPage {

	private Table table;

	public OldTemplateWizardPage() {
		super();
		setTitle("使用自定义模板");
	}

	@Override
	public void createControl(Composite ancestor) {
		super.createControl(ancestor);
		table = findTable(ancestor);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (table != null) {
			if (table.getSelectionIndex() == -1)
				table.select(0);
		}
	}

	Table findTable(Composite com) {
		for (Control c : com.getChildren()) {
			if (c instanceof Table) {
				return (Table) c;
			} else if (c instanceof Composite) {
				Table t = findTable((Composite) c);
				if (t != null)
					return t;
			}
		}
		return null;
	}

}
