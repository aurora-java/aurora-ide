package aurora.ide.preferencepages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.preferencepages.BaseTemplatePreferencePage.Config;
import aurora.ide.preferencepages.BaseTemplatePreferencePage.Template;

public class EditTemplateDialog extends Dialog {
	private Text text_name;
	private Text text_desc;
	private Text text_tpl;
	private Label lbl_status;
	private Template tpl;
	private Button okBtn;
	private Config config;
	private BaseTemplatePreferencePage preferencePage;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public EditTemplateDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.MIN | SWT.MAX);
	}

	public EditTemplateDialog(Shell parentShell, Template tpl) {
		this(parentShell);
		this.tpl = tpl;
	}

	public void setPreferencePage(BaseTemplatePreferencePage page) {
		this.preferencePage = page;
		this.config = page.getConfig();
	}

	public Template getTemplate() {
		return tpl;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		if (tpl == null) {
			getShell().setText(Messages.EditTemplateDialog_0);
			CompositeMap data = new CommentCompositeMap("template"); //$NON-NLS-1$
			tpl = new Template(data);
			tpl.isNew = true;
		} else
			getShell().setText(Messages.EditTemplateDialog_2);
		container.setLayout(new GridLayout(4, false));

		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label.setText(Messages.EditTemplateDialog_3);

		text_name = new Text(container, SWT.BORDER);
		GridData gd_text_name = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gd_text_name.widthHint = 76;
		text_name.setLayoutData(gd_text_name);

		Label label_1 = new Label(container, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label_1.setText(Messages.EditTemplateDialog_4);

		text_desc = new Text(container, SWT.BORDER);
		text_desc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
				false, 1, 1));
		lblNewLabel.setText(Messages.EditTemplateDialog_5);

		text_tpl = new Text(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.CANCEL | SWT.MULTI);
		text_tpl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3,
				1));
		new Label(container, SWT.NONE);

		lbl_status = new Label(container, SWT.NONE);
		lbl_status.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_RED));
		lbl_status.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 3, 1));
		init();
		return container;
	}

	void init() {
		String name = tpl.getName();
		if (name == null)
			name = ""; //$NON-NLS-1$
		text_name.setText(name);
		String desc = tpl.getDescription();
		if (desc == null)
			desc = ""; //$NON-NLS-1$
		text_desc.setText(desc);
		String str = tpl.getTemplateString();
		if (str == null)
			str = ""; //$NON-NLS-1$
		text_tpl.setText(str);
		if (text_name.getText().length() == 0)
			text_name.forceFocus();
		else
			text_tpl.forceFocus();
		// -------
		ModifyListener ml = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				validateInput();
			}
		};
		text_name.addModifyListener(ml);
		text_desc.addModifyListener(ml);
		text_tpl.addModifyListener(ml);
	}

	void validateInput() {
		String name = text_name.getText();
		if (name.length() == 0) {
			updateStatus(Messages.EditTemplateDialog_9);
			return;
		} else {
			for (Template t : config.list) {
				if (tpl == t || t.markDelete)
					continue;
				if (t.getName().equals(name)) {
					updateStatus(Messages.EditTemplateDialog_10);
					return;
				}
			}
		}
		updateStatus(preferencePage.validateTemplate(text_tpl.getText()));
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		okBtn = createButton(parent, IDialogConstants.IGNORE_ID,
				IDialogConstants.OK_LABEL, true);
		if (tpl.isNew)
			okBtn.setEnabled(false);
		else
			validateInput();
		okBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				tpl.setName(text_name.getText());
				tpl.setDescription(text_desc.getText());
				tpl.setTemplateString(text_tpl.getText());
				setReturnCode(IDialogConstants.OK_ID);
				close();
			}

		});
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	protected void updateStatus(String str) {
		lbl_status.setText(str == null ? "" : str); //$NON-NLS-1$
		okBtn.setEnabled(str == null);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
