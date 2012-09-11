package aurora.ide.views.prompts.view;

import org.eclipse.jface.viewers.ICellModifier;

public class PCellModifier implements ICellModifier {

	@Override
	public boolean canModify(Object element, String property) {
		System.out.println("0000000000000000000");
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		System.out.println("0000000000000000000");
		return "0000000000000000000000000";
	}

	@Override
	public void modify(Object element, String property, Object value) {
		System.out.println("..........................");
	}

}
