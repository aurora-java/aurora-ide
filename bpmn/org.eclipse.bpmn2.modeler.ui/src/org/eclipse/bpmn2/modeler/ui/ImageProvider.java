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
package org.eclipse.bpmn2.modeler.ui;

import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CallChoreography;
import org.eclipse.bpmn2.CallConversation;
import org.eclipse.bpmn2.CancelEventDefinition;
import org.eclipse.bpmn2.ChoreographyTask;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ComplexGateway;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.Conversation;
import org.eclipse.bpmn2.ConversationLink;
import org.eclipse.bpmn2.DataAssociation;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataStore;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventBasedGateway;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubChoreography;
import org.eclipse.bpmn2.SubConversation;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.eclipse.bpmn2.TextAnnotation;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.Transaction;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

public class ImageProvider extends AbstractImageProvider {

	private static final String dot16 = ".16"; //$NON-NLS-1$
	private static final String dot20 = ".20"; //$NON-NLS-1$
	private static final String ICONS_16 = "icons/16/"; //$NON-NLS-1$
	private static final String ICONS_20 = "icons/20/"; //$NON-NLS-1$

	public static final String PREFIX = ImageProvider.class.getPackage().getName() + "."; //$NON-NLS-1$

	public static final String IMG_16_START_EVENT = PREFIX + StartEvent.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_END_EVENT = PREFIX + EndEvent.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_TASK = PREFIX + Task.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_EXCLUSIVE_GATEWAY = PREFIX + ExclusiveGateway.class.getSimpleName().toLowerCase()
	        + dot16;
	public static final String IMG_16_SEQUENCE_FLOW = PREFIX + SequenceFlow.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_PARTICIPANT = PREFIX + Participant.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_LANE = PREFIX + Lane.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_TEXT_ANNOTATION = PREFIX + TextAnnotation.class.getSimpleName().toLowerCase()
	        + dot16;
	public static final String IMG_16_ASSOCIATION = PREFIX + Association.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_DATA_ASSOCIATION = PREFIX + DataAssociation.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_INCLUSIVE_GATEWAY = PREFIX + InclusiveGateway.class.getSimpleName().toLowerCase()
	        + dot16;
	public static final String IMG_16_PARALLEL_GATEWAY = PREFIX + ParallelGateway.class.getSimpleName().toLowerCase()
	        + dot16;
	public static final String IMG_16_EVENT_BASED_GATEWAY = PREFIX
	        + EventBasedGateway.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_MESSAGE_FLOW = PREFIX + MessageFlow.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_INTERMEDIATE_CATCH_EVENT = PREFIX
	        + IntermediateCatchEvent.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_INTERMEDIATE_THORW_EVENT = PREFIX
	        + IntermediateThrowEvent.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_COMPLEX_GATEWAY = PREFIX + ComplexGateway.class.getSimpleName().toLowerCase()
	        + dot16;
	public static final String IMG_16_USER_TASK = PREFIX + UserTask.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_MANUAL_TASK = PREFIX + ManualTask.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_SCRIPT_TASK = PREFIX + ScriptTask.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_BUSINESS_RULE_TASK = PREFIX
	        + BusinessRuleTask.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_SERVICE_TASK = PREFIX + ServiceTask.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_SEND_TASK = PREFIX + SendTask.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_RECEIVE_TASK = PREFIX + ReceiveTask.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_CONDITION = PREFIX
	        + ConditionalEventDefinition.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_TIMER = PREFIX + TimerEventDefinition.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_SIGNAL = PREFIX + SignalEventDefinition.class.getSimpleName().toLowerCase()
	        + dot16;
	public static final String IMG_16_MESSAGE = PREFIX + MessageEventDefinition.class.getSimpleName().toLowerCase()
	        + dot16;
	public static final String IMG_16_ESCAlATION = PREFIX
	        + EscalationEventDefinition.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_COMPENSATE = PREFIX
	        + CompensateEventDefinition.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_LINK = PREFIX + LinkEventDefinition.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_ERROR = PREFIX + ErrorEventDefinition.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_CANCEL = PREFIX + CancelEventDefinition.class.getSimpleName().toLowerCase()
	        + dot16;
	public static final String IMG_16_BOUNDARY_EVENT = PREFIX + BoundaryEvent.class.getSimpleName().toLowerCase()
	        + dot16;
	public static final String IMG_16_SUB_PROCESS = PREFIX + SubProcess.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_AD_HOC_SUB_PROCESS = PREFIX + AdHocSubProcess.class.getSimpleName().toLowerCase()
	        + dot16;
	public static final String IMG_16_TRANSACTION = PREFIX + Transaction.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_GROUP = PREFIX + Group.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_DATA_OBJECT = PREFIX + DataObject.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_TERMINATE = PREFIX + TerminateEventDefinition.class.getSimpleName().toLowerCase()
	        + dot16;
	public static final String IMG_16_DATA_STORE = PREFIX + DataStore.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_DATA_INPUT = PREFIX + DataInput.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_DATA_OUTPUT = PREFIX + DataOutput.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_CALL_ACTIVITY = PREFIX + CallActivity.class.getSimpleName().toLowerCase() + dot16;

	public static final String IMG_16_CALL_GLOBAL_TASK = PREFIX + "CallGlobalTask" + dot16;
	public static final String IMG_16_CALL_GLOBAL_BUSINESS_RULE_TASK = PREFIX + "CallGlobalBusinessRuleTask" + dot16;
	public static final String IMG_16_CALL_GLOBAL_MANUAL_TASK = PREFIX + "CallGlobalManualTask" + dot16;
	public static final String IMG_16_CALL_GLOBAL_SCRIPT_TASK= PREFIX + "CallGlobalScriptTask" + dot16;
	public static final String IMG_16_CALL_GLOBAL_USER_TASK = PREFIX + "CallGlobalUserTask" + dot16;
	
	public static final String IMG_16_CONVERSATION = PREFIX + Conversation.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_SUB_CONVERSATION = PREFIX + SubConversation.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_CALL_CONVERSATION = PREFIX + CallConversation.class.getSimpleName().toLowerCase() + dot16;

	
	public static final String IMG_16_CONVERSATION_LINK = PREFIX + ConversationLink.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_CHOREOGRAPHY_TASK = PREFIX + ChoreographyTask.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_SUB_CHOREOGRAPHY = PREFIX + SubChoreography.class.getSimpleName().toLowerCase() + dot16;
	public static final String IMG_16_CALL_CHOREOGRAPHY = PREFIX + CallChoreography.class.getSimpleName().toLowerCase() + dot16;

	public static final String IMG_20_CONDITION = PREFIX + ConditionalEventDefinition.class.getSimpleName().toLowerCase() + dot20;
	public static final String IMG_20_TIMER = PREFIX + TimerEventDefinition.class.getSimpleName().toLowerCase() + dot20;
	public static final String IMG_20_MULTIPLE = PREFIX + "multipleeventdefinition" + dot20; //$NON-NLS-1$

	public static final String IMG_16_ACTION = "default_action"; //$NON-NLS-1$
	
	public static final String IMG_16_EXPAND = PREFIX + "expand" + dot16; //$NON-NLS-1$
	public static final String IMG_16_COLLAPSE = PREFIX + "collapse" + dot16; //$NON-NLS-1$
	public static final String IMG_16_CONFIGURE = PREFIX + "configure" + dot16; //$NON-NLS-1$

	public static final String IMG_16_ADD_PARTICIPANT = PREFIX + "addparticipant" + dot16; //$NON-NLS-1$
	public static final String IMG_16_ADD_MESSAGE = PREFIX + "addmessage" + dot16; //$NON-NLS-1$
	public static final String IMG_16_REMOVE_MESSAGE = PREFIX + "removemessage" + dot16; //$NON-NLS-1$
	public static final String IMG_16_ROTATE = PREFIX + "rotate" + dot16; //$NON-NLS-1$
	public static final String IMG_16_WHITEBOX = PREFIX + "whitebox" + dot16; //$NON-NLS-1$
	public static final String IMG_16_BLACKBOX = PREFIX + "blackbox" + dot16; //$NON-NLS-1$
	public static final String IMG_16_PUSHDOWN = PREFIX + "pushdown" + dot16; //$NON-NLS-1$
	public static final String IMG_16_PULLUP = PREFIX + "pullup" + dot16; //$NON-NLS-1$

	public static final String IMG_16_PROPERTIES = "org.eclipse.bpmn2.modeler.icons." + "properties" + dot16; //$NON-NLS-1$ //$NON-NLS-2$
	public static final String IMG_16_INFO = "org.eclipse.bpmn2.modeler.icons." + "info" + dot16; //$NON-NLS-1$ //$NON-NLS-2$

	public static final String IMG_16_MORPH = PREFIX + "morph" + dot16; //$NON-NLS-1$

	@Override
	protected void addAvailableImages() {
		addImageFilePath(IMG_16_START_EVENT, ICONS_16 + "StartEvent.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_END_EVENT, ICONS_16 + "EndEvent.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_TASK, ICONS_16 + "Task.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_EXCLUSIVE_GATEWAY, ICONS_16 + "ExclusiveGateway.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_SEQUENCE_FLOW, ICONS_16 + "SequenceFlow.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_PARTICIPANT, ICONS_16 + "Participant.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_LANE, ICONS_16 + "Lane.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_TEXT_ANNOTATION, ICONS_16 + "TextAnnotation.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_ASSOCIATION, ICONS_16 + "Association.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_DATA_ASSOCIATION, ICONS_16 + "DataAssociation.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_INCLUSIVE_GATEWAY, ICONS_16 + "InclusiveGateway.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_PARALLEL_GATEWAY, ICONS_16 + "ParallelGateway.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_EVENT_BASED_GATEWAY, ICONS_16 + "EventBasedGateway.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_MESSAGE_FLOW, ICONS_16 + "MessageFlow.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_INTERMEDIATE_CATCH_EVENT, ICONS_16 + "IntermediateThrowEvent.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_INTERMEDIATE_THORW_EVENT, ICONS_16 + "IntermediateThrowEvent.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_COMPLEX_GATEWAY, ICONS_16 + "ComplexGateway.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_USER_TASK, ICONS_16 + "UserTask.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_MANUAL_TASK, ICONS_16 + "ManualTask.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_SCRIPT_TASK, ICONS_16 + "ScriptTask.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_BUSINESS_RULE_TASK, ICONS_16 + "BusinessRuleTask.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_SERVICE_TASK, ICONS_16 + "ServiceTask.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_SEND_TASK, ICONS_16 + "SendTask.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_RECEIVE_TASK, ICONS_16 + "ReceiveTask.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_CONDITION, ICONS_16 + "ConditionalEventDefinition.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_TIMER, ICONS_16 + "TimerEventDefinition.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_SIGNAL, ICONS_16 + "SignalEventDefinition.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_MESSAGE, ICONS_16 + "MessageEventDefinition.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_ESCAlATION, ICONS_16 + "EscalationEventDefinition.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_COMPENSATE, ICONS_16 + "CompensateEventDefinition.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_LINK, ICONS_16 + "LinkEventDefinition.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_ERROR, ICONS_16 + "ErrorEventDefinition.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_CANCEL, ICONS_16 + "CancelEventDefinition.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_BOUNDARY_EVENT, ICONS_16 + "BoundaryEvent.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_SUB_PROCESS, ICONS_16 + "SubProcess.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_AD_HOC_SUB_PROCESS, ICONS_16 + "AdHocSubProcess.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_TRANSACTION, ICONS_16 + "Transaction.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_GROUP, ICONS_16 + "Group.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_DATA_OBJECT, ICONS_16 + "DataObject.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_TERMINATE, ICONS_16 + "TerminateEventDefinition.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_DATA_STORE, ICONS_16 + "DataStore.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_DATA_INPUT, ICONS_16 + "DataInput.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_DATA_OUTPUT, ICONS_16 + "DataOutput.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_CALL_ACTIVITY, ICONS_16 + "CallActivity.png"); //$NON-NLS-1$
		
		addImageFilePath(IMG_16_CALL_GLOBAL_TASK, ICONS_16 + "CallGlobalTask.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_CALL_GLOBAL_BUSINESS_RULE_TASK, ICONS_16 + "CallGlobalBusinessRuleTask.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_CALL_GLOBAL_MANUAL_TASK, ICONS_16 + "CallGlobalManualTask.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_CALL_GLOBAL_SCRIPT_TASK, ICONS_16 + "CallGlobalScriptTask.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_CALL_GLOBAL_USER_TASK, ICONS_16 + "CallGlobalUserTask.png"); //$NON-NLS-1$

		addImageFilePath(IMG_16_CONVERSATION, ICONS_16 + "Conversation.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_SUB_CONVERSATION, ICONS_16 + "SubConversation.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_CALL_CONVERSATION, ICONS_16 + "CallConversation.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_CONVERSATION_LINK, ICONS_16 + "ConversationLink.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_CHOREOGRAPHY_TASK, ICONS_16 + "ChoreographyTask.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_SUB_CHOREOGRAPHY, ICONS_16 + "SubChoreography.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_CALL_CHOREOGRAPHY, ICONS_16 + "CallChoreography.png"); //$NON-NLS-1$

		addImageFilePath(IMG_20_CONDITION, ICONS_20 + "Condition.png"); //$NON-NLS-1$
		addImageFilePath(IMG_20_TIMER, ICONS_20 + "Timer.png"); //$NON-NLS-1$
		addImageFilePath(IMG_20_MULTIPLE, ICONS_20 + "Multiple.png"); //$NON-NLS-1$

		addImageFilePath(IMG_16_ACTION, ICONS_16 + "action.gif"); //$NON-NLS-1$
		addImageFilePath(IMG_16_EXPAND, ICONS_16 + "expand.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_COLLAPSE, ICONS_16 + "collapse.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_CONFIGURE, ICONS_16 + "configure.png"); //$NON-NLS-1$

		addImageFilePath(IMG_16_ADD_PARTICIPANT, ICONS_16 + "addparticipant.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_ADD_MESSAGE, ICONS_16 + "addmessage.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_REMOVE_MESSAGE, ICONS_16 + "removemessage.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_ROTATE, ICONS_16 + "rotate.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_PROPERTIES, ICONS_16 + "properties.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_INFO, ICONS_16 + "info.png"); //$NON-NLS-1$

		addImageFilePath(IMG_16_WHITEBOX, ICONS_16 + "whitebox.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_BLACKBOX, ICONS_16 + "blackbox.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_PUSHDOWN, ICONS_16 + "pushdown.png"); //$NON-NLS-1$
		addImageFilePath(IMG_16_PULLUP, ICONS_16 + "pullup.png"); //$NON-NLS-1$

		addImageFilePath(IMG_16_MORPH, ICONS_16 + "morph.png"); //$NON-NLS-1$
	}

}
