package aurora.ide.excel.bank.format.setting;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.TableViewer;
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

import uncertain.composite.CompositeMap;
import aurora.ide.excel.bank.format.view.WidgetFactory;

public class IDXSettingDialog extends Dialog {

	private TableViewer tableViewer;
	private CompositeMap idxMap;
	private IDXViewer idxViewer;

	public IDXSettingDialog(Shell parentShell, CompositeMap idxMap) {
		super(parentShell);
		this.setIdxMap(idxMap);
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
		l.setText("IDX设置");
		l.setFont(JFaceResources.getBannerFont());
		WidgetFactory.hSeparator(c2);

		
		
		Composite c0 = new Composite(cc, SWT.NONE);
		c0.setLayout(new GridLayout());
		c0.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout ly0 = (GridLayout) c0.getLayout();
		ly0.numColumns = 2;
		

		l = new Label(c0, SWT.NONE);
		l.setText("描述");

		final Text t = new Text(c0, SWT.BORDER);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (idxMap.getString("desc") != null) {
			t.setText(idxMap.getString("desc", ""));
		}
		t.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				getIdxMap().put("desc", t.getText());
			}
		});
		
		Composite c = new Composite(cc, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout ly = (GridLayout) c.getLayout();
		ly.numColumns = 2;


		idxViewer = new IDXViewer();
		idxViewer.addColumn("字段名", 150);
		idxViewer.addColumn("字段值", 150);
		idxViewer.setInput(getIdxMap().getChildsNotNull());
		tableViewer = idxViewer.createContentTable(c);
		return c;
	}

	@Override
	protected Point getInitialSize() {
		Point initialSize = super.getInitialSize();
		return new Point(400, 450);
	}

	public CompositeMap getIdxMap() {

		return idxMap;
	}

	public CompositeMap getResult() {
		CompositeMap m = new CompositeMap("idx");
		List<Object> input = this.idxViewer.getInput();
		for (Object object : input) {
			m.addChild((CompositeMap) object);
		}
		m.put("desc", idxMap.getString("desc", ""));
		return m;

	}

	private void setIdxMap(CompositeMap idxMap) {
		this.idxMap = idxMap;
	}

}
