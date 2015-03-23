package aurora.ide.preferencepages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import aurora.ide.AuroraPlugin;
import aurora.ide.builder.AuroraBuilder;

public class BuildLevelPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	static IPreferenceStore store = AuroraPlugin.getDefault()
			.getPreferenceStore();
	static {
		store.setDefault(AuroraBuilder.CONFIG_PROBLEM, "2");
		store.setDefault(AuroraBuilder.UNDEFINED_BM, "2");
		store.setDefault(AuroraBuilder.UNDEFINED_ATTRIBUTE, "1");
		store.setDefault(AuroraBuilder.UNDEFINED_DATASET, "1");
		store.setDefault(AuroraBuilder.UNDEFINED_FOREIGNFIELD + "_SCREEN", "1");
		store.setDefault(AuroraBuilder.UNDEFINED_FOREIGNFIELD + "_BM", "2");
		store.setDefault(AuroraBuilder.UNDEFINED_LOCALFIELD + "_SCREEN", "1");
		store.setDefault(AuroraBuilder.UNDEFINED_LOCALFIELD + "_BM", "2");
		store.setDefault(AuroraBuilder.UNDEFINED_SCREEN, "2");
		store.setDefault(AuroraBuilder.UNDEFINED_TAG, "1");
		store.setDefault(AuroraBuilder.NONENAMESPACE, "1");
	}

	private static final String[][] levels = { { "Error", "2" },
			{ "Warning", "1" }, { "Ignore", "0" } };

	// IMarker.SEVERITY_WARNING = 1
	// IMarker.SEVERITY_ERROR = 2

	public BuildLevelPage() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(store);
	}

	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
		Composite par = getFieldEditorParent();
		// par.setLayout(new GridLayout());

		addField(new ComboFieldEditor_new(AuroraBuilder.CONFIG_PROBLEM,
				"Config Problem", levels, par));
		addField(new ComboFieldEditor_new(AuroraBuilder.UNDEFINED_BM,
				"Undefined BM", levels, par));
		addField(new ComboFieldEditor_new(AuroraBuilder.UNDEFINED_ATTRIBUTE,
				"Undefined Attribute (all)", levels, par));
		addField(new ComboFieldEditor_new(AuroraBuilder.UNDEFINED_DATASET,
				"Undefined DataSet (screen)", levels, par));
		addField(new ComboFieldEditor_new(AuroraBuilder.UNDEFINED_FOREIGNFIELD
				+ "_SCREEN", "Undefined ForeignField (screen)", levels, par));
		addField(new ComboFieldEditor_new(AuroraBuilder.UNDEFINED_FOREIGNFIELD
				+ "_BM", "Undefined ForeignField (bm)", levels, par));
		addField(new ComboFieldEditor_new(AuroraBuilder.UNDEFINED_LOCALFIELD
				+ "_SCREEN", "Undefined LocalField (screen)", levels, par));
		addField(new ComboFieldEditor_new(AuroraBuilder.UNDEFINED_LOCALFIELD
				+ "_BM", "Undefined LocalField (bm)", levels, par));
		addField(new ComboFieldEditor_new(AuroraBuilder.UNDEFINED_SCREEN,
				"Undefined Screen (screen)", levels, par));
		addField(new ComboFieldEditor_new(AuroraBuilder.UNDEFINED_TAG,
				"Undefined Tag", levels, par));
		addField(new ComboFieldEditor_new(AuroraBuilder.NONENAMESPACE,
				"None namespace", levels, par));
	}

	public static int getBuildLevel(String type) {
		try {
			return AuroraPlugin.getDefault().getPreferenceStore().getInt(type);
		} catch (Exception e) {
		}
		return 0;
	}
}
