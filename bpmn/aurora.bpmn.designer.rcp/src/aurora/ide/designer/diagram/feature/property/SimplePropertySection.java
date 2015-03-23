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

public class SimplePropertySection extends DefaultPropertySection implements ITabbedPropertyConstants {

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.ui.property.AbstractBpmn2PropertySection#createSectionRoot()
	 */
	@Override
	protected AbstractDetailComposite createSectionRoot() {
		return new DescriptionDetailComposite(this);		
	}

	@Override
	public AbstractDetailComposite createSectionRoot(Composite parent, int style) {
		 return new DescriptionDetailComposite(parent, style);
	}

	public class DescriptionDetailComposite extends DefaultDetailComposite {

		/**
		 * @param section
		 */
		public DescriptionDetailComposite(AbstractBpmn2PropertySection section) {
			super(section);
		}
		
		public DescriptionDetailComposite(Composite parent, int style) {
			super(parent,style);
		}

		@Override
		protected void cleanBindings() {
			super.cleanBindings();
			descriptionText = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.bpmn2.modeler.ui.property.AbstractBpmn2DetailComposite
		 * #createBindings(org.eclipse.emf.ecore.EObject)
		 */
		@Override
		public void createBindings(EObject object) {

//			bindDescription(object);
//			bindAttribute(object,"id"); //$NON-NLS-1$
			bindAttribute(object,"name"); //$NON-NLS-1$
			bindList(object, "documentation"); //$NON-NLS-1$
//			bindAppearance(object);

			if (!(object instanceof Group)) {
				EStructuralFeature reference = object.eClass().getEStructuralFeature("categoryValueRef"); //$NON-NLS-1$
				if (reference!=null) {
					if (isModelObjectEnabled(object.eClass(), reference)) {
						String displayName = getBusinessObjectDelegate().getLabel(object, reference);
		
						ObjectEditor editor = new FeatureListObjectEditor(this,object,reference) {
							@Override
							protected boolean canEdit() {
								return !Bpmn2Preferences.getInstance(object).getPropagateGroupCategories();
							}
						};
						editor.createControl(getAttributesParent(),displayName);
					}
				}
			}
		}

		protected boolean isModelObjectEnabled(String className, String featureName) {
			if (featureName!=null && "name".equals(featureName)) //$NON-NLS-1$
					return true;
			return super.isModelObjectEnabled(className,featureName);
		}
		
		protected void bindDescription(EObject be) {
			// don't display the description text if disabled in preferences,
			// or if this is a popup configuration dialog.
			if (Bpmn2Preferences.getInstance(be).getShowDescriptions()) {
				String description = getDescription(be);
	
				if (description != null && !description.isEmpty()) {
					descriptionText = createDescription(this, description);
				}
			}
		}
		
		public String getDescription(EObject object) {
			String description = null;

			if (object instanceof BPMNDiagram
					&& ((BPMNDiagram)object).getPlane()!=null
					&& ((BPMNDiagram)object).getPlane().getBpmnElement()!=null) {
				object = ((BPMNDiagram)object).getPlane().getBpmnElement();
			}
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object);
			if (adapter!=null) {
				description = (String) adapter.getProperty(ExtendedPropertiesAdapter.LONG_DESCRIPTION);
			}
			if (description==null) {
				description = ToolTipProvider.INSTANCE.getLongDescription(getDiagramEditor(), object);
			}
			if (!isModelObjectEnabled(object.eClass())) {
				if (description==null)
					description = "";
				description = "*** The " + ModelUtil.toCanonicalString(object.eClass().getName()) +
						" element is not enabled in this Tool Profile. ***\n" + description;
			}
			return description;
		}
		
		protected void bindAppearance(EObject be) {
			if (Bpmn2FeatureMap.ALL_SHAPES.contains(be.eClass().getInstanceClass())) {
				// don't show appearance section for Participant Bands
				PictogramElement pes[] = getDiagramEditor().getSelectedPictogramElements();
				if (pes.length==1 && ChoreographyUtil.isChoreographyParticipantBand(pes[0])) {
					return;
				}

				EStructuralFeature feature = getFeature(be,ShapeStyle.STYLE_OBJECT);
				if (feature==null || !isModelObjectEnabled(be.eClass(), feature) || !isModelObjectEnabled(ShapeStyle.STYLE_ECLASS,null))
					return;
					
				final BaseElement element = (BaseElement) be;
				EObject style = ShapeStyle.getStyleObject(element);
				if (style==null) {
					style = ShapeStyle.createStyleObject(element);
					if (style==null)
						return;
				}
				final EObject styleObject = style;
				
				Composite container = createSectionComposite(this, Messages.DescriptionPropertySection_Appearance_Label);
				
				if (Bpmn2FeatureMap.CONNECTIONS.contains(be.eClass().getInstanceClass())) {
					bindAttribute(container, style, ShapeStyle.STYLE_SHAPE_FOREGROUND);
					bindAttribute(container, style, ShapeStyle.STYLE_LABEL_FOREGROUND);
					bindAttribute(container, style, ShapeStyle.STYLE_LABEL_FONT);
					bindAttribute(container, style, ShapeStyle.STYLE_LABEL_POSITION);
					bindAttribute(container, style, ShapeStyle.STYLE_ROUTING_STYLE);
				}
				else if (be instanceof TextAnnotation) {
					bindAttribute(container, style, ShapeStyle.STYLE_SHAPE_FOREGROUND);
					bindAttribute(container, style, ShapeStyle.STYLE_LABEL_FOREGROUND);
					bindAttribute(container, style, ShapeStyle.STYLE_LABEL_FONT);
				}
				else if (Bpmn2FeatureMap.EVENTS.contains(be.eClass().getInstanceClass()) ||
						Bpmn2FeatureMap.GATEWAYS.contains(be.eClass().getInstanceClass()) ||
						Bpmn2FeatureMap.DATA.contains(be.eClass().getInstanceClass())) {
					bindAttribute(container, style, ShapeStyle.STYLE_SHAPE_BACKGROUND);
					bindAttribute(container, style, ShapeStyle.STYLE_SHAPE_FOREGROUND);
					bindAttribute(container, style, ShapeStyle.STYLE_LABEL_FOREGROUND);
					bindAttribute(container, style, ShapeStyle.STYLE_LABEL_FONT);
					bindAttribute(container, style, ShapeStyle.STYLE_LABEL_POSITION);
				}
				else {
					// Tasks
					bindAttribute(container, style, ShapeStyle.STYLE_SHAPE_BACKGROUND);
					bindAttribute(container, style, ShapeStyle.STYLE_SHAPE_FOREGROUND);
					bindAttribute(container, style, ShapeStyle.STYLE_LABEL_FOREGROUND);
					bindAttribute(container, style, ShapeStyle.STYLE_LABEL_FONT);
				}
				Button reset = new Button(container, SWT.PUSH);
				reset.setText("Restore Defaults");
				reset.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Bpmn2Preferences preferences = Bpmn2Preferences.getInstance(element);
						final ShapeStyle ss = preferences.getShapeStyle(element);
						TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
								ShapeStyle.setShapeStyle(element, styleObject, ss);
								setBusinessObject(element);
							}
						});
					}
				});
			}
		}
	}
}
