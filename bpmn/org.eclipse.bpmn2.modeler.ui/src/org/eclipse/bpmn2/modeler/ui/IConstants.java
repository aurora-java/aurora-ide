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

/**
 * @author Bob Brodt
 *
 */
public interface IConstants {

	public final String ICON_PATH = "icons/"; //$NON-NLS-1$

	public final String ICON_MESSAGE_16 = "obj16/message.gif"; //$NON-NLS-1$
	public final String ICON_MESSAGE_32 = "obj20/message.png"; //$NON-NLS-1$
	public final String ICON_OPERATION_16 = "obj16/operation.gif"; //$NON-NLS-1$
	public final String ICON_PART_16 = "obj16/message_part.gif";  //$NON-NLS-1$
	public final String ICON_PART_32 = "obj20/message_part.png"; //$NON-NLS-1$
	public final String ICON_PORTTYPE_16 = "obj16/wsdl_port_type.gif";  //$NON-NLS-1$
	public final String ICON_PORTTYPE_32 = "obj20/wsdl_port_type.png"; //$NON-NLS-1$
	public final String ICON_INPUT_16 = "obj16/input.gif"; //$NON-NLS-1$
	public final String ICON_INPUT_32 = "obj20/input.png"; //$NON-NLS-1$
	public final String ICON_OUTPUT_16 = "obj16/output.gif"; //$NON-NLS-1$
	public final String ICON_OUTPUT_32 = "obj20/output.png"; //$NON-NLS-1$
	public final String ICON_THROW_16 = "obj16/throw.gif"; //$NON-NLS-1$
	public final String ICON_THROW_32 = "obj20/throw.png"; //$NON-NLS-1$
	public final String ICON_CHECKBOX_CHECKED_16 = "obj16/checkbox-checked.png"; //$NON-NLS-1$
	public final String ICON_CHECKBOX_UNCHECKED_16 = "obj16/checkbox-unchecked.png"; //$NON-NLS-1$

	public final String ICON_XSD_ATTRIBUTE_DECLARATION_16 = "obj16/XSDAttributeDeclaration.gif"; //$NON-NLS-1$
	public final String ICON_XSD_ELEMENT_DECLARATION_16 = "obj16/XSDElementDeclaration.gif"; //$NON-NLS-1$
	public final String ICON_XSD_COMPLEX_TYPE_DEFINITION_16 = "obj16/XSDComplexTypeDefinition.gif"; //$NON-NLS-1$
	public final String ICON_XSD_SIMPLE_TYPE_DEFINITION_16 = "obj16/XSDSimpleTypeDefinition.gif"; //$NON-NLS-1$
	
	public final String IMAGE_PROCESS = "wizards/process.gif"; //$NON-NLS-1$
	public final String IMAGE_COLLABORATION = "wizards/collaboration.gif"; //$NON-NLS-1$
	public final String IMAGE_CHOREOGRAPHY = "wizards/choreography.gif"; //$NON-NLS-1$
	public final String IMAGE_PROCESS_PUSHED = "wizards/process-pushed.gif"; //$NON-NLS-1$
	public final String IMAGE_COLLABORATION_PUSHED = "wizards/collaboration-pushed.gif"; //$NON-NLS-1$
	public final String IMAGE_CHOREOGRAPHY_PUSHED = "wizards/choreography-pushed.gif"; //$NON-NLS-1$

	public final String ICON_BPMN2_PROCESS_16 = "obj16/bpmn2process.png"; //$NON-NLS-1$
	public final String ICON_BPMN2_INTERFACE_16 = "obj16/bpmn2interface.png"; //$NON-NLS-1$
	public final String ICON_BPMN2_OPERATION_16 = "obj16/bpmn2operation.png"; //$NON-NLS-1$
	public final String ICON_BPMN2_INPUT_16 = "obj16/bpmn2input.png"; //$NON-NLS-1$
	public final String ICON_BPMN2_OUTPUT_16 = "obj16/bpmn2output.png"; //$NON-NLS-1$
	public final String ICON_BPMN2_ERROR_16 = "obj16/bpmn2error.png"; //$NON-NLS-1$

	public final String ICON_JAVA_CLASS_16 = "obj16/javaClass.png"; //$NON-NLS-1$
	public final String ICON_JAVA_INTERFACE_16 = "obj16/javaInterface.png"; //$NON-NLS-1$
	public final String ICON_JAVA_PUBLIC_METHOD_16 = "obj16/javaPublicMethod.png"; //$NON-NLS-1$
	public final String ICON_JAVA_PUBLIC_FIELD_16 = "obj16/javaPublicField.png"; //$NON-NLS-1$
	
	public final String ICON_ADHOCSUBPROCESS = "16/AdHocSubProcess.png"; //$NON-NLS-1$
	public final String ICON_ASSOCIATION = "16/Association.png"; //$NON-NLS-1$
	public final String ICON_BOUNDARYEVENT = "16/BoundaryEvent.png"; //$NON-NLS-1$
	public final String ICON_BUSINESSRULETASK = "16/BusinessRuleTask.png"; //$NON-NLS-1$
	public final String ICON_CALLACTIVITY = "16/CallActivity.png"; //$NON-NLS-1$
	public final String ICON_CALLCHOREOGRAPHY = "16/CallChoreography.png"; //$NON-NLS-1$
	public final String ICON_CANCEL = "16/Cancel.png"; //$NON-NLS-1$
	public final String ICON_CANCELEVENTDEFINITION = "16/CancelEventDefinition.png"; //$NON-NLS-1$
	public final String ICON_CATEGORY = "16/Category.png"; //$NON-NLS-1$
	public final String ICON_CHOREOGRAPHYTASK = "16/ChoreographyTask.png"; //$NON-NLS-1$
	public final String ICON_COMPENSATEEVENTDEFINITION = "16/CompensateEventDefinition.png"; //$NON-NLS-1$
	public final String ICON_COMPLEXGATEWAY = "16/ComplexGateway.png"; //$NON-NLS-1$
	public final String ICON_CONDITIONALEVENTDEFINITION = "16/ConditionalEventDefinition.png"; //$NON-NLS-1$
	public final String ICON_CONVERSATION = "16/Conversation.png"; //$NON-NLS-1$
	public final String ICON_CONVERSATIONLINK = "16/ConversationLink.png"; //$NON-NLS-1$
	public final String ICON_CUSTOMTASK = "16/CustomTask.png"; //$NON-NLS-1$
	public final String ICON_DATAASSOCIATION = "16/DataAssociation.png"; //$NON-NLS-1$
	public final String ICON_DATAINPUTASSOCIATION = "16/DataInputAssociation.png"; //$NON-NLS-1$
	public final String ICON_DATAOUTPUTASSOCIATION = "16/DataOutputAssociation.png"; //$NON-NLS-1$
	public final String ICON_DATAINPUT = "16/DataInput.png"; //$NON-NLS-1$
	public final String ICON_DATAOBJECT = "16/DataObject.png"; //$NON-NLS-1$
	public final String ICON_DATAOBJECTREFERENCE = "16/DataObjectReference.png"; //$NON-NLS-1$
	public final String ICON_DATAOUTPUT = "16/DataOutput.png"; //$NON-NLS-1$
	public final String ICON_DATASTORE = "16/DataStore.png"; //$NON-NLS-1$
	public final String ICON_DATASTOREREFERENCE = "16/DataStoreReference.png"; //$NON-NLS-1$
	public final String ICON_ENDEVENT = "16/EndEvent.png"; //$NON-NLS-1$
	public final String ICON_ERROR = "16/Error.png"; //$NON-NLS-1$
	public final String ICON_ERROREVENTDEFINITION = "16/ErrorEventDefinition.png"; //$NON-NLS-1$
	public final String ICON_ESCALATION = "16/Escalation.png"; //$NON-NLS-1$
	public final String ICON_ESCALATIONEVENTDEFINITION = "16/EscalationEventDefinition.png"; //$NON-NLS-1$
	public final String ICON_EVENTBASEDGATEWAY = "16/EventBasedGateway.png"; //$NON-NLS-1$
	public final String ICON_EXCLUSIVEGATEWAY = "16/ExclusiveGateway.png"; //$NON-NLS-1$
	public final String ICON_GLOBALBUSINESSRULETASK = "16/GlobalBusinessRuleTask.png"; //$NON-NLS-1$
	public final String ICON_GLOBALMANUALTASK = "16/GlobalManualTask.png"; //$NON-NLS-1$
	public final String ICON_GLOBALSCRIPTTASK = "16/GlobalScriptTask.png"; //$NON-NLS-1$
	public final String ICON_GLOBALUSERTASK = "16/GlobalUserTask.png"; //$NON-NLS-1$
	public final String ICON_GROUP = "16/Group.png"; //$NON-NLS-1$
	public final String ICON_INCLUSIVEGATEWAY = "16/InclusiveGateway.png"; //$NON-NLS-1$
	public final String ICON_INTERMEDIATECATCHEVENT = "16/IntermediateCatchEvent.png"; //$NON-NLS-1$
	public final String ICON_INTERMEDIATETHROWEVENT = "16/IntermediateThrowEvent.png"; //$NON-NLS-1$
	public final String ICON_ITEMDEFINITION = "16/ItemDefinition.png"; //$NON-NLS-1$
	public final String ICON_LANE = "16/Lane.png"; //$NON-NLS-1$
	public final String ICON_LINKEVENTDEFINITION = "16/LinkEventDefinition.png"; //$NON-NLS-1$
	public final String ICON_MANUALTASK = "16/ManualTask.png"; //$NON-NLS-1$
	public final String ICON_MESSAGE = "16/Message.png"; //$NON-NLS-1$
	public final String ICON_INMESSAGE = "16/InMessage.png"; //$NON-NLS-1$
	public final String ICON_OUTMESSAGE = "16/OutMessage.png"; //$NON-NLS-1$
	public final String ICON_MESSAGEEVENTDEFINITION = "16/MessageEventDefinition.png"; //$NON-NLS-1$
	public final String ICON_MESSAGEFLOW = "16/MessageFlow.png"; //$NON-NLS-1$
	public final String ICON_OPERATION = "16/Operation.png"; //$NON-NLS-1$
	public final String ICON_PARALLELGATEWAY = "16/ParallelGateway.png"; //$NON-NLS-1$
	public final String ICON_PARTICIPANT = "16/Participant.png"; //$NON-NLS-1$
	public final String ICON_RECEIVETASK = "16/ReceiveTask.png"; //$NON-NLS-1$
	public final String ICON_SCRIPTTASK = "16/ScriptTask.png"; //$NON-NLS-1$
	public final String ICON_SENDTASK = "16/SendTask.png"; //$NON-NLS-1$
	public final String ICON_SEQUENCEFLOW = "16/SequenceFlow.png"; //$NON-NLS-1$
	public final String ICON_SERVICETASK = "16/ServiceTask.png"; //$NON-NLS-1$
	public final String ICON_SIGNAL = "16/Signal.png"; //$NON-NLS-1$
	public final String ICON_SIGNALEVENTDEFINITION = "16/SignalEventDefinition.png"; //$NON-NLS-1$
	public final String ICON_STARTEVENT = "16/StartEvent.png"; //$NON-NLS-1$
	public final String ICON_SUBCHOREOGRAPHY = "16/SubChoreography.png"; //$NON-NLS-1$
	public final String ICON_SUBPROCESS = "16/SubProcess.png"; //$NON-NLS-1$
	public final String ICON_TASK = "16/Task.png"; //$NON-NLS-1$
	public final String ICON_TERMINATEEVENTDEFINITION = "16/TerminateEventDefinition.png"; //$NON-NLS-1$
	public final String ICON_TEXTANNOTATION = "16/TextAnnotation.png"; //$NON-NLS-1$
	public final String ICON_TIMEREVENTDEFINITION = "16/TimerEventDefinition.png"; //$NON-NLS-1$
	public final String ICON_TRANSACTION = "16/Transaction.png"; //$NON-NLS-1$
	public final String ICON_USERTASK = "16/UserTask.png"; //$NON-NLS-1$
	public final String ICON_BPMNSHAPE = "16/BPMNShape.png"; //$NON-NLS-1$
	public final String ICON_BPMNEDGE = "16/BPMNEdge.png"; //$NON-NLS-1$
	public final String ICON_BPMNLABEL = "16/BPMNLabel.png"; //$NON-NLS-1$
	public final String ICON_BPMNDIAGRAM = "16/BPMNDiagram.png"; //$NON-NLS-1$
	public final String ICON_BPMNSUBDIAGRAM = "16/BPMNSubDiagram.png"; //$NON-NLS-1$
	public final String ICON_PROCESS = "16/Process.png"; //$NON-NLS-1$
	public final String ICON_RESOURCE = "16/Resource.png"; //$NON-NLS-1$
	public final String ICON_COLLABORATION = "16/Collaboration.png"; //$NON-NLS-1$
	public final String ICON_CHOREOGRAPHY = "16/Choreography.png"; //$NON-NLS-1$
	public final String ICON_INTERFACE = "16/Interface.png"; //$NON-NLS-1$
	public final String ICON_PARTNERROLE = "16/PartnerRole.png"; //$NON-NLS-1$
	public final String ICON_PARTNERENTITY = "16/PartnerEntity.png"; //$NON-NLS-1$

	public final String ICON_BUSINESS_MODEL = "20/BusinessModel.png"; //$NON-NLS-1$
	public final String ICON_INTERCHANGE_MODEL = "20/InterchangeModel.png"; //$NON-NLS-1$
	public final String ICON_THUMBNAIL = "20/Thumbnail.png"; //$NON-NLS-1$
	
	public final String ICON_DISABLED = "16/disabled.png"; //$NON-NLS-1$
	public final String ICON_FOLDER = "16/folder.png"; //$NON-NLS-1$
	public final String ICON_FOLDER_DISABLED = "16/folder_disabled.png"; //$NON-NLS-1$
}
