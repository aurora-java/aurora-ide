package aurora.ide.celleditor;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.widgets.TableItem;

import aurora.ide.editor.core.ITableViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.LoadSchemaManager;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.helpers.SystemException;


import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.schema.Editor;
import uncertain.schema.Enumeration;
import uncertain.schema.IType;
import uncertain.schema.Restriction;
import uncertain.schema.SimpleType;

public class CellEditorFactory {
	private static CellEditorFactory editorFactory;
	public final static QualifiedName boolean_qn = new QualifiedName(
			"http://www.uncertain-framework.org/schema/simple-schema", "boolean");
	public final static QualifiedName string_qn = new QualifiedName(
			"http://www.uncertain-framework.org/schema/simple-schema", "string");
	public final static QualifiedName int_qn = new QualifiedName(
			"http://www.uncertain-framework.org/schema/simple-schema", "int");
	public final static QualifiedName integer_qn = new QualifiedName(
			"http://www.uncertain-framework.org/schema/simple-schema", "integer");
	public final static QualifiedName long_qn = new QualifiedName(
			"http://www.uncertain-framework.org/schema/simple-schema", "long");
	public final static QualifiedName float_qn = new QualifiedName(
			"http://www.uncertain-framework.org/schema/simple-schema", "float");
	public final static QualifiedName double_qn = new QualifiedName(
			"http://www.uncertain-framework.org/schema/simple-schema", "double");
	public final static String required = "required";

	public final static QualifiedName localFieldReference = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "LocalFieldReference");
	public final static QualifiedName foreignFieldReference = new QualifiedName(
			"http://www.aurora-framework.org/schema/bm", "ForeignFieldReference");
	public final static QualifiedName modelReference = new QualifiedName("http://www.aurora-framework.org/schema/bm",
			"ModelReference");

	private CellEditorFactory() {
	}

	public static CellEditorFactory getInstance() {
		if (editorFactory == null)
			editorFactory = new CellEditorFactory();
		return editorFactory;
	}

	public ICellEditor createCellEditor(ITableViewer viewer, Attribute attribute) throws ApplicationException {
		return createCellEditor(viewer, attribute, null, null);
	}

	public ICellEditor createCellEditor(ITableViewer viewer, Attribute attribute, CompositeMap record,
			TableItem tableItem) throws ApplicationException {
		ICellEditor cellEditor = null;
		if (attribute == null)
			return cellEditor;

		CellInfo cellProperties = createCellProperties(viewer, attribute, record, tableItem);
		// 个性化编辑器
		cellEditor = createDefinedEditor(attribute, cellProperties);
		// 如果没有定义个性化编辑，采取内建编辑器
		if (cellEditor == null)
			cellEditor = createDefaultEditor(attribute, cellProperties);

		if (cellEditor != null) {
			cellEditor.init();
		}
		return cellEditor;
	}

	private CellInfo createCellProperties(ITableViewer viewer, Attribute attribute, CompositeMap record,
			TableItem tableItem) {
		return new CellInfo(viewer, attribute, record, tableItem);
	}

	private ICellEditor createDefaultEditor(Attribute attribute, CellInfo cellProperties) {
		ICellEditor cellEditor = null;
		QualifiedName typeQname = attribute.getTypeQName();

		// 如果定义了SimpleType的Enumerations，显示为Combox
		IType attributeType = LoadSchemaManager.getSchemaManager().getType(typeQname);
		if (attributeType != null && attributeType instanceof SimpleType) {
			SimpleType simpleType = (SimpleType) attributeType;
			Restriction rest = simpleType.getRestriction();
			if (rest != null) {
				Enumeration[] emus = rest.getEnumerations();
				if (emus != null) {
					cellEditor = new ComboxCellEditor(cellProperties);
					return cellEditor;
				}
			}
		}
		// 没有定义类型的，但自定为必输，采用必输字符串编辑器
		if (typeQname == null) {
			if (required.equals(attribute.getUse())) {
				cellEditor = new StringTextCellEditor(cellProperties);
				return cellEditor;
			}
			return cellEditor;
		}
		// bool型采取checkbox
		if (typeQname.equals(boolean_qn)) {
			cellEditor = new BoolCellEditor(cellProperties);
			return cellEditor;
		}

		if (typeQname.equals(string_qn)) {
			cellEditor = new StringTextCellEditor(cellProperties);
			return cellEditor;
			// 数字型采取number型，数字靠右
		} else if (typeQname.equals(int_qn) || typeQname.equals(integer_qn) || typeQname.equals(double_qn)
				|| typeQname.equals(long_qn) || typeQname.equals(float_qn) || typeQname.equals(double_qn)) {
			cellEditor = new NumberTextCellEditor(cellProperties);
			return cellEditor;
		}
		return cellEditor;

	}

	private ICellEditor createDefinedEditor(Attribute attribute, CellInfo cellProperties) throws ApplicationException {
		Object editor;
		QualifiedName typeQname = attribute.getTypeQName();
		if (typeQname == null)
			return null;
		IType type = LoadSchemaManager.getSchemaManager().getType(typeQname);
		if (type == null || !(type instanceof SimpleType)) {
			return null;
		}
		SimpleType simpleType = (SimpleType) type;
		QualifiedName editorQName = simpleType.getEditorQName();
		Editor ed = LoadSchemaManager.getSchemaManager().getEditor(editorQName);
		if (ed == null)
			return null;
		String cls_name = ed.getInstanceClass();
		Class cls;
		try {
			cls = Class.forName(cls_name);
		} catch (ClassNotFoundException e) {
			throw new ApplicationException(LocaleMessage.getString("editor.class") + cls_name
					+ LocaleMessage.getString("not.valid"), e);
		}
		Constructor constructor = null;
		try {
			Class[] constructorClasses = new Class[]{CellInfo.class};
			constructor = cls.getConstructor(constructorClasses);

			Object[] constructorObjects = new Object[]{cellProperties};
			editor = constructor.newInstance(constructorObjects);
			ICellEditor cellEditor = (ICellEditor) editor;
			return cellEditor;
		} catch (SecurityException e) {
			throw new SystemException(e);
		} catch (NoSuchMethodException e) {
			throw new SystemException(e);
		} catch (IllegalArgumentException e) {
			throw new SystemException(e);
		} catch (InstantiationException e) {
			throw new SystemException(e);
		} catch (IllegalAccessException e) {
			throw new SystemException(e);
		} catch (InvocationTargetException e) {
			throw new SystemException(e);
		}
	}
}
