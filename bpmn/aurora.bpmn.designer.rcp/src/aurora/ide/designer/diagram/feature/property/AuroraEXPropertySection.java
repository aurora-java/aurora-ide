package aurora.ide.designer.diagram.feature.property;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.TextAnnotation;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.ToolTipProvider;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultPropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.FeatureListObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ObjectEditor;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.diagram.Bpmn2FeatureMap;
import org.eclipse.bpmn2.modeler.ui.property.Messages;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

import aurora.ide.bpmn.model.ex.AuroraEXModel;

public class AuroraEXPropertySection extends DefaultPropertySection implements
		ITabbedPropertyConstants {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.bpmn2.modeler.ui.property.AbstractBpmn2PropertySection#
	 * createSectionRoot()
	 */
	@Override
	protected AbstractDetailComposite createSectionRoot() {
		return new AuroraEXComposite(this);
	}

	public AbstractDetailComposite createSectionRoot(Composite parent, int style) {
		return new AuroraEXComposite(parent, style);
	}

	public class AuroraEXComposite extends DefaultDetailComposite {

		public AuroraEXComposite(AbstractBpmn2PropertySection section) {
			super(section);

		}

		public AuroraEXComposite(Composite parent, int style) {
			super(parent, style);
		}

		public void createBindings(EObject object) {

			bindAppearance(object);
		}
		protected boolean isModelObjectEnabled(String className, String featureName) {
			if (featureName!=null && "name".equals(featureName)) //$NON-NLS-1$
					return true;
			return super.isModelObjectEnabled(className,featureName);
		}

		protected void bindAppearance(EObject be) {
			if (Bpmn2FeatureMap.ALL_SHAPES.contains(be.eClass()
					.getInstanceClass())) {
				// don't show appearance section for Participant Bands
				PictogramElement pes[] = getDiagramEditor()
						.getSelectedPictogramElements();
				if (pes.length == 1
						&& ChoreographyUtil
								.isChoreographyParticipantBand(pes[0])) {
					return;
				}

				EStructuralFeature feature = getFeature(be,
						AuroraEXModel.OBJ_NAME);
				// if (feature==null || !isModelObjectEnabled(be.eClass(),
				// feature) ||
				// !isModelObjectEnabled(ShapeStyle.STYLE_ECLASS,null))
				// return;

				final BaseElement element = (BaseElement) be;
				EObject style = AuroraEXModel.getStyleObject(element);
				if (style == null) {
					style = AuroraEXModel.createExtObject(element);
					if (style == null)
						return;
				}
				final EObject styleObject = style;

				Composite container = createSectionComposite(this, "Aurora EX");
				bindAttribute(container, style, "name");
				
				// if
				// (Bpmn2FeatureMap.CONNECTIONS.contains(be.eClass().getInstanceClass()))
				// {
				// bindAttribute(container, style,
				// ShapeStyle.STYLE_SHAPE_FOREGROUND);
				// bindAttribute(container, style,
				// ShapeStyle.STYLE_LABEL_FOREGROUND);
				// bindAttribute(container, style, ShapeStyle.STYLE_LABEL_FONT);
				// bindAttribute(container, style,
				// ShapeStyle.STYLE_LABEL_POSITION);
				// bindAttribute(container, style,
				// ShapeStyle.STYLE_ROUTING_STYLE);
				// }
				// else if (be instanceof TextAnnotation) {
				// bindAttribute(container, style,
				// ShapeStyle.STYLE_SHAPE_FOREGROUND);
				// bindAttribute(container, style,
				// ShapeStyle.STYLE_LABEL_FOREGROUND);
				// bindAttribute(container, style, ShapeStyle.STYLE_LABEL_FONT);
				// }
				// else if
				// (Bpmn2FeatureMap.EVENTS.contains(be.eClass().getInstanceClass())
				// ||
				// Bpmn2FeatureMap.GATEWAYS.contains(be.eClass().getInstanceClass())
				// ||
				// Bpmn2FeatureMap.DATA.contains(be.eClass().getInstanceClass()))
				// {
				// bindAttribute(container, style,
				// ShapeStyle.STYLE_SHAPE_BACKGROUND);
				// bindAttribute(container, style,
				// ShapeStyle.STYLE_SHAPE_FOREGROUND);
				// bindAttribute(container, style,
				// ShapeStyle.STYLE_LABEL_FOREGROUND);
				// bindAttribute(container, style, ShapeStyle.STYLE_LABEL_FONT);
				// bindAttribute(container, style,
				// ShapeStyle.STYLE_LABEL_POSITION);
				// }
				// else {
				// // Tasks
				// bindAttribute(container, style,
				// ShapeStyle.STYLE_SHAPE_BACKGROUND);
				// bindAttribute(container, style,
				// ShapeStyle.STYLE_SHAPE_FOREGROUND);
				// bindAttribute(container, style,
				// ShapeStyle.STYLE_LABEL_FOREGROUND);
				// bindAttribute(container, style, ShapeStyle.STYLE_LABEL_FONT);
				// }
				// Button reset = new Button(container, SWT.PUSH);
				// reset.setText("Restore Defaults");
				// reset.addSelectionListener(new SelectionAdapter() {
				// @Override
				// public void widgetSelected(SelectionEvent e) {
				// Bpmn2Preferences preferences =
				// Bpmn2Preferences.getInstance(element);
				// final ShapeStyle ss = preferences.getShapeStyle(element);
				// TransactionalEditingDomain domain =
				// getDiagramEditor().getEditingDomain();
				// domain.getCommandStack().execute(new RecordingCommand(domain)
				// {
				// @Override
				// protected void doExecute() {
				// ShapeStyle.setShapeStyle(element, styleObject, ss);
				// setBusinessObject(element);
				// }
				// });
				// }
				// });
			}
		}

	}

}
