package aurora.ide.meta.gef.editors.models.io;

import org.eclipse.draw2d.geometry.Dimension;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.DatasetField;
import aurora.ide.meta.gef.editors.models.Label;

public class LabelHandler extends DefaultIOHandler {

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new Label();
	}

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		Label label = (Label) ac;
		String type = label.getType();
		map.put(Label.PROMPT, label.getPrompt());
		map.put(Label.TYPE, type);
		map.put(Label.WIDTH, label.getSize().width);
		map.put(Label.RENDERER, label.getRenderer());
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		Label label = (Label) ac;
		DatasetField df = label.getDatasetField();
		CompositeMap dfMap = new DatasetFieldHandler().toCompositeMap(df, mic);
		map.addChild(dfMap);
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		Label label = (Label) ac;
		label.setPrompt(map.getString(Label.PROMPT));
		label.setType(map.getString(Label.TYPE));
		label.setSize(new Dimension(map.getInt(Label.WIDTH),
				label.getSize().height));
		label.setRenderer(map.getString(Label.RENDERER));
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		Label label = (Label) ac;
		CompositeMap dfMap = map.getChild(DatasetField.class.getSimpleName());
		if (dfMap != null) {
			DatasetField df = new DatasetFieldHandler().fromCompositeMap(dfMap,
					mic);
			label.setDatasetField(df);
		}
	}
}
