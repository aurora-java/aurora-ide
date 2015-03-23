package aurora.ide.designer.diagram;

import org.eclipse.graphiti.tb.IToolBehaviorProvider;

import aurora.ide.designer.diagram.feature.AuroraBPMN2FeatureProvider;

public class AuroraBpmn2DiagramTypeProvider extends
		org.eclipse.bpmn2.modeler.ui.diagram.Bpmn2DiagramTypeProvider {
	public AuroraBpmn2DiagramTypeProvider() {
		super();
		this.setFeatureProvider(new AuroraBPMN2FeatureProvider(this));
	}

	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		return new IToolBehaviorProvider[] { new AuroraBpmn2ToolBehaviorProvider(
				this) };
	}

}
