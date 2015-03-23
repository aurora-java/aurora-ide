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
package org.eclipse.bpmn2.modeler.ui;
import java.util.Enumeration;

import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XNIException;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.LifecycleEvent;
import org.eclipse.bpmn2.modeler.core.IBpmn2RuntimeExtension;
import org.eclipse.bpmn2.modeler.core.LifecycleEvent.EventType;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil.Bpmn2DiagramType;
import org.eclipse.bpmn2.modeler.ui.diagram.Bpmn2FeatureMap;
import org.eclipse.bpmn2.modeler.ui.property.StyleChangeAdapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.xml.type.AnyType;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.xml.sax.InputSource;


public class DefaultBpmn2RuntimeExtension implements IBpmn2RuntimeExtension {

	private static final String targetNamespace = "http://org.eclipse.bpmn2/default"; //$NON-NLS-1$
	
	public DefaultBpmn2RuntimeExtension() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.IBpmn2RuntimeExtension#isContentForRuntime(org.eclipse.ui.IEditorInput)
	 */
	@Override
	public boolean isContentForRuntime(IEditorInput input) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.IBpmn2RuntimeExtension#getTargetNamespace(org.eclipse.bpmn2.modeler.core.utils.ModelUtil.Bpmn2DiagramType)
	 */
	@Override
	public String getTargetNamespace(Bpmn2DiagramType diagramType){
		String type = ""; //$NON-NLS-1$
		switch (diagramType) {
		case PROCESS:
			type = "/process"; //$NON-NLS-1$
			break;
		case COLLABORATION:
			type = "/collaboration"; //$NON-NLS-1$
			break;
		case CHOREOGRAPHY:
			type = "/choreography"; //$NON-NLS-1$
			break;
		default:
			type = ""; //$NON-NLS-1$
			break;
		}
		return targetNamespace + type;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.IBpmn2RuntimeExtension#notify(org.eclipse.bpmn2.modeler.core.LifecycleEvent)
	 */

	@Override
	public void notify(LifecycleEvent event) {
//		System.out.println(event.eventType+ ": " + event.target);
		if (event.eventType.equals(EventType.PICTOGRAMELEMENT_ADDED)) {
			StyleChangeAdapter.adapt((PictogramElement) event.target);
		}
		else if (event.eventType.equals(EventType.BUSINESSOBJECT_CREATED) && event.target instanceof BaseElement) {
			BaseElement object = (BaseElement) event.target;
			if (Bpmn2FeatureMap.ALL_SHAPES.contains(object.getClass()) && ShapeStyle.hasStyle(object))
				ShapeStyle.createStyleObject(object);
		}
	}

	/**
	 * A simple XML parser that checks if the root element of an xml document contains any
	 * namespace definitions matching the given namespace URI.
	 * 
	 * @author bbrodt
	 */
	public static class RootElementParser extends SAXParser {
		private String namespace;
		private boolean result = false;
		
		/**
		 * @param namespace - the namespace URI to scan for.
		 */
		public RootElementParser(String namespace) {
			this.namespace = namespace;
		}
		
		public boolean getResult() {
			return result;
		}
		
		public void parse(InputSource source) {
			result = false;
			try {
				super.parse(source);
			} catch (AcceptedException e) {
				result = true;
			} catch (Exception e) {
			}
		}
		
		@Override
		public void startElement(QName qName, XMLAttributes attributes, Augmentations augmentations)
				throws XNIException {

			super.startElement(qName, attributes, augmentations);

			// search the "definitions" for a namespace that matches the required namespace
			if ("definitions".equals(qName.localpart)) { //$NON-NLS-1$
				String namespace = attributes.getValue("targetNamespace"); //$NON-NLS-1$
				if (this.namespace.equals(namespace))
					throw new AcceptedException(qName.localpart);
				Enumeration<?> e = fNamespaceContext.getAllPrefixes();
				while (e.hasMoreElements()) {
					String prefix = (String)e.nextElement();
					namespace = fNamespaceContext.getURI(prefix);
					if (this.namespace.equals(namespace))
						throw new AcceptedException(qName.localpart);
				}
				throw new RejectedException();
			} else {
				throw new RejectedException();
			}
		}
	}

	public static class AcceptedException extends RuntimeException {
		public String acceptedRootElement;

		public AcceptedException(String acceptedRootElement) {
			this.acceptedRootElement = acceptedRootElement;
		}

		private static final long serialVersionUID = 1L;
	}

	public static class RejectedException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
}
