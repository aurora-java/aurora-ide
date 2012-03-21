package aurora.ide.meta.gef.designer;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import aurora.ide.meta.gef.designer.model.BMModel;

public class BMModelContentProvider implements IStructuredContentProvider {

	private int type = BMModel.RECORD;

	public BMModelContentProvider(int type) {
		super();
		this.type = type;
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	public Object[] getElements(Object inputElement) {
		BMModel model = (BMModel) inputElement;
		return type == BMModel.RECORD ? model.getRecords() : model
				.getRelations();
	}
}
