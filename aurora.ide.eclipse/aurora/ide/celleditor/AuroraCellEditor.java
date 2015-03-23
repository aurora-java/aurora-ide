/**
 * 
 */
package aurora.ide.celleditor;

import java.util.HashMap;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;

/**
 * @author linjinxiao
 *
 */
public class AuroraCellEditor {
	
	
	private static AuroraCellEditor auroraCellEditor ;
	private HashMap dataType2EditorName = new HashMap();
	private final String defaultEditor = "textField";
	public static final String aurorUri = "http://www.aurora-framework.org/application";
	private AuroraCellEditor(){
		dataType2EditorName.put("DATE", "datePicker");
		dataType2EditorName.put("boolean", "checkBox");
		dataType2EditorName.put("BIGINT", "numberField");
		dataType2EditorName.put("options", "comboBox");
		dataType2EditorName.put("lovService", "lov");
	}
	
	public static synchronized AuroraCellEditor getInstance(){
		if(auroraCellEditor ==null)
			auroraCellEditor= new AuroraCellEditor();
		return auroraCellEditor;
	}
	public String getEditorName(String dataType){
		Object typeObject = dataType2EditorName.get(dataType);
		String editorName = null;
		if(typeObject != null)
			editorName = (String)typeObject;
		else{
			editorName = defaultEditor;
		}
		return editorName;
	}
	public void addEditorName(String dataType,String editorName){
		dataType2EditorName.put(dataType, editorName);
	}
	public QualifiedName getEdiotrQN(String dataType){
		return new QualifiedName(aurorUri, getEditorName(dataType));
	}
	public QualifiedName getEditorQN(CompositeMap record){
		return getEdiotrQN(getDatabaseType(record));
	}
	public String getEditorName(CompositeMap record){
		return getEditorName(getDatabaseType(record));
	}
	
	public String getDatabaseType(CompositeMap record){
		if(record  == null)
			return null;
		String databaseType = record.getString("databaseType");
		if(databaseType != null){
			return databaseType;
		}
		String options = record.getString("options");
		if(options != null){
			return "options";
		}
		String lovService = record.getString("lovService");
		if(lovService != null){
			return "lovService";
		}
		return null;
	}

}
