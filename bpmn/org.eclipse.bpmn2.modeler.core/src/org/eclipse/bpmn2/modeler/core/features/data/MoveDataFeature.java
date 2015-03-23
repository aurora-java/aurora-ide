/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features.data;

import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.features.DefaultMoveBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * MoveFeature class for all Data items including DataObject, DataStore,
 * DataObjectReference, DataStoreReference, DataInput and DataOutput.
 * <p>
 * The first four of these (referred to collectively as "DataObjects" here) are
 * both ItemAwareElements and FlowElements, so their containment rules are
 * different than DataInput and DataOutput items: DataObjects are contained in
 * FlowElementContainers (in the "flowElements" containment list) whereas
 * DataInput and DataOutput objects are contained in an InputOutputSpecification
 * container and must also be included in at least one InputSet or OutputSet,
 * depending on the item type.
 */
public class MoveDataFeature extends DefaultMoveBPMNShapeFeature {

	public MoveDataFeature(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canMoveShape(IMoveShapeContext context) {
		if (super.canMoveShape(context)) {
			Shape shape = context.getShape();
			EObject dataObject = BusinessObjectUtil.getBusinessObjectForPictogramElement(shape);
			if (dataObject instanceof ItemAwareElement) {
				if (dataObject instanceof FlowElement) {
					if (FeatureSupport.isValidDataTarget(context))
						return true;
				}
				else {
					// It must be a DataInput or DataOutput object, which is NOT
					// a FlowElement. These can only be moved into a container
					// that has an InputOutputSpecification.
					Shape targetContainer = context.getTargetContainer();
					if (targetContainer==context.getSourceContainer())
						// target is the same as source container
						return true;
					// target is different from source container
					EObject targetObject = FeatureSupport.getTargetObject(context);
					if (targetObject instanceof Process) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	protected void postMoveShape(IMoveShapeContext context) {
		Shape shape = context.getShape();
		ItemAwareElement dataObject = (ItemAwareElement) BusinessObjectUtil.getBusinessObjectForPictogramElement(shape);
		ContainerShape sourceContainer = context.getSourceContainer();
		ContainerShape targetContainer = context.getTargetContainer();
		if (sourceContainer!=targetContainer) {
			EObject targetObject = FeatureSupport.getTargetObject(context);
			if (dataObject instanceof FlowElement) {
				// The Item Aware data object is also a FlowElement, so it
				// belongs in a FlowElementsContainer.
				((FlowElementsContainer)targetObject).getFlowElements().add((FlowElement)dataObject);
			}
			else {
				// The data object must be either a DataInput or DataOutput,
				// so it belongs in an ioSpecification.
				Resource resource = targetObject.eResource();
				InputOutputSpecification ioSpec = null;
				EStructuralFeature f = targetObject.eClass().getEStructuralFeature("ioSpecification"); //$NON-NLS-1$
				ioSpec = (InputOutputSpecification) targetObject.eGet(f);
				if (ioSpec==null) {
					ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(targetObject); 
					ioSpec = (InputOutputSpecification) adapter.getFeatureDescriptor(f).createFeature(resource, null);
					targetObject.eSet(f, ioSpec);
				}
				// add the data object to the ioSpec and input/output set
				// bulletproofing: the ioSpec SHOULD have at least one InputSet AND
				// at least one OutputSet. If not, add them here.
				if (ioSpec.getInputSets().size()==0)
					ioSpec.getInputSets().add(Bpmn2ModelerFactory.eINSTANCE.createInputSet());
				if (ioSpec.getOutputSets().size()==0)
					ioSpec.getOutputSets().add(Bpmn2ModelerFactory.eINSTANCE.createOutputSet());
				InputOutputSpecification oldIoSpec = (InputOutputSpecification) dataObject.eContainer();
				if (dataObject instanceof DataInput) {
					ioSpec.getDataInputs().add((DataInput)dataObject);
					ioSpec.getInputSets().get(0).getDataInputRefs().add((DataInput)dataObject);
					for (InputSet is : oldIoSpec.getInputSets())
						is.getDataInputRefs().remove(dataObject);
				}
				else {
					ioSpec.getDataOutputs().add((DataOutput)dataObject);
					ioSpec.getOutputSets().get(0).getDataOutputRefs().add((DataOutput)dataObject);
					for (OutputSet os : oldIoSpec.getOutputSets())
						os.getDataOutputRefs().remove(dataObject);
				}
			}
		}
		super.postMoveShape(context);
	}
}
