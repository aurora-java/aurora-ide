package aurora.ide.excel.bank.format.setting;

import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aurora.excel.model.format.runner.OutputFileSetting;
import aurora.ide.excel.bank.format.view.WidgetFactory;

public class OutputFileSettingDialog extends Dialog {

	private OutputFileSetting setting = new OutputFileSetting();

	public OutputFileSettingDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return super.createButtonBar(parent);
	}

	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite cc = (Composite) super.createDialogArea(parent);

		Composite c2 = new Composite(cc, SWT.NONE);
		c2.setLayout(new GridLayout());
		c2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l = new Label(c2, SWT.NONE);
		l.setText(Messages.OutputFileSettingDialog_0);
		l.setFont(JFaceResources.getBannerFont());
		WidgetFactory.hSeparator(c2);

		Composite c = new Composite(cc, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout ly = (GridLayout) c.getLayout();
		ly.numColumns = 2;
		// 1 代号（金融统计监测管理信息系统-数值型统计指标数据－B）
		// 2 标志位（头文件：I；数据文件：J；数据说明文件：D）
		// 3—6 机构类代码
		// 7—13 地区代码
		// 14—21 年（4位），月（2位，01、02…11、12月），日（2位）
		// 22 频度
		// 23 批次
		// 24 顺序号（文件名的顺序码没有特别的含义，主要为区分多次报送而设置，也可以在数据修改阶段，用于对不同时间报送的数据进行区分）

		// final Text t1 = createProperty(c,
		// Messages.OutputFileSettingDialog_1);
		// t1.addModifyListener(new ModifyListener() {
		//
		// @Override
		// public void modifyText(ModifyEvent e) {
		// getSetting().setCode(t1.getText());
		// }
		// });
		//		t1.setText("B"); //$NON-NLS-1$
		final Text t2 = createProperty(c, Messages.OutputFileSettingDialog_3);
		t2.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				getSetting().setOrg_code(t2.getText());
			}
		});
		t2.setText("g305"); //$NON-NLS-1$
		final Text t3 = createProperty(c, Messages.OutputFileSettingDialog_5);
		t3.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				getSetting().setArea_code(t3.getText());
			}
		});
		t3.setText("1200000"); //$NON-NLS-1$
		final Text t4 = createProperty(c, Messages.OutputFileSettingDialog_7);
		t4.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				getSetting().setDate(t4.getText());
			}
		});
		t4.setText((new java.text.SimpleDateFormat("yyyyMMdd")) //$NON-NLS-1$
				.format(new Date()));
		final Text t5 = createProperty(c, Messages.OutputFileSettingDialog_9);
		t5.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				getSetting().setRate(t5.getText());
			}
		});
		t5.setText("4"); //$NON-NLS-1$
		final Text t6 = createProperty(c, Messages.OutputFileSettingDialog_11);
		t6.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				getSetting().setBatch(t6.getText());
			}
		});
		t6.setText("1"); //$NON-NLS-1$
		final Text t7 = createProperty(c, Messages.OutputFileSettingDialog_13);
		t7.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				getSetting().setOrder(t7.getText());
			}
		});
		t7.setText("1"); //$NON-NLS-1$
		return c;
	}

	private Text createProperty(Composite c, String text) {
		Label n = new Label(c, SWT.NONE);
		n.setText(text);
		Text t = new Text(c, SWT.WRAP | SWT.BORDER);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// t.setText(value);
		return t;
	}

	@Override
	protected Point getInitialSize() {
		Point initialSize = super.getInitialSize();
		return new Point(400, 450);
	}

	public OutputFileSetting getSetting() {
		return setting;
	}

	public void setSetting(OutputFileSetting setting) {
		this.setting = setting;
	}

}
