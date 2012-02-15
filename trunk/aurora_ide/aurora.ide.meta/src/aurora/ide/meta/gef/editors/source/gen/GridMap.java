package aurora.ide.meta.gef.editors.source.gen;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.Grid;
import aurora.ide.meta.gef.editors.models.ResultDataSet;

public class GridMap extends AbstractComponentMap {

	private Grid c;

	public GridMap(Grid c) {
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
				Object value = null;
				if (Grid.NAVBAR_TYPE.equals(id)) {
					value = c.getNavBarType();
					map.put(Grid.NAVBAR, !Grid.NAVBAR_NONE.equals(value));
				} else
					value = c.getPropertyValue(id).toString();
				if (value != null && !("".equals(value)))
					map.putString(id, value.toString());
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
			this.add(ResultDataSet.SELECTION_MODE);
			this.add(ResultDataSet.PAGE_SIZE);
			this.add(ResultDataSet.SELECTABLE);
			this.add(ResultDataSet.QUERY_DATASET);
			this.add(ResultDataSet.QUERY_CONTAINER);
			this.add(ResultDataSet.AUTO_QUERY);
			this.add(ResultDataSet.MODEL);
			this.add(ResultDataSet.ID);
		}
	};

	public boolean isCompositMapKey(String key) {
		return !keys.contains(key);
	}
}
