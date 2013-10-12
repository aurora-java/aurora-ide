package aurora.ide.meta.gef.editors.wizard.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.meta.gef.control.ConsultantComposite;
import aurora.plugin.source.gen.screen.model.Combox;
import aurora.plugin.source.gen.screen.model.DatePicker;
import aurora.plugin.source.gen.screen.model.Form;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.HBox;
import aurora.plugin.source.gen.screen.model.LOV;
import aurora.plugin.source.gen.screen.model.NumberField;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.TextField;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class SysLovDialog extends Dialog {

	public SysLovDialog(Shell parentShell) {
		super(parentShell);
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
//		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}

	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout());
		ConsultantComposite vsEditor = new ConsultantComposite();
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
		form.addChild(new TextField());
		form.addChild(new Combox());
		form.addChild(new Combox());
		form.addChild(new Combox());
		return form;
	}
	public Grid createGrid() {
		Grid grid = new Grid();
		grid.setSize(450, 300);
		grid.addCol(new GridColumn());
		grid.addCol(new GridColumn());
		grid.addCol(new GridColumn());
		grid.setPropertyValue(ComponentProperties.navBarType,
				Grid.NAVBAR_COMPLEX);
//		grid.getDataset().setPropertyValue(ComponentProperties.selectionModel,
//				Dataset.SELECT_MULTI);
		return grid;
	}
	
	
	
	
	
	
	
	
	
	
	
}
