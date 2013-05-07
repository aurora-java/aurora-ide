package aurora.ide.create.component;

import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.extensions.ComponentFactory;
import aurora.ide.meta.gef.Util;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.Dataset;
import aurora.plugin.source.gen.screen.model.Form;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.GridColumn;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.Toolbar;

public class ViewDiagramCreator {

	private List<CompositeMap> input;

	public ViewDiagramCreator(List<CompositeMap> input) {
		this.input = input;
	}

	public ScreenBody createPrototypeDiagram(CompositeMap typeMap) {
		ScreenBody diagram = new ScreenBody();
		if (typeMap == null)
			return diagram;
		String type = typeMap.getString(ComponentListFactory.TYPE, "");

		if (ComponentListFactory.FORM_QUERY.equals(type)) {
			Form fillForm = fillQueryForm(input);
			diagram.addChild(fillForm);
		}
		if (ComponentListFactory.FORM_EDIT.equals(type)) {
			Form fillForm = fillEditForm(input);
			diagram.addChild(fillForm);
		}
		if (ComponentListFactory.GRID.equals(type)) {
			Grid fillGrid = fillGrid(input);
			diagram.addChild(fillGrid);

		}
		if (ComponentListFactory.FORM_GRID.equals(type)) {
			Form fillForm = fillQueryForm(input);
			diagram.addChild(fillForm);
			Grid fillGrid = fillGrid(input);
			Dataset dataset = fillGrid.getDataset();
			dataset.setQueryContainer(fillForm);
			diagram.addChild(fillGrid);
		}
		return diagram;
	}

	private boolean isQueryNameMap(CompositeMap f) {
		return "query-field".equals(f.getName());
	}

	private boolean isRefFieldMap(CompositeMap f) {
		return "ref-field".equals(f.getName());
	}

	private Grid fillGrid(List<CompositeMap> fields) {
		Grid container = new Grid();
		Dataset ds = new Dataset();
		ds.setComponentType(Dataset.RESULTDATASET);
		container.setDataset(ds);
		for (CompositeMap f : fields) {
			if (isRefFieldMap(f) || isField(f)) {
				if (ds.getModel() == null || "".equals(ds.getModel().trim())) {
					ds.setModel(f.getString("model", ""));
				}
				String string = Util.getPrompt(f, "");
				GridColumn gc = new GridColumn();
				gc.setPrompt(string);
				String name = f.getString("name");
				name = name == null ? "" : name;
				gc.setName(name);
				String type = Util.getType(f);
				gc.setEditor(type);
				container.addChild(gc);
			}
		}
		container.setNavbarType(Grid.NAVBAR_COMPLEX);
		Toolbar tb = createToolbar();
		container.addChild(tb);
		container.setSelectionMode(Dataset.SELECT_MULTI);
		return container;
	}

	private Toolbar createToolbar() {
		Toolbar tb = new Toolbar();
		Button b1 = new Button();
		b1.setButtonType(Button.ADD);
		tb.addChild(b1);

		Button b2 = new Button();
		b2.setButtonType(Button.DELETE);
		tb.addChild(b2);

		Button b3 = new Button();
		b3.setButtonType(Button.SAVE);
		tb.addChild(b3);
		return tb;
	}

	private boolean isField(CompositeMap f) {
		return "field".equals(f.getName());
	}

	private Form fillQueryForm(List<CompositeMap> fields) {
		Form container = new Form();
		Dataset ds = new Dataset();
		ds.setComponentType(Dataset.QUERYDATASET);
		container.setDataset(ds);
		for (CompositeMap field : fields) {
			if (isQueryNameMap(field) || isField(field)) {
				if (ds.getModel() == null || "".equals(ds.getModel().trim())) {
					ds.setModel(field.getString("model", ""));
				}
				fillForm(container, field);
			}
		}
		return container;
	}

	private Form fillEditForm(List<CompositeMap> fields) {
		Form container = new Form();
		Dataset ds = new Dataset();
		ds.setComponentType(Dataset.RESULTDATASET);
		container.setDataset(ds);
		for (CompositeMap field : fields) {
			if (isField(field)) {
				if (ds.getModel() == null || "".equals(ds.getModel().trim())) {
					ds.setModel(field.getString("model", ""));
				}
				fillForm(container, field);
			}
		}
		return container;
	}

	private void fillForm(Form container, CompositeMap field) {
		String name = (String) field.get("field");
		name = name == null ? field.getString("name") : name;
		name = name == null ? "" : name;
		String type = Util.getType(field);
//		AuroraComponent input = new Input();
//		if (CheckBox.CHECKBOX.equals(type)) {
//			input = new CheckBox();
//		}
		AuroraComponent input = ComponentFactory.createComponent(type);
		input.setComponentType(type);
		input.setName(name);
		input.setPrompt(Util.getPrompt(field, ""));
		container.addChild(input);
	}
}
