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

package org.eclipse.bpmn2.modeler.ui.features.choreography;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BpmnDiFactory;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.internal.util.ui.PopupMenu;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * This Custom Feature associates an existing BPMNDiagram page and its Process or creates a
 * new Process and BPMNDiagram page for the selected Choreography Participant Band.
 * The new BPMNDiagram page contains the Process referenced by the Participant Band.
 *  
 * @author Bob Brodt
 */
public class WhiteboxFeature extends AbstractCustomFeature {

	protected boolean changesDone = false;
	
	// label provider for the popup menu that displays allowable Activity subclasses
	private static ILabelProvider labelProvider = new ILabelProvider() {

		public void removeListener(ILabelProviderListener listener) {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void dispose() {

		}

		public void addListener(ILabelProviderListener listener) {

		}

		public String getText(Object element) {
			return ModelUtil.getTextValue(element);
		}

		public Image getImage(Object element) {
			return null;
		}

	};

	/**
	 * @param fp
	 */
	public WhiteboxFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public String getName() {
	    return Messages.WhiteboxFeature_Name;
	}
	
	@Override
	public String getDescription() {
	    return Messages.WhiteboxFeature_Description;
	}

	@Override
	public String getImageId() {
		return ImageProvider.IMG_16_WHITEBOX;
	}

	@Override
	public boolean isAvailable(IContext context) {
		if (context instanceof ICustomContext) {
			PictogramElement[] pes = ((ICustomContext)context).getPictogramElements();
			if (pes != null && pes.length == 1) {
				PictogramElement pe = pes[0];
				if (ChoreographyUtil.isChoreographyParticipantBand(pe)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			PictogramElement pe = pes[0];
			if (ChoreographyUtil.isChoreographyParticipantBand(pe)) {
				Participant participant = (Participant) getBusinessObjectForPictogramElement(pe);
				Process process = participant.getProcessRef();
				// if the Participant Band does not reference a Process, or if the referenced
				// Process does not have its own BPMNDiagram page yet, then we can "whitebox"
				// this Participant Band.
				if (process==null)
					return true;
				BPMNDiagram bpmnDiagram = DIUtils.findBPMNDiagram(process);
				return bpmnDiagram==null;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		PictogramElement pe = context.getPictogramElements()[0];
		Participant participant = (Participant)getBusinessObjectForPictogramElement(pe);
		Definitions definitions = ModelUtil.getDefinitions(participant);
		BPMNDiagram bpmnDiagram = selectBPMNDiagram(definitions, participant);

		if (bpmnDiagram!=null) {
	        // add the Participant to the first Choreography or Collaboration we find.
	        // TODO: when (and if) multipage editor allows additional Choreography or
	        // Collaboration diagrams to be created, this will be the specific diagram
	        // that is being rendered on the current page.
			if (participant!=null) {
		        for (RootElement element : definitions.getRootElements()) {
		            if (element instanceof Collaboration) {
		            	((Collaboration)element).getParticipants().add(participant);
		                break;
		            }
		        }
			}
		}
	}
	
	@Override
	public boolean hasDoneChanges() {
		return changesDone;
	}

	private BPMNDiagram selectBPMNDiagram(Definitions definitions, Participant participant) {

		Resource resource = definitions.eResource();
		List<BPMNDiagram> diagramList = new ArrayList<BPMNDiagram>();
		BPMNDiagram newDiagram = BpmnDiFactory.eINSTANCE.createBPMNDiagram();
		ModelUtil.setID(newDiagram, resource);
        newDiagram.setName(Messages.WhiteboxFeature_New_Process);

		BPMNPlane plane = BpmnDiFactory.eINSTANCE.createBPMNPlane();
		ModelUtil.setID(plane, resource);
		
		Process process = Bpmn2ModelerFactory.create(Process.class);
		plane.setBpmnElement(process);
		newDiagram.setPlane(plane);

		diagramList.add(newDiagram);
		
		for (BPMNDiagram d : definitions.getDiagrams()) {
			BaseElement bpmnElement = d.getPlane().getBpmnElement();
			if (bpmnElement instanceof Process) {
				diagramList.add(d);
			}
		}
		
		BPMNDiagram result = newDiagram;
		if (diagramList.size()>1) {
			PopupMenu popupMenu = new PopupMenu(diagramList, labelProvider);
			changesDone = popupMenu.show(Display.getCurrent().getActiveShell());
			if (changesDone) {
				result = (BPMNDiagram) popupMenu.getResult();
			}
			else
				return null;
		}
		else
			changesDone = true;
		
		if (changesDone) {
			if (result==newDiagram) { // the new one
				String name = NLS.bind(Messages.WhiteboxFeature_Process_For, ExtendedPropertiesProvider.getTextValue(participant));
		        process.setName(name);
		        newDiagram.setName(name);
		        definitions.getRootElements().add(process);
				definitions.getDiagrams().add(result);
			}
			else {
				process = (Process) result.getPlane().getBpmnElement();
			}
		}
    	participant.setProcessRef(process);
		ModelUtil.setID(process);

		return result;
	}
}
