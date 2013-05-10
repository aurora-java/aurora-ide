package aurora.ide.create.component;

import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;

import uncertain.composite.CompositeMap;

public class ComponentListFactory {
	public static final String TYPE = "type";
	public static final String FORM_GRID = "form_grid";
	public static final String GRID = "grid";
	public static final String FORM_EDIT = "form_edit";
	public static final String FORM_QUERY = "form_query";

	public static CompositeMap createInput() {
		CompositeMap form_query = new CompositeMap("Form(查询)");
		form_query.put(TYPE, FORM_QUERY);

		CompositeMap form_result = new CompositeMap("Form(编辑)");
		form_result.put(TYPE, FORM_EDIT);

		CompositeMap grid = new CompositeMap("Grid(编辑)");
		grid.put(TYPE, GRID);

		CompositeMap form_grid = new CompositeMap("Form(查询) + Grid(编辑)");
		form_grid.put(TYPE, FORM_GRID);

		CompositeMap input = new CompositeMap();
		input.addChild(form_query);
		input.addChild(form_result);
		input.addChild(grid);
		input.addChild(form_grid);
		return input;
	}

	public static Image getImage(CompositeMap element) {
		String type = element.getString(TYPE, "");
		if (FORM_QUERY.equals(type)) {
			return PrototypeImagesUtils.getImage("palette/form.png");
		}
		if (FORM_EDIT.equals(type)) {
			return PrototypeImagesUtils.getImage("palette/form.png");
		}
		if (GRID.equals(type)) {
			return PrototypeImagesUtils.getImage("palette/grid.png");
		}
		if (FORM_GRID.equals(type)) {
			return PrototypeImagesUtils.getImage("palette/grid.png");
		}
		return null;
	}
}
