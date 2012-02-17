package aurora.ide.meta.gef.editors.models.io;

import org.eclipse.draw2d.geometry.Dimension;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.DatasetField;
import aurora.ide.meta.gef.editors.models.GridColumn;

public class GridColumnHandler extends DefaultIOHandler {

	@Override
	protected void storeSimpleAttribute(CompositeMap map, AuroraComponent ac) {
		GridColumn gc = (GridColumn) ac;
		map.put(GridColumn.PROMPT, gc.getPrompt());
		map.put(GridColumn.WIDTH, gc.getSize().width);
		map.put(GridColumn.EDITOR, gc.getEditor());
	}

	@Override
	protected void storeComplexAttribute(CompositeMap map, AuroraComponent ac) {
		GridColumn gc = (GridColumn) ac;
		DatasetFieldHandler dsf = new DatasetFieldHandler();
		CompositeMap dsMap = dsf.toCompositeMap(gc.getDatasetField(), mic);
		map.addChild(dsMap);
	}

	@Override
	protected void restoreSimpleAttribute(AuroraComponent ac, CompositeMap map) {
		GridColumn gc = (GridColumn) ac;
		gc.setPrompt(map.getString(GridColumn.PROMPT));
		gc.setSize(new Dimension(map.getInt(GridColumn.WIDTH),
				gc.getSize().height));
		gc.setEditor(map.getString(GridColumn.EDITOR));
	}

	@Override
	protected void restoreComplexAttribute(AuroraComponent ac, CompositeMap map) {
		GridColumn gc = (GridColumn) ac;
		CompositeMap dfMap = map.getChild(DatasetField.class.getSimpleName());
		DatasetField df = new DatasetFieldHandler()
				.fromCompositeMap(dfMap, mic);
		gc.setDatasetField(df);
	}

	@Override
	protected AuroraComponent getNewObject(CompositeMap map) {
		return new GridColumn();
	}

}
