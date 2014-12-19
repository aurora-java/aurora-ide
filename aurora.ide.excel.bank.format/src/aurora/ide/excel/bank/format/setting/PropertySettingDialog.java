package aurora.ide.excel.bank.format.setting;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import aurora.ide.excel.bank.format.view.WidgetFactory;

public class PropertySettingDialog extends Dialog {

	private String xls_code;
	private CompositeMap xlsSetting;
	private DATViewer datViewer;
	private IDXListViewer v2;

	public PropertySettingDialog(Shell parentShell, String xls_code) {
		super(parentShell);
		this.xls_code = xls_code;
		xlsSetting = PreferencesSetting.loadXLSSetting(xls_code);
	}

	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return super.createButtonBar(parent);
	}

	@Override
	protected void okPressed() {
		super.okPressed();
		PreferencesSetting.saveXLSSetting(xls_code, createSetting());
	}

	private CompositeMap createSetting() {

		CompositeMap compositeMap = new CompositeMap("xls_setting"); //$NON-NLS-1$
		List<Object> input = v2.getInput();
		for (Object object : input) {
			compositeMap.addChild((CompositeMap) object);
		}
		//
		CompositeMap dat = new CompositeMap("dat"); //$NON-NLS-1$
		List<Object> input2 = this.datViewer.getInput();
		for (Object object : input2) {
			dat.addChild((CompositeMap) object);
		}
		compositeMap.addChild(dat);
		return compositeMap;

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
		l.setText(xls_code);
		l.setFont(JFaceResources.getBannerFont());
		WidgetFactory.hSeparator(c2);

		GridLayout ly;

		Composite c = new Composite(cc, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		ly = (GridLayout) c.getLayout();
		ly.numColumns = 2;

		datViewer = new DATViewer();
		datViewer.addColumn(Messages.PropertySettingDialog_2, 150);
		datViewer.addColumn(Messages.PropertySettingDialog_3, 150);
		datViewer.setInput(xlsSetting.getChild("dat").getChildsNotNull()); //$NON-NLS-1$

		l = new Label(c, SWT.NONE);
		l.setText(Messages.PropertySettingDialog_5);
		GridData d1 = new GridData(GridData.FILL_HORIZONTAL);
		d1.horizontalSpan = 2;
		l.setLayoutData(d1);

		v2 = new IDXListViewer(this.xls_code);
		v2.addColumn(Messages.PropertySettingDialog_6, 100);
		v2.addColumn(Messages.PropertySettingDialog_7, 400);

		v2.setInput(getIDXChilds());
		v2.createContentTable(c);

		// idxViewer.createContentTable(c);

		WidgetFactory.hSeparator(c).setLayoutData(d1);
		l = new Label(c, SWT.NONE);
		l.setText(Messages.PropertySettingDialog_8);
		l.setLayoutData(d1);

		datViewer.createContentTable(c);
		return c;
	}

	@Override
	protected Point getInitialSize() {
		Point initialSize = super.getInitialSize();
		return new Point(550, 600);
	}

	private List<Object> getIDXChilds() {
		List<Object> l = new ArrayList<Object>();
		List childsNotNull = xlsSetting.getChildsNotNull();
		for (Object object : childsNotNull) {
			if (object instanceof CompositeMap) {
				if ("idx".equals(((CompositeMap) object).getName())) { //$NON-NLS-1$
					l.add((CompositeMap) object);
				}
			}
		}
		return l;
	}

}
