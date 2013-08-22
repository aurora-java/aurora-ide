package aurora.ide.meta.gef.editors.consultant.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TextAreaFSDPropertyDescriptor extends FSDPropertyDescriptor {

	public TextAreaFSDPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public Control createControl(Composite parent) {
		Composite c = new Composite(parent,SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		c.setLayout(new GridLayout(2,false));
		Label l = new Label(c,SWT.NONE);
		l.setText(this.getDisplayName());
		Text text = new Text(c, SWT.BORDER | SWT.MULTI);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		return text;
	}
}
