/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
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

package org.eclipse.bpmn2.modeler.core;

import java.lang.reflect.Field;

import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.utils.JavaReflectionUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class ToolTipProvider extends NLS implements IToolTipProvider {

	public final static ToolTipProvider INSTANCE = new ToolTipProvider();
	
	/**
	 * 
	 */
	public ToolTipProvider() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.IToolTipProvider#getToolTip(java.lang.Object, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public String getToolTip(Object context, EObject object) {
		// TODO Auto-generated method stub
		return getUIText(context, object, "tooltip");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.IToolTipProvider#getLongDescription(java.lang.Object, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public String getLongDescription(Object context, EObject object) {
		// TODO Auto-generated method stub
		return getUIText(context, object, "description");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.IToolTipProvider#getToolTip(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
	 */
	@Override
	public String getToolTip(Object context, EObject object, EStructuralFeature feature) {
		return getUIText(context, object, feature, "tooltip");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.IToolTipProvider#getLongDescription(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
	 */
	@Override
	public String getLongDescription(Object context, EObject object, EStructuralFeature feature) {
		return getUIText(context, object, feature, "description");
	}
	
	/**
	 * Get the verbose description for a given object. If the object is a
	 * {@code org.eclipse.bpmn2.di.BPMNDiagram} instance, the DiagramType name
	 * is prepended to the object description. If the object represents a
	 * {@code org.eclipse.bpmn2.ParticipantBand} the description is prefixed with
	 * "ParticipantBand".
	 * 
	 * 
	 * @param context any object in the defining plug-in. This object's
	 *            Class Loader is used to search for a {@code Messages} class
	 *            which is assumed to contain String fields in the form
	 * 
	 * <pre>
	 * UI_<i>ObjectTypeName</i>_description
	 * </pre>
	 * 
	 *            where <i>ObjectTypeName</i> is the name of the model object's
	 *            type (the EClass).
	 * @param object the object to search for.
	 * @return a verbose description string or an empty string if no description is found.
	 */
	private String getUIText(Object context, EObject object, String type) {
		String name = ""; //$NON-NLS-1$
		String description = ""; //$NON-NLS-1$
		if (object instanceof BPMNDiagram) {
			switch(ModelUtil.getDiagramType(object)) {
			case NONE:
				name = "UnknownDiagram"; //$NON-NLS-1$
				break;
			case PROCESS:
				name = "Process"; //$NON-NLS-1$
				break;
			case CHOREOGRAPHY:
				name = "Choreography"; //$NON-NLS-1$
				break;
			case COLLABORATION:
				name = "Collaboration"; //$NON-NLS-1$
				break;
			case CONVERSATION:
				name = "Conversation"; //$NON-NLS-1$
				break;
			default:
				break;
			}
		}
		else if (object instanceof Participant) {
			Participant participant = (Participant) object;
			EObject container = participant.eContainer();
			if (container instanceof Choreography) {
				for (FlowElement fe : ((Choreography)container).getFlowElements()) {
					if (fe instanceof ChoreographyActivity) {
						ChoreographyActivity ca = (ChoreographyActivity) fe;
						if (ca.getParticipantRefs().contains(participant)) {
							name = "ParticipantBand"; //$NON-NLS-1$
							break;
						}
					}
				}
			}
		}
		// Get the model object's long description from the Messages class.
		// The field in Messages that contains the description will have the
		// form: "UI_<objectName>_description".
		// The Messages class must be contained somewhere in the package hierarchy
		// that contains the searchObject's class.
		try {
			if (name.isEmpty()) {
				description = getUIText(context,object,null, type);
			}
			else {
	        	String fieldName = "UI_" + name + "_" + type; //$NON-NLS-1$ //$NON-NLS-2$
	        	Class messages = JavaReflectionUtil.findClass(context, "Messages"); //$NON-NLS-1$
				Field field = messages.getField(fieldName);
				description = (String)field.get(null);
			}
		} catch (Exception e) {
			description = getUIText(context,object,null, type);
		}
		
		return description;
	}

	/**
	 * Get the verbose description for a given object feature.
	 * 
	 * @param context any object in the defining plug-in. This object's
	 *            Class Loader is used to search for a {@code Messages} class
	 *            which is assumed to contain String fields in the form
	 * 
	 * <pre>
	 * UI_<i>ObjectTypeName</i>_<i>FeatureName</i>_description
	 * </pre>
	 * 
	 *            where <i>ObjectTypeName</i> is the name of the model object's
	 *            type (the EClass) and <i>FeatureName</i> is the feature name.
	 * @param object the object to search for.
	 * @param feature the object's feature.
	 * @return a verbose description string or an empty string if no description is found.
	 */
	private String getUIText(Object context, EObject object, EStructuralFeature feature, String type) {
		String fieldName;
		Field field;
		String description = ""; //$NON-NLS-1$
		
		// Get the model feature's long description from the Messages class.
		// The field in Messages that contains the description will have the
		// form: "UI_<objectName>_<featureName>_description".
		// If that entry is not found, try looking for something in the form:
		// "UI_Any_<featureName>_description".
		// The Messages class must be contained somewhere in the package hierarchy
		// that contains the searchObject's class.
		Class messages = JavaReflectionUtil.findClass(context, "Messages"); //$NON-NLS-1$
		if (messages!=null) {
			ClassLoader classLoader = messages.getClassLoader();
			boolean found = false;
			do {
				try {
					// fetch the description for this EClass and feature
					EClass eClass = object instanceof EClass ? (EClass)object : object.eClass();
	    			String className = eClass.getName().replaceAll("Impl$", ""); //$NON-NLS-1$ //$NON-NLS-2$
	    			if (feature==null)
	    				fieldName = "UI_" + className + "_" + type; //$NON-NLS-1$ //$NON-NLS-2$
	    			else
	    				fieldName = "UI_" + className + "_" + feature.getName() + "_" + type; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		    		field = messages.getField(fieldName);
		    		description += (String)field.get(null);
		    		found = true;
				}
				catch (Exception e) {
		    		try {
		    			// if a description is not found for this EClass, try "Any"
		    			if (feature!=null) {
		    				fieldName = "UI_Any_" + feature.getName() + "_" + type; //$NON-NLS-1$ //$NON-NLS-2$
				    		field = messages.getField(fieldName);
				    		description += (String)field.get(null);
				    		found = true;
		    			}
		    		}
		    		catch (Exception e2) {
		    		}
				}
				if (!found) {
					// try looking for a Messages class in the parent package
					String packageName = messages.getPackage().getName();
					messages = null;
					int index;
					while ((index = packageName.lastIndexOf(".")) != -1) { //$NON-NLS-1$
						packageName = packageName.substring(0, index);
						String className = packageName + ".Messages";  //$NON-NLS-1$
						try {
							messages = Class.forName(className, true, classLoader);
							break;
						}
						catch (Exception e3) {
						}
					}
				}
			}
			while (!found && messages!=null);
		}
		return description;
	}
	
}
