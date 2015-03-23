package aurora.ide.designer.diagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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
import org.eclipse.bpmn2.DataObjectReference;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataStoreReference;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.EventBasedGateway;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.GlobalBusinessRuleTask;
import org.eclipse.bpmn2.GlobalManualTask;
import org.eclipse.bpmn2.GlobalScriptTask;
import org.eclipse.bpmn2.GlobalTask;
import org.eclipse.bpmn2.GlobalUserTask;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.Message;
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

public class AuroraBpmn2FeatureMap {

	public static final List<Class> CONNECTIONS;
	public static final List<Class> EVENT_DEFINITIONS;
	public static final List<Class> EVENTS;
	public static final List<Class> GATEWAYS;
	public static final List<Class> TASKS;
	public static final List<Class> GLOBAL_TASKS;
	public static final List<Class> SUBPROCESS;
	public static final List<Class> CHOREOGRAPHY;
	public static final List<Class> CONVERSATION;
	public static final List<Class> DATA;
	public static final List<Class> ALL_DATA;
	public static final List<Class> ARTIFACTS;
	public static final List<Class> SWIMLANES;
	public static HashSet<Class> ALL_SHAPES;
	
	static {
		ArrayList<Class> features = new ArrayList<Class>();
		features.add(SequenceFlow.class);
//		features.add(MessageFlow.class);
//		features.add(Association.class);
//		features.add(DataAssociation.class);
//		features.add(ConversationLink.class);
		CONNECTIONS = Collections.unmodifiableList(features);

		features = new ArrayList<Class>();
//		features.add(ConditionalEventDefinition.class);
		features.add(TimerEventDefinition.class);
		features.add(SignalEventDefinition.class);
		features.add(MessageEventDefinition.class);
//		features.add(EscalationEventDefinition.class);
//		features.add(CompensateEventDefinition.class);
//		features.add(LinkEventDefinition.class);
		features.add(ErrorEventDefinition.class);
//		features.add(CancelEventDefinition.class);
		features.add(TerminateEventDefinition.class);
		EVENT_DEFINITIONS = Collections.unmodifiableList(features);

		features = new ArrayList<Class>();
		features.add(StartEvent.class);
		features.add(EndEvent.class);
//		features.add(IntermediateThrowEvent.class);
//		features.add(IntermediateCatchEvent.class);
//		features.add(BoundaryEvent.class);
		EVENTS = Collections.unmodifiableList(features);

		features = new ArrayList<Class>();
		features.add(InclusiveGateway.class);
		features.add(ExclusiveGateway.class);
		features.add(ParallelGateway.class);
		features.add(EventBasedGateway.class);
		features.add(ComplexGateway.class);

		GATEWAYS = Collections.unmodifiableList(features);

		features = new ArrayList<Class>();
//		features.add(Task.class);
		features.add(ManualTask.class);
		features.add(UserTask.class);
		features.add(ScriptTask.class);
		features.add(BusinessRuleTask.class);
		features.add(ServiceTask.class);
		features.add(SendTask.class);
		features.add(ReceiveTask.class);
		TASKS = Collections.unmodifiableList(features);

		features = new ArrayList<Class>();
		features.add(CallActivity.class);
//		features.add(GlobalTask.class);
//		features.add(GlobalBusinessRuleTask.class);
//		features.add(GlobalManualTask.class);
//		features.add(GlobalScriptTask.class);
//		features.add(GlobalUserTask.class);
		GLOBAL_TASKS = Collections.unmodifiableList(features);

		features = new ArrayList<Class>();
//		features.add(AdHocSubProcess.class);
		features.add(SubProcess.class);
//		features.add(Transaction.class);
		SUBPROCESS = Collections.unmodifiableList(features);

		// this is used by the tool palette
		// it does NOT include DataObjectReference because
		// that object needs to be handled by the {@link AddDataObjectFeature}
		features = new ArrayList<Class>();
		features.add(DataObject.class);
//		features.add(DataObjectReference.class);
		features.add(DataStoreReference.class);
		features.add(DataInput.class);
		features.add(DataOutput.class);
		features.add(Message.class);
		DATA = Collections.unmodifiableList(features);

		// and this is used by the Appearance Preference Page
		// this DOES include DataObjectReference because
		// we MAY want to be able to change the appearance of
		// these things independently of DataObjects 
		features = new ArrayList<Class>();
		features.add(DataObject.class);
		features.add(DataObjectReference.class);
		features.add(DataStoreReference.class);
		features.add(DataInput.class);
		features.add(DataOutput.class);
		features.add(Message.class);
		ALL_DATA = Collections.unmodifiableList(features);

		features = new ArrayList<Class>();
		features.add(ChoreographyTask.class);
		features.add(SubChoreography.class);
		features.add(CallChoreography.class);
		CHOREOGRAPHY = Collections.unmodifiableList(features);

		features = new ArrayList<Class>();
		features.add(Conversation.class);
		features.add(SubConversation.class);
		features.add(CallConversation.class);
		CONVERSATION = Collections.unmodifiableList(features);

		features = new ArrayList<Class>();
		features.add(Lane.class);
		features.add(Participant.class);
		SWIMLANES = Collections.unmodifiableList(features);

		features = new ArrayList<Class>();
		features.add(TextAnnotation.class);
		features.add(Group.class);
		ARTIFACTS = Collections.unmodifiableList(features);

		ALL_SHAPES = new HashSet<Class>();
		ALL_SHAPES.addAll(CONNECTIONS);
		ALL_SHAPES.addAll(EVENTS);
		ALL_SHAPES.addAll(GATEWAYS);
		ALL_SHAPES.addAll(TASKS);
		ALL_SHAPES.addAll(GLOBAL_TASKS);
		ALL_SHAPES.addAll(SUBPROCESS);
		ALL_SHAPES.addAll(CHOREOGRAPHY);
		ALL_SHAPES.addAll(CONVERSATION);
		// By default we want to ignore DataInput and DataOutput from Shapes
		// because they are created in Activity inputs and outputs as well
		// as in Process-level diagrams. The extension plugin can override
		// this behavior with its own {@link IObjectDecorator} implementation.
		// {@see DefaultObjectDecorator} for details.
		ALL_SHAPES.add(DataObject.class);
		ALL_SHAPES.add(DataStoreReference.class);
		ALL_SHAPES.add(Message.class);
		ALL_SHAPES.addAll(ARTIFACTS);
		ALL_SHAPES.addAll(SWIMLANES);
	}

}
