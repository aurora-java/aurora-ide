package aurora.ide.prototype.consultant.product.demonstrate;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;

import aurora.ide.prototype.consultant.product.Activator;
import aurora.ide.prototype.consultant.product.editor.EditorManager;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.TextField;
import aurora.ide.swt.util.WidgetFactory;
import aurora.plugin.source.gen.screen.model.DemonstrateData;

public class DemonstrateOpeningPage extends WizardPage {

	private DemonstrateData data;
	private Button uipButton;
	private Button msgButton;
	private TextField uipField;
	private Text msgText;

	protected DemonstrateOpeningPage(String pageName, String title,
			ImageDescriptor titleImage, DemonstrateData data) {
		super(pageName, title, titleImage);
		this.setData(data);
		this.setMessage(Messages.DemonstrateOpeningPage_0);
	}

	@Override
	public void createControl(Composite root) {
		Composite parent = WidgetFactory.composite(root);
		parent.setLayout(GridLayoutUtil.COLUMN_LAYOUT_1);
		uipButton = new org.eclipse.swt.widgets.Button(parent, SWT.RADIO);
		uipButton.setText(Messages.DemonstrateOpeningPage_1);

		Composite p = WidgetFactory.composite(parent);
		p.setLayout(GridLayoutUtil.COLUMN_LAYOUT_3);
		p.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		uipField = WidgetFactory.createTextButtonField(p, "UIP", Messages.DemonstrateOpeningPage_3); //$NON-NLS-1$

		WidgetFactory.hSeparator(parent).setLayoutData(
				new GridData(GridData.FILL_HORIZONTAL));

		msgButton = new org.eclipse.swt.widgets.Button(parent, SWT.RADIO);
		msgButton.setText(Messages.DemonstrateOpeningPage_4);
		GridData gd = new GridData(GridData.FILL_BOTH);
		msgText = new Text(parent, SWT.BORDER | SWT.MULTI);
		msgText.setLayoutData(gd);
		setControl(parent);
		makeListener();
		init();
	}

	private void makeListener() {
		msgButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getData().setOpenType(DemonstrateData.OPEN_MESSAGE);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		uipButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getData().setOpenType(DemonstrateData.OPEN_TYPE_UIP);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		msgText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				getData().setOpenMessage(msgText.getText());
			}
		});
		uipField.addButtonClickListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String queryFile = queryFile();
				if (queryFile != null) {
					IPath afp = EditorManager.getActiveEditorFile();	
					IPath p = new Path(queryFile);
					IPath makeRelativeTo = p.makeRelativeTo(afp);
					uipField.setText(makeRelativeTo.toString());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		uipField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getData().setOpenUIPPath(uipField.getText().getText());
			}
		});
	}

	private String queryFile() {
		FileDialog dialog = new FileDialog(this.getShell(), SWT.OPEN);
		dialog.setText("Open File"); //$NON-NLS-1$
		dialog.setFilterExtensions(new String[] { "*.uip" }); //$NON-NLS-1$
		String path = dialog.open();
		if (path != null && path.length() > 0)
			return path;
		return null;
	}

	private void init() {
		String openType = getData().getOpenType();
		if (DemonstrateData.OPEN_MESSAGE.equals(openType)) {
			msgButton.setSelection(true);
		}
		if (DemonstrateData.OPEN_TYPE_UIP.equals(openType)) {
			uipButton.setSelection(true);
		}
		String openUIPPath = getData().getOpenUIPPath();
		if (openUIPPath != null)
			uipField.setText(openUIPPath);
		String msg = getData().getOpenMessage();
		if (msg != null)
			msgText.setText(msg);
	}

	public DemonstrateData getData() {
		return data;
	}

	public void setData(DemonstrateData data) {
		this.data = data;
	}

}
