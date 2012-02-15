package aurora.ide.meta.gef.editors.source.gen;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.ResultDataSet;

public class DatasetMap extends AbstractComponentMap {

	private Dataset c;

	public DatasetMap(Dataset c) {
		this.c = c;
	}

	public CompositeMap toCompositMap() {
		String type = c.getType();
		CompositeMap map = AuroraComponent2CompositMap.createChild(type);
		IPropertyDescriptor[] propertyDescriptors = c.getPropertyDescriptors();
		for (IPropertyDescriptor iPropertyDescriptor : propertyDescriptors) {
			Object id = iPropertyDescriptor.getId();

			boolean isKey = this.isCompositMapKey(id.toString());
			if (isKey) {
				if (ResultDataSet.QUERY_CONTAINER.equals(id)) {
					id = ResultDataSet.QUERY_DATASET;
					//TODO autocreate = true;
				}
				Object value = c.getPropertyValue(id);
				if (ResultDataSet.SELECTION_MODE.equals(id)
						&& c instanceof ResultDataSet) {
					value = ((ResultDataSet) c).getSelectionMode();
				}

				if (value != null && !("".equals(value)))
					map.putString(id, value.toString());
			}
		}
		return map;
	}

	public boolean isCompositMapKey(String key) {
		return true;
	}

}
