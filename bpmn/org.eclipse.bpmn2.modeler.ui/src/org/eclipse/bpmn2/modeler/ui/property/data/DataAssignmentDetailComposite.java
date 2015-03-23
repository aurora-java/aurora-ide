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
 * @author Innar Made
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.property.data;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.Expression;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.PropertiesCompositeFactory;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.util.PropertyUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class DataAssignmentDetailComposite extends DefaultDetailComposite {

	private AbstractDetailComposite fromDetails;
	private AbstractDetailComposite toDetails;

	public DataAssignmentDetailComposite(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param section
	 */
	public DataAssignmentDetailComposite(AbstractBpmn2PropertySection section) {
		super(section);
	}
	
	@Override
	protected void cleanBindings() {
		super.cleanBindings();
		fromDetails = null;
		toDetails = null;
	}
	
	@SuppressWarnings("restriction")
	@Override
	public void createBindings(final EObject be) {
		
		if (be instanceof Assignment) {
			
			final Assignment assignment = (Assignment) be;
			
			// an MultipleAssignments is not really valid without both a From and To
			Expression toExp = assignment.getTo();
			if (toExp==null) {
				editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
					@Override
					protected void doExecute() {
						Expression exp = createModelObject(FormalExpression.class);
						assignment.setTo(exp);
					}
				});
				toExp = assignment.getTo();
			}
			
			Expression fromExp = assignment.getFrom();
			if (fromExp==null) {
				editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
					@Override
					protected void doExecute() {
						Expression exp = createModelObject(FormalExpression.class);
						assignment.setFrom(exp);
					}
				});
				fromExp = assignment.getFrom();
			}
			
			if (toDetails==null) {
				toDetails = PropertiesCompositeFactory.INSTANCE.createDetailComposite(
						Expression.class, this, SWT.NONE);
			}
			toDetails.setBusinessObject(toExp);
			toDetails.setTitle(Messages.DataAssignmentDetailComposite_From_Title);
	
			if (fromDetails==null) {
				fromDetails = PropertiesCompositeFactory.INSTANCE.createDetailComposite(
						Expression.class, this, SWT.NONE);
			}
			fromDetails.setBusinessObject(fromExp);
			fromDetails.setTitle(Messages.DataAssignmentDetailComposite_To_Title);
		}
	}
}
