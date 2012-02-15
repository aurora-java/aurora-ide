package aurora.ide.meta.gef.editors.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public abstract class TypeChangeAction extends Action {

	public TypeChangeAction() {
		super();
	}

	public TypeChangeAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	public TypeChangeAction(String text, int style) {
		super(text, style);
	}

	public TypeChangeAction(String text) {
		super(text);
	}

	public abstract void apply();

	public abstract void unApply();
}
