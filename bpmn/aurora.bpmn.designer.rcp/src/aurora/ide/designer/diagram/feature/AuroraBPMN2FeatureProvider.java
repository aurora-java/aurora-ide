package aurora.ide.designer.diagram.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.LifecycleEvent;
import org.eclipse.bpmn2.modeler.core.LifecycleEvent.EventType;
import org.eclipse.bpmn2.modeler.core.di.DIImport;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateConnectionFeature;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2CreateFeature;
import org.eclipse.bpmn2.modeler.core.features.CustomConnectionFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.CustomElementFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.CustomShapeFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.CustomShapeFeatureContainer.CreateCustomShapeFeature;
import org.eclipse.bpmn2.modeler.core.features.DefaultCopyBPMNElementFeature;
import org.eclipse.bpmn2.modeler.core.features.DefaultDeleteBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.features.DefaultPasteBPMNElementFeature;
import org.eclipse.bpmn2.modeler.core.features.DefaultRemoveBPMNShapeFeature;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.IConnectionFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.ICustomElementFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.IFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.IShapeFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.bendpoint.AddBendpointFeature;
import org.eclipse.bpmn2.modeler.core.features.bendpoint.MoveBendpointFeature;
import org.eclipse.bpmn2.modeler.core.features.bendpoint.RemoveBendpointFeature;
import org.eclipse.bpmn2.modeler.core.features.flow.AbstractCreateFlowFeature;
import org.eclipse.bpmn2.modeler.core.features.label.LabelFeatureContainer;
import org.eclipse.bpmn2.modeler.core.runtime.CustomTaskDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.FeatureContainerDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.ui.diagram.BPMN2FeatureProvider;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2Editor;
import org.eclipse.bpmn2.modeler.ui.features.choreography.ChoreographyMessageLinkFeatureContainer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddBendpointFeature;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICopyFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveBendpointFeature;
import org.eclipse.graphiti.features.IMoveConnectionDecoratorFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IPasteFeature;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IRemoveBendpointFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddBendpointContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveBendpointContext;
import org.eclipse.graphiti.features.context.IMoveConnectionDecoratorContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IRemoveBendpointContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

public class AuroraBPMN2FeatureProvider extends BPMN2FeatureProvider {

	private LinkedHashMap<Class,IFeatureContainer> containers;
	private LinkedHashMap<String,ICustomElementFeatureContainer> customTaskContainers;
	private ICreateFeature[] createFeatures;
	private ICreateConnectionFeature[] createConnectionFeatures;
	private HashMap<Class,IFeature> mapBusinessObjectClassToCreateFeature = new HashMap<Class,IFeature>();
	private DefaultCopyBPMNElementFeature defaultCopyFeature = new DefaultCopyBPMNElementFeature(this);
	private DefaultPasteBPMNElementFeature defaultPasteFeature = new DefaultPasteBPMNElementFeature(this);
	
	public AuroraBPMN2FeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp); 
		updateFeatureLists();
	}
	
	private void initializeFeatureContainers() {
		containers = new LinkedHashMap<Class,IFeatureContainer>();
		containers.put(Group.class,new AGroupFeatureContainer());
		containers.put(DataObject.class,new ADataObjectFeatureContainer());
		containers.put(DataObjectReference.class,new ADataObjectReferenceFeatureContainer());
		containers.put(DataStoreReference.class,new ADataStoreReferenceFeatureContainer());
		containers.put(DataInput.class,new ADataInputFeatureContainer());
		containers.put(DataOutput.class,new ADataOutputFeatureContainer());
		containers.put(Message.class,new AMessageFeatureContainer());
		containers.put(StartEvent.class,new AStartEventFeatureContainer());
		containers.put(EndEvent.class,new AEndEventFeatureContainer());
		containers.put(IntermediateCatchEvent.class,new AIntermediateCatchEventFeatureContainer());
		containers.put(IntermediateThrowEvent.class,new AIntermediateThrowEventFeatureContainer());
		containers.put(BoundaryEvent.class,new ABoundaryEventFeatureContainer());
		containers.put(Task.class,new ATaskFeatureContainer());
		containers.put(ScriptTask.class,new AScriptTaskFeatureContainer());
		containers.put(ServiceTask.class,new AServiceTaskFeatureContainer());
		containers.put(UserTask.class,new AUserTaskFeatureContainer());
		containers.put(ManualTask.class,new AManualTaskFeatureContainer());
		containers.put(BusinessRuleTask.class,new ABusinessRuleTaskFeatureContainer());
		containers.put(SendTask.class,new ASendTaskFeatureContainer());
		containers.put(ReceiveTask.class,new AReceiveTaskFeatureContainer());
		containers.put(ChoreographyTask.class,new AChoreographyTaskFeatureContainer());
		containers.put(ExclusiveGateway.class,new AExclusiveGatewayFeatureContainer());
		containers.put(InclusiveGateway.class,new AInclusiveGatewayFeatureContainer());
		containers.put(ParallelGateway.class,new AParallelGatewayFeatureContainer());
		containers.put(EventBasedGateway.class,new AEventBasedGatewayFeatureContainer());
		containers.put(ComplexGateway.class,new AComplexGatewayFeatureContainer());
		containers.put(AdHocSubProcess.class,new AAdHocSubProcessFeatureContainer());
		containers.put(CallActivity.class,new ACallActivityFeatureContainer());
		containers.put(GlobalTask.class,new ACallGlobalTaskFeatureContainer());
		containers.put(GlobalBusinessRuleTask.class,new ACallGlobalBusinessRuleTaskFeatureContainer());
		containers.put(GlobalManualTask.class,new ACallGlobalManualTaskFeatureContainer());
		containers.put(GlobalScriptTask.class,new ACallGlobalScriptTaskFeatureContainer());
		containers.put(GlobalUserTask.class,new ACallGlobalUserTaskFeatureContainer());
		containers.put(Transaction.class,new ATransactionFeatureContainer());
		containers.put(SubProcess.class,new ASubProcessFeatureContainer());
		containers.put(ConditionalEventDefinition.class,new AConditionalEventDefinitionContainer());
		containers.put(MessageEventDefinition.class,new AMessageEventDefinitionContainer());
		containers.put(TimerEventDefinition.class,new ATimerEventDefinitionContainer());
		containers.put(SignalEventDefinition.class,new ASignalEventDefinitionContainer());
		containers.put(EscalationEventDefinition.class,new AEscalationEventDefinitionContainer());
		containers.put(CompensateEventDefinition.class,new ACompensateEventDefinitionContainer());
		containers.put(LinkEventDefinition.class,new ALinkEventDefinitionContainer());
		containers.put(ErrorEventDefinition.class,new AErrorEventDefinitionContainer());
		containers.put(CancelEventDefinition.class,new ACancelEventDefinitionContainer());
		containers.put(TerminateEventDefinition.class,new ATerminateEventDefinitionFeatureContainer());
		containers.put(SequenceFlow.class,new ASequenceFlowFeatureContainer());
		containers.put(MessageFlow.class,new AMessageFlowFeatureContainer());
		containers.put(Association.class,new AAssociationFeatureContainer());
		containers.put(Conversation.class,new AConversationFeatureContainer());
		containers.put(SubConversation.class,new ASubConversationFeatureContainer());
		containers.put(CallConversation.class,new ACallConversationFeatureContainer());
		containers.put(ConversationLink.class,new AConversationLinkFeatureContainer());
		containers.put(DataAssociation.class,new ADataAssociationFeatureContainer());
		containers.put(SubChoreography.class,new ASubChoreographyFeatureContainer());
		containers.put(CallChoreography.class,new ACallChoreographyFeatureContainer());
		containers.put(Participant.class,new AParticipantFeatureContainer());
		containers.put(Lane.class,new ALaneFeatureContainer());
		containers.put(TextAnnotation.class,new ATextAnnotationFeatureContainer());
		containers.put(BPMNDiagram.class,new ABPMNDiagramFeatureContainer());
		// these have no BPMN2 element equivalents
		containers.put(ChoreographyMessageLinkFeatureContainer.class,new AChoreographyMessageLinkFeatureContainer());
		containers.put(LabelFeatureContainer.class,new ALabelFeatureContainer());
	}
	
	public void addFeatureContainer(String id, ICustomElementFeatureContainer fc) throws Exception {
		
		if (customTaskContainers==null) {
			customTaskContainers = new LinkedHashMap<String,ICustomElementFeatureContainer>();
		}
		customTaskContainers.put(id,fc);
		updateFeatureLists();
	}
	
	private void updateFeatureLists() {

		initializeFeatureContainers();
		
		// Collect all of the <featureContainerDelegate> extensions defined by the current TargetRuntime
		// and replace the ones in our list of FeatureContainers
		BPMN2Editor editor = BPMN2Editor.getActiveEditor(); //(BPMN2Editor)getDiagramTypeProvider().getDiagramEditor();;
		TargetRuntime rt = editor.getTargetRuntime();
		for (FeatureContainerDescriptor fcd : rt.getFeatureContainerDescriptors()) {
			IFeatureContainer fc = fcd.getFeatureContainer();
			if (fc instanceof IConnectionFeatureContainer) {
				ICreateConnectionFeature createConnectionFeature = ((IConnectionFeatureContainer)fc)
						.getCreateConnectionFeature(this);
				if (createConnectionFeature!=null) {
					containers.put(fcd.getType(), fc);
				}
			}
			if (fc instanceof IShapeFeatureContainer) {
				ICreateFeature createFeature = ((IShapeFeatureContainer)fc).getCreateFeature(this);
				if (createFeature != null) {
					containers.put(fcd.getType(), fc);
				}
			}
		}

		// build the list of CreateFeatures from our new list of all FeatureContainers
		List<ICreateFeature> createFeaturesList = new ArrayList<ICreateFeature>();
		List<ICreateConnectionFeature> createConnectionFeatureList = new ArrayList<ICreateConnectionFeature>();

		for (IFeatureContainer fc : containers.values()) {
			if (fc instanceof IConnectionFeatureContainer) {
				ICreateConnectionFeature createConnectionFeature = ((IConnectionFeatureContainer) fc)
						.getCreateConnectionFeature(this);
				if (createConnectionFeature != null) {
					createConnectionFeatureList.add(createConnectionFeature);
				}
			}
			if (fc instanceof IShapeFeatureContainer) {
				ICreateFeature createFeature = ((IShapeFeatureContainer) fc).getCreateFeature(this);
				if (createFeature != null) {
					createFeaturesList.add(createFeature);
				}
			}
		}
		if (customTaskContainers!=null) {
			for (IFeatureContainer fc : customTaskContainers.values()) {
				if (fc instanceof IConnectionFeatureContainer) {
					ICreateConnectionFeature createConnectionFeature = ((IConnectionFeatureContainer) fc)
							.getCreateConnectionFeature(this);
					if (createConnectionFeature != null) {
						createConnectionFeatureList.add(createConnectionFeature);
					}
				}
				if (fc instanceof IShapeFeatureContainer) {
					ICreateFeature createFeature = ((IShapeFeatureContainer) fc).getCreateFeature(this);
					if (createFeature != null) {
						createFeaturesList.add(createFeature);
					}
				}
			}
		}
		
		createFeatures = createFeaturesList.toArray(new ICreateFeature[createFeaturesList.size()]);
		createConnectionFeatures = createConnectionFeatureList
				.toArray(new ICreateConnectionFeature[createConnectionFeatureList.size()]);
		
		mapBusinessObjectClassToCreateFeature.clear();
		for (IFeature cf : createFeatures) {
			if (cf instanceof AbstractBpmn2CreateFeature) {
				if (cf instanceof CreateCustomShapeFeature) {
					continue;
				}
				AbstractBpmn2CreateFeature acf = (AbstractBpmn2CreateFeature)cf;
				mapBusinessObjectClassToCreateFeature.put(acf.getFeatureClass().getInstanceClass(), cf);
			}
		}
		for (IFeature cf : createConnectionFeatures) {
			if (cf instanceof AbstractCreateFlowFeature) {
				AbstractBpmn2CreateConnectionFeature acf = (AbstractBpmn2CreateConnectionFeature)cf;
				mapBusinessObjectClassToCreateFeature.put(acf.getFeatureClass().getInstanceClass(), cf);
			}
		}
	}
	
	private EObject getApplyObject(IContext context) {
		if (context instanceof IAddContext) {
			Object object = ((IAddContext) context).getNewObject();
			if (object instanceof EObject)
				return (EObject)object;
		} else if (context instanceof IPictogramElementContext) {
			return BusinessObjectUtil.getFirstElementOfType(
					(((IPictogramElementContext) context).getPictogramElement()), EObject.class);
		} else if (context instanceof IReconnectionContext) {
			return BusinessObjectUtil.getFirstElementOfType(
					(((IReconnectionContext) context).getConnection()), EObject.class);
		}
		return null;
	}

	public IFeatureContainer getFeatureContainer(Class clazz) {
		return containers.get(clazz);
	}

	public IFeatureContainer getFeatureContainer(IContext context) {
		
		// The special LabelFeatureContainer is used to add labels to figures that were
		// added within the given IContext
//		LabelFeatureContainer lfc = (LabelFeatureContainer) containers.get(LabelFeatureContainer.class);
//		if (lfc.getApplyObject(context)!=null)
//			return lfc;
		
		BPMN2Editor editor = (BPMN2Editor)getDiagramTypeProvider().getDiagramEditor();;
		TargetRuntime rt = editor.getTargetRuntime();
		EObject object = getApplyObject(context);
		if (object!=null) {
			FeatureContainerDescriptor fcd = rt.getFeatureContainer(object.eClass());
			if (fcd!=null)
				return fcd.getFeatureContainer();
		}
		
		Object id = CustomElementFeatureContainer.getId(context); 
		for (IFeatureContainer container : containers.values()) {
			if (id!=null && !(container instanceof ICustomElementFeatureContainer))
				continue;
			Object o = container.getApplyObject(context);
			if (o != null && container.canApplyTo(o)) {
				return container;
			}
		}
		if (id!=null) {
			for (CustomTaskDescriptor ct : rt.getCustomTaskDescriptors()) {
				if (id.equals(ct.getId())) {
					ICustomElementFeatureContainer container = (ICustomElementFeatureContainer)ct.getFeatureContainer();
					return container;
				}
			}
		}
		// last chance: check custom task feature containers
		if (customTaskContainers!=null) {
			if (id!=null) {
				IFeatureContainer container = customTaskContainers.get(id);
				if (container!=null && container.getApplyObject(context)!=null)
					return container;
			}
			else {
				for (IFeatureContainer container : customTaskContainers.values()) {
					if (container.getApplyObject(context)!=null)
						return container;
				}
			}
		}
		return null;
	}

	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		// only here do we need to search all of the Custom Task extensions to check if
		// the newObject (in AddContext) is a Custom Task. This is because of a chicken/egg
		// problem during DIImport: the Custom Task feature containers are not added to
		// the toolpalette until AFTER the file is loaded (in DIImport) and getAddFeature()
		// is called during file loading.
		Object id = CustomElementFeatureContainer.getId(context); 
		if (id!=null) {
			BPMN2Editor editor = (BPMN2Editor)getDiagramTypeProvider().getDiagramEditor();
			TargetRuntime rt = editor.getTargetRuntime();
			for (CustomTaskDescriptor ct : rt.getCustomTaskDescriptors()) {
				if (id.equals(ct.getId())) {
					ICustomElementFeatureContainer container = (ICustomElementFeatureContainer)ct.getFeatureContainer();
					return container.getAddFeature(this);
				}
			}
		}
		
		IFeatureContainer container = getFeatureContainer(context);
		if (container!=null) {
			IAddFeature feature = container.getAddFeature(this);
			if (feature != null)
				return feature;
		}
		return super.getAddFeature(context);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		return createFeatures;
	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		IFeatureContainer container = getFeatureContainer(context);
		if (container!=null) {
			IUpdateFeature feature = container.getUpdateFeature(this);
			if (feature != null)
				return feature;
		}
		return super.getUpdateFeature(context);
	}

	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
		return createConnectionFeatures;
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
		IFeatureContainer container = getFeatureContainer(context);
		if (container!=null) {
			IDirectEditingFeature feature = container.getDirectEditingFeature(this);
			if (feature != null)
				return feature;
		}
		return super.getDirectEditingFeature(context);
	}

	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
		IFeatureContainer container = getFeatureContainer(context);
		if (container!=null) {
			ILayoutFeature feature = container.getLayoutFeature(this);
			if (feature != null)
				return feature;
		}
		return super.getLayoutFeature(context);
	}

	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
		IFeatureContainer container = getFeatureContainer(context);
		if (container instanceof IShapeFeatureContainer) {
			IMoveShapeFeature feature = ((IShapeFeatureContainer)container).getMoveFeature(this);
			if (feature != null)
				return feature;
		}
		return super.getMoveShapeFeature(context);
	}

	@Override
	public IMoveConnectionDecoratorFeature getMoveConnectionDecoratorFeature(IMoveConnectionDecoratorContext context) {
		IFeatureContainer container = getFeatureContainer(context);
		if (container instanceof IConnectionFeatureContainer) {
			IMoveConnectionDecoratorFeature feature = ((IConnectionFeatureContainer)container).getMoveConnectionDecoratorFeature(this);
			if (feature != null)
				return feature;
		}
		return super.getMoveConnectionDecoratorFeature(context);
	}

	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
		IFeatureContainer container = getFeatureContainer(context);
		if (container instanceof IShapeFeatureContainer) {
			IResizeShapeFeature feature = ((IShapeFeatureContainer)container).getResizeFeature(this);
			if (feature != null)
				return feature;
		}
		return super.getResizeShapeFeature(context);
	}

	@Override
	public IAddBendpointFeature getAddBendpointFeature(IAddBendpointContext context) {
		return new AddBendpointFeature(this);
	}

	@Override
	public IMoveBendpointFeature getMoveBendpointFeature(IMoveBendpointContext context) {
		return new MoveBendpointFeature(this);
	}

	@Override
	public IRemoveBendpointFeature getRemoveBendpointFeature(IRemoveBendpointContext context) {
		return new RemoveBendpointFeature(this);
	}

	@Override
	public IReconnectionFeature getReconnectionFeature(IReconnectionContext context) {
		IFeatureContainer container = getFeatureContainer(context);
		if (container instanceof IConnectionFeatureContainer) {
			IReconnectionFeature feature = ((IConnectionFeatureContainer)container).getReconnectionFeature(this);
			if (feature != null)
				return feature;
		}
		return super.getReconnectionFeature(context);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		IFeatureContainer container = getFeatureContainer(context);
		if (container!=null) {
			IDeleteFeature feature = container.getDeleteFeature(this);
			if (feature != null)
				return feature;
		}
		return new DefaultDeleteBPMNShapeFeature(this);
	}

	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) {
		IFeatureContainer container = getFeatureContainer(context);
		if (container!=null) {
			IRemoveFeature feature = container.getRemoveFeature(this);
			if (feature != null)
				return feature;
		}
		return new DefaultRemoveBPMNShapeFeature(this);
	}


	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		List<ICustomFeature> list = new ArrayList<ICustomFeature>();

		BPMN2Editor editor = (BPMN2Editor)getDiagramTypeProvider().getDiagramEditor();;
		TargetRuntime rt = editor.getTargetRuntime();
		String id = CustomElementFeatureContainer.getId(context);
		if (id!=null) {
			for (CustomTaskDescriptor ct : rt.getCustomTaskDescriptors()) {
				ICustomElementFeatureContainer ctf = ct.getFeatureContainer();
				if (ctf!=null && id.equals(ctf.getId())) {
					ICustomFeature[] cfa = ctf.getCustomFeatures(this);
					if (cfa!=null) {
						for (ICustomFeature cf : cfa) {
							if (cf.isAvailable(context)) {
								boolean found = false;
								for (ICustomFeature cfl : list) {
									if (cfl.getClass() == cf.getClass()) {
										found = true;
										break;
									}
								}
								if (!found)
									list.add(cf);
							}
						}
					}
				}
			}
		}
		else {
			for (IFeatureContainer fc : containers.values()) {
				Object o = fc.getApplyObject(context);
				if (o!=null && fc.canApplyTo(o)) {
					ICustomFeature[] cfa = fc.getCustomFeatures(this);
					if (cfa!=null) {
						for (ICustomFeature cf : cfa) {
							boolean found = false;
							for (ICustomFeature cfl : list) {
								if (cfl.getClass() == cf.getClass()) {
									found = true;
									break;
								}
							}
							if (!found)
								list.add(cf);
						}
					}
				}
			}
		}
		
//		list.add(new ValidateModelFeature(this));
		return list.toArray(new ICustomFeature[list.size()]);
	}

	// TODO: move this to the adapter registry
	public IFeature getCreateFeatureForPictogramElement(PictogramElement pe) {
		if (pe!=null) {
			String id = Graphiti.getPeService().getPropertyValue(pe,GraphitiConstants.CUSTOM_ELEMENT_ID);
			if (id!=null) {
				for (IFeatureContainer container : containers.values()) {
					if (container instanceof ICustomElementFeatureContainer) {
						ICustomElementFeatureContainer ctf = (ICustomElementFeatureContainer)container;
						if (id.equals(ctf.getId())) {
							if (ctf instanceof CustomShapeFeatureContainer)
								return ((CustomShapeFeatureContainer)ctf).getCreateFeature(this);
							else if (ctf instanceof CustomConnectionFeatureContainer)
								return ((CustomConnectionFeatureContainer)ctf).getCreateConnectionFeature(this);
						}
					}
				}
			}

			EObject be = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			return getCreateFeatureForBusinessObject(be);
		}
		return null;
	}
	
	public IFeature getCreateFeatureForBusinessObject(Object be) {
		IFeature feature = null;
		if (be!=null) {
			Class[] ifs = be.getClass().getInterfaces();
			for (int i=0; i<ifs.length && feature==null; ++i) {
				feature = mapBusinessObjectClassToCreateFeature.get(ifs[i]);
			}
		}
		return feature;
	}
	
	public IFeature getCreateFeatureForBusinessObject(Class clazz) {
		return mapBusinessObjectClassToCreateFeature.get(clazz);
	}
	
	public void link(PictogramElement element, Object[] objects) {
		if (element.getLink()==null) {
			super.link(element, objects);
		}
		else {
			for (Object o : objects) {
				if (o instanceof EObject && !element.getLink().getBusinessObjects().contains(o)) {
					element.getLink().getBusinessObjects().add((EObject) o);
				}
			}
		}
	}

	@Override
	public ICopyFeature getCopyFeature(ICopyContext context) {
		// TODO: COPY-PASTE enable this once copy-paste functionality is working
		if (defaultCopyFeature.canCopy(context))
			return defaultCopyFeature;
		return null;
	}

	@Override
	public IPasteFeature getPasteFeature(IPasteContext context) {
		// TODO: COPY-PASTE enable this once copy-paste functionality is working
		if (defaultPasteFeature.canPaste(context))
			return defaultPasteFeature;
		return null;
	}

	@Override
	public IReason canAdd(IAddContext context) {
		IReason reason = super.canAdd(context);
		LifecycleEvent event = new LifecycleEvent(EventType.PICTOGRAMELEMENT_CAN_ADD, this, context, context.getNewObject());
		event.doit = reason.toBoolean();
		TargetRuntime.getCurrentRuntime().notify(event);
		if (event.doit != reason.toBoolean())
			reason = (event.doit ? Reason.createTrueReason() : Reason.createFalseReason());
		return reason;
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		IReason reason = super.updateNeeded(context);
		LifecycleEvent event = new LifecycleEvent(EventType.PICTOGRAMELEMENT_UPDATE_NEEDED, this, context, context.getPictogramElement());
		event.doit = reason.toBoolean();
		TargetRuntime.getCurrentRuntime().notify(event);
		if (event.doit != reason.toBoolean())
			reason = (event.doit ? Reason.createTrueReason() : Reason.createFalseReason());
		return reason;
	}

	@Override
	public IReason canUpdate(IUpdateContext context) {
		IReason reason = super.canUpdate(context);
		LifecycleEvent event = new LifecycleEvent(EventType.PICTOGRAMELEMENT_CAN_UPDATE, this, context, context.getPictogramElement());
		event.doit = reason.toBoolean();
		TargetRuntime.getCurrentRuntime().notify(event);
		if (event.doit != reason.toBoolean())
			reason = (event.doit ? Reason.createTrueReason() : Reason.createFalseReason());
		return reason;
	}

	@Override
	public IReason canLayout(ILayoutContext context) {
		IReason reason = super.canLayout(context);
		LifecycleEvent event = new LifecycleEvent(EventType.PICTOGRAMELEMENT_CAN_LAYOUT, this, context, context.getPictogramElement());
		event.doit = reason.toBoolean();
		TargetRuntime.getCurrentRuntime().notify(event);
		if (event.doit != reason.toBoolean())
			reason = (event.doit ? Reason.createTrueReason() : Reason.createFalseReason());
		return reason;
	}
	
	@Override
	public PictogramElement addIfPossible(IAddContext context) {
		IAddFeature addElementFeature = getAddFeature(context);
		PictogramElement pe = super.addIfPossible(context);
		if (pe!=null) {
			PictogramElement le = null;
			if (addElementFeature instanceof AbstractBpmn2AddFeature) {
				context.putProperty(GraphitiConstants.PICTOGRAM_ELEMENT, pe);
				IAddFeature addLabelFeature = ((AbstractBpmn2AddFeature)addElementFeature).getAddLabelFeature(this);
				if (addLabelFeature!=null && addLabelFeature.canAdd(context)) {
					le = addLabelFeature.add(context);
				}
			}
			
			TargetRuntime rt = TargetRuntime.getCurrentRuntime();
			List<PictogramElement> pes = new ArrayList<PictogramElement>();
			rt.notify(new LifecycleEvent(EventType.PICTOGRAMELEMENT_ADDED, this, context, pe));
			
			if (le!=null) {
				((AbstractBpmn2AddFeature)addElementFeature).updatePictogramElement(context, pe);
				if (!DIImport.isImporting(context))
					((AbstractBpmn2AddFeature)addElementFeature).layoutPictogramElement(context, pe);
			}
		}		
		return pe;
	}
}


