package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.DatasetField;

public class DatasetFieldHandler implements IOHandler {
	static final String[] keys = { DatasetField.LOV_GRID_HEIGHT,
			DatasetField.LOV_HEIGHT, DatasetField.LOV_URL, DatasetField.TITLE,
			DatasetField.CHECKED_VALUE, DatasetField.UNCHECKED_VALUE };

	public CompositeMap toCompositeMap(AuroraComponent ac, ModelIOContext mic) {
		DatasetField df = (DatasetField) ac;
		CompositeMap dfMap = new CompositeMap();
		dfMap.setName(DatasetField.class.getSimpleName());
		dfMap.put(DatasetField.REQUIRED,
				df.getPropertyValue(DatasetField.REQUIRED));
		dfMap.put(DatasetField.READONLY,
				df.getPropertyValue(DatasetField.READONLY));

		for (String key : keys) {
			Object val = df.getPropertyValue(key);
			if (val == null || "".equals(val)
					|| (val instanceof Integer && (Integer) val == 0))
				continue;
			dfMap.put(key, val);
		}
		return dfMap;
	}

	public DatasetField fromCompositeMap(CompositeMap map, ModelIOContext mic) {
		DatasetField df = new DatasetField();
		df.setPropertyValue(DatasetField.REQUIRED,
				map.getBoolean(DatasetField.REQUIRED));
		df.setPropertyValue(DatasetField.READONLY,
				map.getBoolean(DatasetField.READONLY));

		for (String key : keys) {
			Object val = map.get(key);
			if (val != null)
				df.setPropertyValue(key, val);
		}
		return df;
	}

}
