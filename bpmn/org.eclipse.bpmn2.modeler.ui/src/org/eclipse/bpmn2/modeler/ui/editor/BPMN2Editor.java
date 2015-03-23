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
package org.eclipse.bpmn2.modeler.ui.editor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CallChoreography;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.Category;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.CorrelationPropertyRetrievalExpression;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataObjectReference;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataStore;
import org.eclipse.bpmn2.DataStoreReference;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.Expression;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.GlobalBusinessRuleTask;
import org.eclipse.bpmn2.GlobalManualTask;
import org.eclipse.bpmn2.GlobalScriptTask;
import org.eclipse.bpmn2.GlobalTask;
import org.eclipse.bpmn2.GlobalUserTask;
import org.eclipse.bpmn2.HumanPerformer;
import org.eclipse.bpmn2.Import;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Performer;
import org.eclipse.bpmn2.PotentialOwner;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.ResourceAssignmentExpression;
import org.eclipse.bpmn2.ResourceParameterBinding;
import org.eclipse.bpmn2.ResourceRole;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StandardLoopCharacteristics;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TextAnnotation;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.LifecycleEvent;
import org.eclipse.bpmn2.modeler.core.LifecycleEvent.EventType;
import org.eclipse.bpmn2.modeler.core.builder.BPMN2Builder;
import org.eclipse.bpmn2.modeler.core.di.DIImport;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.Bpmn2TabbedPropertySheetPage;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultDetailComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultDialogComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.DefaultListComposite;
import org.eclipse.bpmn2.modeler.core.merrimac.clad.PropertiesCompositeFactory;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerResourceImpl;
import org.eclipse.bpmn2.modeler.core.model.ModelHandler;
import org.eclipse.bpmn2.modeler.core.model.ModelHandlerLocator;
import org.eclipse.bpmn2.modeler.core.model.ProxyURIConverterImplExtension;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.preferences.ModelEnablements;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.runtime.ToolPaletteDescriptor;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.DiagramEditorAdapter;
import org.eclipse.bpmn2.modeler.core.utils.ErrorUtils;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil.Bpmn2DiagramType;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.bpmn2.modeler.core.validation.BPMN2ProjectValidator;
import org.eclipse.bpmn2.modeler.core.validation.BPMN2ValidationStatusLoader;
import org.eclipse.bpmn2.modeler.help.IHelpContexts;
import org.eclipse.bpmn2.modeler.ui.Activator;
import org.eclipse.bpmn2.modeler.ui.Bpmn2DiagramEditorInput;
import org.eclipse.bpmn2.modeler.ui.diagram.Bpmn2ToolBehaviorProvider;
import org.eclipse.bpmn2.modeler.ui.property.PropertyTabDescriptorProvider;
import org.eclipse.bpmn2.modeler.ui.property.artifact.CategoryDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.artifact.TextAnnotationDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.connectors.MessageFlowDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.connectors.SequenceFlowDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.data.DataAssignmentDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.data.DataObjectPropertySection.DataObjectDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.data.DataObjectReferencePropertySection.DataObjectReferenceDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.data.DataStorePropertySection.DataStoreDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.data.DataStoreReferencePropertySection.DataStoreReferenceDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.data.ExpressionDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.data.InterfaceDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.data.ItemAwareElementDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.data.MessageDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.data.MessageListComposite;
import org.eclipse.bpmn2.modeler.ui.property.data.OperationDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.data.ResourceAssignmentExpressionDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.data.ResourceParameterBindingDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.data.ResourceRoleDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.diagrams.CorrelationPropertyREListComposite;
import org.eclipse.bpmn2.modeler.ui.property.diagrams.DefinitionsPropertyComposite.ImportDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.diagrams.ItemDefinitionDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.diagrams.ItemDefinitionListComposite;
import org.eclipse.bpmn2.modeler.ui.property.diagrams.ProcessDiagramDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.diagrams.PropertyListComposite;
import org.eclipse.bpmn2.modeler.ui.property.diagrams.ResourceRoleListComposite;
import org.eclipse.bpmn2.modeler.ui.property.events.BoundaryEventDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.events.CatchEventDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.events.CommonEventDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.events.CommonEventPropertySection.EventDefinitionDialogComposite;
import org.eclipse.bpmn2.modeler.ui.property.events.ConditionalEventDefinitionDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.events.EndEventDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.events.StartEventDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.events.ThrowEventDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.events.TimerEventDefinitionDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.gateways.GatewayDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.tasks.ActivityDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.tasks.ActivityInputDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.tasks.ActivityOutputDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.tasks.DataAssociationDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.tasks.IoParametersDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.tasks.ManualTaskDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.tasks.MultiInstanceLoopCharacteristicsDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.tasks.ScriptTaskDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.tasks.StandardLoopCharacteristicsDetailComposite;
import org.eclipse.bpmn2.modeler.ui.property.tasks.TaskDetailComposite;
import org.eclipse.bpmn2.modeler.ui.views.outline.BPMN2EditorOutlinePage;
import org.eclipse.bpmn2.modeler.ui.views.outline.BPMN2EditorSelectionSynchronizer;
import org.eclipse.bpmn2.modeler.ui.wizards.BPMN2DiagramCreator;
import org.eclipse.bpmn2.modeler.ui.wizards.FileService;
import org.eclipse.bpmn2.util.Bpmn2ResourceImpl;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain.Lifecycle;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.internal.editor.GFPaletteRoot;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptorProvider;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

@SuppressWarnings("restriction")
public class BPMN2Editor extends DiagramEditor implements IPreferenceChangeListener, IGotoMarker {
	
	static {
		TargetRuntime.createTargetRuntimes();
		PropertiesCompositeFactory.register(EObject.class, DefaultDetailComposite.class);
		PropertiesCompositeFactory.register(EObject.class, DefaultListComposite.class);
		PropertiesCompositeFactory.register(EObject.class, DefaultDialogComposite.class);
		PropertiesCompositeFactory.register(Message.class, MessageDetailComposite.class);
		PropertiesCompositeFactory.register(Message.class, MessageListComposite.class);
		PropertiesCompositeFactory.register(MessageFlow.class, MessageFlowDetailComposite.class);
		PropertiesCompositeFactory.register(Property.class, ItemAwareElementDetailComposite.class);
		PropertiesCompositeFactory.register(CallActivity.class, ActivityDetailComposite.class);
		PropertiesCompositeFactory.register(GlobalTask.class, ActivityDetailComposite.class);
		PropertiesCompositeFactory.register(GlobalBusinessRuleTask.class, ActivityDetailComposite.class);
		PropertiesCompositeFactory.register(GlobalManualTask.class, ActivityDetailComposite.class);
		PropertiesCompositeFactory.register(GlobalScriptTask.class, ActivityDetailComposite.class);
		PropertiesCompositeFactory.register(GlobalUserTask.class, ActivityDetailComposite.class);
		PropertiesCompositeFactory.register(Import.class, ImportDetailComposite.class);
		PropertiesCompositeFactory.register(Category.class, CategoryDetailComposite.class);
		PropertiesCompositeFactory.register(TextAnnotation.class, TextAnnotationDetailComposite.class);
		PropertiesCompositeFactory.register(SequenceFlow.class, SequenceFlowDetailComposite.class);
		PropertiesCompositeFactory.register(DataObject.class, DataObjectDetailComposite.class);
		PropertiesCompositeFactory.register(DataObjectReference.class, DataObjectDetailComposite.class);
		PropertiesCompositeFactory.register(Assignment.class, DataAssignmentDetailComposite.class);
		PropertiesCompositeFactory.register(Expression.class, ExpressionDetailComposite.class);
		PropertiesCompositeFactory.register(FormalExpression.class, ExpressionDetailComposite.class);
		PropertiesCompositeFactory.register(ResourceAssignmentExpression.class, ResourceAssignmentExpressionDetailComposite.class);
		PropertiesCompositeFactory.register(ResourceParameterBinding.class, ResourceParameterBindingDetailComposite.class);
		PropertiesCompositeFactory.register(PotentialOwner.class, ResourceRoleDetailComposite.class);
		PropertiesCompositeFactory.register(HumanPerformer.class, ResourceRoleDetailComposite.class);
		PropertiesCompositeFactory.register(Performer.class, ResourceRoleDetailComposite.class);
		PropertiesCompositeFactory.register(DataObjectReference.class, DataObjectReferenceDetailComposite.class);
		PropertiesCompositeFactory.register(DataStore.class, DataStoreDetailComposite.class);
		PropertiesCompositeFactory.register(DataStoreReference.class, DataStoreReferenceDetailComposite.class);
		PropertiesCompositeFactory.register(Interface.class, InterfaceDetailComposite.class);
		PropertiesCompositeFactory.register(Operation.class, OperationDetailComposite.class);
		PropertiesCompositeFactory.register(ItemDefinition.class, ItemDefinitionDetailComposite.class);
		PropertiesCompositeFactory.register(ItemDefinition.class, ItemDefinitionListComposite.class);
		PropertiesCompositeFactory.register(CorrelationPropertyRetrievalExpression.class, CorrelationPropertyREListComposite.class);
		PropertiesCompositeFactory.register(Property.class, PropertyListComposite.class);
		PropertiesCompositeFactory.register(ResourceRole.class, ResourceRoleListComposite.class);
		PropertiesCompositeFactory.register(Event.class, CommonEventDetailComposite.class);
		PropertiesCompositeFactory.register(StartEvent.class, StartEventDetailComposite.class);
		PropertiesCompositeFactory.register(EndEvent.class, EndEventDetailComposite.class);
		PropertiesCompositeFactory.register(CatchEvent.class, CatchEventDetailComposite.class);
		PropertiesCompositeFactory.register(ThrowEvent.class, ThrowEventDetailComposite.class);
		PropertiesCompositeFactory.register(BoundaryEvent.class, BoundaryEventDetailComposite.class);
		PropertiesCompositeFactory.register(TimerEventDefinition.class, TimerEventDefinitionDetailComposite.class);
		PropertiesCompositeFactory.register(ConditionalEventDefinition.class, ConditionalEventDefinitionDetailComposite.class);
		PropertiesCompositeFactory.register(CompensateEventDefinition.class, EventDefinitionDialogComposite.class);
		PropertiesCompositeFactory.register(ConditionalEventDefinition.class, EventDefinitionDialogComposite.class);
		PropertiesCompositeFactory.register(ErrorEventDefinition.class, EventDefinitionDialogComposite.class);
		PropertiesCompositeFactory.register(EscalationEventDefinition.class, EventDefinitionDialogComposite.class);
		PropertiesCompositeFactory.register(LinkEventDefinition.class, EventDefinitionDialogComposite.class);
		PropertiesCompositeFactory.register(MessageEventDefinition.class, EventDefinitionDialogComposite.class);
		PropertiesCompositeFactory.register(SignalEventDefinition.class, EventDefinitionDialogComposite.class);
		PropertiesCompositeFactory.register(TimerEventDefinition.class, EventDefinitionDialogComposite.class);
		PropertiesCompositeFactory.register(Process.class, ProcessDiagramDetailComposite.class);
		PropertiesCompositeFactory.register(EndEvent.class, EndEventDetailComposite.class);
		PropertiesCompositeFactory.register(StartEvent.class, StartEventDetailComposite.class);
		PropertiesCompositeFactory.register(ThrowEvent.class, ThrowEventDetailComposite.class);
		PropertiesCompositeFactory.register(StandardLoopCharacteristics.class, StandardLoopCharacteristicsDetailComposite.class);
		PropertiesCompositeFactory.register(MultiInstanceLoopCharacteristics.class, MultiInstanceLoopCharacteristicsDetailComposite.class);
		PropertiesCompositeFactory.register(Gateway.class, GatewayDetailComposite.class);
		PropertiesCompositeFactory.register(Activity.class, ActivityInputDetailComposite.class);
		PropertiesCompositeFactory.register(InputOutputSpecification.class, ActivityInputDetailComposite.class);
		PropertiesCompositeFactory.register(Activity.class, ActivityOutputDetailComposite.class);
		PropertiesCompositeFactory.register(CallChoreography.class, ActivityDetailComposite.class);
		PropertiesCompositeFactory.register(InputOutputSpecification.class, IoParametersDetailComposite.class);
		PropertiesCompositeFactory.register(DataInput.class, DataAssociationDetailComposite.class);
		PropertiesCompositeFactory.register(DataOutput.class, DataAssociationDetailComposite.class);
		PropertiesCompositeFactory.register(ManualTask.class, ManualTaskDetailComposite.class);
		PropertiesCompositeFactory.register(ScriptTask.class, ScriptTaskDetailComposite.class);
		PropertiesCompositeFactory.register(SubProcess.class, ActivityDetailComposite.class);
		PropertiesCompositeFactory.register(Task.class, TaskDetailComposite.class);
	}

	public static final String EDITOR_ID = "org.eclipse.bpmn2.modeler.ui.bpmn2editor"; //$NON-NLS-1$
	public static final String CONTRIBUTOR_ID = "org.eclipse.bpmn2.modeler.ui.PropertyContributor"; //$NON-NLS-1$

	private ModelHandler modelHandler;
	private URI modelUri;
	private URI diagramUri;
	private boolean editable = true;

	protected BPMNDiagram bpmnDiagram;
	protected Bpmn2ResourceImpl bpmnResource;
	
	private IWorkbenchListener workbenchListener;
	private IPartListener2 selectionListener;
    private IResourceChangeListener markerChangeListener;
	private boolean workbenchShutdown = false;
	private static BPMN2Editor activeEditor;
	// We need this to find BPMN2 Editors that are already open for this file
	// Used when opening a New Editor window for an already open editor.
	private IEditorInput currentInput;
	private static ITabDescriptorProvider tabDescriptorProvider;
	private BPMN2EditingDomainListener editingDomainListener;
	
	private Bpmn2Preferences preferences;
	private TargetRuntime targetRuntime;
	private ModelEnablements modelEnablements;
	private boolean importInProgress;
	private BPMN2EditorSelectionSynchronizer synchronizer;

	protected DiagramEditorAdapter editorAdapter;
	protected BPMN2MultiPageEditor multipageEditor;
	protected IPropertySheetPage propertySheetPage;
	protected IContentOutlinePage outlinePage;
	
	protected boolean saveInProgress = false;
	private static NotificationFilter filterNone = new NotificationFilter.Custom() {
		@Override
		public boolean matches(Notification notification) {
			return false;
		}
	};
	
	public BPMN2Editor(BPMN2MultiPageEditor mpe) {
		multipageEditor = mpe;
	}
	
	public static BPMN2Editor getActiveEditor() {
		return activeEditor;
	}
	
	public IEditorInput getEditorInput() {
		return currentInput;
	}
	
	private void setActiveEditor(BPMN2Editor editor) {
		activeEditor = editor;
		if (activeEditor!=null) {
			Bpmn2Preferences.setActiveProject(activeEditor.getProject());
			TargetRuntime.setCurrentRuntime( activeEditor.getTargetRuntime() );
		}
	}

	public BPMN2MultiPageEditor getMultipageEditor() {
		return multipageEditor;
	}
	
	protected DiagramEditorAdapter getEditorAdapter() {
		return editorAdapter;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (input instanceof IFileEditorInput) {
			BPMN2Builder.INSTANCE.loadExtensions( ((IFileEditorInput)input).getFile().getProject() );
		}
		setActiveEditor(this);
		currentInput = input;
		
		if (this.getDiagramBehavior()==null) {
			super.init(site, input);
			// add a listener so we get notified if the workbench is shutting down.
			// in this case we don't want to delete the temp file!
			addWorkbenchListener();
			addSelectionListener();
			addMarkerChangeListener();
		}
		else {
			if (input instanceof Bpmn2DiagramEditorInput) {
				bpmnDiagram = ((Bpmn2DiagramEditorInput)input).getBpmnDiagram();
				if (bpmnDiagram!=null) {
					setBpmnDiagram(bpmnDiagram);
				}
			}
		}
	}
	
	/**
	 * Beware, creates a new input and changes this editor!
	 */
	private Bpmn2DiagramEditorInput createNewDiagramEditorInput(IEditorInput input, Bpmn2DiagramType diagramType, String targetNamespace)
			throws PartInitException {
		
		modelUri = FileService.getInputUri(input);
		if (modelUri==null)
			throw new PartInitException(Messages.BPMN2Editor_Cannot_Create_Editor_Input);
		input = BPMN2DiagramCreator.createDiagram(input, modelUri, diagramType,targetNamespace,this);
		diagramUri = ((Bpmn2DiagramEditorInput)input).getUri();

		return (Bpmn2DiagramEditorInput)input;
	}

	/**
	 * Bypasses Graphiti's Persistency Behavior code and save only the BPMN2 model resource. 
	 * This is only used after a successful Import if the BPMN2 model was changed in any way,
	 * e.g. missing DI elements were added.
	 */
	private void saveModelFile() {
		try {
			bpmnResource.save(null);
			((BasicCommandStack) getEditingDomain().getCommandStack()).saveIsDone();
			updateDirtyState();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setInput(IEditorInput input) {
		try {
			if (input instanceof Bpmn2DiagramEditorInput) {
				Bpmn2DiagramType diagramType = Bpmn2DiagramType.NONE;
				String targetNamespace = null;
				diagramType = ((Bpmn2DiagramEditorInput)input).getInitialDiagramType();
				targetNamespace = ((Bpmn2DiagramEditorInput)input).getTargetNamespace();
				input = createNewDiagramEditorInput(input, diagramType, targetNamespace);
			}
		}
		catch (Exception e) {
			Activator.logError(e);
		}

		// Check if this is a New Editor Window for an already open editor
		currentInput = input;
		BPMN2Editor otherEditor = findOpenEditor(this,input);
		
		// Determine which Target Runtime to use for this input and initialize the ResourceSet
		TargetRuntime targetRuntime = getTargetRuntime(input);
		getTargetRuntime().notify(new LifecycleEvent(EventType.EDITOR_STARTUP,this));
		
		ResourceSet resourceSet = getEditingDomain().getResourceSet();
		resourceSet.setURIConverter(new ProxyURIConverterImplExtension(modelUri));
		resourceSet.eAdapters().add(editorAdapter = new DiagramEditorAdapter(this));

		// Tell the TargetRuntime about the ResourceSet. This allows the TargetRuntime to provide its
		// own ResourceFactory if needed.
		targetRuntime.registerExtensionResourceFactory(resourceSet);
		
		// Now create the BPMN2 model resource, or reuse the one from the already open editor.
		if (otherEditor==null) {
			bpmnResource = (Bpmn2ResourceImpl) resourceSet.createResource(modelUri, Bpmn2ModelerResourceImpl.BPMN2_CONTENT_TYPE_ID);
		}
		else {
			bpmnResource = otherEditor.bpmnResource;
		}
		
		// Set this input in Graphiti DiagramEditor
		super.setInput(input);
		
		// Hook a transaction exception handler so we can get diagnostics about EMF validation errors.
		getEditingDomainListener();
		
		// This does the actual loading of the resource.
		// TODO: move the loading code to BPMN2PersistencyBehavior where it belongs,
		// and get rid of ModelHandler and ModelHandlerLocator
		modelHandler = ModelHandlerLocator.createModelHandler(modelUri, bpmnResource);
		ModelHandlerLocator.put(diagramUri, modelHandler);

		// Allow the runtime extension to construct custom tasks and whatever else it needs
		// custom tasks should be added to the current target runtime's custom tasks list
		// where they will be picked up by the toolpalette refresh.
		setActiveEditor(this);	// set the Bpmn2Preferences.activeProject just before RT extension is initialized
		getTargetRuntime().notify(new LifecycleEvent(EventType.EDITOR_INITIALIZED,this));

		if (otherEditor==null) {
			try {
				getPreferences().setDoCoreValidation(false);
				// Import the BPMNDI model that creates the Graphiti shapes, connections, etc.
				BasicCommandStack commandStack = (BasicCommandStack) getEditingDomain().getCommandStack();
				commandStack.execute(new RecordingCommand(getEditingDomain()) {
					@Override
					protected void doExecute() {
						importDiagram();
					}
				});
		
				Definitions definitions = ModelUtil.getDefinitions(bpmnResource);
				if (definitions!=null) {
					// we'll need this in case doSaveAs()
					((Bpmn2DiagramEditorInput)input).setTargetNamespace(definitions.getTargetNamespace());
					((Bpmn2DiagramEditorInput)input).setInitialDiagramType(ModelUtil.getDiagramType(this));
				}
				// Reset the save point and initialize the undo stack
				commandStack.saveIsDone();
				commandStack.flush();
			}
			finally {
				getPreferences().setDoCoreValidation(true);
			}
		}
		
		// Load error markers
		loadMarkers();
	}
	
	protected DiagramEditorInput convertToDiagramEditorInput(IEditorInput input) throws PartInitException {
		IEditorInput newInput = createNewDiagramEditorInput(input, Bpmn2DiagramType.NONE, ""); //$NON-NLS-1$
		if (newInput==null)
			newInput = super.convertToDiagramEditorInput(input);
		return (DiagramEditorInput) newInput;
	}
	
	private void importDiagram() {
		try {
			importInProgress = true;
			// make sure this guy is active, otherwise it's not selectable
			Diagram diagram = getDiagramTypeProvider().getDiagram();
			IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();
			diagram.setActive(true);
			Bpmn2DiagramEditorInput input = (Bpmn2DiagramEditorInput) getEditorInput();
			Bpmn2DiagramType diagramType = input.getInitialDiagramType();
			String targetNamespace = input.getTargetNamespace();
	
			if (diagramType != Bpmn2DiagramType.NONE) {
				bpmnDiagram = modelHandler.createDiagramType(diagramType, targetNamespace);
				featureProvider.link(diagram, bpmnDiagram);
				// If the bpmn file was missing DI elements, they would have been added by the importer
				// so save the file now in case it was changed.
				saveModelFile();
			}
			
			DIImport di = new DIImport(this);
			di.setModelHandler(modelHandler);
	
			di.generateFromDI();
		}
		finally {
			importInProgress = false;
		}
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isEditable() {
	    return editable;
	}

	@Override
	public boolean isDirty() {
		if (!editable)
			return false;
		if (getEditorInput()==null)
			return false;
		return super.isDirty();
	}
	
	protected DiagramBehavior createDiagramBehavior() {
		DiagramBehavior diagramBehavior = new BPMN2EditorDiagramBehavior(this);
		return diagramBehavior;
	}
    
	public Bpmn2Preferences getPreferences() {
		if (preferences==null) {
			loadPreferences(getProject());
		}
		return preferences;
	}
	
	private void loadPreferences(IProject project) {
		preferences = Bpmn2Preferences.getInstance(project);
		preferences.addPreferenceChangeListener(this);
	}

	/**
	 * ID for tabbed property sheets.
	 * 
	 * @return the contributor id
	 */
	@Override
	public String getContributorId() {
		return CONTRIBUTOR_ID;
	}
	
	public TargetRuntime getTargetRuntime() {
		if (targetRuntime==null) {
			targetRuntime = getTargetRuntime(getEditorInput());
		}
		return targetRuntime;
	}
	
	public ModelEnablements getModelEnablements() {
		String profileId = getPreferences().getDefaultToolProfile(getTargetRuntime());
		if (modelEnablements!=null) {
			if (!modelEnablements.getId().equals(profileId)) {
				modelEnablements = null;
			}
				
		}
		if (modelEnablements==null) {
			modelEnablements = getPreferences().getModelEnablements(getTargetRuntime(), profileId);
			if (modelEnablements.size()==0) {
				// This Target Runtime doesn't define a Tool Profile
				// so we'll use the one for Default Runtime
				profileId = getPreferences().getDefaultToolProfile(TargetRuntime.getDefaultRuntime());
				ModelEnablements defaultEnablements = getPreferences().getModelEnablements(TargetRuntime.getDefaultRuntime(), profileId);
				modelEnablements.copy(defaultEnablements);
			}
		}
		return modelEnablements;
	}
	
	protected TargetRuntime getTargetRuntime(IEditorInput input) {
		if (targetRuntime==null && input!=null) {
			 // If the project has not been configured for a specific runtime through the "BPMN2"
			 // project properties page (i.e. the target is "None") then allow the runtime extension
			 // plug-ins an opportunity to identify the given process file contents as their own.
			 // If none of the plug-ins respond with "yes, this file is targeted for my runtime",
			 // then use the "None" as the extension. This will configure the BPMN2 Modeler with
			 // generic property sheets and other default behavior.
			targetRuntime = getPreferences().getRuntime();
			if (targetRuntime == TargetRuntime.getDefaultRuntime()) {
				for (TargetRuntime rt : TargetRuntime.createTargetRuntimes()) {
					if (rt.getRuntimeExtension().isContentForRuntime(input)) {
						targetRuntime = rt;
						break;
					}
				}
			}
			if (targetRuntime==null)
				targetRuntime = TargetRuntime.getDefaultRuntime();
			
			TargetRuntime.setCurrentRuntime(targetRuntime);
		}
		return targetRuntime;
	}

	public void updatePalette() {
		GFPaletteRoot pr = (GFPaletteRoot)getPaletteRoot();
		if (pr!=null) {
			// force a reload of this
			modelEnablements = null;
			pr.updatePaletteEntries();
			Bpmn2ToolBehaviorProvider toolBehaviorProvider = 
					(Bpmn2ToolBehaviorProvider)getDiagramTypeProvider().
					getCurrentToolBehaviorProvider();
			toolBehaviorProvider.createPaletteProfilesGroup(this, pr);
		}
	}
	
	private void addWorkbenchListener() {
		if (workbenchListener==null) {
			workbenchListener = new IWorkbenchListener() {
				@Override
				public boolean preShutdown(IWorkbench workbench, boolean forced) {
					workbenchShutdown = true;
					return true;
				}

				@Override
				public void postShutdown(IWorkbench workbench) {
				}

			};
			PlatformUI.getWorkbench().addWorkbenchListener(workbenchListener);
		}
	}
	
    @Override
    public void gotoMarker(IMarker marker) {
        final EObject target = getTargetObject(marker);
        if (target == null) {
            return;
        }
        final PictogramElement pe = getDiagramTypeProvider().getFeatureProvider().getPictogramElementForBusinessObject(
                target);
        if (pe == null) {
            return;
        }
        selectPictogramElements(new PictogramElement[] {pe });
    }

    private void loadMarkers() {
    	if (getModelFile()!=null) {
	        // read in the markers
	        BPMN2ValidationStatusLoader vsl = new BPMN2ValidationStatusLoader(this);
	
	        try {
	            vsl.load(Arrays.asList(getModelFile().findMarkers(null, true, IResource.DEPTH_ZERO)));
	        } catch (CoreException e) {
	            Activator.logStatus(e.getStatus());
	        }
    	}
    }
    
    private EObject getTargetObject(IMarker marker) {
        final String uriString = marker.getAttribute(EValidator.URI_ATTRIBUTE, null);
        final URI uri = uriString == null ? null : URI.createURI(uriString);
        if (uri == null) {
            return null;
        }
        return getEditingDomain().getResourceSet().getEObject(uri, false);
    }

	private void removeWorkbenchListener()
	{
		if (workbenchListener!=null) {
			PlatformUI.getWorkbench().removeWorkbenchListener(workbenchListener);
			workbenchListener = null;
		}
	}
	
	private void addSelectionListener() {
		if (selectionListener == null) {
			IWorkbenchPage page = getSite().getPage();
			selectionListener = new IPartListener2() {
				public void partActivated(IWorkbenchPartReference partRef) {
				}

				@Override
				public void partBroughtToTop(IWorkbenchPartReference partRef) {
					IWorkbenchPart part = partRef.getPart(false);
					if (part instanceof BPMN2MultiPageEditor) {
						BPMN2MultiPageEditor mpe = (BPMN2MultiPageEditor)part;
						setActiveEditor(mpe.getDesignEditor());
					}
				}

				@Override
				public void partClosed(IWorkbenchPartReference partRef) {
				}

				@Override
				public void partDeactivated(IWorkbenchPartReference partRef) {
				}

				@Override
				public void partOpened(IWorkbenchPartReference partRef) {
				}

				@Override
				public void partHidden(IWorkbenchPartReference partRef) {
				}

				@Override
				public void partVisible(IWorkbenchPartReference partRef) {
				}

				@Override
				public void partInputChanged(IWorkbenchPartReference partRef) {
				}
			};
			page.addPartListener(selectionListener);
		}
	}

	private void removeSelectionListener()
	{
		if (selectionListener!=null) {
			getSite().getPage().removePartListener(selectionListener);
			selectionListener = null;
		}
	}

	private void addMarkerChangeListener() {
		if (getModelFile()!=null) {
			if (markerChangeListener==null) {
				markerChangeListener = new BPMN2MarkerChangeListener(this);
		        getModelFile().getWorkspace().addResourceChangeListener(markerChangeListener, IResourceChangeEvent.POST_BUILD);
			}
		}
	}
	
	private void removeMarkerChangeListener() {
		if (markerChangeListener!=null) {
			getModelFile().getWorkspace().removeResourceChangeListener(markerChangeListener);
			markerChangeListener = null;
		}
	}
	
	public void refreshTitle() {
		if (getEditorInput()!=null) {
			String name = getEditorInput().getName();
			setPartName(URI.decode(name));
		}
	}

	public BPMN2EditingDomainListener getEditingDomainListener() {
		if (editingDomainListener==null) {
			TransactionalEditingDomainImpl editingDomain = (TransactionalEditingDomainImpl)getEditingDomain();
			if (editingDomain==null) {
				return null;
			}
			editingDomainListener = new BPMN2EditingDomainListener(this);

			Lifecycle domainLifeCycle = (Lifecycle) editingDomain.getAdapter(Lifecycle.class);
			domainLifeCycle.addTransactionalEditingDomainListener(editingDomainListener);
		}
		return editingDomainListener;
	}
	
	public BasicDiagnostic getDiagnostics() {
		return getEditingDomainListener().getDiagnostics();
	}
	
	@Override
	protected SelectionSynchronizer getSelectionSynchronizer() {
		if (synchronizer == null)
			synchronizer = new BPMN2EditorSelectionSynchronizer();
		return synchronizer;
	}

	@Override
	public Object getAdapter(Class required) {
		if (required==DiagramEditor.class) {
			return this;
		}
		if (required==ITabDescriptorProvider.class) {
			if (tabDescriptorProvider==null) {
				tabDescriptorProvider = new PropertyTabDescriptorProvider();
//				IWorkbenchPage page = getEditorSite().getPage();
//				String viewID = "org.eclipse.ui.views.PropertySheet"; //$NON-NLS-1$
//				try {
//					page.showView(viewID, null, IWorkbenchPage.VIEW_CREATE);
//					page.showView(viewID, null,  IWorkbenchPage.VIEW_ACTIVATE);
//				}
//				catch (Exception e) {}
			}
			return tabDescriptorProvider;
		}
		if (required==TargetRuntime.class)
			return getTargetRuntime();
		if (required==Bpmn2Preferences.class)
			return getPreferences();
		if (required == IPropertySheetPage.class) {
			if (propertySheetPage==null) {
				propertySheetPage = new Bpmn2TabbedPropertySheetPage(this);
				Display.getCurrent().asyncExec(new Runnable() {
					@Override
					public void run() {
						propertySheetPage.getControl().addDisposeListener(new DisposeListener() {
							@Override
							public void widgetDisposed(DisposeEvent e) {
								propertySheetPage = null;
							}
						});
						
					}
				});
			}
			return propertySheetPage;
		}
		if (required == SelectionSynchronizer.class) {
			return getSelectionSynchronizer();
		}
		if (required == IContentOutlinePage.class) {
			if (getDiagramTypeProvider() != null) {
				if (outlinePage==null) {
					outlinePage = new BPMN2EditorOutlinePage(this);
				}
				return outlinePage;
			}
		}
		if (required == StructuredTextEditor.class) {
			// ugly hack to disable selection in Property Viewer while source viewer is active
			if (multipageEditor.getActiveEditor() == multipageEditor.getSourceViewer())
				return multipageEditor.getSourceViewer();
		}
		if (required == ModelEnablements.class) {
			return getModelEnablements();
		}
		if (required == ToolPaletteDescriptor.class) {
			String profileId = getPreferences().getDefaultToolProfile(getTargetRuntime());
			return getTargetRuntime().getToolPalette(profileId);
		}
		if (required == NotificationFilter.class) {
			if (saveInProgress)
				return filterNone;
			else
				return null;
		}
		if (required==GraphicalViewer.class) {
			return getGraphicalViewer();
		}
		
		return super.getAdapter(required);
	}

	@Override
	public void dispose() {
		getTargetRuntime().notify(new LifecycleEvent(EventType.EDITOR_SHUTDOWN,this));

		// clear ID mapping tables if no more instances of editor are active
		int instances = 0;
		IWorkbenchPage[] pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();
		for (IWorkbenchPage p : pages) {
			IEditorReference[] refs = p.findEditors(null, EDITOR_ID, IWorkbenchPage.MATCH_ID);
			for (IEditorReference r : refs) {
				if (r.getEditor(false) instanceof BPMN2MultiPageEditor) {
					if (((BPMN2MultiPageEditor)r.getEditor(false)).designEditor != this)
						++instances;
				}
			}
		}
		BPMN2Editor otherEditor = findOpenEditor(this, getEditorInput());
		if (otherEditor==null) {
			// we can delete the Graphiti Diagram file if there are no other
			// editor windows open for this BPMN2 file.
			File diagramFile = new File(diagramUri.toFileString());
			if (diagramFile.exists()) {
				try {
					diagramFile.delete();
				}
				catch (Exception e) {
				}
			}
		}
		if (modelHandler!=null) {
			ModelUtil.clearIDs(modelHandler.getResource(), instances==0);
			modelHandler.dispose();
		}
		getPreferences().removePreferenceChangeListener(this);
		
		// cancel the Property Sheet Page job
		if (propertySheetPage!=null)
			propertySheetPage.selectionChanged(this, null);
		
		// get rid of cached Property Tab Descriptors
		if (tabDescriptorProvider instanceof PropertyTabDescriptorProvider)
			((PropertyTabDescriptorProvider)tabDescriptorProvider).disposeTabDescriptors(bpmnResource);
		
		getResourceSet().eAdapters().remove(getEditorAdapter());
		removeSelectionListener();
		if (instances==0)
			setActiveEditor(null);
		
		super.dispose();
		ModelHandlerLocator.remove(modelUri);
		// get rid of temp files and folders, but NOT if the workbench is being shut down.
		// when the workbench is restarted, we need to have those temp files around!
		if (!workbenchShutdown) {
			if (FileService.isTempFile(modelUri)) {
				FileService.deleteTempFile(modelUri);
			}
		}

		removeWorkbenchListener();
		removeMarkerChangeListener();
		getPreferences().dispose();
		currentInput = null;
	}

	public IPath getModelPath() {
		if (getModelFile()!=null)
			return getModelFile().getFullPath();
		return null;
	}
	
	public IProject getProject() {
		if (getModelFile()!=null)
			return getModelFile().getProject();
		return null;
	}
	
	public IFile getModelFile() {
		if (modelUri!=null) {
			String uriString = modelUri.trimFragment().toPlatformString(true);
			if (uriString!=null) {
				IPath fullPath = new Path(uriString);
				return ResourcesPlugin.getWorkspace().getRoot().getFile(fullPath);
			}
		}
		return null;
	}
	
	public URI getModelUri() {
		return modelUri;
	}
	
	public URI getDiagramUri() {
		return diagramUri;
	}
	
	public ModelHandler getModelHandler() {
		return modelHandler;
	}
	
	public Resource getResource() {
		return bpmnResource;
	}
	
	public ResourceSet getResourceSet() {
		return getEditingDomain().getResourceSet();
	}
	
	public void refresh() {
		if (!importInProgress)
			getDiagramBehavior().getRefreshBehavior().refresh();
	}
	
	public void createPartControl(Composite parent) {
		if (getGraphicalViewer()==null) {
			super.createPartControl(parent);
			PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IHelpContexts.TOC);
		}
	}
	
	public BPMNDiagram getBpmnDiagram() {
		if (bpmnDiagram==null) {
			Definitions definitions = ModelUtil.getDefinitions(bpmnResource);
			if (definitions!=null && definitions.getDiagrams().size()>0)
				bpmnDiagram = definitions.getDiagrams().get(0);
		}

//		if (bpmnDiagram!=null) {
//			GraphicalViewer viewer = getGraphicalViewer();
//			mapDiagramToViewer.put(bpmnDiagram, viewer);
//		}
		return bpmnDiagram;
	}
	
	public void setBpmnDiagram(final BPMNDiagram bpmnDiagram) {
		// create a new Graphiti Diagram if needed
		Diagram diagram = DIUtils.getOrCreateDiagram(getDiagramBehavior(), bpmnDiagram);
		
		// clear current selection to avoid confusing the GraphicalViewer
		selectPictogramElements(new PictogramElement[] {});

		// Tell the DTP about the new Diagram
		getDiagramTypeProvider().resourceReloaded(diagram);
		getDiagramBehavior().getRefreshBehavior().initRefresh();
		setPictogramElementsForSelection(null);
		// set Diagram as contents for the graphical viewer and refresh
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(diagram);
		EditPart ep = viewer.getRootEditPart().getContents();
		if (ep instanceof AbstractGraphicalEditPart) {
			IFigure fig = ((AbstractGraphicalEditPart)ep).getFigure();
			fig.setBorder(new MarginBorder(50));
		}
		
		ConnectionLayerClippingStrategy.applyTo(getGraphicalViewer());
		
		getDiagramBehavior().refreshContent();
		
		// remember this for later
		this.bpmnDiagram = bpmnDiagram;
		modelEnablements = null;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
//		long start = System.currentTimeMillis();
		try {
			saveInProgress = true;
//			System.out.print("Saving...");
			super.doSave(monitor);
		}
		finally {
			saveInProgress = false;
		}
//		System.out.println("done in "+(System.currentTimeMillis()-start)+" ms");
		Resource resource = getResourceSet().getResource(modelUri, false);
		BPMN2ProjectValidator.validateOnSave(resource, monitor);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return getModelFile()!=null;
	}
	
	@Override
	public void doSaveAs() {
		IFile oldFile = getModelFile();
		SaveAsDialog saveAsDialog = new SaveAsDialog(getSite().getShell());
		saveAsDialog.setOriginalFile(oldFile);
		saveAsDialog.create();
		if (saveAsDialog.open() == SaveAsDialog.CANCEL) {
			return;
		}
		IPath newFilePath = saveAsDialog.getResult();
		if (newFilePath == null){
			return;
		}
		
        IFile newFile = ResourcesPlugin.getWorkspace().getRoot().getFile(newFilePath);
        IWorkbenchPage page = getSite().getPage();
        try {
        	// if new file exists, close its editor (if open) and delete the existing file
            if (newFile.exists()) {
    			IEditorPart editorPart = ResourceUtil.findEditor(page, newFile);
    			if (editorPart!=null)
	    			page.closeEditor(editorPart, false);
        		newFile.delete(true, null);
            }
            // make a copy
			oldFile.copy(newFilePath, true, null);
		} catch (CoreException e) {
			showErrorDialogWithLogging(e);
			return;
		}
        
        // change the Resource URI and save it to the new file
		URI newURI = URI.createPlatformResourceURI(newFile.getFullPath().toString(), true);
    	handleResourceMoved(bpmnResource,newURI);
    	doSave(null);
	}

	public void closeEditor() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				boolean closed = getSite().getPage().closeEditor(BPMN2Editor.this, false);
				if (!closed){
					// If close editor fails, try again with explicit editorpart 
					// of the old file
					IFile oldFile = ResourcesPlugin.getWorkspace().getRoot().getFile(getModelPath());
					IEditorPart editorPart = ResourceUtil.findEditor(getSite().getPage(), oldFile);
					closed = getSite().getPage().closeEditor(editorPart, false);
				}
			}
		});
	}

	// Show error dialog and log the error
	private void showErrorDialogWithLogging(Exception e) {
		Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
		ErrorUtils.showErrorWithLogging(status);
	}

	////////////////////////////////////////////////////////////////////////////////
	// WorkspaceSynchronizer handlers called from delegate
	////////////////////////////////////////////////////////////////////////////////
	
	public boolean handleResourceChanged(Resource resource) {
		if (resource==bpmnResource) {
			URI newURI = resource.getURI();
			if (!modelUri.equals(newURI)) {
				ModelHandlerLocator.remove(modelUri);
				modelUri = newURI;
				if (preferences!=null) {
					preferences.removePreferenceChangeListener(this);
					preferences.dispose();
					preferences = null;
				}
				targetRuntime = null;
				modelHandler = ModelHandlerLocator.createModelHandler(modelUri, (Bpmn2ResourceImpl)resource);
				ModelHandlerLocator.put(diagramUri, modelHandler);
				
		    	Bpmn2DiagramEditorInput input = (Bpmn2DiagramEditorInput)getEditorInput();
		    	input.updateUri(newURI);
		    	multipageEditor.setInput(input);
			}
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (getEditorInput()!=null) {
					updateDirtyState();
			    	refreshTitle();
				}
			}
		});
		return true;
	}

	public boolean handleResourceDeleted(Resource resource) {
		closeEditor();
		return true;
	}

	public boolean handleResourceMoved(Resource resource, URI newURI) {
		URI oldURI = resource.getURI();
		// The XML loader uses a lazy reference loading: references to internal objects
		// are initialized as proxies until first accessed (with eGet()).
		// Before we change the URI, make sure all references are resolved
		// otherwise the proxy URI (of unresolved references) will still be the old one.
		TreeIterator<EObject> iter = resource.getAllContents();
		while (iter.hasNext()) {
			EObject o = iter.next();
			for (EReference r : o.eClass().getEAllReferences()) {
				// the eGet() will handle proxy resolving
				o.eGet(r);
			}
		}
		resource.setURI(newURI);
		
		if (resource == bpmnResource) {
			ModelHandlerLocator.remove(modelUri);
			modelUri = newURI;
			if (preferences!=null) {
				preferences.removePreferenceChangeListener(this);
				preferences.dispose();
				preferences = null;
			}
			targetRuntime = null;
			modelHandler = ModelHandlerLocator.createModelHandler(modelUri, (Bpmn2ResourceImpl)resource);
			ModelHandlerLocator.put(diagramUri, modelHandler);
			
	    	Bpmn2DiagramEditorInput input = (Bpmn2DiagramEditorInput)getEditorInput();
	    	input.updateUri(newURI);
	    	multipageEditor.setInput(input);
	    	
	    	handleResourceChanged(resource);
		}
		else if (diagramUri.equals(oldURI)) {
			ModelHandlerLocator.remove(diagramUri);
			diagramUri = newURI;
			ModelHandlerLocator.put(diagramUri, modelHandler);
		}

		return true;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// Other handlers
	////////////////////////////////////////////////////////////////////////////////

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// Graphiti understands multipage editors
		super.selectionChanged(part,selection); // Graphiti's DiagramEditorInternal
		// but apparently GEF doesn't
		updateActions(getSelectionActions()); // usually done in GEF's GraphicalEditor
		
		// if the selected element is obscured by another shape
		// send it to the top of the z-stack.
		// FIXME: this should be done in the figure's UpdateFeature or LayoutFeature, not here.
//		final List<ContainerShape> moved = new ArrayList<ContainerShape>();
//		for (PictogramElement pe : getSelectedPictogramElements()) {
//			if (pe instanceof ContainerShape && !(pe instanceof Diagram)) {
//				final ContainerShape shape = (ContainerShape)pe;
//				ContainerShape container = shape.getContainer();
//				// make sure this shape has not been deleted
//				if (container==null)
//					continue;
//				int size = container.getChildren().size();
//				if (size>1) {
//					// don't send Choreography Participant bands, Pools or Lanes to front
//					// they're already there...
//					BaseElement baseElement = BusinessObjectUtil.getFirstBaseElement(shape);
//					if (baseElement instanceof Participant || baseElement instanceof Lane)
//						continue;
//					boolean obscured = false;
//					int index = container.getChildren().indexOf(shape);
//					for (int i=index+1; i<container.getChildren().size(); ++i) {
//						PictogramElement sibling = container.getChildren().get(i);
//						if (sibling instanceof ContainerShape &&
//								!FeatureSupport.isLabelShape((ContainerShape)sibling)) {
//							if (GraphicsUtil.intersects(shape, (ContainerShape)sibling)) {
//								boolean siblingIsBoundaryEvent = false;
//								if (baseElement instanceof Activity) {
//									BaseElement be = BusinessObjectUtil.getFirstBaseElement(sibling);
//									for (BoundaryEvent boundaryEvent : ((Activity)baseElement).getBoundaryEventRefs()) {
//										if (be==boundaryEvent) {
//											siblingIsBoundaryEvent = true;
//											break;
//										}
//									}
//								}
//								if (!siblingIsBoundaryEvent) {
//									obscured = true;
//								}
//							}
//						}
//					}
//					// if the selected shape is an Activity, it may have Boundary Event shapes
//					// attached to it - these will have to be moved to the top so they're
//					// not obscured by the Activity.
//					if (baseElement instanceof Activity) {
//						for (BoundaryEvent be : ((Activity)baseElement).getBoundaryEventRefs()) {
//							for (PictogramElement child : container.getChildren()) {
//								if (child instanceof ContainerShape && BusinessObjectUtil.getFirstBaseElement(child) == be) {
//									index = container.getChildren().indexOf(child);
//									for (int i=index+1; i<container.getChildren().size(); ++i) {
//										PictogramElement sibling = container.getChildren().get(i);
//										if (sibling!=shape && sibling instanceof ContainerShape) {
//											if (GraphicsUtil.intersects((ContainerShape)child, (ContainerShape)sibling)) {
//												obscured = true;
//												moved.add((ContainerShape)child);
//											}
//										}
//									}
//								}
//							}
//						}
//					}
//					if (obscured) {
//						moved.add(0,shape);
//					}
//				}
//			}
//		}
//		if (!moved.isEmpty()) {
//			Display.getDefault().asyncExec(new Runnable() {
//				@Override
//				public void run() {
//					getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {
//						@Override
//						protected void doExecute() {
//							for (ContainerShape child : moved) {
//								GraphicsUtil.sendToFront(child);
//							}
//						}
//					});
//				}
//			});
//		}
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent event) {
		getPreferences().reload();
		
		if (event.getKey().contains("/"+Bpmn2Preferences.PREF_MODEL_ENABLEMENT+"/")) //$NON-NLS-1$ //$NON-NLS-2$
			modelEnablements = null;
		
		if (event.getKey().contains(Bpmn2Preferences.PREF_SHOW_ADVANCED_PROPERTIES) ||
				event.getKey().contains("/"+Bpmn2Preferences.PREF_TOOL_PROFILE+"/")) { //$NON-NLS-1$ //$NON-NLS-2$
			// get rid of cached Property Tab Descriptors
			if (tabDescriptorProvider instanceof PropertyTabDescriptorProvider)
				((PropertyTabDescriptorProvider)tabDescriptorProvider).disposeTabDescriptors(bpmnResource);
		}
		
		if (event.getKey().contains("/"+Bpmn2Preferences.PREF_SHAPE_STYLE+"/")) { //$NON-NLS-1$ //$NON-NLS-2$
			getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {
				@Override
				protected void doExecute() {
					IFeatureProvider fp = BPMN2Editor.this.getDiagramTypeProvider().getFeatureProvider();
					IPeService peService = Graphiti.getPeService();
					TreeIterator<EObject> iter = getDiagramTypeProvider().getDiagram().eAllContents();
					while (iter.hasNext()) {
						EObject o = iter.next();
						if (o instanceof PictogramElement) {
							PictogramElement pe = (PictogramElement)o;
							BaseElement be = BusinessObjectUtil.getFirstElementOfType(pe, BaseElement.class);
							if (be!=null) {
								TreeIterator<EObject> childIter = pe.eAllContents();
								while (childIter.hasNext()) {
									o = childIter.next();
									if (o instanceof GraphicsAlgorithm) {
										GraphicsAlgorithm ga = (GraphicsAlgorithm)o;
										if (peService.getPropertyValue(ga, Bpmn2Preferences.PREF_SHAPE_STYLE)!=null) {
											StyleUtil.applyStyle(ga, be);
										}
									}
			
								}
							}
							if (pe instanceof Shape && FeatureSupport.isLabelShape((Shape)pe)) {
								UpdateContext context = new UpdateContext(pe);
								IUpdateFeature feature = fp.getUpdateFeature(context);
								if (feature!=null) {
									feature.update(context);
								}
							}
						}
					}
				}
			});
		}
		
		if (event.getKey().contains(ShapeStyle.Category.GRID.name())) { //$NON-NLS-1$
			getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {
				@Override
				protected void doExecute() {
					ShapeStyle ss = getPreferences().getShapeStyle(ShapeStyle.Category.GRID);
					Diagram diagram = getDiagramTypeProvider().getDiagram();
					diagram.setGridUnit(ss.getDefaultWidth());
					diagram.setVerticalGridUnit(ss.getDefaultHeight());
					diagram.setSnapToGrid(ss.getSnapToGrid());
					GraphicsAlgorithm ga = diagram.getGraphicsAlgorithm();
					IGaService gaService = Graphiti.getGaService();
					ga.setForeground(gaService.manageColor(diagram, ss.getShapeForeground()));
					refresh();
				}
			});
		}
		
		if (event.getKey().contains(ShapeStyle.Category.CANVAS.name())) { //$NON-NLS-1$
			getEditingDomain().getCommandStack().execute(new RecordingCommand(getEditingDomain()) {
				@Override
				protected void doExecute() {
					ShapeStyle ss = getPreferences().getShapeStyle(ShapeStyle.Category.CANVAS);
					Diagram diagram = getDiagramTypeProvider().getDiagram();
					GraphicsAlgorithm ga = diagram.getGraphicsAlgorithm();
					IGaService gaService = Graphiti.getGaService();
					ga.setBackground(gaService.manageColor(diagram, ss.getShapeBackground()));
					refresh();
				}
			});
		}
	}
	
	public static BPMN2Editor findOpenEditor(IEditorPart newEditor, IEditorInput newInput) {
		if (newEditor!=null && newInput!=null) {
			IWorkbenchPage[] pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();
			for (IWorkbenchPage page : pages) {
				IEditorReference[] otherEditors = page.findEditors(newInput, null, IWorkbenchPage.MATCH_INPUT);
				for (IEditorReference ref : otherEditors) {
					IEditorPart part = ref.getEditor(true);
					if (part instanceof BPMN2MultiPageEditor) {
						BPMN2Editor otherEditor = ((BPMN2MultiPageEditor)part).getDesignEditor();
						if (otherEditor!=newEditor) {
							return otherEditor;
						}
					}
					else if (part instanceof BPMN2Editor) {
						BPMN2Editor otherEditor = (BPMN2Editor)part;
						if (otherEditor!=newEditor) {
							return otherEditor;
						}
					}
				}
			}
		}
		return null;
	}
	
	public static IEditorPart openEditor(URI modelURI) {
		IEditorPart part = null;
		try {
			Bpmn2DiagramEditorInput input = BPMN2DiagramCreator.createDiagram(modelURI, Bpmn2DiagramType.NONE, ""); //$NON-NLS-1$
			part = BPMN2DiagramCreator.openEditor(input);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return part;
	}

	@Override
	protected void configureGraphicalViewer() {
	    super.configureGraphicalViewer();
	    // add zooming action with "CTRL + Mouse Wheel"
	    GraphicalViewer viewer = getGraphicalViewer();
	    viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1), MouseWheelZoomHandler.SINGLETON);
	}
}
