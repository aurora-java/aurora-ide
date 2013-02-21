package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.DatasetField;

public class DatasetFieldHandler implements IOHandler {
	private static final Object[][] keymap = {
			{ DatasetField.REQUIRED, Boolean.class },
			{ DatasetField.READONLY, Boolean.class },
			{ DatasetField.LOV_GRID_HEIGHT, Integer.class },
			{ DatasetField.LOV_WIDTH, Integer.class },
			{ DatasetField.LOV_HEIGHT, Integer.class },
			{ DatasetField.LOV_URL, String.class },
			{ DatasetField.TITLE, String.class },
			{ DatasetField.CHECKED_VALUE, String.class },
			{ DatasetField.UNCHECKED_VALUE, String.class },
			{ DatasetField.DEFAULT_VALUE, String.class } };

	public CompositeMap toCompositeMap(AuroraComponent ac, ModelIOContext mic) {
		DatasetField df = (DatasetField) ac;
		CompositeMap dfMap = new CommentCompositeMap();
		dfMap.setName(DatasetField.class.getSimpleName());
		dfMap.put(DefaultIOHandler.COMPONENT_TYPE, ac.getType());
		for (Object[] key : keymap) {
			Object val = df.getPropertyValue(key[0]);
			if (val == null || "".equals(val)
					|| (val instanceof Integer && (Integer) val == 0))
				continue;
			dfMap.put(key[0], val);
		}
		return dfMap;
	}

	public DatasetField fromCompositeMap(CompositeMap map, ModelIOContext mic) {
		DatasetField df = new DatasetField();
		String component_type = map.getString(DefaultIOHandler.COMPONENT_TYPE);
		if (component_type != null)
			df.setType(component_type);
		for (Object[] key : keymap) {
			String val = map.getString(key[0]);
			if (val != null) {
				df.setPropertyValue(key[0],
						convertValue(val, (Class<?>) key[1]));
			}
		}
		return df;
	}

	private Object convertValue(String val, Class<?> cls) {
		if (Boolean.class.equals(cls))
			return Boolean.valueOf(val);
		else if (Integer.class.equals(cls)) {
			if (val == null || val.length() == 0)
				return 0;
			return Integer.valueOf(val);
		}
		return val;
	}
}
