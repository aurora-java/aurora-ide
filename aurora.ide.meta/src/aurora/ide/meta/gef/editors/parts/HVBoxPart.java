package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;

import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.editors.figures.HVBoxFigure;
import aurora.ide.meta.gef.editors.figures.VirtualBoxBorder;
import aurora.ide.meta.gef.editors.policies.ContainerLayoutEditPolicy;
import aurora.plugin.source.gen.screen.model.BOX;

public class HVBoxPart extends BoxPart {
	protected IFigure createFigure() {
		HVBoxFigure hvBoxFigure = new HVBoxFigure();
		BOX model = (BOX) getModel();
		hvBoxFigure.setBox(model);
		return hvBoxFigure;
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new ContainerLayoutEditPolicy() {
					@Override
					protected void eraseLayoutTargetFeedback(Request request) {
						if (MetaPlugin.isDemonstrate == false)
							this.getHostFigure().setBorder(null);
						super.eraseLayoutTargetFeedback(request);
					}

					@Override
					protected void showLayoutTargetFeedback(Request request) {
						if (MetaPlugin.isDemonstrate == false)
							this.getHostFigure().setBorder(
									new VirtualBoxBorder(getComponent()
											.getComponentType()));
						super.showLayoutTargetFeedback(request);
					}
				});
	}
}
