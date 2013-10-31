package aurora.ide.meta.gef.editors.components.part;

import java.io.FileNotFoundException;

import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import aurora.ide.libs.AuroraImagesUtils;
import aurora.ide.meta.gef.editors.components.command.ChangeImageCommand;
import aurora.ide.meta.gef.editors.layout.InputFieldLayout;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.plugin.source.gen.screen.model.CustomICon;

public class CustomIconPart extends ComponentPart {

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		ImageFigure inputField = new ImageFigure();
		// ImageDescriptor imageDescriptor =
		// aurora.ide.prototype.consultant.product.Activator
		// .getImageDescriptor("/icons/zzzzz.png");
		// ImageData imageData = imageDescriptor.getImageData();
		// AuroraImagesUtils.toBytes(imageData,SWT.IMAGE_PNG);
		// String string =
		// AuroraImagesUtils.toString(AuroraImagesUtils.toBytes(imageData,SWT.IMAGE_PNG));
		// System.out.println(string);
		return inputField;
	}

	public void updateImage() {
		IFigure figure = this.getFigure();
		if (figure instanceof ImageFigure) {
			byte[] iconByteData = AuroraImagesUtils.toBytes(this.getModel()
					.getIconByteData());
			if (iconByteData != null) {
				ImageData idd = AuroraImagesUtils.toImageData(iconByteData);
				ImageDescriptor id = ImageDescriptor.createFromImageData(idd);
				Image image = id.createImage();
				((ImageFigure) figure).setImage(image);
			}
		}

	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		getFigure().setToolTip(new Label(getModel().getComponentType()));
		IFigure figure = this.getFigure();
		if (figure instanceof ImageFigure) {
			updateImage();
		}
		super.refreshVisuals();
	}

	public CustomICon getModel() {
		return (CustomICon) super.getModel();
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
	}

	@Override
	public int getResizeDirection() {
		return NSEW;
	}

	public Rectangle layout() {
		InputFieldLayout layout = new InputFieldLayout();
		return layout.layout(this);
	}

	public Command getCommand(Request request) {
		return super.getCommand(request);
	}

	@Override
	public EditPolicy getEditPolicy(Object key) {
		return super.getEditPolicy(key);
	}

	@Override
	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType())) {
			String path = AuroraImagesUtils.queryFile(this.getViewer()
					.getControl().getShell());
			if (path != null) {
				try {
					Path p = new Path(path);
					String fileExtension = p.getFileExtension();
					int iconType = AuroraImagesUtils.getIconType(fileExtension);
					if (iconType == -1)
						return;
					ImageData loadImageData = AuroraImagesUtils
							.loadImageData(p);
					ChangeImageCommand cic = new ChangeImageCommand(
							this.getModel(), loadImageData, iconType);
					this.getViewer().getEditDomain().getCommandStack()
							.execute(cic);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		super.performRequest(req);
	}

}
