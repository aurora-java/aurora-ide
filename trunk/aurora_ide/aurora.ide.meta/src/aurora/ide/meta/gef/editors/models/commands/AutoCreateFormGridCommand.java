package aurora.ide.meta.gef.editors.models.commands;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.ResultDataSet;

public class AutoCreateFormGridCommand extends DropBMCommand {

	private Container container;
	private Button search;
	private Button reset;
	private Form form;
	private Grid grid;

	public void execute() {
		Form createForm = createForm();
		container.addChild(createForm);

		container.addChild(createButtons());

		container.addChild(createGrid());

	}

	protected AuroraComponent createGrid() {
		grid = new Grid();
		fillGrid(grid);
		Dataset dataset = grid.getDataset();
		// search.setTargetComponent(grid);
		if (dataset instanceof ResultDataSet) {
			((ResultDataSet) dataset).setQueryContainer(form);
			dataset.setModel(this.getBm().getProjectRelativePath().toString());
			dataset.setId(this.getBm().getFullPath().removeFileExtension()
					.lastSegment()
					+"_result_ds");
		}

		return grid;
	}

	protected AuroraComponent createButtons() {
		// TODO js 查询
		HBox hbox = new HBox();
		search = new Button();
		// search.setButtonType(Button.B_SEARCH);

		search.setText("查询");

		reset = new Button();
		reset.setText("重置");
		// reset.setButtonType(Button.B_RESET);
		// reset.setTargetComponent(form);
		hbox.addChild(search);
		hbox.addChild(reset);
		return hbox;
	}

	protected Form createForm() {
		form = new Form();
		Dataset dataset = form.getDataset();
		// search.setTargetComponent(grid);

		dataset.setModel(this.getBm().getProjectRelativePath().toString());
		dataset.setId(this.getBm().getFullPath().removeFileExtension()
				.lastSegment()
				+ "_query_ds");

		fillForm(form);
		return form;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

}
