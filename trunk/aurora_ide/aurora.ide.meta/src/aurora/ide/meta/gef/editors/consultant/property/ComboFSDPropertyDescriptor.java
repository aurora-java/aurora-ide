package aurora.ide.meta.gef.editors.consultant.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class ComboFSDPropertyDescriptor extends FSDPropertyDescriptor {

	private String[] items;

	public ComboFSDPropertyDescriptor(Object id, String displayName,String[] items) {
		super(id, displayName);
		this.items = items;
	}

	public Control createControl(Composite parent) {
		Composite c = new Composite(parent,SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		c.setLayout(new GridLayout(2,false));
		Label l = new Label(c,SWT.NONE);
		l.setText(this.getDisplayName());
		Combo text = new Combo(c, SWT.BORDER |SWT.READ_ONLY| SWT.SINGLE);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setItems(items);
		return text;
//		return null;
	}
}
