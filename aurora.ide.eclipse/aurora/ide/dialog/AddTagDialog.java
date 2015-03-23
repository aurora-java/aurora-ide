package aurora.ide.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aurora.ide.helpers.DialogUtil;

public class AddTagDialog extends Dialog {
	private Combo cboNamespace;
	private Text txtTag;
	private String namespace;
	private String selectNamespace;
	private Set<String> tags = new TreeSet<String>();
	private String[] namespaces;
	private Map<String, List<String>> customMap;
	private Map<String, List<String>> baseMap;
	private StringBuffer sb = new StringBuffer();

	public AddTagDialog(Shell parentShell, String[] namespaces, Map<String, List<String>> baseMap, String selectNamespace) {
		super(parentShell);
		this.namespaces = namespaces;
		this.baseMap = baseMap;
		this.selectNamespace = selectNamespace;
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.HELP;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 300);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("Add Tags");
		GridLayout layou = new GridLayout();
		layou.numColumns = 2;
		container.setLayout(layou);
		Label lblNamespace = new Label(container, SWT.NONE);
		lblNamespace.setText("Namespace:");

		GridData gdNamespace = new GridData(GridData.FILL_HORIZONTAL);
		cboNamespace = new Combo(container, SWT.DROP_DOWN);
		cboNamespace.setLayoutData(gdNamespace);
		for (int i = 0; i < namespaces.length; i++) {
			cboNamespace.add(namespaces[i]);
			if(namespaces[i].equals(selectNamespace)){
				cboNamespace.select(i);
			}
		}

		GridData gdlblTag = new GridData();
		gdlblTag.verticalAlignment = SWT.TOP;
		Label lblTag = new Label(container, SWT.NONE);
		lblTag.setLayoutData(gdlblTag);
		lblTag.setText("Tag:");

		GridData gdTag = new GridData(GridData.FILL_BOTH);
		txtTag = new Text(container, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		txtTag.setLayoutData(gdTag);

		return container;

	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 0;
		layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());
		// Add the buttons to the button bar.
		createButtonsForButtonBar(composite);
		return composite;
	}

	@Override
	protected void okPressed() {
		namespace = cboNamespace.getText().trim();
		for (String s : txtTag.getText().split("\r\n")) {
			tags.add(s.trim());
		}
		if ("".equals(namespace.trim())) {
			DialogUtil.showWarningMessageBox("请输入命名空间。");
			return;
		} else if ("".equals(txtTag.getText().trim())) {
			DialogUtil.showWarningMessageBox("请输入标签，以换行符分割。");
			return;
		}
		fillMap();
		super.okPressed();
	}

	private void fillMap() {
		customMap = new TreeMap<String, List<String>>();
		customMap.put(namespace, new ArrayList<String>());
		sb.append("*" + namespace);
		for (String s : tags) {
			if (!"".equals(s)) {
				if (baseMap.containsKey(namespace)) {
					if (baseMap.get(namespace).contains(s)) {
						continue;
					}
				}
				customMap.get(namespace).add(s);
				sb.append("!" + s);
			}
		}
		sb.append("!");
	}

	public String getStoreValue() {
		return sb.toString();
	}

	public Map<String, List<String>> getCustomMap() {
		return customMap;
	}
}
