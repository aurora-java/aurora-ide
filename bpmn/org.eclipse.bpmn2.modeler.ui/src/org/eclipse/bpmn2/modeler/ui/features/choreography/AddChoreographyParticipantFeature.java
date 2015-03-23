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

import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.internal.util.ui.PopupMenu;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Bob Brodt
 *
 */
public class AddChoreographyParticipantFeature extends AbstractCustomFeature {
	
	protected boolean changesDone = false;
	
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
			return ((Participant)element).getName();
		}

		public Image getImage(Object element) {
			return null;
		}

	};

	/**
	 * @param fp
	 */
	public AddChoreographyParticipantFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public String getName() {
	    return Messages.AddChoreographyParticipantFeature_Name;
	}
	
	@Override
	public String getDescription() {
	    return Messages.AddChoreographyParticipantFeature_Description;
	}

	@Override
	public String getImageId() {
		return ImageProvider.IMG_16_ADD_PARTICIPANT;
	}

	@Override
	public boolean isAvailable(IContext context) {
		return true;
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			PictogramElement pe = pes[0];
			Object bo = getBusinessObjectForPictogramElement(pe);
			if (bo instanceof ChoreographyActivity) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			PictogramElement pe = pes[0];
			ContainerShape choreographyActivityShape = null;
			Object bo = getBusinessObjectForPictogramElement(pe);
			if (pe instanceof ContainerShape && bo instanceof ChoreographyActivity) {
				choreographyActivityShape = (ContainerShape)pe;
				ChoreographyActivity choreographyActivity = (ChoreographyActivity)bo;
				
				Participant participant = null;
				List<Participant> participantList = new ArrayList<Participant>();
				participant = Bpmn2ModelerFactory.create(Participant.class);
				participant.setName(Messages.AddChoreographyParticipantFeature_New_Participant);
				ModelUtil.setID(participant, choreographyActivity.eResource());
				
				participantList.add(participant);
				TreeIterator<EObject> iter = ModelUtil.getDefinitions(choreographyActivity).eAllContents();
				while (iter.hasNext()) {
					EObject obj = iter.next();
					if (obj instanceof Participant && !choreographyActivity.getParticipantRefs().contains(obj))
						participantList.add((Participant)obj);
				}
				Participant result = participant;

				if (participantList.size()>1) {
					PopupMenu popupMenu = new PopupMenu(participantList, labelProvider);
					changesDone = popupMenu.show(Display.getCurrent().getActiveShell());
					if (changesDone) {
						result = (Participant) popupMenu.getResult();
					}
				}
				else
					changesDone = true;
				
				if (changesDone) {
					if (result==participant) { // the new one
						participant.setName( ModelUtil.toCanonicalString(participant.getId()) );
						Choreography choreography = (Choreography)choreographyActivity.eContainer();
						choreography.getParticipants().add(result);
						/*
						 Finish this later after we figure out how to deal with multiple BPMNDiagrams and BPMNPlanes
						 
						Process process = (Process) PropertyUtil.createObject(task.eResource(), Bpmn2Package.eINSTANCE.getProcess());
						// NOTE: this is needed because it fires the InsertionAdapter, which adds the new Process
						// to Definitions.rootElements, otherwise the Process would be a dangling object
						process.setName(participant.getName()+" Process");
						participant.setProcessRef(process);
						*/
					}

					if (choreographyActivity.getInitiatingParticipantRef() == null) {
						choreographyActivity.setInitiatingParticipantRef(result);
					}

					int index = choreographyActivity.getParticipantRefs().size();
					if (index>1)
						--index;
					choreographyActivity.getParticipantRefs().add(index, result);
					// if the Choreography Activity is too short to fit all of the Participant Bands
					// then we need to resize it first.
					List<ContainerShape> bandShapes = FeatureSupport.getParticipantBandContainerShapes(choreographyActivityShape);
					int bandHeight = 0;
					for (ContainerShape s : bandShapes) {
						bandHeight += Graphiti.getGaLayoutService().calculateSize(s.getGraphicsAlgorithm()).getHeight();
					}
					GraphicsAlgorithm choreographyActivityGA = choreographyActivityShape.getGraphicsAlgorithm();
					int containerHeight = choreographyActivityGA.getHeight();
					int w = choreographyActivityGA.getWidth();
					int x = choreographyActivityGA.getX();
					int y = choreographyActivityGA.getY();
					if (bandHeight + 100 > containerHeight) {
						ResizeShapeContext resizeContext = new ResizeShapeContext(choreographyActivityShape);
						resizeContext.setSize(w, bandHeight + 100);
						resizeContext.setLocation(x, y);
						resizeContext.setDirection(IResizeShapeContext.DIRECTION_SOUTH);
						IResizeShapeFeature resizeFeature = getFeatureProvider().getResizeShapeFeature(resizeContext);
						resizeFeature.resizeShape(resizeContext);
					}
					else {
						ChoreographyUtil.updateParticipantBands(getFeatureProvider(), choreographyActivityShape);
					}
				}
			}
		}
	}

	@Override
	public boolean hasDoneChanges() {
		return changesDone;
	}
}
