package aurora.ide.prototype.consultant.product.demonstrate;

import java.io.File;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import uncertain.composite.CompositeMap;

import aurora.ide.libs.AuroraImagesUtils;
import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.figures.ViewDiagramLayout;
import aurora.ide.meta.gef.editors.parts.ViewDiagramPart;
import aurora.ide.meta.gef.editors.wizard.dialog.DemonstratingDialog;
import aurora.ide.prototype.consultant.demonstrate.DemonstrateEditorMode;
import aurora.ide.prototype.consultant.view.property.page.ProjectDemonstratePropertyPage;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;

public class LoginDiagramPart extends ViewDiagramPart {
	protected IFigure createFigure() {

		// ImageFigure ifi = new ImageFigure();
		// ifi.add(super.createFigure());
		// ifi.setImage(image);

		Figure figure = new FreeformLayer() {

			@Override
			public void paint(Graphics graphics) {
				Image image = getImage();
				if (image != null) {
					ImageFigure imageFigure = new ImageFigure();
					imageFigure.setImage(image);
					imageFigure.setSize(this.getSize());
					imageFigure.setOpaque(true);
					imageFigure.paint(graphics);
				}
				super.paint(graphics);
			}

		};
		ViewDiagramLayout manager = new ViewDiagramLayout(false, this);
		figure.setLayoutManager(manager);
		return figure;
	}

	private Image getImage() {
		EditorMode editorMode = this.getEditorMode();
		if (editorMode instanceof DemonstrateEditorMode) {
			DemonstratingDialog demonstratingDialog = ((DemonstrateEditorMode) editorMode)
					.getDemonstratingDialog();
			File project = demonstratingDialog.getProject();
			CompositeMap loadDemonProperties = ResourceUtil
					.loadDemonProperties(project);
			if (loadDemonProperties == null)
				return null;

			CompositeMap child = loadDemonProperties
					.getChild(ProjectDemonstratePropertyPage.LOGIN_IMG);
			if (child != null && child.getText() != null
					&& "".equals(child.getText()) == false) {
				byte[] iconByteData = AuroraImagesUtils
						.toBytes(child.getText());
				if (iconByteData != null) {
					ImageData idd = AuroraImagesUtils.toImageData(iconByteData);
					ImageDescriptor id = ImageDescriptor
							.createFromImageData(idd);
					Image image = id.createImage();
					return image;
				}
			}

		}
		return null;
	}
}
