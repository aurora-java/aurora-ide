package aurora.ide.preferencepages;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import aurora.ide.AuroraPlugin;

public class RefactorSettingPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	public final static String REFACTOR_SETTING_FILE_RENAME = "refactor_setting_file_rename";
	public final static String REFACTOR_SETTING_FOLDER_RENAME = "refactor_setting_folder_rename";
	public final static String REFACTOR_SETTING_FILE_MOVE = "refactor_setting_file_move";
	public final static String REFACTOR_SETTING_FOLDER_MOVE = "refactor_setting_folder_move";
	private final static String REFACTOR_SETTING_FILE_DEL = "refactor_setting_file_del";
	private final static String REFACTOR_SETTING_FOLDER_DEL = "refactor_setting_folder_del";

	public RefactorSettingPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
	}

	public void init(IWorkbench workbench) {
		IPreferenceStore store = AuroraPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		store.setDefault(REFACTOR_SETTING_FILE_MOVE, true);
		store.setDefault(REFACTOR_SETTING_FOLDER_MOVE, true);
		store.setDefault(REFACTOR_SETTING_FILE_RENAME, true);
		store.setDefault(REFACTOR_SETTING_FOLDER_RENAME, true);
		store.setDefault(REFACTOR_SETTING_FILE_DEL, false);
		store.setDefault(REFACTOR_SETTING_FOLDER_DEL, false);
	}

	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(REFACTOR_SETTING_FILE_RENAME,
				"启用修改文件名", BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new BooleanFieldEditor(REFACTOR_SETTING_FOLDER_RENAME,
				"启用修改目录名", BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new BooleanFieldEditor(REFACTOR_SETTING_FILE_MOVE, "启用移动文件",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new BooleanFieldEditor(REFACTOR_SETTING_FOLDER_MOVE, "启用移动目录",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
//		addField(new BooleanFieldEditor(REFACTOR_SETTING_FILE_DEL, "启用删除文件",
//				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
//		addField(new BooleanFieldEditor(REFACTOR_SETTING_FOLDER_DEL, "启用删除目录",
//				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
	}

	public static boolean getRefactorStatus(String key) {
		return AuroraPlugin.getDefault().getPreferenceStore().getBoolean(key);
	}
}
