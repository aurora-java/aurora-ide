package aurora.ide.meta.gef.editors.models.io;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.CheckboxDatasetField;
import aurora.ide.meta.gef.editors.models.ComboDatasetField;
import aurora.ide.meta.gef.editors.models.DatasetField;
import aurora.ide.meta.gef.editors.models.LovDatasetField;

public class DatasetFieldHandler implements IOHandler {

	public CompositeMap toCompositeMap(AuroraComponent ac, ModelIOContext mic) {
		DatasetField df = (DatasetField) ac;
		CompositeMap dfMap = new CompositeMap();
		dfMap.setName(DatasetField.class.getSimpleName());
		dfMap.put("class", df.getClass().getName());
		dfMap.put(DatasetField.REQUIRED,
				df.getPropertyValue(DatasetField.REQUIRED));
		dfMap.put(DatasetField.READONLY,
				df.getPropertyValue(DatasetField.READONLY));

		if (df instanceof ComboDatasetField) {
			dfMap.put(DatasetField.DISPLAY_FIELD,
					df.getPropertyValue(DatasetField.DISPLAY_FIELD));
			dfMap.put(DatasetField.VALUE_FIELD,
					df.getPropertyValue(DatasetField.VALUE_FIELD));
			dfMap.put(DatasetField.RETURN_FIELD,
					df.getPropertyValue(DatasetField.RETURN_FIELD));
			dfMap.put(DatasetField.OPTIONS,
					df.getPropertyValue(DatasetField.OPTIONS));
		} else if (df instanceof LovDatasetField) {
			dfMap.put(DatasetField.LOV_GRID_HEIGHT,
					df.getPropertyValue(DatasetField.LOV_GRID_HEIGHT));
			dfMap.put(DatasetField.LOV_HEIGHT,
					df.getPropertyValue(DatasetField.LOV_HEIGHT));
			dfMap.put(DatasetField.LOV_WIDTH,
					df.getPropertyValue(DatasetField.LOV_WIDTH));
			dfMap.put(DatasetField.LOV_SERVICE,
					df.getPropertyValue(DatasetField.LOV_SERVICE));
			dfMap.put(DatasetField.LOV_URL,
					df.getPropertyValue(DatasetField.LOV_URL));
			dfMap.put(DatasetField.TITLE,
					df.getPropertyValue(DatasetField.TITLE));
		} else if (df instanceof CheckboxDatasetField) {
			dfMap.put(DatasetField.CHECKED_VALUE,
					df.getPropertyValue(DatasetField.CHECKED_VALUE));
			dfMap.put(DatasetField.UNCHECKED_VALUE,
					df.getPropertyValue(DatasetField.UNCHECKED_VALUE));
			dfMap.put(DatasetField.DEFAULT_VALUE,
					df.getPropertyValue(DatasetField.DEFAULT_VALUE));
		}
		return dfMap;
	}

	public DatasetField fromCompositeMap(CompositeMap map, ModelIOContext mic) {
		DatasetField df = null;
		try {
			df = (DatasetField) Class.forName(map.getString("class"))
					.newInstance();
			df.setPropertyValue(DatasetField.REQUIRED,
					map.getBoolean(DatasetField.REQUIRED));
			df.setPropertyValue(DatasetField.READONLY,
					map.getBoolean(DatasetField.READONLY));
			if (df instanceof ComboDatasetField) {
				df.setPropertyValue(DatasetField.DISPLAY_FIELD,
						map.getString(DatasetField.DISPLAY_FIELD));
				df.setPropertyValue(DatasetField.VALUE_FIELD,
						map.getString(DatasetField.VALUE_FIELD));
				df.setPropertyValue(DatasetField.RETURN_FIELD,
						map.getString(DatasetField.RETURN_FIELD));
				df.setPropertyValue(DatasetField.OPTIONS,
						map.getString(DatasetField.OPTIONS));
			} else if (df instanceof LovDatasetField) {
				df.setPropertyValue(DatasetField.LOV_GRID_HEIGHT,
						map.getInt(DatasetField.LOV_GRID_HEIGHT));
				df.setPropertyValue(DatasetField.LOV_HEIGHT,
						map.getInt(DatasetField.LOV_HEIGHT));
				df.setPropertyValue(DatasetField.LOV_WIDTH,
						map.getInt(DatasetField.LOV_WIDTH));
				df.setPropertyValue(DatasetField.LOV_SERVICE,
						map.getString(DatasetField.LOV_SERVICE));
				df.setPropertyValue(DatasetField.LOV_URL,
						map.getString(DatasetField.LOV_URL));
				df.setPropertyValue(DatasetField.TITLE,
						map.getString(DatasetField.TITLE));
			} else if (df instanceof CheckboxDatasetField) {
				df.setPropertyValue(DatasetField.CHECKED_VALUE,
						map.getString(DatasetField.CHECKED_VALUE));
				df.setPropertyValue(DatasetField.UNCHECKED_VALUE,
						map.getString(DatasetField.UNCHECKED_VALUE));
				df.setPropertyValue(DatasetField.DEFAULT_VALUE,
						map.getString(DatasetField.DEFAULT_VALUE));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return df;
	}

}
