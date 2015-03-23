package aurora.bpmn.designer.rcp.viewer.action.wizard;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.bpmn.designer.ws.BPMNDefineCategory;
import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.ide.swt.util.TextField;
import aurora.ide.swt.util.UWizardPage;

public class CreateBPMDefinePage extends UWizardPage {

	public static final String DESCRIPTION = "description";

	public static final String PROCESS_CODE = "process_code";

	public static final String PROCESS_VERSION = "process_version";
	public static final String CATEGORY_ID = "category_id";

	public static final String NAME = "name";

	private BPMNDefineModel copyFrom;
	private boolean isNewVer;

	private BPMNDefineCategory[] categorys;

	public static String[] properties = new String[] { NAME, PROCESS_VERSION,
			PROCESS_CODE, DESCRIPTION };

	protected CreateBPMDefinePage(String pageName) {
		super(pageName);
	}

	@Override
	protected String[] getModelPropertyKeys() {
		return properties;
	}

	@Override
	protected String verifyModelProperty(String key, Object val) {
		if (DESCRIPTION.equals(key))
			return null;
		if (val == null || "".equals(val.toString().trim()))
			return key + ": " + "必须输入";
		return null;
	}

	@Override
	protected Composite createPageControl(Composite parent) {

		Composite c = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		c.setLayout(layout);
		TextField ni = this.createInputField(c, "Name", NAME);
		TextField ci = this.createInputField(c, "Code", PROCESS_CODE);

		this.createInputField(c, "Version", PROCESS_VERSION);

		Label l = new Label(c, SWT.NONE);
		l.setText("Category");
		final Combo combo = new Combo(c, SWT.BORDER | SWT.READ_ONLY);

		for (BPMNDefineCategory cc : categorys) {
			combo.add(cc.getName());
		}

		combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateModel(CATEGORY_ID,
						categorys[combo.getSelectionIndex()].getId());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		if (isNewVer) {
			ni.getText().setEditable(false);
			ni.getText().setEditable(false);
			ci.getText().setEditable(false);
			ci.getText().setEditable(false);
			ni.setText(this.copyFrom.getName());
			ci.getText().setText(this.copyFrom.getProcess_code());
			combo.setVisible(false);
			l.setVisible(false);
		}
		Label n = new Label(c, SWT.NONE);
		n.setText("Description");
		Text t = new Text(c, SWT.BORDER | SWT.MULTI);
		GridData data = new GridData(GridData.FILL_BOTH);
		t.setLayoutData(data);
		t.addModifyListener(new TextModifyListener(DESCRIPTION, t));
		// combo.select(index);
		return c;
	}

	// private String[] getCategorys() {
	// String[] r = new String[categorys.size()];
	// for (Iterator iterator = categorys.iterator(); iterator.hasNext();) {
	// BPMNDefineCategory c = (BPMNDefineCategory) iterator.next();
	// c.getName();
	// }
	// return r;
	// }

	public void setCategorys(Collection<BPMNDefineCategory> categorys) {
		this.categorys = categorys.toArray(new BPMNDefineCategory[categorys
				.size()]);
	}

	public void setNewVer(BPMNDefineModel copyFrom) {
		this.isNewVer = true;
		this.copyFrom = copyFrom;
	}
}
