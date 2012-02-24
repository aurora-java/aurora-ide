package aurora.ide.meta.gef.editors.models.value;

import java.util.HashMap;
import java.util.Map;

import aurora.ide.meta.gef.editors.models.DatasetField;

public class DefaultValueManager {

	// IDatasetFieldDelegate
	private static final Map<String, Object> ds_fields_values = new HashMap<String, Object>() {
		{
			put(DatasetField.READONLY, false);
			put(DatasetField.REQUIRED, false);
		}
	};

	public static final boolean isDefault(Class clazz, String name, Object value) {
		return false;
	}

}
