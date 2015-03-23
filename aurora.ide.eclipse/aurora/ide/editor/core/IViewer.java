/**
 * 
 */
package aurora.ide.editor.core;

public interface IViewer {
	/**
	 * The acitons of this viewer can use the
	 * <code>true<code> value.Its parent use <code>false<code> to refresh this viewer.
	 * @param isDirtyStateShouldBeChanged
	 */
	public void refresh(boolean isDirtyStateShouldBeChanged);
}
