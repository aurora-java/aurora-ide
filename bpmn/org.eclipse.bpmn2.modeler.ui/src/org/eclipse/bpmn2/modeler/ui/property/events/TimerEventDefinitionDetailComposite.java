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

package org.eclipse.bpmn2.modeler.ui.property.events;

import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.AbstractBpmn2PropertySection;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.TextObjectEditor;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Bob Brodt
 *
 */
public class TimerEventDefinitionDetailComposite extends DefaultDetailComposite {

	protected Button timeDateButton;
	protected Button timeCycleButton;
	protected Button timeDurationButton;
	protected TextObjectEditor timeValueEditor;

	public enum TimerType {
		NONE,
		TIMEDATE,
		TIMECYCLE,
		TIMEDURATION
	};
	protected TimerType timerType = TimerType.NONE;
	
	/**
	 * @param parent
	 * @param style
	 */
	public TimerEventDefinitionDetailComposite(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param section
	 */
	public TimerEventDefinitionDetailComposite(AbstractBpmn2PropertySection section) {
		super(section);
	}

	@Override
	public void cleanBindings() {
		super.cleanBindings();
		timeDateButton = null;
		timeCycleButton = null;
		timeDurationButton = null;
	}

	@Override
	public void createBindings(EObject be) {
		final TimerEventDefinition event = (TimerEventDefinition)be;

		Composite composite = getAttributesParent();

		createLabel(composite, Messages.TimerEventDefinitionDetailComposite_Type);
		Composite buttonComposite = toolkit.createComposite(composite);
		buttonComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		FillLayout layout = new FillLayout();
		layout.marginWidth = 10;
		layout.spacing = 20;
		buttonComposite.setLayout(layout);
		
		FormalExpression exp = null;
		
		timeDateButton = toolkit.createButton(buttonComposite, Messages.TimerEventDefinitionDetailComposite_Time_Date, SWT.RADIO);
		timeCycleButton = toolkit.createButton(buttonComposite, Messages.TimerEventDefinitionDetailComposite_Interval, SWT.RADIO);
		timeDurationButton = toolkit.createButton(buttonComposite, Messages.TimerEventDefinitionDetailComposite_Duration, SWT.RADIO);
		
		if (!isModelObjectEnabled(event.eClass(), PACKAGE.getTimerEventDefinition_TimeDate()))
			timeDateButton.setVisible(false);
		if (!isModelObjectEnabled(event.eClass(), PACKAGE.getTimerEventDefinition_TimeCycle()))
			timeCycleButton.setVisible(false);
		if (!isModelObjectEnabled(event.eClass(), PACKAGE.getTimerEventDefinition_TimeDuration()))
			timeDurationButton.setVisible(false);
		
		if (event.getTimeDate()!=null) {
			exp = (FormalExpression)event.getTimeDate();
			timeDateButton.setSelection(true);
			timerType = TimerType.TIMEDATE;
		}
		else if (event.getTimeCycle()!=null) {
			exp = (FormalExpression)event.getTimeCycle();
			timeCycleButton.setSelection(true);
			timerType = TimerType.TIMECYCLE;
		}
		else if (event.getTimeDuration()!=null) {
			exp = (FormalExpression)event.getTimeDuration();
			timeDurationButton.setSelection(true);
			timerType = TimerType.TIMEDURATION;
		}
		else{
			timerType = TimerType.NONE;
			exp = createModelObject(FormalExpression.class);
		}
		
		timeValueEditor = new TextObjectEditor(this, exp, PACKAGE.getFormalExpression_Body());
		
		timeDateButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				if (timeDateButton.getSelection() && timerType!=TimerType.TIMEDATE) {
					TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
					domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						protected void doExecute() {
							FormalExpression exp = createModelObject(FormalExpression.class);
							event.eUnset(PACKAGE.getTimerEventDefinition_TimeCycle());
							event.eUnset(PACKAGE.getTimerEventDefinition_TimeDuration());
							event.setTimeDate(exp);
							exp.setBody(""); //$NON-NLS-1$
							timeValueEditor.setObject(exp);
							timerType = TimerType.TIMEDATE;
						}
					});
				}
			}
		});
		
		timeCycleButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				if (timeCycleButton.getSelection() && timerType!=TimerType.TIMECYCLE) {
					TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
					domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						protected void doExecute() {
							FormalExpression exp = createModelObject(FormalExpression.class);
							event.eUnset(PACKAGE.getTimerEventDefinition_TimeDate());
							event.eUnset(PACKAGE.getTimerEventDefinition_TimeDuration());
							event.setTimeCycle(exp);
							exp.setBody(""); //$NON-NLS-1$
							timeValueEditor.setObject(exp);
							timerType = TimerType.TIMECYCLE;
						}
					});
				}
			}
		});
		
		timeDurationButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				if (timeDurationButton.getSelection() && timerType!=TimerType.TIMEDURATION) {
					TransactionalEditingDomain domain = getDiagramEditor().getEditingDomain();
					domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						protected void doExecute() {
							FormalExpression exp = createModelObject(FormalExpression.class);
							event.eUnset(PACKAGE.getTimerEventDefinition_TimeDate());
							event.eUnset(PACKAGE.getTimerEventDefinition_TimeCycle());
							event.setTimeDuration(exp);
							exp.setBody(""); //$NON-NLS-1$
							timeValueEditor.setObject(exp);
							timerType = TimerType.TIMEDURATION;
						}
					});
				}
			}
		});
		
		timeValueEditor.createControl(composite, Messages.TimerEventDefinitionDetailComposite_Value);
	}

}
