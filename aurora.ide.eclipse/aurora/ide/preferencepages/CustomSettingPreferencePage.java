package aurora.ide.preferencepages;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import aurora.ide.AuroraPlugin;
import aurora.ide.helpers.LocaleMessage;

public class CustomSettingPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	protected final static String screenEditorInitPageId = "screenEditorInitPageId";
	protected final static String bmEditorInitPageId = "bmEditorInitPageId";
	protected final static String intinebuildId = "intimebuildId";
	static IPreferenceStore store = AuroraPlugin.getDefault()
			.getPreferenceStore();
	static {
		store.setDefault(intinebuildId, "true");
	}
	private static String[][] screenPage = {
			{ LocaleMessage.getString("screen.file"), "0" },
			{ LocaleMessage.getString("source.file"), "1" } };
	private static String[][] bmPage = {
			{ LocaleMessage.getString("business.model.file"), "0" },
			{ LocaleMessage.getString("source.file"), "1" } };

	public CustomSettingPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(store);
	}

	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		addField(new ComboFieldEditor_new(screenEditorInitPageId,
				"Screen编辑器初始选项卡", screenPage, getFieldEditorParent()));
		addField(new ComboFieldEditor_new(bmEditorInitPageId, "BM编辑器初始选项卡",
				bmPage, getFieldEditorParent()));
		addField(new BooleanFieldEditor(intinebuildId, "启用即时build",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
	}

	public static int getScreenEditorInitPageIndex() {
		try {
			return Integer.parseInt(AuroraPlugin.getDefault()
					.getPreferenceStore().getString(screenEditorInitPageId));
		} catch (Exception e) {
		}
		return 0;
	}

	public static int getBMEditorInitPageIndex() {
		try {
			return Integer.parseInt(AuroraPlugin.getDefault()
					.getPreferenceStore().getString(bmEditorInitPageId));
		} catch (Exception e) {
		}
		return 0;
	}

	public static boolean getIntimeBuildEnable() {
		return AuroraPlugin.getDefault().getPreferenceStore()
				.getBoolean(intinebuildId);
	}
}
