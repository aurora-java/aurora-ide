/*
 * Created on 2007-7-6
 */
package aurora.plugin.sap.jco3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.logging.ILogger;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class Table {
	public static final String IMPORT = "import";
	public static final String EXPORT = "export";

	/** Name of table */
	public String Name;

	/**
	 * Where to put export table. If table is fetched as CompositeMap, this is a
	 * 'map path', such as '/model/result/list'; if table is fetched as Array,
	 * this is a 'attribute path', such as '/model/result/@field'
	 */
	public String Target;

	/**
	 * Type of table, 'export' or 'import'
	 */
	public String Type = EXPORT;

	public boolean fetchAll = false;

	/**
	 * Source field path for import table
	 */
	public String Source_field;

	ILogger logger;

	FieldMapping[] field_mappings;
	// HashMap source_map;
	HashMap name_map;

	public Table() {
	}

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

	public JCoTable getJCOTable(JCoParameterList list) {
		try {
			JCoTable table = list.getTable(Name);
			if (table == null)
				throw new IllegalArgumentException("Table '" + Name
						+ "' doesn't exist");
			return table;
		} catch (Throwable t) {
			throw new IllegalArgumentException("Can't get table '" + Name
					+ "':" + t.getMessage());
		}
	}

	public void fillJCOTable(JCoTable table, CompositeMap context) {
		CompositeMap map = (CompositeMap) context.getObject(Source_field);
		int line = 0;
		if (map.getChildIterator() != null) {
			List records = map.getChilds();
			table.appendRows(records.size());
			logger.info("Appending " + records.size() + " rows to ABAP table "
					+ Name);
			Iterator iterator = records.iterator();
			while (iterator.hasNext()) {
				CompositeMap record = (CompositeMap) iterator.next();
				logger.info("================ end line " + line
						+ "=====================");
				for (int i = 0; i < field_mappings.length; i++) {
					FieldMapping mapping = field_mappings[i];
					Object value = record.getObject(mapping.Source_field);
					if (value == null)
						value = mapping.Value;
					logger.info(mapping.Name + " -> " + value);
					table.setValue(mapping.Name, value);
				}
				table.nextRow();
				line++;
			}
			logger.info("\r\nTable transfered");
		}
	}

	/**
	 * Fill a CompositeMap with records fetched from JCO.Table
	 * 
	 * @param records
	 *            An instance of JCO.Table containing data
	 * @param result
	 *            Target CompositeMap to be filled with, each record in
	 *            JCO.Table will be created as a child record of CompositeMap
	 * @return filled CompositeMap
	 */
	public CompositeMap fillCompositeMap(JCoTable records, CompositeMap result) {
		FieldMapping mapping = null;
		String returnField;
		records.firstRow();
		if (field_mappings != null && field_mappings.length > 0) {
			if (!fetchAll) {
				for (int n = 0; n < records.getNumRows(); n++, records
						.nextRow()) {
					CompositeMap item = new CompositeMap(
							(int) (records.getFieldCount() * 1.5));
					item.setName("record");
					for (int i = 0; i < field_mappings.length; i++) {
						mapping = field_mappings[i];
						returnField = mapping.Return_field;
						if (returnField == null)
							throw new ConfigurationError(
									"Must set 'return_field' for <field-mapping>");
						item.putObject(returnField,
								records.getValue(mapping.Name));
					}
					result.addChild(item);
				}
				return result;
			}
		}
		String fieldName;
		for (int n = 0; n < records.getNumRows(); n++, records.nextRow()) {
			CompositeMap item = new CompositeMap(
					(int) (records.getFieldCount() * 1.5));
			item.setName("record");
			Iterator<JCoField> it = records.iterator();
			while (it.hasNext()) {
				JCoField field = it.next();
				fieldName = field.getName();
				if (name_map != null)
					mapping = (FieldMapping) name_map.get(fieldName
							.toLowerCase());
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
		}
		return result;
	}
}
