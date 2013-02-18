package aurora.ide.meta.gef.editors.models.io;

import org.eclipse.draw2d.geometry.Dimension;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.extensions.ComponentFactory;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.models.DatasetField;
import aurora.ide.meta.gef.editors.models.Input;

public class InputHandler extends DefaultIOHandler {

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		Input input = (Input) ac;
		String type = input.getType();
		map.put(Input.PROMPT, input.getPrompt());
		map.put(Input.TYPE, type);
		map.put(Input.WIDTH, input.getSize().width);
		map.put(Input.EMPTYTEXT, input.getEmptyText());
		map.put(Input.TYPECASE, input.getTypeCase());
		if (input instanceof CheckBox) {
			map.put(CheckBox.TEXT, ((CheckBox) input).getText());
		}
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		Input input = (Input) ac;
		DatasetField df = input.getDatasetField();
		CompositeMap dfMap = new DatasetFieldHandler().toCompositeMap(df, mic);
		map.addChild(dfMap);
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		Input input = (Input) ac;
		input.setPrompt(map.getString(Input.PROMPT));
		input.setType(map.getString(Input.TYPE));
		input.setSize(new Dimension(map.getInt(Input.WIDTH),
				input.getSize().height));
		input.setEmptyText(map.getString(Input.EMPTYTEXT));
		input.setTypeCase(map.getString(Input.TYPECASE));
		if (input instanceof CheckBox) {
			((CheckBox) input).setText(map.getString(CheckBox.TEXT));
		}
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		Input input = (Input) ac;
		CompositeMap dfMap = map.getChild(DatasetField.class.getSimpleName());
		if (dfMap != null) {
			DatasetField df = new DatasetFieldHandler().fromCompositeMap(dfMap,
					mic);
			input.setDatasetField(df);
		}
	}

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		String type = map.getString("type");
		return ComponentFactory.createComponent(type);
//		if (CheckBox.CHECKBOX.equals(map.getName()))
//			return new CheckBox();
//		return new Input();
	}
}
