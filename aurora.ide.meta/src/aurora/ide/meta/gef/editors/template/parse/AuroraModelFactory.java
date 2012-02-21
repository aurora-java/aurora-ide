package aurora.ide.meta.gef.editors.template.parse;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.models.FieldSet;
import aurora.ide.meta.gef.editors.models.Form;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.HBox;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.TabFolder;
import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.models.VBox;

public class AuroraModelFactory {
	@SuppressWarnings("unchecked")
	public static <T extends AuroraComponent> T createModel(String type) {
		if (type == null)
			throw new RuntimeException("the model type should not be null.");
		type = type.toLowerCase();
		if ("form".equals(type)) {
			return (T) new Form();
		} else if ("fieldset".equals(type)) {
			return (T) new FieldSet();
		} else if ("vbox".equals(type)) {
			return (T) new VBox();
		} else if ("hbox".equals(type)) {
			return (T) new HBox();
		} else if ("textfield".equals(type)) {
			Input input = new Input();
			input.setType(Input.TEXT);
			return (T) input;
		} else if ("number".equals(type)) {
			Input input = new Input();
			input.setType(Input.NUMBER);
			return (T) input;
		} else if ("lov".equals(type)) {
			Input input = new Input();
			input.setType(Input.LOV);
			return (T) input;
		} else if ("combo".equals(type)) {
			Input input = new Input();
			input.setType(Input.Combo);
			return (T) input;
		} else if ("cal".equals(type)) {
			Input input = new Input();
			input.setType(Input.CAL);
			return (T) input;
		} else if ("datatimepicker".equals(type)) {
			Input input = new Input();
			input.setType(Input.DATETIMEPICKER);
			return (T) input;
		} else if ("checkbox".equals(type)) {
			return (T) new CheckBox();
		} else if ("grid".equals(type)) {
			return (T) new Grid();
		} else if ("tabfolder".equals(type)) {
			return (T) new TabFolder();
		} else if ("tabitem".equals(type)) {
			return (T) new TabItem();
		}
		throw new RuntimeException("the model type is not valid.");
	}

}
