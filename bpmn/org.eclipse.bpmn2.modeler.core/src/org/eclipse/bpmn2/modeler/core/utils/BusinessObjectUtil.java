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
 * @author Ivar Meikas
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.ConversationLink;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

public class BusinessObjectUtil {

	@SuppressWarnings("rawtypes")
	public static boolean containsElementOfType(PictogramElement elem, Class clazz) {
		if (elem.getLink() == null) {
			return false;
		}
		// if this is a connection point, look at business objects of the connection
		if (AnchorUtil.isConnectionPoint(elem)) {
			elem = AnchorUtil.getConnectionPointOwner((Shape)elem);
		}
		EList<EObject> businessObjs = elem.getLink().getBusinessObjects();
		for (EObject eObject : businessObjs) {
			if (clazz.isInstance(eObject)) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean containsChildElementOfType(PictogramElement root, Class clazz) {
		if (AnchorUtil.isConnectionPoint(root)) {
			root = AnchorUtil.getConnectionPointOwner((Shape)root);
		}
		if (root instanceof ContainerShape) {
			ContainerShape rootContainer = (ContainerShape) root;
			for (Shape currentShape : rootContainer.getChildren()) {
				if (containsElementOfType(currentShape, clazz)) {
					return true;
				}
			}
		}
		return false;
	}

	public static <T extends EObject> T getFirstElementOfType(PictogramElement elem, Class<T> clazz) {
		return getFirstElementOfType(elem,clazz,false);
	}

	@SuppressWarnings("unchecked")
	public static <T extends EObject> T getFirstElementOfType(PictogramElement elem, Class<T> clazz, boolean searchParents) {
		if (elem==null || elem.getLink() == null) {
			if (searchParents) {
				while (elem!=null && elem.getLink()==null && elem.eContainer() instanceof PictogramElement)
					elem = (PictogramElement)elem.eContainer();
			}
			if (elem==null || elem.getLink() == null)
				return null;
		}
		// if this is a connection point, look at business objects of the connection
		if (AnchorUtil.isConnectionPoint(elem)) {
			elem = AnchorUtil.getConnectionPointOwner((Shape)elem);
		}
		EList<EObject> businessObjs = elem.getLink().getBusinessObjects();
		for (EObject eObject : businessObjs) {
			if (clazz.isInstance(eObject)) {
				return (T) eObject;
			}
		}
		return null;
	}

	/**
	 * Returns a list of {@link PictogramElement}s which contains an element to the
	 * assigned businessObjectClazz, i.e. the list contains {@link PictogramElement}s
	 * which meet the following constraint:<br>
	 * <code>
	 * 	foreach child of root:<br>
	 *  BusinessObjectUtil.containsChildElementOfType(child, businessObjectClazz) == true
	 * </code>
	 * @param root
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<PictogramElement> getChildElementsOfType(ContainerShape root, Class clazz) {
		List<PictogramElement> result = new ArrayList<PictogramElement>();
		for (Shape currentShape : root.getChildren()) {
			if (containsElementOfType(currentShape, clazz)) {
				result.add(currentShape);
			}
			if (containsChildElementOfType(currentShape, clazz)) {
				result.add(currentShape);
			}
		}
		return result;
	}

	public static BaseElement getFirstBaseElement(PictogramElement pe) {
		return BusinessObjectUtil.getFirstElementOfType(pe, BaseElement.class);
	}
	
	public static PictogramElement getFirstBaseElementFromDiagram(Diagram diagram, BaseElement e) {
		PictogramElement foundElem = null;

		IPeService peService = Graphiti.getPeService();
		for (Diagram d : getAllDiagrams(diagram)) {
			Collection<PictogramElement> elements = peService.getAllContainedPictogramElements(d);
			for (PictogramElement pe : elements) {
				BaseElement be = getFirstElementOfType(pe, e.getClass());
				if (be != null && be.equals(e)) {
					foundElem = pe;
					break;
				}
			}
		}

		return foundElem;
	}

	@SuppressWarnings("unchecked")
	public static <T extends PictogramElement> T getPictogramElementForProperty(PictogramElement container, String property, String expectedValue, Class<T> clazz) {
		IPeService peService = Graphiti.getPeService();
		Iterator<PictogramElement> iterator = peService.getAllContainedPictogramElements(container).iterator();
		while (iterator.hasNext()) {
			PictogramElement pe = iterator.next();
			String value = peService.getPropertyValue(pe, property);
			if (value != null && value.equals(expectedValue) && clazz.isInstance(pe)) {
				return (T) pe;
			}
		}
		
		// also search the linked objects
		PictogramElement pe = getFirstElementOfType(container, PictogramElement.class);
		if (pe!=null) {
			String value = peService.getPropertyValue(pe, property);
			if (value != null && value.equals(expectedValue) && clazz.isInstance(pe)) {
				return (T) pe;
			}
			return getPictogramElementForProperty(pe, property, expectedValue, clazz);
		}
		return null;
	}
	
	public static PictogramElement getPictogramElementFromDiagram(Diagram diagram, BPMNShape bpmnShape) {
		PictogramElement foundElem = null;

		IPeService peService = Graphiti.getPeService();
		for (Diagram d : getAllDiagrams(diagram)) {
			Collection<PictogramElement> elements = peService.getAllContainedPictogramElements(d);
			for (PictogramElement pe : elements) {
				BPMNShape s = getFirstElementOfType(pe, BPMNShape.class);
				if (s != null && s.equals(bpmnShape)) {
					foundElem = pe;
					break;
				}
			}
		}
		
		return foundElem;
	}

	public static PictogramElement getPictogramElementForSelection(ISelection selection) {
		EditPart editPart = getEditPartForSelection(selection);
		if (editPart != null && editPart.getModel() instanceof PictogramElement) {
			return (PictogramElement) editPart.getModel();
		}
		if (editPart instanceof AbstractTreeEditPart) {
			return (PictogramElement) editPart.getAdapter(PictogramElement.class);
		}
		if (selection instanceof IStructuredSelection) {
			Object o = ((IStructuredSelection)selection).getFirstElement();
			if (o instanceof PictogramElement)
				return (PictogramElement)o;
		}
		return null;
	}

	public static EObject getBusinessObjectForSelection(ISelection selection) {
		PictogramElement pe = getPictogramElementForSelection(selection);
		if (pe!=null)
			return getBusinessObjectForPictogramElement(pe);
		
		if (selection instanceof IStructuredSelection &&
				((IStructuredSelection) selection).isEmpty()==false) {
		
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			EditPart editPart = null;
			if (firstElement instanceof EditPart) {
				editPart = (EditPart) firstElement;
				if (editPart.getModel() instanceof EObject) {
					return (EObject)editPart.getModel();
				}
			}
			else if (firstElement instanceof EObject) {
				return (EObject)firstElement;
			}
		}
		return null;
	}

	public static EObject getBusinessObjectForPictogramElement(PictogramElement pe) {
		if (pe!=null) {
			// Substitute the Connection for a ConnectionDecorator because these
			// do not have linked business objects although a connection decorator
			// can still be selected. The net effect is that when a connection
			// decorator is selected, it is the same as selecting the connection
			// that owns it.
			if (pe instanceof ConnectionDecorator) {
				pe = ((ConnectionDecorator)pe).getConnection();
			}
			Object be = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			if (be instanceof EObject)
				return (EObject) be;
		}
		return null;
	}
	
	public static <T extends BaseElement> T getBusinessObject(IContext context, Class<T> clazz) {
		Object o = null;
		if (context instanceof IAddContext) {
			o = ((IAddContext) context).getNewObject();
		}
		else if (context instanceof IPictogramElementContext) {
			return BusinessObjectUtil.getFirstElementOfType(
					(((IPictogramElementContext) context).getPictogramElement()), clazz);
		}
		else if (context instanceof ICustomContext) {
			PictogramElement[] pes = ((ICustomContext) context).getPictogramElements();
			if (pes.length==1)
				o = BusinessObjectUtil.getFirstElementOfType(pes[0], clazz);
		}
		if (clazz.isInstance(o))
			return (T)o;
		return null;
	}

	public static EditPart getEditPartForSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection &&
				((IStructuredSelection) selection).isEmpty()==false) {
		
			Object firstElement = ((IStructuredSelection) selection).getFirstElement();
			EditPart editPart = null;
			if (firstElement instanceof EditPart) {
				editPart = (EditPart) firstElement;
			} else if (firstElement instanceof IAdaptable) {
				editPart = (EditPart) ((IAdaptable) firstElement).getAdapter(EditPart.class);
			}
			return editPart;
		}
		return null;
	}
	
	public static boolean isConnection(Class be) {
		return
				be == SequenceFlow.class ||
				be == Association.class ||
				be == MessageFlow.class ||
				be == DataInputAssociation.class ||
				be == DataOutputAssociation.class ||
				be == ConversationLink.class;
	}

	public static List<Diagram> getAllDiagrams(Diagram diagram) {
		List<Diagram> list = new ArrayList<Diagram>();
		list.add(diagram);
		
		Resource resource = diagram.eResource();
		for (EObject o : resource.getContents()) {
			if (o instanceof Diagram && o!=diagram) {
				list.add((Diagram)o);
			}
		}
		
		return list;
	}
}