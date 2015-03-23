/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.model;

import java.util.Comparator;

import org.eclipse.bpmn2.Category;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.CorrelationProperty;
import org.eclipse.bpmn2.DataStore;
import org.eclipse.bpmn2.EndPoint;
import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.Escalation;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.PartnerEntity;
import org.eclipse.bpmn2.PartnerRole;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Resource;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.Signal;

public final class RootElementComparator implements Comparator<RootElement> {
	private static Class[] elements = new Class[] {
		ItemDefinition.class,
		Error.class,
		Signal.class,
		Escalation.class,
		Resource.class,
		Message.class,
		EventDefinition.class,
		DataStore.class,
		EndPoint.class,
		Interface.class,
		Category.class,
		CorrelationProperty.class,
		PartnerRole.class,
		PartnerEntity.class,
		Choreography.class,
		Collaboration.class,
		Process.class,
	};
	
	@Override
	public int compare(RootElement a, RootElement b) {
		int aOrder = getOrder(a);
		int bOrder = getOrder(b);
		return aOrder - bOrder;
	}
	
	private int getOrder(RootElement element) {
		for (int i=0; i<elements.length; ++i) {
			if (element.eClass().getInstanceClass() == elements[i])
				return i;
		}
		
		return Integer.MAX_VALUE;
	}
}
