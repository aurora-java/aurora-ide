package aurora.ide.meta.gef.editors.wizard.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Mapping;

public class MappingDialog extends Dialog {
	private Mapping para;
	private Mapping _para = new Mapping();
	private Text valueField;
	private Text nameField;

	public MappingDialog(Shell parentShell,
			Mapping para, AuroraComponent context) {
		super(parentShell);
		this.para = para;
	}

	protected Control createSuperDialogArea(Composite parent) {
		// create a composite with standard margins and spacing

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		// layout.marginHeight =
		// convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		// layout.marginWidth =
		// convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		// layout.verticalSpacing =
		// convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		// layout.horizontalSpacing =
		// convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		return composite;
	}

	protected Control createDialogArea(Composite parent) {
		// create composite
		Composite composite = (Composite) createSuperDialogArea(parent);
		Label name = new Label(composite, SWT.NONE);
		name.setText("From :");
		nameField = new Text(composite, getInputTextStyle());
		nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				_para.setFrom(nameField.getText());
			}
		});
		// Label container = new Label(composite, SWT.NONE);
		// container.setText("Container :");
		// containerField = new Combo(composite, getInputTextStyle()
		// | SWT.READ_ONLY);
		// containerField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		// containerField.addModifyListener(new ModifyListener() {
		// public void modifyText(ModifyEvent e) {
		// int selectionIndex = containerField.getSelectionIndex();
		// Container container = null;
		// try {
		// container = containers[selectionIndex];
		// } finally {
		// _para.setContainer(container);
		// }
		// }
		// });

		Label value = new Label(composite, SWT.NONE);
		value.setText("To :");
		valueField = new Text(composite, getInputTextStyle());
		valueField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		valueField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				_para.setTo(valueField.getText());
			}
		});
		applyDialogFont(composite);
		init();
		return composite;
	}

	private void init() {
		// if (containers != null) {
		// for (int i = 0; i < containers.length; i++) {
		// containerField.add(containers[i].toDisplayString(), i);
		// }
		// }
		if (para != null) {
			nameField.setText(para.getFrom() != null ? para.getFrom() : "");

			// int index =
			// Arrays.asList(containers).indexOf(para.getContainer());
			// if (index != -1) {
			// containerField.select(index);
			// }
			valueField.setText(para.getTo()!= null ? para.getTo() : "");
		}
	}

	public Mapping getMapping() {
		return _para;
	}





	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Mapping");
		shell.setMinimumSize(400, 150);
	}

	protected int getInputTextStyle() {
		return SWT.SINGLE | SWT.BORDER;
	}
}
