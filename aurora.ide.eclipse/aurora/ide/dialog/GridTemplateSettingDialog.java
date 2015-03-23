package aurora.ide.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class GridTemplateSettingDialog extends Dialog {

	public GridTemplateSettingDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 300);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("Grid设置~");
		container.setLayout(new GridLayout(2, false));

		Label lblId = new Label(container, SWT.NONE);
		lblId.setText("Grid Id:");
		Text txtId = new Text(container, SWT.BORDER);
		txtId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Group group = new Group(container, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);
		group.setText("ToolBar");
		Label label = new Label(group, SWT.NONE);
		label.setText("是否显示ToolBar");
		Button btnY = new Button(group, SWT.RADIO);
		btnY.setText("Yes");
		btnY.setSelection(true);
		Button btnN = new Button(group, SWT.RADIO);
		btnN.setText("No");

		Label lblButtonType = new Label(group, SWT.NONE);
		lblButtonType.setText("ToolBar按钮");
		String[] buttonType = { "新增", "保存", "删除", "清除", "导出" };
		Button[] buttons = new Button[buttonType.length];
		for (int i = 0; i < buttonType.length; i++) {
			Button btn = new Button(group, SWT.CHECK);
			btn.setText(buttonType[i]);
			buttons[i] = btn;
		}

		return container;
	}
}
