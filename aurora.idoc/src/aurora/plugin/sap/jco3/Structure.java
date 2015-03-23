package aurora.plugin.sap.jco3;

import java.util.HashMap;
import java.util.Iterator;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.logging.ILogger;

public class Structure {
	public static final String IMPORT = "import";
	public static final String EXPORT = "export";

	/** Name of Structure */
	public String Name;

	public String Target;

	/**
	 * Type of Structure, 'export' or 'import'
	 */
	public String Type = IMPORT;

	ILogger logger;

	HashMap name_map;

	boolean fetchAll;

	FieldMapping[] field_mappings;

	public void setLogger(ILogger logger) {
		this.logger = logger;
	}

	public boolean getFetchAll() {
		return fetchAll;
	}

	public void setFetchAll(boolean fetchAll) {
		this.fetchAll = fetchAll;
	}

	public void setFieldMappings(FieldMapping[] m) {
		field_mappings = m;
		name_map = new HashMap();
		for (int i = 0; i < m.length; i++) {
			if (m[i].Name == null)
				throw new ConfigurationError(
						"Must set 'name' for <field-mapping>");
			name_map.put(m[i].Name.toLowerCase(), m[i]);
		}
	}

	public FieldMapping[] getFieldMappings() {
		return field_mappings;
	}

	public boolean isImport() {
		return IMPORT.equalsIgnoreCase(Type);
	}

	public JCoStructure getJCOStructure(JCoParameterList list) {
		try {
			JCoStructure structure = list.getStructure(Name);
			if (structure == null)
				throw new IllegalArgumentException("Structure '" + Name
						+ "' doesn't exist");
			return structure;
		} catch (Throwable t) {
			throw new IllegalArgumentException("Can't get Structure '" + Name
					+ "':" + t.getMessage());
		}
	}

	public void fillJCOStructure(JCoStructure structure, CompositeMap context) {
		logger.info("ABAP Structure " + Name);
		if (field_mappings != null) {
			for (int i = 0; i < field_mappings.length; i++) {
				FieldMapping fieldMapping = field_mappings[i];
				Object value = context.getObject(fieldMapping.Source_field);
				if (value == null)
					value = fieldMapping.Value;
				structure.setValue(fieldMapping.Name, value);
				logger.info(fieldMapping.Name + " -> " + value);
			}
		}
		logger.info("\r\nStructure transfered");
	}

	public CompositeMap fillCompositeMap(JCoStructure records,
			CompositeMap result) {
		FieldMapping mapping = null;
		String returnField;
		CompositeMap item = new CompositeMap(
				(int) (records.getFieldCount() * 1.5));
		item.setName("record");
		if (field_mappings != null && field_mappings.length > 0) {
			if (!fetchAll) {

				for (int i = 0; i < field_mappings.length; i++) {
					mapping = field_mappings[i];
					returnField = mapping.Return_field;
					if (returnField == null)
						throw new ConfigurationError(
								"Must set 'return_field' for <field-mapping>");
					item.putObject(returnField, records.getValue(mapping.Name));
				}
				result.addChild(item);
				return result;
			}
		}
		String fieldName;
		Iterator<JCoField> it = records.iterator();
		while (it.hasNext()) {
			JCoField field = it.next();
			fieldName = field.getName();
			if (name_map != null)
				mapping = (FieldMapping) name_map.get(fieldName.toLowerCase());
			if (mapping != null) {
				returnField = mapping.Return_field;
				if (returnField == null)
					throw new ConfigurationError(
							"Must set 'return_field' for <field-mapping>");
				item.putObject(returnField, field.getValue());
			} else {
				item.put(fieldName, field.getValue());
			}
		}
		result.addChild(item);
		return result;
	}
}
