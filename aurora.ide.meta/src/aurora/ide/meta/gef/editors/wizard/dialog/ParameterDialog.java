package aurora.ide.meta.gef.editors.wizard.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.Container;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.Parameter;
import aurora.plugin.source.gen.screen.model.ScreenBody;

public class ParameterDialog extends Dialog {
	private Container[] containers;
	private Parameter para;
	private Parameter _para = new Parameter();
	private Combo containerField;
	private Text valueField;
	private Text nameField;
	private AuroraComponent context;

	public ParameterDialog(Shell parentShell, Container[] containers,
			Parameter para, AuroraComponent context) {
		super(parentShell);
		this.containers = containers;
		this.para = para;
		this.context = context;
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
		name.setText("Name :");
		nameField = new Text(composite, getInputTextStyle());
		nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				_para.setName(nameField.getText());
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
		value.setText("Value :");
		valueField = new Text(composite, getInputTextStyle());
		valueField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		valueField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				_para.setValue(valueField.getText());
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
			nameField.setText(para.getName() != null ? para.getName() : "");

			// int index =
			// Arrays.asList(containers).indexOf(para.getContainer());
			// if (index != -1) {
			// containerField.select(index);
			// }
			valueField.setText(para.getValue() != null ? para.getValue() : "");
		}
	}

	public Parameter getParameter() {
		_para.setContainer(findContainer());
		return _para;
	}

	private Container findContainer() {
		Container findGrid = this.findGrid(context);
		if (findGrid != null) {
			return findGrid;
		}
		if (context instanceof Button) {
			return findForm();
		}
		return null;
	}

	private Container findForm() {
		ScreenBody diagram = getDiagram(context);
		if (diagram != null) {
			List<Container> sectionContainers = diagram.getSectionContainers(
					diagram, new String[] { Container.SECTION_TYPE_QUERY });
			if (sectionContainers.size() > 0) {
				return sectionContainers.get(0);
			}
		}
		return null;
	}

	private ScreenBody getDiagram(AuroraComponent ac) {
		if (ac != null) {
			Container parent = ac.getParent();
			if (parent instanceof ScreenBody) {
				return (ScreenBody) parent;
			}
			return getDiagram(parent);
		}
		return null;
	}

	private Container findGrid(AuroraComponent ac) {
		if (ac == null)
			return null;
		Container parent = ac.getParent();
		if (parent instanceof Grid) {
			return parent;
		}
		if (parent != null) {
			return findGrid(parent.getParent());
		}
		return null;
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Parameter");
		shell.setMinimumSize(400, 150);
	}

	protected int getInputTextStyle() {
		return SWT.SINGLE | SWT.BORDER;
	}
}
