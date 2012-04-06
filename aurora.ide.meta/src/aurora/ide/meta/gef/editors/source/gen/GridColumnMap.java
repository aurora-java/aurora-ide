package aurora.ide.meta.gef.editors.source.gen;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.source.gen.core.AuroraComponent2CompositMap;
import aurora.ide.meta.gef.editors.source.gen.core.ScreenGenerator;

public class GridColumnMap extends AbstractComponentMap {

	private GridColumn c;
	private ScreenGenerator screenGenerator;

	public GridColumnMap(GridColumn c, ScreenGenerator screenGenerator) {
		this.screenGenerator = screenGenerator;
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

				if (value != null && !("".equals(value)))
					map.putString(id, value.toString());
			}
		}
		if (isCombo()) {
			map.putString("name", c.getName() + "_display");
		}
		return map;
	}

	private boolean isCombo() {

		if (c instanceof GridColumn) {
			return Input.Combo.equals(((GridColumn) c).getEditor());
		}
		return false;
	}

	
	static private List<String> keys = new ArrayList<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			this.add(GridColumn.EDITOR);
			this.add(GridColumn.RENDERER);
			this.add(ResultDataSet.READONLY);
			this.add(ResultDataSet.REQUIRED);
		}
	};

	public boolean isCompositMapKey(String key) {
		return !keys.contains(key);
	}
}
