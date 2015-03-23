package aurora.ide.designer.diagram.feature;

import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.modeler.core.features.event.AbstractCreateEventFeature;
import org.eclipse.bpmn2.modeler.core.features.event.definitions.AbstractCreateEventDefinitionFeature;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.bpmn2.modeler.ui.diagram.BPMN2FeatureProvider;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl.SimpleFeatureMapEntry;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import aurora.ide.bpmn.model.ex.AuroraEXModel;

public class CreateIntermediateCatchTimerEventFeature extends
		AbstractCreateEventFeature<IntermediateCatchEvent> {

	private IFeatureProvider featureProvider;

	public CreateIntermediateCatchTimerEventFeature(IFeatureProvider fp) {
		super(fp);
		this.featureProvider = fp;
	}

	public String getStencilImageId() {
		return ImageProvider.IMG_16_TIMER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.bpmn2.modeler.core.features.AbstractCreateFlowElementFeature
	 * #getFlowElementClass()
	 */
	@Override
	public EClass getBusinessObjectClass() {
		return Bpmn2Package.eINSTANCE.getIntermediateCatchEvent();
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return super.canCreate(context);
	}

	@Override
	public Object[] create(ICreateContext context) {
		Object[] create = super.create(context);
		Object object = create[0];
		createEventDefinition(context, (Event) object);
		ee((BaseElement) object);
		// Event e = (Event)
		// getBusinessObjectForPictogramElement(context.getTargetContainer());
		// List<EventDefinition> eventDefinitions =
		// ModelUtil.getEventDefinitions(e);
		// EventDefinition definition = createBusinessObject(context);
		// eventDefinitions.add(definition);
		// PictogramElement pe = addGraphicalRepresentation(context,
		// definition);
		// ModelUtil.setID(definition);
		// return new Object[] { definition, pe };
		//
		List<ExtensionAttributeValue> extensionValues = ((BaseElement) object).getExtensionValues();
		for (ExtensionAttributeValue extensionAttributeValue : extensionValues) {
			FeatureMap value2 = extensionAttributeValue.getValue();
			Object value = value2.getValue(0);
			System.out.println(value);
			System.out.println(value2.getValue(1));
		}
		
		return create;
	}

	public Object[] createEventDefinition(ICreateContext context, Event event) {
		if (featureProvider instanceof BPMN2FeatureProvider) {
			AbstractCreateEventDefinitionFeature createFeatureForBusinessObject = (AbstractCreateEventDefinitionFeature) ((BPMN2FeatureProvider) featureProvider)
					.getCreateFeatureForBusinessObject(TimerEventDefinition.class);
			EventDefinition definition = (EventDefinition) createFeatureForBusinessObject
					.createBusinessObject(context);

			// Event e = (Event)
			// getBusinessObjectForPictogramElement(context.getTargetContainer());
			List<EventDefinition> eventDefinitions = ModelUtil
					.getEventDefinitions(event);
			// EventDefinition definition = createBusinessObject(context);
			eventDefinitions.add(definition);
			PictogramElement pe = addGraphicalRepresentation(context,
					definition);
			ModelUtil.setID(definition);


			

			return new Object[] { definition, pe };
		}
		return new Object[] { null };
	}

	public void ee(BaseElement definition) {
//		auroraEX.Test test = AuroraEXFactory.eINSTANCE.createTest();
//		test.setName("good man");
//		addExtensionElement(definition, AuroraEXPackage.Literals.ROOT__TEST,
//				test);
		AuroraEXModel.createExtObject(definition);
	}

	public static boolean addExtensionElement(BaseElement baseElement,
			EReference eReference, Object o) {
		final FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
				(org.eclipse.emf.ecore.EStructuralFeature.Internal) eReference,
				o);
//		if (baseElement.getExtensionValues().size() > 0) {
//			baseElement.getExtensionValues()..get(0).getValue()
//					.add(extensionElementEntry);
//		} else {
			ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE
					.createExtensionAttributeValue();

			extensionElement.getValue().add(extensionElementEntry);
			baseElement.getExtensionValues().add(extensionElement);
//		}
		return false;
	}

	// public String getCreateDescription() {
	// this.getCreateName();
	// return "Catche Timer Event";
	// }
	public String getCreateName() {
		// TODO: get name from Messages by generating a field name using the
		// business object class
		// return ModelUtil.toCanonicalString(getFeatureClass().getName());
		return "Catche Timer Event";
	}

}
