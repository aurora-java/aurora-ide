/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc.
 *  All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 *
 * @author Bob Brodt
 ******************************************************************************/

package org.eclipse.bpmn2.modeler.ui.adapters.properties;

import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.ParticipantMultiplicity;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BpmnDiFactory;
import org.eclipse.bpmn2.di.BpmnDiPackage;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Bob Brodt
 *
 */
public class ParticipantPropertiesAdapter extends ExtendedPropertiesAdapter<Participant> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public ParticipantPropertiesAdapter(AdapterFactory adapterFactory, Participant object) {
		super(adapterFactory, object);
		

		EStructuralFeature ref = Bpmn2Package.eINSTANCE.getParticipant_ProcessRef();
    	setProperty(ref, UI_CAN_CREATE_NEW, Boolean.FALSE);

    	setFeatureDescriptor(ref, new RootElementRefFeatureDescriptor<Participant>(this,object,ref));

		setObjectDescriptor(new ObjectDescriptor<Participant>(this,object) {
			
			@Override
			public Participant createObject(Resource resource, EClass eclass) {
				Participant participant = super.createObject(resource, eclass);
				
				Definitions definitions = null;
				if (resource!=null)
					definitions = ModelUtil.getDefinitions(resource);
				else {
					definitions = ModelUtil.getDefinitions(participant);
				}

		        // create a Process for this Participant
//		        Process process = (Process) ModelUtil.createObject(resource, Bpmn2Package.eINSTANCE.getProcess());
//		        participant.setProcessRef(process);
		        
		        // NOTE: this is needed because it fires the InsertionAdapter, which adds the new Process
		        // to Definitions.rootElements, otherwise the Process would be a dangling object
//		        process.setName(participant.getName()+" Process");

		        // add the Participant to the first Choreography or Collaboration we find.
		        // TODO: when (and if) multipage editor allows additional Choreography or
		        // Collaboration diagrams to be created, this will be the specific diagram
		        // that is being rendered on the current page.
				if (definitions!=null) {
			        List<RootElement> rootElements = definitions.getRootElements();
			        for (RootElement element : rootElements) {
			            if (element instanceof Collaboration || element instanceof Choreography) {
			            	((Collaboration)element).getParticipants().add(participant);
			                break;
			            }
			        }
			        if (participant.eContainer()==null) {
			        	// no Collaboration element found - create one
			        	Collaboration collaboration = Bpmn2ModelerFactory.create(resource, Collaboration.class);
			        	definitions.getRootElements().add(collaboration);
			        	collaboration.getParticipants().add(participant);
			        }
				}
		        
				return participant;
			}
			
		});
		
		ref = Bpmn2Package.eINSTANCE.getParticipant_ParticipantMultiplicity();
		setProperty(ref, UI_CAN_EDIT_INLINE, Boolean.FALSE);
		setProperty(ref, UI_CAN_CREATE_NEW, Boolean.TRUE);
		setProperty(ref, UI_CAN_EDIT, Boolean.TRUE);
		setProperty(ref, UI_IS_MULTI_CHOICE, Boolean.FALSE);
    	setFeatureDescriptor(ref, new FeatureDescriptor<Participant>(this,object,ref) {

			@Override
			public String getLabel() {
				return Messages.ParticipantPropertiesAdapter_Multiplicity;
			}

			@Override
			public String getTextValue() {
				 ParticipantMultiplicity pm = object.getParticipantMultiplicity();
				 if (pm!=null) {
					 return pm.getMinimum() + ".." + pm.getMaximum(); //$NON-NLS-1$
				 }
				 return ""; //$NON-NLS-1$
			}

    	});

	}
}
