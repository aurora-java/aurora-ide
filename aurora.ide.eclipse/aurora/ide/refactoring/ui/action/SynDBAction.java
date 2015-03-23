package aurora.ide.refactoring.ui.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.datatype.DataType;
import uncertain.datatype.DataTypeRegistry;
import aurora.ide.AuroraPlugin;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.bm.BMUtil;
import aurora.ide.celleditor.CellInfo;
import aurora.ide.celleditor.ICellEditor;
import aurora.ide.celleditor.StringTextCellEditor;
import aurora.ide.editor.textpage.TextPage;
import aurora.ide.editor.widgets.GridViewer;
import aurora.ide.editor.widgets.core.IGridViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DBConnectionUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.DocumentUtil;
import aurora.ide.search.cache.CacheManager;

public class SynDBAction extends Action {

	private TextPage textPage;

	private List<String> bmFields;

	public GridViewer gridViewer;

	public CompositeMap selectedFields;

	//final private static String SQL = "select  t.COLUMN_NAME,c.comments,t.nullable,t.data_type,t.data_length  from user_tab_columns t, user_col_comments c where  t.column_name = c.column_name and t.table_name = c.table_name and t.table_name=?";
	final private static String COLUMN_SQL = "select  t.COLUMN_NAME,t.nullable,t.data_type,t.data_length  from user_tab_columns t where  t.table_name=?";
	final private static String COMMENT_SQL = "select  c.COLUMN_NAME,c.comments  from  user_col_comments c where  c.table_name=?";

	// select distinct t.DATA_TYPE from user_tab_columns t
	static private final String[] columnNames = { "COLUMN_NAME", "COMMENTS",
			"NULLABLE", "DATA_TYPE", "DATA_LENGTH" };
	static private final String[] excluedColumns = { "CREATED_BY",
			"CREATION_DATE", "LAST_UPDATED_BY", "LAST_UPDATE_DATE" };
	static private final String[] columnTitles = { "列名", "描述", "可空", "类型",
			"大小", };

	public SynDBAction() {
		this.setText("数据库表同步");
		this.setToolTipText("数据库表同步");
		this.setId("aurora.ide.refactoring.ui.action.SynDBAction");
	}

	public SynDBAction(TextPage textPage) {
		this();
		this.textPage = textPage;
	}

	@Override
	public boolean isEnabled() {
		String t = getBaseTable();
		if (t == null || "".equals(t)) {
			return false;
		}
		return AuroraPlugin.getActivePage().getActiveEditor().isDirty() == false;

	}

	public String getBaseTable() {
		String fileProperty = this.getFileProperty("baseTable");
		return fileProperty == null ? fileProperty : fileProperty.toUpperCase();
	}

	public String getDataSourceName() {
		String fileProperty = this.getFileProperty("dataSourceName");
		return "".equals(fileProperty) ? null : fileProperty;
	}

	public String getFileProperty(String key) {
		IFile file = textPage.getFile();
		if (file != null) {
			try {
				CompositeMap model = CacheManager.getCompositeMap(file);
				String table = CompositeMapUtil.getValueIgnoreCase(model, key);
				return table;
			} catch (CoreException e) {
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public CompositeMap getRootMap() {
		IFile file = textPage.getFile();
		if (file != null) {
			try {
				CompositeMap model = CacheManager.getCompositeMap(file);
				return model;
			} catch (CoreException e) {
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public List<String> getBmFields() {
		final List<String> fields = new ArrayList<String>();
		IFile file = textPage.getFile();
		if (file != null) {
			try {
				CompositeMap model = CacheManager.getCompositeMap(file);
				model.iterate(new IterationHandle() {
					public int process(CompositeMap map) {
						if ("field".equalsIgnoreCase(map.getName())) {
							String string = map.getString("name", "");
							if ("".equals(string) == false)
								fields.add(string.toUpperCase());
						}
						return IterationHandle.IT_CONTINUE;
					}
				}, false);
			} catch (CoreException e) {
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
		}
		return fields;
	}

	private boolean isExcluedColumns(String c) {
		for (String s : excluedColumns) {
			if (s.equalsIgnoreCase(c)) {
				return true;
			}
		}
		return false;
	}

	public CompositeMap queryColumns(String table, Connection conn) {
		CompositeMap input = new CommentCompositeMap();
		PreparedStatement sta = null;
		if (conn != null) {
			try {
				sta = conn.prepareStatement(COLUMN_SQL);
				sta.setString(1, table);
				ResultSet resultSet = sta.executeQuery();
				while (resultSet.next()) {
					if (isExcluedColumns(resultSet.getString(columnNames[0]))) {
						continue;
					}
					if (isExist(resultSet.getString(columnNames[0]))) {
						continue;
					}
					CompositeMap element = new CommentCompositeMap();
					element.put(columnNames[0],
							resultSet.getString(columnNames[0]));
					element.put(columnNames[2],
							resultSet.getString(columnNames[2]));
					element.put(columnNames[3],
							resultSet.getString(columnNames[3]));
					element.put(columnNames[4],
							new Integer(resultSet.getInt(columnNames[4])));
					input.addChild(element);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (sta != null) {
					try {
						sta.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return input;
	}

	private CompositeMap findColumn(CompositeMap columns, String name) {
		List childs = columns.getChilds();
		if (childs != null) {
			for (Object object : childs) {
				if (object instanceof CompositeMap) {
					String string = ((CompositeMap) object).getString(
							columnNames[0], "");
					if (string.equals(name)) {
						return (CompositeMap) object;
					}
				}
			}
		}
		return null;
	}

	public void queryComments(CompositeMap columns, String table,
			Connection conn) {
		PreparedStatement sta = null;
		if (conn != null) {
			try {
				sta = conn.prepareStatement(COMMENT_SQL);
				sta.setString(1, table);
				ResultSet resultSet = sta.executeQuery();
				while (resultSet.next()) {
					CompositeMap element = findColumn(columns,
							resultSet.getString(columnNames[0]));
					if (element != null)
						element.put(columnNames[1],
								resultSet.getString(columnNames[1]));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (sta != null) {
					try {
						sta.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	// public CompositeMap queryColumns2(String table, Connection conn) {
	// CompositeMap input = new CommentCompositeMap();
	// PreparedStatement sta = null;
	// if (conn != null) {
	// try {
	// sta = conn.prepareStatement(SQL);
	// sta.setString(1, table);
	// ResultSet resultSet = sta.executeQuery();
	// while (resultSet.next()) {
	// if (isExcluedColumns(resultSet.getString(columnNames[0]))) {
	// continue;
	// }
	// if (isExist(resultSet.getString(columnNames[0]))) {
	// continue;
	// }
	// CompositeMap element = new CommentCompositeMap();
	// element.put(columnNames[0],
	// resultSet.getString(columnNames[0]));
	// element.put(columnNames[1],
	// resultSet.getString(columnNames[1]));
	// element.put(columnNames[2],
	// resultSet.getString(columnNames[2]));
	// element.put(columnNames[3],
	// resultSet.getString(columnNames[3]));
	// element.put(columnNames[4],
	// new Integer(resultSet.getInt(columnNames[4])));
	// input.addChild(element);
	// }
	// } catch (SQLException e) {
	// e.printStackTrace();
	// } finally {
	// if (sta != null) {
	// try {
	// sta.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if (conn != null) {
	// try {
	// conn.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	// return input;
	// }

	private boolean isExist(String string) {
		return this.bmFields.contains(string.toUpperCase());
	}

	public void run() {
		Shell shell = textPage.getSite().getShell();
		IDocument document = textPage.getDocument();
		bmFields = getBmFields();
		selectedFields = null;
		ColumnsDialog dia = new ColumnsDialog(shell);
		if (dia.open() == ColumnsDialog.OK) {
			if (selectedFields != null) {
				int offset = -1;
				CompositeMap rootMap = getRootMap();
				CompositeMap fields = rootMap.getChild("fields");
				String insertText = "";
				if (fields != null) {
					try {
						offset = DocumentUtil.getMapLineOffset(document,
								fields, -1, false);
						StringBuilder sb = new StringBuilder();
						List childsNotNull = selectedFields.getChildsNotNull();
						for (Object object : childsNotNull) {
							if (object instanceof CompositeMap) {
								String xml = ((CompositeMap) object).toXML();
								sb.append(xml);
							}
						}
						insertText = sb.toString();
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				} else {
					try {
						offset = DocumentUtil.getMapLineOffset(document,
								rootMap, 0, true);
						insertText = selectedFields.toXML();
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
				IRewriteTarget target = (IRewriteTarget) textPage
						.getAdapter(IRewriteTarget.class);
				if (target != null)
					target.beginCompoundChange();
				if (offset != -1) {
					try {
						document.replace(offset, 0, insertText);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
				if (target != null)
					target.endCompoundChange();
			}
		}

	}

	public CompositeMap getSelectedFields() {
		String pre = BMUtil.BMPrefix;
		CompositeMap rootMap = getRootMap();
		if (rootMap != null) {
			pre = rootMap.getPrefix();
		}
		CompositeMap fieldsArray = new CommentCompositeMap("fields");
		fieldsArray.setPrefix(pre);

		Object[] elements = gridViewer.getCheckedElements();
		for (int j = 0; j < elements.length; j++) {
			CompositeMap record = (CompositeMap) elements[j];
			CompositeMap field = new CommentCompositeMap("field");
			field.setPrefix(pre);
			String fieldName = record.getString("COLUMN_NAME").toLowerCase();
			field.put("name", fieldName);
			field.put("physicalName", record.getString("COLUMN_NAME"));
			String dataType = record.getString("DATA_TYPE");
			field.put("databaseType", dataType);
			Integer db_data_type = getDataType(dataType);
			DataTypeRegistry dtr = DataTypeRegistry.getInstance();
			DataType dt = dtr.getType(db_data_type.intValue());
			field.put("datatype", dt.getJavaType().getName());
			String prompt = record.getString("COMMENTS");
			field.put("prompt", prompt);
			fieldsArray.addChild(field);
		}
		return fieldsArray;
	}

	private class ColumnsDialog extends Dialog {

		protected ColumnsDialog(Shell parentShell) {
			super(parentShell);
		}

		protected boolean isResizable() {
			return true;
		}

		protected Control createDialogArea(Composite parent) {
			Composite dialogArea = (Composite) super.createDialogArea(parent);
			CompositeMap input = queryColumns(getBaseTable(), getConnection());
			queryComments(input, getBaseTable(), getConnection());
			gridViewer = new GridViewer(columnNames, IGridViewer.isMulti
					| IGridViewer.isAllChecked | IGridViewer.isOnlyUpdate);
			try {
				gridViewer.setColumnTitles(columnTitles);
				gridViewer.createViewer(dialogArea);
				CellEditor[] celleditors = new CellEditor[columnNames.length];
				CellInfo cellProperties = new CellInfo(gridViewer, "REMARKS",
						false);
				ICellEditor cellEditor = new StringTextCellEditor(
						cellProperties);
				celleditors[1] = cellEditor.getCellEditor();
				cellEditor.init();
				gridViewer.addEditor("REMARKS", cellEditor);
				gridViewer.setCellEditors(celleditors);
				gridViewer.setData(input);
			} catch (ApplicationException e) {
				DialogUtil.logErrorException(e);
			}
			return dialogArea;
		}

		protected void initializeBounds() {
			super.initializeBounds();
			this.getShell().setSize(680, 350);
		}

		protected void okPressed() {
			selectedFields = getSelectedFields();
			super.okPressed();
		}

		@Override
		public boolean close() {
			// releaseResources();
			return super.close();
		}
	}

	public Connection getConnection() {
		IFile file = textPage.getFile();
		try {
			return DBConnectionUtil.getDBConnection(file.getProject(),
					getDataSourceName());
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final Map<String, Integer> types = new HashMap<String, Integer>();
	static {
		types.put("LONG", Types.BIGINT);
		types.put("TIMESTAMP(6)", Types.TIMESTAMP);
		types.put("NVARCHAR2", Types.VARCHAR);
		types.put("NUMBER", Types.NUMERIC);
		types.put("CLOB", Types.CLOB);
		types.put("CHAR", Types.CHAR);
		types.put("DATE", Types.DATE);
		types.put("TIMESTAMP(9)", Types.TIMESTAMP);
		types.put("VARCHAR2", Types.VARCHAR);
	}

	public static int getDataType(String type) {
		Integer integer = types.get(type);
		return integer != null ? integer : Types.VARCHAR;
	}
}
