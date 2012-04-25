package aurora.ide.meta.gef.editors.wizard.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Renderer;

public class CridColumnDialog extends Dialog {

	private GridColumn gridColumn;
	private Text txtPrompt;
	private Text txtTitle;
	private Text txtUrl;
	private Renderer renderer;

	public CridColumnDialog(Shell parentShell, GridColumn gridColumn) {
		super(parentShell);
		this.gridColumn = gridColumn;
	}

	public CridColumnDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(container, SWT.None);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lbl = new Label(composite, SWT.None);
		lbl.setText("Prompt : ");
		txtPrompt = new Text(composite, SWT.BORDER);
		txtPrompt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		lbl = new Label(composite, SWT.None);
		lbl.setText("显示文本 : ");
		txtTitle = new Text(composite, SWT.BORDER);
		txtTitle.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		lbl = new Label(composite, SWT.None);
		lbl.setText("目标文件 : ");
		txtUrl = new Text(composite, SWT.BORDER);
		txtUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return container;
	}

	@Override
	protected void okPressed() {
		renderer = new Renderer();
		renderer.setOpenPath(txtUrl.getText());
//		renderer.getParameters().addAll();
		renderer.setLabelText(txtTitle.getText());
		
		super.okPressed();
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 300);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Add GridColumn Link");
	}

	public GridColumn getGridColumn() {
		return gridColumn;
	}

	public void setGridColumn(GridColumn gridColumn) {
		this.gridColumn = gridColumn;
	}

	public Renderer getRenderer() {
		return renderer;
	}
}
