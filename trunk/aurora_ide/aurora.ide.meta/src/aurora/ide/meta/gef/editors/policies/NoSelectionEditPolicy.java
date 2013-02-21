package aurora.ide.meta.gef.editors.policies;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy;

public class NoSelectionEditPolicy extends SelectionHandlesEditPolicy {
	public static final String TRANS_SELECTION_KEY = "translate_selection_to_parent";

	@Override
	protected List<?> createSelectionHandles() {
		return Collections.EMPTY_LIST;
	}

	public EditPart getTargetEditPart(Request request) {
		if (RequestConstants.REQ_SELECTION.equals(request.getType()))
			return getHost().getParent();
		return null;
	}

}
