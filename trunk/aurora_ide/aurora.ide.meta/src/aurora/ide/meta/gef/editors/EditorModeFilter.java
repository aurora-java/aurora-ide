package aurora.ide.meta.gef.editors;

public class EditorModeFilter extends PaletteCategoryFilter {
	public boolean isShowCategory(Object o) {
		if (o instanceof EditorMode) {
			return false == EditorMode.Template.equals(((EditorMode) o)
					.getMode());
		}
		return true;
	}
}
