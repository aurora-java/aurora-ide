package aurora.ide.meta.gef.designer.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.meta.gef.designer.DesignerMessages;
import aurora.ide.meta.gef.editors.figures.ColorConstants;

public class LookupCodeDialog extends Dialog implements SelectionListener,
		ISelectionChangedListener, MouseListener {
	private Text text;
	private String value;
	CompositeMap codemap;
	String errorMessage = null;
	private Tree tree;
	private LookupCodeViewer treeViewer;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public LookupCodeDialog(Shell parentShell) {
		super(parentShell);
		IFile file = AuroraPlugin.getActiveIFile();
		if (file != null) {
			try {
				codemap = LookupCodeUtil.load(file.getProject());
			} catch (Exception e) {
				errorMessage = e.getMessage();
			}
		}
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	public Control createDialogArea(Composite parent) {
		parent.getShell().setText(DesignerMessages.LookupCodeDialog_2);
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));

		Label lblLookupcode = new Label(container, SWT.NONE);
		lblLookupcode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblLookupcode.setText(DesignerMessages.LookupCodeDialog_1);

		text = new Text(container, SWT.BORDER);
		text.addVerifyListener(new VerifyListener() {

			public void verifyText(VerifyEvent e) {
				if (e.text != null)
					e.text = e.text.toUpperCase();
				e.character = Character.toUpperCase(e.character);
			}
		});
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		if (value != null)
			text.setText(value);
		Label lblcode = new Label(container, SWT.NONE);
		lblcode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblcode.setText(DesignerMessages.LookupCodeDialog_0);
		if (errorMessage != null) {
			Text errText = new Text(container, SWT.MULTI | SWT.READ_ONLY);
			errText.setText(errorMessage);
			errText.setForeground(ColorConstants.red);
			errText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
					1, 1));
		} else {
			treeViewer = new LookupCodeViewer(container, SWT.BORDER
					| SWT.V_SCROLL | SWT.SINGLE);
			tree = treeViewer.getTree();
			tree.addMouseListener(this);
			tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
					1));
			treeViewer.setInput(codemap);
			treeViewer.select(value);
			treeViewer.addSelectionChangedListener(this);
		}
		text.selectAll();
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button btn = createButton(parent, IDialogConstants.IGNORE_ID,
				IDialogConstants.OK_LABEL, true);
		btn.addSelectionListener(this);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	/**
	 * if value contains '.' or any low case char,then ignore it<br/>
	 * because it is not a {@code lookupCode}(maybe previous {@code options})
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		if (value == null || value.indexOf('.') != -1
				|| !value.toUpperCase().equals(value))
			return;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void widgetSelected(SelectionEvent e) {
		performFinish();
	}

	private void performFinish() {
		value = text.getText().toUpperCase();
		// /close
		setReturnCode(OK);
		close();
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public void selectionChanged(SelectionChangedEvent event) {
		ISelection sel = event.getSelection();
		if (sel instanceof IStructuredSelection) {
			Object m = ((IStructuredSelection) sel).getFirstElement();
			m = LookupCodeUtil.getCodeRoot(m);
			if (m != null) {
				text.setText(LookupCodeUtil.getCode(m));
			}
		}
	}

	public void mouseDoubleClick(MouseEvent e) {
		TreeItem ti = tree.getItem(new Point(e.x, e.y));
		if (ti != null)
			performFinish();
	}

	public void mouseDown(MouseEvent e) {

	}

	public void mouseUp(MouseEvent e) {

	}
}
