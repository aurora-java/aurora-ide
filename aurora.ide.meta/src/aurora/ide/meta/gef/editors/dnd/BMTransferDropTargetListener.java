package aurora.ide.meta.gef.editors.dnd;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.AutoexposeHelper;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.swt.dnd.DND;

import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.request.DropBMRequest;

public class BMTransferDropTargetListener extends
		AbstractTransferDropTargetListener {

	public BMTransferDropTargetListener(EditPartViewer viewer) {
		super(viewer);
		this.setTransfer(BMTransfer.getInstance());
	}

	/**
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#createTargetRequest()
	 */
	protected Request createTargetRequest() {
		DropBMRequest request = new DropBMRequest();
		// Create factory
		IFile bm = BMTransfer.getInstance().getBM();
		request.setBm(bm);

		return request;
	}

	/**
	 * A helper method that casts the target Request to a CreateRequest.
	 * 
	 * @return CreateRequest
	 */
	protected final DropBMRequest getDropRequest() {
		return ((DropBMRequest) getTargetRequest());
	}

	/**
	 * Returns the appropriate Factory object to be used for the specified
	 * template. This Factory is used on the CreateRequest that is sent to the
	 * target EditPart.
	 * 
	 * @param template
	 *            the template Object
	 * @return a Factory
	 */
	protected CreationFactory getFactory(Object template) {
		if (template instanceof CreationFactory) {
			return ((CreationFactory) template);
		} else if (template instanceof Class) {
			return new SimpleFactory((Class) template);
		} else
			return null;
	}

	/**
	 * The purpose of a template is to be copied. Therefore, the drop operation
	 * can't be anything but <code>DND.DROP_COPY</code>.
	 * 
	 * @see AbstractTransferDropTargetListener#handleDragOperationChanged()
	 */
	protected void handleDragOperationChanged() {
		getCurrentEvent().detail = DND.DROP_COPY;
		super.handleDragOperationChanged();
	}

	/**
	 * The purpose of a template is to be copied. Therefore, the Drop operation
	 * is set to <code>DND.DROP_COPY</code> by default.
	 * 
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#handleDragOver()
	 */
	protected void handleDragOver() {

		getCurrentEvent().detail = DND.DROP_COPY;
		getCurrentEvent().feedback = DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
		// getCurrentEvent().feedback = DND.FEEDBACK_NONE;
		super.handleDragOver();
	}

	@Override
	protected void setAutoexposeHelper(AutoexposeHelper helper) {
		super.setAutoexposeHelper(helper);
	}

	/**
	 * Overridden to select the created object.
	 * 
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#handleDrop()
	 */
	protected void handleDrop() {
		super.handleDrop();
		getCurrentEvent().detail = DND.DROP_COPY;
		selectAddedObject();
	}

	private void selectAddedObject() {
		// Object model = getCreateRequest().getNewObject();
		// if (model == null)
		// return;
		// EditPartViewer viewer = getViewer();
		// viewer.getControl().forceFocus();
		// Object editpart = viewer.getEditPartRegistry().get(model);
		// if (editpart instanceof EditPart) {
		// // Force a layout first.
		// getViewer().flush();
		// viewer.select((EditPart) editpart);
		// }
		EditPart targetEditPart = this.getTargetEditPart();
		this.getViewer().select(targetEditPart);
	}

	/**
	 * Assumes that the target request is a {@link CreateRequest}.
	 */
	protected void updateTargetRequest() {
		DropBMRequest request = this.getDropRequest();
		request.setLocation(getDropLocation());
	}

}
