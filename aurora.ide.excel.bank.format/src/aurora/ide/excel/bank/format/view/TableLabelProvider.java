package aurora.ide.excel.bank.format.view;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TableLabelProvider extends BaseLabelProvider implements
		ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int i) {

		if (element instanceof String) {
			if (i == 0) {
				Path p = new Path(element.toString());
				return  p.removeFileExtension().lastSegment();
			}
			if (i == 1) {
				return element.toString();
			}
		}
		return ""; //$NON-NLS-1$
	}

}
