package aurora.ide.meta.gef.editors.source.gen;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.source.gen.core.AuroraComponent2CompositMap;

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
				Object value = c.getPropertyValue(id);
				if (ResultDataSet.SELECTION_MODE.equals(id)
						&& c instanceof ResultDataSet) {
					value = ((ResultDataSet) c).getSelectionMode();
				}

				if (value != null && !("".equals(value))) {
					map.putString(id, value.toString());
					if (ResultDataSet.SELECTION_MODE.equals(id)
							&& c instanceof ResultDataSet) {
						map.put(ResultDataSet.SELECTABLE, Boolean.TRUE);
					}
				}
			}
		}
		return map;
	}

	static private List<String> keys = new ArrayList<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			this.add(ResultDataSet.QUERY_CONTAINER);
			this.add(ResultDataSet.MODEL);
		}
	};

	public boolean isCompositMapKey(String key) {
		return !keys.contains(key);
	}

}
