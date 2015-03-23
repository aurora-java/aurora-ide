/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 * All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.property.tasks;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.DataAssociation;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultListComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.ListCompositeColumnProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.TableColumn;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.services.Graphiti;

public class IoParametersListComposite extends DefaultListComposite {

	/**
	 * 
	 */
	protected Activity activity;
	protected CallableElement element;
	protected EStructuralFeature ioFeature;
	protected boolean isInput;
	
	public IoParametersListComposite(IoParametersDetailComposite detailComposite, EObject container, InputOutputSpecification ioSpec, EStructuralFeature ioFeature) {
		super(detailComposite, DEFAULT_STYLE);
		this.ioFeature = ioFeature;
//		businessObject = ioSpec;
		isInput = ("dataInputs".equals(ioFeature.getName())); //$NON-NLS-1$
		if (container instanceof Activity) {
			this.activity = (Activity)container;
			columnProvider = new ListCompositeColumnProvider(this);
			EClass listItemClass = (EClass)ioFeature.getEType();
			setListItemClass(listItemClass);
			
			EStructuralFeature f;
			if (isInput) {
				f = PACKAGE.getActivity_DataInputAssociations();
				columnProvider.add(new IoParameterMappingColumn(activity,f)).setHeaderText(Messages.IoParametersListComposite_From_Header);

				f = (EAttribute)listItemClass.getEStructuralFeature("name"); //$NON-NLS-1$
				columnProvider.add(new IoParameterNameColumn(activity,f)).setHeaderText(Messages.IoParametersListComposite_To_Header);

				columnProvider.add(new TableColumn(activity,PACKAGE.getDataInput_IsCollection()));
			}
			else {
				f = (EAttribute)listItemClass.getEStructuralFeature("name"); //$NON-NLS-1$
				columnProvider.add(new IoParameterNameColumn(activity,f)).setHeaderText(Messages.IoParametersListComposite_From_Header);

				f = PACKAGE.getActivity_DataOutputAssociations();
				columnProvider.add(new IoParameterMappingColumn(activity,f)).setHeaderText(Messages.IoParametersListComposite_To_Header);

				columnProvider.add(new TableColumn(activity,PACKAGE.getDataOutput_IsCollection()));
			}
		}
		else if (container instanceof CallableElement) {
			this.element = (CallableElement)container;
		}
	}

	InputOutputSpecification getIoSpec() {
		return (InputOutputSpecification) getBusinessObject();
	}
	
	@Override
	protected EObject addListItem(EObject object, EStructuralFeature feature) {
		EObject param = null;
		
		// Make sure that the getIoSpec() is contained in our Activity.
		InsertionAdapter.executeIfNeeded(getIoSpec());
		
		param = super.addListItem(object, feature);
		
		// make sure the getIoSpec() has both a default InputSet and OutputSet
		if (getIoSpec().getInputSets().size()==0) {
			InputSet is = Bpmn2ModelerFactory.create(getIoSpec().eResource(), InputSet.class);
			getIoSpec().getInputSets().add(is);
		}
		if (getIoSpec().getOutputSets().size()==0) {
			OutputSet os = Bpmn2ModelerFactory.create(getIoSpec().eResource(), OutputSet.class);
			getIoSpec().getOutputSets().add(os);
		}
		
		if (activity!=null) {
			// this is an Activity - create an Input or Output DataAssociation
			if (param instanceof DataInput) {
				DataInputAssociation inputAssociation = createModelObject(DataInputAssociation.class);
				activity.getDataInputAssociations().add(inputAssociation);
				inputAssociation.setTargetRef((DataInput) param);
			}
			else if (param instanceof DataOutput)
			{
				DataOutputAssociation outputAssociation = createModelObject(DataOutputAssociation.class);
				activity.getDataOutputAssociations().add(outputAssociation);
				outputAssociation.getSourceRef().clear();
				outputAssociation.getSourceRef().add((DataOutput) param);
			}
		}
		else if (element!=null) {
			// this is a CallableElement - it has no DataAssociations so we're all done
		}
		return param;
	}

	@Override
	protected EObject editListItem(EObject object, EStructuralFeature feature) {
		return super.editListItem(object, feature);
	}

	@Override
	protected Object removeListItem(EObject object, EStructuralFeature feature, int index) {
		EList<EObject> list = (EList<EObject>)object.eGet(feature);
		EObject item = list.get(index);

		if (item instanceof DataInput) {
			// remove parameter from inputSets
			List<InputSet> inputSets = getIoSpec().getInputSets();
			for (InputSet is : inputSets) {
				if (is.getDataInputRefs().contains(item))
					is.getDataInputRefs().remove(item);
			}
		}
		else if (item instanceof DataOutput) {
			// remove parameter from outputSets
			List<OutputSet> OutputSets = getIoSpec().getOutputSets();
			for (OutputSet is : OutputSets) {
				if (is.getDataOutputRefs().contains(item))
					is.getDataOutputRefs().remove(item);
			}
		}
		
		if (activity!=null) {
			// this is an Activity
			// remove Input or Output DataAssociations
			List<DataAssociation> dataAssociationsRemoved = new ArrayList<DataAssociation>();
			List<BPMNEdge> edgesRemoved = new ArrayList<BPMNEdge>();
			
			if (item instanceof DataInput) {
				List<DataInputAssociation> dataInputAssociations = activity.getDataInputAssociations();
				for (DataInputAssociation da : dataInputAssociations) {
					if (da.getTargetRef()!=null && da.getTargetRef().equals(item)) {
						dataAssociationsRemoved.add(da);
						BPMNEdge edge = DIUtils.findBPMNEdge(da);
						if (edge!=null) {
							edgesRemoved.add(edge);
						}
					}
				}
				dataInputAssociations.removeAll(dataAssociationsRemoved);
			}
			else if (item instanceof DataOutput) {
				List<DataOutputAssociation> dataOutputAssociations = activity.getDataOutputAssociations();
				for (DataOutputAssociation da : dataOutputAssociations) {
					if (da.getSourceRef()!=null && da.getSourceRef().contains(item)) {
						dataAssociationsRemoved.add(da);
						BPMNEdge edge = DIUtils.findBPMNEdge(da);
						if (edge!=null) {
							edgesRemoved.add(edge);
						}
					}
				}
				dataOutputAssociations.removeAll(dataAssociationsRemoved);
			}

			// If the Data Association has a BPMNEdge and Connection line
			// associated with it, remove that too.
			for (BPMNEdge edge : edgesRemoved) {
				org.eclipse.graphiti.mm.pictograms.Diagram diagram = getDiagramEditor().getDiagramTypeProvider().getDiagram();
				for (Object pe : Graphiti.getPeService().getLinkedPictogramElements(new EObject[] {edge}, diagram)) {
					if (pe instanceof Connection) {
						Graphiti.getPeService().deletePictogramElement((Connection)pe);
					}
				}
				EcoreUtil.delete(edge,true);
			}
		}
		else if (element!=null) {
			// this is a CallableElement
		}
		else
			return false;

		return super.removeListItem(object, feature, index);
	}
	
}