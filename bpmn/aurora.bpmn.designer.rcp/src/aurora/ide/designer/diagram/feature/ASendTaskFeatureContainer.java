package aurora.ide.designer.diagram.feature;

import org.eclipse.bpmn2.modeler.ui.features.activity.task.SendTaskFeatureContainer;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.custom.ICustomFeature;

import aurora.ide.bpmn.model.ex.feature.ShowWebSettingFeature;

public class ASendTaskFeatureContainer extends SendTaskFeatureContainer {
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		ICustomFeature[] superFeatures = super.getCustomFeatures(fp);
		ICustomFeature[] thisFeatures = new ICustomFeature[1 + superFeatures.length];
		for (int i = 0; i < superFeatures.length; ++i) {
			thisFeatures[i] = superFeatures[i];
		}
		thisFeatures[superFeatures.length] = new ShowWebSettingFeature(fp);
		return thisFeatures;
	}
}
