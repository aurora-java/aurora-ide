package aurora.ide.meta.gef.editors.wizard.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.meta.gef.control.ConsultantDemonstratingComposite;
import aurora.ide.prototype.consultant.demonstrate.LOVDemonstrating;
import aurora.plugin.source.gen.screen.model.Combox;
import aurora.plugin.source.gen.screen.model.Form;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.HBox;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.TextField;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class SysLovDialog extends Dialog {

	private LovDialogInput input;
	private LOVDemonstrating demon;

	public SysLovDialog(Shell parentShell, LOVDemonstrating demon) {
		super(parentShell);
		this.demon = demon;
	}

	protected Point getInitialSize() {
		return new Point(500, 530);
	}

	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		return super.createButton(parent, id, label, defaultButton);
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return super.createButtonBar(parent);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// super.createButtonsForButtonBar(parent);
	}

	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}

	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout());
		ConsultantDemonstratingComposite vsEditor = new ConsultantDemonstratingComposite(this);
		ScreenBody viewDiagram = new ScreenBody();
		viewDiagram.addChild(createForm());
		viewDiagram.addChild(createButtons());
		viewDiagram.addChild(createGrid());
		vsEditor.setInput(viewDiagram);
		vsEditor.createPartControl(container);
		vsEditor.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		return container;
	}

	protected HBox createButtons() {
		HBox hb = new HBox();
		aurora.plugin.source.gen.screen.model.Button child = new aurora.plugin.source.gen.screen.model.Button();
		child.setText("查询");
		hb.addChild(child);
		child = new aurora.plugin.source.gen.screen.model.Button();
		child.setText("确定");
		hb.addChild(child);
		return hb;
	}

	public Form createForm() {
		Form form = new Form();
		form.setSize(450, 85);
		form.setCol(2);
		TextField tf0 = createTextField(0);
		if (tf0 != null)
			form.addChild(tf0);
		Combox cb1 = createCombox(1);
		if (cb1 != null)
			form.addChild(cb1);
		TextField tf2 = createTextField(2);
		if (tf2 != null)
			form.addChild(tf2);
		Combox cb3 = createCombox(3);
		if (cb3 != null)
			form.addChild(cb3);
		return form;
	}

	private TextField createTextField(int i) {
		TextField textField = new TextField();
		if (input != null) {
			String head = input.getHead(i);
			if (head != null) {
				textField.setPropertyValue(
						ComponentInnerProperties.INPUT_SIMPLE_DATA, head);
			} else {
				return null;
			}
		}
		return textField;
	}

	private Combox createCombox(int i) {
		Combox combox = new Combox();
		if (input != null) {
			String head = input.getHead(i);
			if (head != null) {
				combox.setPropertyValue(
						ComponentInnerProperties.INPUT_SIMPLE_DATA, head);
			} else {
				return null;
			}
		}
		return combox;
	}

	public Grid createGrid() {
		Grid grid = new Grid();
		grid.setSize(450, 300);
		if (input == null || input.columns() == 0) {
			grid.addCol(new GridColumn());
			grid.addCol(new GridColumn());
			grid.addCol(new GridColumn());
		} else {
			for (int i = 0; i < 5; i++) {
				GridColumn cc = createGridColumn(i);
				if (cc != null) {
					grid.addChild(cc);
				}
			}
		}
		grid.setPropertyValue(ComponentProperties.navBarType,
				Grid.NAVBAR_COMPLEX);
		return grid;
	}

	public GridColumn createGridColumn(int i) {
		GridColumn gridColumn = new GridColumn();
		if (input != null && input.columns() > 0) {
			List<String> rows = input.getColumn(i);
			if (rows == null)
				return null;
			else {
				int max = rows.size() < 9 ? rows.size() : 9;
				for (int j = 0; j < (max); j++) {
					String s = rows.get(j);
					if (s == null)
						break;
					if (j == 0) {
						gridColumn.setPropertyValue(ComponentProperties.prompt,
								s);
					} else {
						gridColumn
								.setPropertyValue(
										ComponentInnerProperties.GRID_COLUMN_SIMPLE_DATA
												+ (j), s);
					}
				}
			}
		}
		return gridColumn;
	}

	public void setInput(LovDialogInput input) {
		this.input = input;
	}

	public void applyValue(String value) {
		demon.applyValue(value);
		this.close();
	}

}
