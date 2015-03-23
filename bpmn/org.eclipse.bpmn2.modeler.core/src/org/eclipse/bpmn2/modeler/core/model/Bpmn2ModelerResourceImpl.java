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
package org.eclipse.bpmn2.modeler.core.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.DataAssociation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.Expression;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Import;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNLabel;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.di.BpmnDiPackage;
import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.IExtensionValueAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectPropertyProvider;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory.Bpmn2ModelerDocumentRootImpl;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.runtime.TypeLanguageDescriptor;
import org.eclipse.bpmn2.modeler.core.utils.ImportUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.NamespaceUtil;
import org.eclipse.bpmn2.util.Bpmn2ResourceImpl;
import org.eclipse.bpmn2.util.ImportHelper;
import org.eclipse.bpmn2.util.OnlyContainmentTypeInfo;
import org.eclipse.bpmn2.util.QNameURIHandler;
import org.eclipse.bpmn2.util.XmlExtendedMetadata;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.DcFactory;
import org.eclipse.dd.dc.DcPackage;
import org.eclipse.dd.dc.Point;
import org.eclipse.dd.di.DiPackage;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EObjectWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLLoad;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.XMLSave;
import org.eclipse.emf.ecore.xmi.impl.ElementHandlerImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLLoadImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLSaveImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLString;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.wst.wsdl.util.WSDLResourceImpl;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <!-- begin-user-doc --> The <b>Resource </b> associated with the package.
 * 
 * @implements Bpmn2Resource <!-- end-user-doc -->
 * @see org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl
 */
public class Bpmn2ModelerResourceImpl extends Bpmn2ResourceImpl {

	public static final String BPMN2_CONTENT_TYPE_ID = "org.eclipse.bpmn2.content-type.xml"; //$NON-NLS-1$
	protected BpmnXmlHelper xmlHelper;
	protected QNameURIHandler uriHandler;
	public HashMap xmlNameToFeatureMap = new HashMap();
	protected static HashSet<EStructuralFeature> qnameMap = new HashSet<EStructuralFeature>();
	static {
		qnameMap.add(Bpmn2Package.eINSTANCE.getExtension_Definition());
		qnameMap.add(Bpmn2Package.eINSTANCE.getRelationship_Sources());
		qnameMap.add(Bpmn2Package.eINSTANCE.getRelationship_Targets());
		qnameMap.add(Bpmn2Package.eINSTANCE.getAssociation_SourceRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getAssociation_TargetRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getGroup_CategoryValueRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getCorrelationKey_CorrelationPropertyRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getCorrelationProperty_Type());
		qnameMap.add(Bpmn2Package.eINSTANCE.getCorrelationPropertyBinding_CorrelationPropertyRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getCorrelationPropertyRetrievalExpression_MessageRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getCorrelationSubscription_CorrelationPropertyBinding());
		qnameMap.add(Bpmn2Package.eINSTANCE.getError_StructureRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getEscalation_StructureRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getFlowElement_CategoryValueRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getFlowNode_Incoming());
		qnameMap.add(Bpmn2Package.eINSTANCE.getFlowNode_Outgoing());
		qnameMap.add(Bpmn2Package.eINSTANCE.getFormalExpression_EvaluatesToTypeRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getInputOutputBinding_OperationRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getItemDefinition_StructureRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getMessage_ItemRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getResourceParameter_Type());
		qnameMap.add(Bpmn2Package.eINSTANCE.getInterface_ImplementationRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getOperation_InMessageRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getOperation_OutMessageRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getOperation_ErrorRefs());
		qnameMap.add(Bpmn2Package.eINSTANCE.getOperation_ImplementationRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getCallConversation_CalledCollaborationRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getConversationAssociation_InnerConversationNodeRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getConversationAssociation_OuterConversationNodeRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getConversationLink_SourceRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getConversationLink_TargetRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getConversationNode_MessageFlowRefs());
		qnameMap.add(Bpmn2Package.eINSTANCE.getConversationNode_ParticipantRefs());
		qnameMap.add(Bpmn2Package.eINSTANCE.getConversationNode_CorrelationKeys());
		qnameMap.add(Bpmn2Package.eINSTANCE.getMessageFlow_SourceRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getMessageFlow_TargetRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getMessageFlow_MessageRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getMessageFlowAssociation_InnerMessageFlowRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getMessageFlowAssociation_OuterMessageFlowRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getParticipant_InterfaceRefs());
		qnameMap.add(Bpmn2Package.eINSTANCE.getParticipant_EndPointRefs());
		qnameMap.add(Bpmn2Package.eINSTANCE.getParticipant_ProcessRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getParticipantAssociation_InnerParticipantRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getParticipantAssociation_OuterParticipantRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getCallableElement_SupportedInterfaceRefs());
		qnameMap.add(Bpmn2Package.eINSTANCE.getCallActivity_CalledElementRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getMultiInstanceLoopCharacteristics_LoopDataInputRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getMultiInstanceLoopCharacteristics_LoopDataOutputRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getMultiInstanceLoopCharacteristics_OneBehaviorEventRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getMultiInstanceLoopCharacteristics_NoneBehaviorEventRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getReceiveTask_MessageRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getReceiveTask_OperationRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getResourceRole_ResourceRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getSendTask_MessageRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getSendTask_OperationRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getServiceTask_OperationRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getItemAwareElement_ItemSubjectRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getBoundaryEvent_AttachedToRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getCatchEvent_EventDefinitionRefs());
		qnameMap.add(Bpmn2Package.eINSTANCE.getCompensateEventDefinition_ActivityRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getErrorEventDefinition_ErrorRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getEscalationEventDefinition_EscalationRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getLinkEventDefinition_Source());
		qnameMap.add(Bpmn2Package.eINSTANCE.getLinkEventDefinition_Target());
		qnameMap.add(Bpmn2Package.eINSTANCE.getMessageEventDefinition_OperationRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getMessageEventDefinition_MessageRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getSignal_StructureRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getSignalEventDefinition_SignalRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getThrowEvent_EventDefinitionRefs());
		qnameMap.add(Bpmn2Package.eINSTANCE.getProcess_Supports());
		qnameMap.add(Bpmn2Package.eINSTANCE.getProcess_DefinitionalCollaborationRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getLane_PartitionElementRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getGlobalChoreographyTask_InitiatingParticipantRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getChoreographyActivity_ParticipantRefs());
		qnameMap.add(Bpmn2Package.eINSTANCE.getChoreographyActivity_InitiatingParticipantRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getChoreographyTask_MessageFlowRef());
		qnameMap.add(Bpmn2Package.eINSTANCE.getCallChoreography_CalledChoreographyRef());
		qnameMap.add(BpmnDiPackage.eINSTANCE.getBPMNPlane_BpmnElement());
		qnameMap.add(BpmnDiPackage.eINSTANCE.getBPMNShape_BpmnElement());
		qnameMap.add(BpmnDiPackage.eINSTANCE.getBPMNShape_ChoreographyActivityShape());
		qnameMap.add(BpmnDiPackage.eINSTANCE.getBPMNEdge_BpmnElement());
		qnameMap.add(BpmnDiPackage.eINSTANCE.getBPMNEdge_SourceElement());
		qnameMap.add(BpmnDiPackage.eINSTANCE.getBPMNEdge_TargetElement());
		qnameMap.add(BpmnDiPackage.eINSTANCE.getBPMNLabel_LabelStyle());
	}

	public class OnlyNamedContainmentTypeInfo extends OnlyContainmentTypeInfo {
		
	    public boolean shouldSaveType(EClass objectType, EClassifier featureType, EStructuralFeature feature) {
	    	String name = ExtendedMetaData.INSTANCE.getName(objectType);
	    	if (name==null || name.isEmpty())
	    		return false;
	        return super.shouldSaveType(objectType, featureType, feature);
	    }

	    public boolean shouldSaveType(EClass objectType, EClass featureType, EStructuralFeature feature) {
	    	String name = ExtendedMetaData.INSTANCE.getName(objectType);
	    	if (name==null || name.isEmpty())
	    		return false;
	    	return super.shouldSaveType(objectType, featureType, feature);
	    }
	}
	
	/**
	 * Creates an instance of the resource.
	 * 
	 * @param uri
	 *            the URI of the new resource.
	 */
	public Bpmn2ModelerResourceImpl(URI uri) {
		super(uri);
		
		// override helper and uri handler in options map
		this.xmlHelper = (BpmnXmlHelper)createXMLHelper();
        this.uriHandler = new FragmentQNameURIHandler(xmlHelper);
        uriHandler.setBaseURI(uri);
        
        this.getDefaultLoadOptions().put(XMLResource.OPTION_URI_HANDLER, uriHandler);
        this.getDefaultLoadOptions().put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, true);
        this.getDefaultLoadOptions().put(XMLResource.OPTION_DISABLE_NOTIFY, true);
        this.getDefaultSaveOptions().put(XMLResource.OPTION_URI_HANDLER, uriHandler);

        ExtendedMetaData extendedMetadata = new XmlExtendedMetadata();
        this.getDefaultSaveOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetadata);
        this.getDefaultLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetadata);
        this.getDefaultSaveOptions().put(XMLResource.OPTION_SAVE_TYPE_INFORMATION, new OnlyNamedContainmentTypeInfo());
        this.getDefaultSaveOptions().put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
        this.getDefaultLoadOptions().put(XMLResource.OPTION_USE_LEXICAL_HANDLER, Boolean.TRUE);
        this.getDefaultSaveOptions().put(XMLResource.OPTION_ELEMENT_HANDLER, new ElementHandlerImpl(true));
        this.getDefaultSaveOptions().put(XMLResource.OPTION_ENCODING, "UTF-8"); //$NON-NLS-1$
        this.getDefaultSaveOptions().put(XMLResource.OPTION_USE_CACHED_LOOKUP_TABLE, new ArrayList<Object>());
        
        // some interesting things to play with:
//        this.getDefaultLoadOptions().put(XMLResource.OPTION_LAX_FEATURE_PROCESSING, true);
//        this.getDefaultLoadOptions().put(XMLResource.OPTION_LAX_WILDCARD_PROCESSING, true);
//        this.getDefaultLoadOptions().put(XMLResource.OPTION_RECORD_UNKNOWN_FEATURE, true);
//        this.getDefaultLoadOptions().put(XMLResource.OPTION_ANY_TYPE, BpmnDiPackage.eINSTANCE.getBPMNPlane());
        this.getDefaultLoadOptions().put(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP, xmlNameToFeatureMap);

        // only necessary if this resource will not be added to a ResourceSet instantly
        this.eAdapters().add(oppositeReferenceAdapter);
	}

	public void save(Map<?, ?> options) throws IOException {
		uriHandler.setBaseURI(getURI());
		xmlHelper.setResource(this);
		super.save(options);
	}

    @Override
    protected XMLHelper createXMLHelper() {
    	if (xmlHelper!=null)
    		return xmlHelper;
        return new Bpmn2ModelerXmlHelper(this);
    }

	/**
	 * Override this method to hook in our own XmlHandler
	 */
	@Override
	protected XMLLoad createXMLLoad() {
		return new XMLLoadImpl(createXMLHelper()) {
			Bpmn2ModelerXmlHandler handler;
			
			@Override
			protected DefaultHandler makeDefaultHandler() {
				handler = new Bpmn2ModelerXmlHandler(resource, helper, options);
				return handler;
			}
			
			@Override
			public void load(XMLResource resource, InputStream inputStream, Map<?, ?> options) throws IOException {
				try {
					super.load(resource, inputStream, options);
				}
				catch (Exception e) {
					BPMNDiagnostic error = new BPMNDiagnostic(e.getMessage());
					error.setLine(handler.getLineNumber());
					error.setColumn(handler.getColumnNumber());
					error.setLocation(handler.getLocation());
					if (!resource.getErrors().contains(error))
						resource.getErrors().add(error);
					throw new IOException(e);
				}
			}
		};
	}

	static class BPMNDiagnostic implements Resource.Diagnostic {

		private String message;
		private String location;
		private int column;
		private int line;

		public BPMNDiagnostic(String message) {
			this.message = message;
		}
		
		public void setLocation(String location) {
			this.location = location;
		}

		public String getLocation() {
			return location;
		}

		public void setColumn(int column) {
			this.column = column;;
		}

		public int getColumn() {
			return column;
		}

		public void setLine(int line) {
			this.line = line;
		}

		public int getLine() {
			return line;
		}

		public String getMessage() {
			return message;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof BPMNDiagnostic) {
				BPMNDiagnostic that = (BPMNDiagnostic)obj;
				return
						this.message.equals(that.message) &&
						this.line==that.line &&
						this.column==that.column;
			}
			else if (obj instanceof Exception) {
				String message = ((Exception)obj).getMessage();
				return this.message.equals(message);
			}
			return super.equals(obj);
		}
	}

	@Override
	protected XMLSave createXMLSave() {
        prepareSave();
		return new Bpmn2ModelerXMLSave(createXMLHelper()) {
		};
	}

	@Override
	protected void prepareSave() {
		EObject cur;
//		Definitions definitions = ImportHelper.getDefinitions(this);
		for (Iterator<EObject> iter = getAllContents(); iter.hasNext();) {
			cur = iter.next();

			setDefaultId(cur);

//			for (EObject referenced : cur.eCrossReferences()) {
//				setDefaultId(referenced);
//				if (definitions != null) {
//					Resource refResource = referenced.eResource();
//					if (refResource != null && refResource != this) {
//						createImportIfNecessary(definitions, refResource);
//					}
//				}
//			}
		}
	}

	/**
	 * Generate an ID attribute for the given BPMN2 element if not already set.
	 * 
	 * @param obj
	 *            The object whose ID should be set.
	 */
	private void setDefaultId(EObject obj) {
		if (obj.eClass() != null) {
			EStructuralFeature idAttr = obj.eClass().getEIDAttribute();
			if (idAttr != null && !obj.eIsSet(idAttr)) {
				obj.eSetDeliver(false);
				ModelUtil.setID(obj);
				obj.eSetDeliver(true);
			}
		}
	}

	@Override
	public void setURI(URI uri) {
		super.setURI(uri);
		xmlHelper.setResource(this);
		uriHandler.setBaseURI(uri);
	}

	/**
	 * We need to extend the standard SAXXMLHandler to hook into the handling of
	 * attribute references which may be either simple ID Strings or QNames.
	 * We'll search through all of the objects' IDs first to find the one we're
	 * looking for. If not, we'll try a QName search.
	 */
	protected static class Bpmn2ModelerXmlHandler extends BpmnXmlHandler {

		Bpmn2Preferences preferences = null;
		ImportUtil importHandler = new ImportUtil();
		String targetNamespace = null;

		public Bpmn2ModelerXmlHandler(XMLResource xmiResource, XMLHelper helper, Map<?, ?> options) {
			super(xmiResource, helper, options);
		}

		@Override
		public void startDocument() {
			super.startDocument();
			Bpmn2ModelerFactory.setEnableModelExtensions(false);
			preferences = Bpmn2Preferences.getInstance(xmlResource);
		}

		@Override
		public void endDocument() {
			super.endDocument();
			// Make sure there's a namespace prefix definition for typeLanguage.
			// We'll need that prefix to qualify data types defined in the type language.
			Definitions definitions = ModelUtil.getDefinitions(xmlResource);
			String typeLanguage = definitions.getTypeLanguage();
			if (typeLanguage!=null) {
				String prefix = NamespaceUtil.getPrefixForNamespace(helper.getResource(), typeLanguage);
    			if (prefix==null) {
    				TargetRuntime rt = TargetRuntime.getCurrentRuntime();
    				TypeLanguageDescriptor tld = rt.getTypeLanguageDescriptor(typeLanguage);
    				if (tld!=null)
    					prefix = tld.getPrefix();
    				NamespaceUtil.addNamespace(helper.getResource(), prefix, typeLanguage);
    			}
			}
			// Load all of the Imports and generate Interfaces, Operations, Messages, Faults and ItemDefinitions
			for (Import imp : definitions.getImports()) {
				try {
	            	Object importObject = importHandler.loadImport(imp);
	            	if (importObject!=null) {
	            		importHandler.addImportObjects(imp, importObject);
	            	}
				}
				catch (Exception e) {
					BPMNDiagnostic error = new BPMNDiagnostic(e.getMessage());
					error.setLine(getLineNumber());
					error.setColumn(getColumnNumber());
					error.setLocation(getLocation());
					xmlResource.getErrors().add(error);
				}
            }
			
			// Fix up the descriptions for BPMNDiagrams
			for (BPMNDiagram d : definitions.getDiagrams()) {
	            // Set the diagram's description AFTER it has been completely populated
				ExtendedPropertiesAdapter epa = ExtendedPropertiesAdapter.adapt(d);
				if (epa!=null) {
					epa.setProperty(ExtendedPropertiesAdapter.LONG_DESCRIPTION, epa.getDescription(d));
				}
			}
			Bpmn2ModelerFactory.setEnableModelExtensions(true);
		}
		
		@Override
		protected void handleForwardReferences(boolean isEndDocument) {
			if (isEndDocument) {
				List<SingleReference> resolved = new ArrayList<SingleReference>();
				for (SingleReference ref : forwardSingleReferences) {
					EObject referencedObject = null;
					try {
						referencedObject = xmlResource.getEObject((String) ref.getValue());
					} catch (RuntimeException exception) {}
					if (referencedObject==null) {
						// The forward reference may be in an external document.
						// Check the reference type and its owner, then search
						// external documents contained in the same project.
						EObject referencingObject = ref.getObject();
						EStructuralFeature referencingFeature = ref.getFeature();
						String id = (String) ref.getValue();
						referencedObject = importHandler.resolveExternalReference(referencingObject, referencingFeature, id);
						if (referencedObject != null) {
							resolved.add(ref);
							if (shouldResolveExternals(referencingObject, referencedObject, id)) {
								importHandler.addImport(xmlResource, ModelUtil.getDefinitions(referencedObject));
								xmlResource.getResourceSet().getResources().add(referencedObject.eResource());
						        setFeatureValue(referencingObject, referencingFeature, referencedObject, ref.getPosition());
							}
						}
					}
				}
				if (!resolved.isEmpty())
					forwardSingleReferences.removeAll(resolved);
			}
			super.handleForwardReferences(isEndDocument);
		}

		/**
		 * Check if the loader should allow external references to other files
		 * in the project. This is controlled by a User Preference setting.
		 * 
		 * @param referencingObject the object referencing an external object.
		 * @param referencedObject the referenced object contained in another
		 *            file in the project.
		 * @param id the ID string of the referenced object.
		 * @return true if externals should be resolved, false to cause an error
		 *         for missing references.
		 */
		private boolean shouldResolveExternals(EObject referencingObject, EObject referencedObject, String id) {
			if (preferences.getResolveExternals()==0)
				return false;
			if (preferences.getResolveExternals()==1)
				return true;
			try {
				Resource resource = referencedObject.eResource();
				IPath path = new Path(resource.getURI().toPlatformString(true));
				boolean doit = MessageDialog.openQuestion(new Shell(),
						Messages.Bpmn2ModelerResourceSetImpl_External_Reference_Found_Title,
						NLS.bind(
								Messages.Bpmn2ModelerResourceSetImpl_External_Reference_Found_Message,
								new Object[] {
									referencedObject.eClass().getName(),
									id,
									ModelUtil.getLabel(referencingObject)+" \""+ModelUtil.getName((BaseElement)referencingObject)+"\"",
									path.toString()}
						)
					);
				return doit;
			}
			catch (Exception e) {}
			return true;
		}
		
		@Override
		protected EStructuralFeature getFeature(EObject object, String prefix, String name, boolean isElement) {
			EStructuralFeature feature = null;
			String nsURI = (prefix==null ? targetNamespace : helper.getURI(prefix));
			ModelDecorator md = ModelDecorator.getModelDecorator(nsURI);
			if (md!=null) {
				feature = md.getEStructuralFeature(object, name);
				if (feature!=null) {
//					System.out.println("found feature "+object.eClass().getName()+"."+name+" in pkg "+pkg.getName());
					return feature;
				}
			}
			feature = super.getFeature(object, prefix, name, isElement);
//			if (feature!=null)
//				System.out.println("found feature "+object.eClass().getName()+"."+name);
//			else
//				System.out.println("feature "+prefix+":"+name+" not found!!!");
			return feature;
		}

		@Override
		protected EObject createObjectFromFeatureType(EObject peekObject, EStructuralFeature feature) {
			int lineNumber = getLineNumber();
			EObject newObject = null;
			String nsURI = ExtendedMetaData.INSTANCE.getNamespace(feature);
			EPackage pkg = ModelDecorator.getEPackage(nsURI);
			if (pkg!=null) {
				EClassifier eType = feature.getEType();
				newObject = pkg.getEFactoryInstance().create((EClass)eType);
				
//				ModelDecoratorAdapter mda = AdapterUtil.adapt(pkg, ModelDecoratorAdapter.class);
//				feature = mda.getEStructuralFeature(peekObject, feature);
				if (feature!=null) {
					ExtendedPropertiesAdapter epa = ExtendedPropertiesAdapter.adapt(peekObject);
					if (epa!=null) {
						epa.getFeatureDescriptor(feature).setValue(newObject);
						processObject(newObject);
						handleObjectAttribs(newObject);
					}
				}
			}
			else {
				newObject = super.createObjectFromFeatureType(peekObject, feature);
				TargetRuntime rt = TargetRuntime.getCurrentRuntime();
				String id = rt.getCustomTaskId(newObject);
				if (id!=null) {
					// if this is a CustomElement we need to discard this object and construct it
					// properly such that all extension attributes and elements are created and
					// initialized.
					EFactory factory = newObject.eClass().getEPackage().getEFactoryInstance();
					ObjectPropertyProvider adapter = ObjectPropertyProvider.adapt(factory, xmlResource);
					try {
						// remove all traces of the old object
						objects.pop();
						mixedTargets.pop();
						types.pop();
						EcoreUtil.delete(newObject);
						// set up the BPMN2 object factory to construct this CustomElement
						adapter.setProperty(GraphitiConstants.CUSTOM_ELEMENT_ID, id);
						newObject = super.createObjectFromFeatureType(peekObject, feature);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			if (newObject!=null) {
				ExtendedPropertiesAdapter epa = ExtendedPropertiesAdapter.adapt(newObject);
				if (epa!=null) {
					epa.setProperty(ExtendedPropertiesAdapter.LINE_NUMBER, lineNumber);
				}
			}

			return newObject;
		}
		
		@Override
		protected void setFeatureValue(EObject object, EStructuralFeature feature, Object value, int position) {
			ModelDecorator md = ModelDecorator.getModelDecorator(feature);
			if (md!=null) {
				EStructuralFeature f = md.getEStructuralFeature(object, feature.getName());
				if (f!=null) {
					ExtendedPropertiesAdapter epa = ExtendedPropertiesAdapter.adapt(object);
					epa.getFeatureDescriptor(f).setValue(value, position);
					return;
				}
				
//				ExtendedPropertiesAdapter epa = ExtendedPropertiesAdapter.adapt(object);
//				epa.getFeatureDescriptor(feature).setValue(value, position);
			}
			super.setFeatureValue(object, feature, value, position);
		}

		@Override
		protected void handleObjectAttribs(EObject obj) {
			super.handleObjectAttribs(obj);
			if (attribs != null) {
				for (int i = 0, size = attribs.getLength(); i < size; ++i) {
					String name = attribs.getQName(i);
					if (name.equals(XMLResource.XML_NS)) {
						// create an ns prefix in the prefix map for this default namespace
						// and qualify any qnameFeatures contained in this object...
						String namespaceURI = attribs.getValue(i);
						for (EStructuralFeature f : obj.eClass().getEAllStructuralFeatures()) {
							if (qnameMap.contains(f)) {
								Object value = obj.eGet(f);
								if (ModelUtil.isStringWrapper(value)) {
									String localpart = ModelUtil.getStringWrapperValue(value);
									if (localpart!=null && !localpart.isEmpty() && !localpart.contains(":")) { //$NON-NLS-1$
										String prefix = helper.getPrefix(namespaceURI);
										if (prefix==null || prefix.isEmpty()) {
											for (int index = 0; true; ++index) {
												prefix = "ns" + index; //$NON-NLS-1$
												String ns = helper.getPrefixToNamespaceMap().get(prefix);
												if (ns==null)
													break;
											}
											helper.addPrefix(prefix, namespaceURI);	
										}
										ModelUtil.setStringWrapperValue(value, prefix + ":" + localpart); //$NON-NLS-1$
									}
								}
							}
						}
					}
				}
			}

			if (obj instanceof BPMNShape) {
				BPMNShape bpmnShape = (BPMNShape)obj;

				Hashtable<String,String> map = new Hashtable<String,String>();
				if (attribs != null) {
					for (int i = 0, size = attribs.getLength(); i < size; ++i) {
						String key = attribs.getQName(i);
						String value = attribs.getValue(i);
						map.put(key, value);
					}
					preferences.applyBPMNDIDefaults(bpmnShape, map);
				}
			}
			else if (obj instanceof ItemDefinition) {
				ItemDefinition itemDef = (ItemDefinition)obj;

				Definitions definitions = ModelUtil.getDefinitions(xmlResource);
				URI referencingURI = ImportHelper.makeURICanonical(resourceURI);
				String location = ModelUtil.getStringWrapperValue(itemDef.getStructureRef());
				if (location!=null) {
					int i = location.indexOf("$"); //$NON-NLS-1$
					if (i>0)
						location = location.substring(0,i);
					URI uri = URI.createURI(location).resolve(referencingURI);
					uri = uri.trimFragment();
					Import imp = ImportHelper.findImportForLocation(definitions, uri);
					itemDef.setImport(imp);
				}
			}
            else if (obj instanceof Definitions) {
            	// fetch the targetNamespace from Definitions object
            	targetNamespace = ((Definitions)obj).getTargetNamespace();
            }

		}

		/**
		 * Overridden to be able to convert ID references in attributes to URIs
		 * during load. If the reference can't be found by its ID, we'll try a
		 * QName search (done in the super class)
		 * 
		 * @param ids
		 *            In our case the parameter will contain exactly one ID that
		 *            we resolve to URI.
		 */
		@Override
		protected void setValueFromId(EObject object, EReference eReference, String ids) {

			Object value = null;
			// Handle QNames and arbitrary strings in BPMN2 element references
			if ( qnameMap.contains(eReference) ) {
				// This reference might be a QName (according to the BPMN2 spec!)
				// or it might not, in the case of some jBPM Java type references.
				int i = ids.indexOf(":"); //$NON-NLS-1$
				if (i>0) {
					// if the ID string is a QName, try to resolve and load the object
					String prefix = ids.substring(0,i);
					String localname = ids.substring(i+1);
					String namespace = helper.getNamespaceURI(prefix);
					if (namespace!=null && namespace.equals(targetNamespace)) {
						// namespace for prefix is the same as targetNamespace
						// so remove the prefix to avoid confusing the XMLHandler
						ids = localname;
					}
					else {
						// this thing is in another namespace, possibly in an external document
						Import imp = importHandler.findImportForNamespace(helper.getResource(), namespace);
						if (imp!=null) {
							value = importHandler.getObjectForLocalname(imp, object, eReference, localname);
						}
					}
				}
				
				if (value==null) {
					// we can't find the object in any of our imports,
					// so create a string wrapper EObject for this thing
					value = ModelUtil.createStringWrapper(ids);
				}
				
				if (value!=null && eReference.getEType().isInstance(value)) {
					try {
						if (eReference.isMany()) {
							((EList)object.eGet(eReference)).add(value);
						}
						else {
							object.eSet(eReference, value);
						}
						return;
					} catch (Exception e) {
						String msg = NLS.bind(
							Messages.Bpmn2ModelerResourceImpl_Invalid_Reference,
							new Object[] {object, eReference, value});
						IStatus s = new Status(Status.ERROR, Activator.PLUGIN_ID,
								msg, e);
						Activator.getDefault().logStatus(s);
					}
				}
			}
			
			// And yet another hack to deal with files generated by Savara:
			// Savara creates some object references as QNames and, while the
			// "Official" BPMN 2.0 xsd says these SHOULD be QNames, the bpmn2 EMF
			// model has these defined as "resolveProxies=false" which means they
			// will NOT get resolved.
			if (/*!eReference.isResolveProxies() && */ids.contains(":")) { //$NON-NLS-1$
				// Resolve QNames here: if they are internal objects,
				// simply replace the ID string with the URI fragment.
				// If they are not internal objects, then there's a
				// problem with the file!
				String resolvedId = ((QNameURIHandler) uriHandler).convertQNameToUri(ids);
				URI resolvedURI = URI.createURI(resolvedId);
				URI resourceURI = xmlResource.getURI();
				if (resolvedURI.trimFragment().equals(resourceURI))
					ids = resolvedURI.fragment();
			}
			super.setValueFromId(object, eReference, ids);
		}

		public int getLineNumber() {
			return super.getLineNumber();
		}

		public int getColumnNumber() {
			return super.getColumnNumber();
		}

		public String getLocation() {
			return super.getLocation();
		}

        @Override
        public void startElement(String uri, String localName, String name) {
			super.startElement(uri, localName, name);
		    EObject peekObject = objects.peekEObject();
//		    if (peekObject instanceof Definitions) {
//		    	// create all model extensions for this target runtime so that all
//		    	// extension attributes and elements are available during loading.
//		    	TargetRuntime rt = TargetRuntime.getCurrentRuntime();
//		    	rt.createModelExtensions(peekObject.eResource());
//		    }
            if (peekObject!=null && peekObject.eClass() == Bpmn2Package.eINSTANCE.getExpression()) {
            	// If the element is an Expression (not a FormalExpression) then use the CDATA
            	// as the body of a Formal Expression (because Expression does not have a body)
                text = new StringBuffer();
            }
		}
		
		@Override
        public void endElement(String uri, String localName, String name) {
            EObject peekObject = objects.peek();
            if (peekObject instanceof Expression) {
            	// if the element is an Expression, replace it with a FormalExpression and set
            	// its body using the CDATA of the Expression element.
   				FormalExpression fe = Bpmn2Factory.eINSTANCE.createFormalExpression();
   				EObject owner = peekObject.eContainer();
   				if (owner!=null) {
	   				for (EStructuralFeature f : owner.eClass().getEAllStructuralFeatures()) {
	   					if (owner.eGet(f) == peekObject) {
	   		   				owner.eSet(f, fe);
	   		   				break;
	   					}
	   				}
	   				objects.pop();
	   				objects.push(fe);
	                types.pop();
	                types.push(Bpmn2Package.eINSTANCE.getFormalExpression_Body());
	                
	                EStructuralFeature mixedFeature = extendedMetaData.getMixedFeature(fe.eClass());
	                if (mixedFeature != null)
	                {
	                  mixedTargets.push((FeatureMap)fe.eGet(mixedFeature));
	                }
	                else
	                {
	                  mixedTargets.push(null);
	                }
   				}
            }
            super.endElement(uri, localName, name);
        }
	}
	
	public class Bpmn2ModelerXMLSave extends XMLSaveImpl {
		protected float minX = Float.MAX_VALUE;
		protected float minY = Float.MAX_VALUE;
		protected int lineNum = 1;
		protected int lineOffset = 0;

		@SuppressWarnings("serial")
		protected class Bpmn2ModelerXMLString extends XMLString {
			public Bpmn2ModelerXMLString(String publicId, String systemId) {
				super(Integer.MAX_VALUE, publicId, systemId, null);
			}
        	@Override
        	public void addAttribute(String name, String value) {
        		// This special little hack removes namespace declarations
        		// and schemaLocation attributes for the XSI namespace if the prefix
        		// is anything other than "xsi". The EMF serializers rely on the fact
        		// that the XSI namespace prefix is ALWAYS "xsi" and they WILL create
        		// a duplicate namespace declaration if one already existed under a
        		// different prefix. This would result a nasty warning from the parser.
        		if (XSI_URI.equals(value) && name.startsWith("xmlns:")) { //$NON-NLS-1$
        			int i = name.indexOf(":"); //$NON-NLS-1$
        			String prefix = name.substring(i+1);
        			if (!ExtendedMetaData.XSI_PREFIX.equals(prefix))
        				return;
        		}
        		if (name.contains(":schemaLocation")) { //$NON-NLS-1$
        			if (!XSI_SCHEMA_LOCATION.equals(name))
        				return;
        		}
        		super.addAttribute(name, value);
        	}

			@Override
			public void addAttributeNS(String prefix, String localName, String value) {
				// Same hack as above - see comments.
				if (XSI_URI.equals(value) && !ExtendedMetaData.XSI_PREFIX.equals(localName))
					return;
				super.addAttributeNS(prefix, localName, value);
			}
			
			@Override
			public void addLine() {
				++lineNum;
				super.addLine();
				lineOffset = getLength();
			}
			
			public int getLineNum() {
				return lineNum;
			}
			
			public int getColumnNum() {
				return getLength() - lineOffset + 1;
			}
		};
		
		public Bpmn2ModelerXMLSave(XMLHelper helper) {
			super(helper);
			helper.getPrefixToNamespaceMap().clear();
		}

		@Override
		protected void addNamespaceDeclarations() {
			EMap<String,String> map = helper.getPrefixToNamespaceMap();
			Definitions definitions = ModelUtil.getDefinitions(helper.getResource());
			String typeLanguage = definitions.getTypeLanguage();
			if (!map.containsValue(typeLanguage)) {
				String prefix = "tl"; //$NON-NLS-1$
				TargetRuntime rt = TargetRuntime.getCurrentRuntime();
				TypeLanguageDescriptor tld = rt.getTypeLanguageDescriptor(typeLanguage);
				if (tld!=null)
					prefix = tld.getPrefix();
				doc.addAttributeNS(XMLResource.XML_NS, prefix, typeLanguage);
			}
			super.addNamespaceDeclarations();
		}

		@Override
		protected void init(XMLResource resource, Map<?, ?> options) {
			super.init(resource, options);
			featureTable = new Bpmn2ModelerXMLSave.Bpmn2Lookup(map, extendedMetaData, elementHandler);
			
			final List<BPMNDiagram> diagrams = getAll(BPMNDiagram.class, resource);
			for (BPMNDiagram bpmnDiagram : diagrams) {
				findMinXY(bpmnDiagram);
			}
			
			doc = createXMLString();
		}

		protected XMLString createXMLString() {
			return new Bpmn2ModelerXMLString(publicId, systemId);
		}
		
		protected Bpmn2ModelerXMLString getXMLString() {
			if (doc==null) {
				createXMLString();
			}
			return (Bpmn2ModelerXMLString)doc;
		}
		
        @Override
		protected void endSave(List<? extends EObject> contents) throws IOException
		{
        	Bpmn2ModelerDocumentRootImpl documentRoot = null;
        	if (contents.size()>0 && contents.get(0) instanceof Bpmn2ModelerDocumentRootImpl) {
        		documentRoot = (Bpmn2ModelerDocumentRootImpl)contents.get(0);
				documentRoot.setDeliver(false);
			}
        	
			super.endSave(contents);
			
			if (documentRoot!=null) {
				documentRoot.setDeliver(true);
			}
		}

		@Override
        protected boolean shouldSaveFeature(EObject o, EStructuralFeature f) {
            if (o instanceof BPMNShape && f==BpmnDiPackage.eINSTANCE.getBPMNShape_IsHorizontal()) {
            	BPMNShape s = (BPMNShape)o;
            	if (s.getBpmnElement() instanceof Lane || s.getBpmnElement() instanceof Participant)
            		return true;
            }
            
            // we also want to store x and y with value zero, would be skipped because of default value otherwise
            if (o instanceof Bounds || o instanceof Point) {
            	return true;
            }
            
            // empty Expressions should not be saved
            if (f!=null && (f.getEType() == Bpmn2Package.eINSTANCE.getExpression() ||
            		f.getEType() == Bpmn2Package.eINSTANCE.getFormalExpression())) {
            	Expression expression = (Expression)o.eGet(f);
            	if (expression==null)
            		return false;
            	if (expression instanceof FormalExpression) {
	            	FormalExpression formalExpression = (FormalExpression)expression;
            		String body = ModelUtil.getExpressionBody(formalExpression);
	            	if (body==null) {
	            		return false;
	            	}
            	}
            }
            
            if (o!=null && o instanceof Documentation) {
            	Documentation doc = (Documentation)o;
            	if (doc.getText()==null || doc.getText().isEmpty())
            		return false;
            }
            
            if (f!=null && f.getEType() == Bpmn2Package.eINSTANCE.getDocumentation()) {
            	EList<Documentation> docList = (EList<Documentation>)o.eGet(f);
            	if (docList.isEmpty())
            		return false;
            	int empty = 0;
            	for (Documentation doc : docList) {
            		if (doc.getText()==null || doc.getText().isEmpty())
            			++empty;
            	}
            	if (empty==docList.size())
            		return false;
            }            	
            
            // don't serialize the "body" attribute of FormalExpressions because the expression text
            // is already in the CDATA section of the <bpmn2:expression> element. This would cause
            // the expression text to be duplicated on deserialization.
            // Same goes for Documentation.text
			if (Bpmn2Package.eINSTANCE.getFormalExpression_Body().equals(f) ||
					Bpmn2Package.eINSTANCE.getDocumentation_Text().equals(f))
				return false;
            
			// don't save Assignments if they are invalid: Assignments must have
			// both a "from" and "to" expression and they may not be empty strings.
			if (o instanceof DataAssociation && "assignment".equals(f.getName())) { //$NON-NLS-1$
				DataAssociation da = (DataAssociation)o;
				for (Assignment a : da.getAssignment()) {
					Expression from = a.getFrom();
					if (from instanceof FormalExpression) {
						String body = ModelUtil.getExpressionBody(((FormalExpression)from));
						if (body==null || body.isEmpty())
							return false;
					}
					Expression to = a.getTo();
					if (to instanceof FormalExpression) {
						String body = ModelUtil.getExpressionBody(((FormalExpression)to));
						if (body==null || body.isEmpty())
							return false;
					}
				}
			}

			if (f == Bpmn2Package.eINSTANCE.getBaseElement_ExtensionValues()) {
				// check if this element is (or should be) empty
				boolean shouldSave = true;
				for (ExtensionAttributeValue ev : (EList<ExtensionAttributeValue>)o.eGet(f)) {
					BasicFeatureMap map = (BasicFeatureMap) ev.getValue();
					Iterator<FeatureMap.Entry> mi = map.iterator();
					while (mi.hasNext()) {
						FeatureMap.Entry entry = mi.next();
						Object v = entry.getValue();
						if (v instanceof EObject) {
							Iterator<Adapter> ai = ((EObject)v).eAdapters().iterator();
							while (ai.hasNext()) {
								Adapter a = ai.next();
								if (a instanceof IExtensionValueAdapter) {
									if (!((IExtensionValueAdapter)a).shouldSaveElement((EObject)v)) {
										shouldSave = false;
										break;
									}
								}
							}
						}
					}
				}
				return shouldSave;
			}
			
			Iterator<Adapter> ai = o.eAdapters().iterator();
			while (ai.hasNext()) {
				Adapter a = ai.next();
				if (a instanceof IExtensionValueAdapter) {
					if (!((IExtensionValueAdapter)a).shouldSaveFeature(o,f))
						return false;
				}
			}
			
            return super.shouldSaveFeature(o, f);
        }
		
		protected <T> List<T> getAll(Class<T> class1, Resource resource) {
			ArrayList<T> l = new ArrayList<T>();
			TreeIterator<EObject> contents = resource.getAllContents();
			for (; contents.hasNext();) {
				Object t = contents.next();
				if (class1.isInstance(t)) {
					l.add((T) t);
				}
			}
			return l;
		}
		
		protected void findMinXY(BPMNDiagram bpmnDiagram) {
			EList<DiagramElement> elements = (EList<DiagramElement>) bpmnDiagram.getPlane().getPlaneElement();
			for (DiagramElement e : elements) {
				if (e instanceof BPMNShape) {
					Bounds b = ((BPMNShape)e).getBounds();
					minX = Math.min(minX, b.getX());
					minY = Math.min(minY, b.getY());
				}
				else if (e instanceof BPMNEdge) {
					List<Point> points = ((BPMNEdge)e).getWaypoint();
					for (Point p : points) {
						minX = Math.min(minX, p.getX());
						minY = Math.min(minY, p.getY());
					}

				}
				else if (e instanceof BPMNLabel) {
					Bounds b = ((BPMNLabel)e).getBounds();
					minX = Math.min(minX, b.getX());
					minY = Math.min(minY, b.getY());
				}
			}
		}

		@Override
		protected void saveContainedMany(EObject o, EStructuralFeature f) {
			if (o instanceof BPMNPlane && f==DiPackage.eINSTANCE.getPlane_PlaneElement()) {
				// Sort the Diagram Elements in ascending Z-order
				BPMNPlane plane = (BPMNPlane) o;
				EList<DiagramElement> originalList = new BasicEList<DiagramElement>();
				originalList.addAll(plane.getPlaneElement());
				
				plane.eSetDeliver(false);
				ECollections.sort((EList<DiagramElement>) plane.getPlaneElement(), new DIZorderComparator());
				super.saveContainedMany(o, f);
				ECollections.setEList((EList)plane.getPlaneElement(),originalList);
				plane.eSetDeliver(true);
			}
			else if (o instanceof Definitions && f==Bpmn2Package.eINSTANCE.getDefinitions_RootElements()) {
				// Sort the Definitions Root Elements to avoid forward references
				Definitions definitions = (Definitions) o;
				EList<RootElement> originalList = new BasicEList<RootElement>();
				originalList.addAll(definitions.getRootElements());

				definitions.eSetDeliver(false);
				ECollections.sort((EList<RootElement>)definitions.getRootElements(), new RootElementComparator());
				super.saveContainedMany(o, f);
				ECollections.setEList((EList)definitions.getRootElements(),originalList);
				definitions.eSetDeliver(true);
			}
			else {
				super.saveContainedMany(o, f);
			}
		}

		@Override
		protected void saveElement(EObject o, EStructuralFeature f) {
			
			Iterator<Adapter> ai = o.eAdapters().iterator();
			while (ai.hasNext()) {
				Adapter a = ai.next();
				if (a instanceof IExtensionValueAdapter) {
					if (!((IExtensionValueAdapter)a).shouldSaveElement(o))
						return;
				}
			}

			float oldX = 0, oldY = 0;
			List<Point> oldPoints = null;
			
			if (minX<0 || minY<0) {
				if (o instanceof BPMNShape) {
					Bounds b = ((BPMNShape)o).getBounds();
					b.eSetDeliver(false);
					if (minX<0) {
						oldX = b.getX();
						b.setX(oldX - minX);
					}
					if (minY<0) {
						oldY = b.getY();
						b.setY(oldY - minY);
					}
				}
				else if (o instanceof BPMNEdge) {
					List<Point> points = ((BPMNEdge)o).getWaypoint();
					oldPoints = new ArrayList<Point>();
					for (Point p : points) {
						p.eSetDeliver(false);
						Point oldPoint = DcFactory.eINSTANCE.createPoint();
						oldPoint.setX(p.getX());
						oldPoint.setY(p.getY());
						oldPoints.add(oldPoint);
						if (minX<0)
							p.setX( p.getX() - minX);
						if (minY<0)
							p.setY( p.getY() - minY);
					}
				}
				else if (o instanceof BPMNLabel) {
					Bounds b = ((BPMNLabel)o).getBounds();
					if (b!=null) {
						b.eSetDeliver(false);
						if (minX<0) {
							oldX = b.getX();
							b.setX(oldX - minX);
						}
						if (minY<0) {
							oldY = b.getY();
							b.setY(oldY - minY);
						}
					}
				}
			}

			super.saveElement(o, f);
			
			if (minX<0 || minY<0) {
				if (o instanceof BPMNShape) {
					Bounds b = ((BPMNShape)o).getBounds();
					if (minX<0) {
						b.setX(oldX);
					}
					if (minY<0) {
						b.setY(oldY);
					}
					b.eSetDeliver(true);
				}
				else if (o instanceof BPMNEdge) {
					List<Point> points = ((BPMNEdge)o).getWaypoint();
					int index = 0;
					for (Point p : points) {
						if (minX<0)
							p.setX(oldPoints.get(index).getX());
						if (minY<0)
							p.setY(oldPoints.get(index).getY());
						p.eSetDeliver(true);
						++index;
					}
				}
				else if (o instanceof BPMNLabel) {
					Bounds b = ((BPMNLabel)o).getBounds();
					if (b!=null) {
						if (minX<0) {
							b.setX(oldX);
						}
						if (minY<0) {
							b.setY(oldY);
						}
						b.eSetDeliver(true);
					}
				}
			}
		}

		@Override
		protected String convertURI(String s) {
			URI uri = helper.deresolve( URI.createURI(s) );
			return uri.toString();
		}

		@Override
		public void traverse(List<? extends EObject> contents) {
			for (EObject e : contents) {
				if (e instanceof Definitions) {
					List<RootElement> roots = ((Definitions) e).getRootElements();
					Process p = null;
					for (RootElement root : roots) {
						if (root instanceof Process) {
							p = (Process) root;
						}
					}
					if (p != null) {
						((Definitions) e).getRootElements().remove(p);
						((Definitions) e).getRootElements().add((RootElement) p);
					}
				}
			}
			super.traverse(contents);
		}

		public class Bpmn2Lookup extends XMLSaveImpl.Lookup {
			public Bpmn2Lookup(XMLMap map, ExtendedMetaData extendedMetaData, ElementHandler elementHandler) {
				super(map, extendedMetaData, elementHandler);
			}

			@Override
			public EStructuralFeature[] getFeatures(EClass cls) {
				int index = getIndex(cls);
				EClass c = classes[index];

				if (c == cls) {
					return features[index];
				}

				EStructuralFeature[] featureList = listFeatures(cls);
				EStructuralFeature[] newFeatureList = featureList;
				if (c == null) {
					newFeatureList = reorderFeatureList(cls, featureList);
					classes[index] = cls;
					features[index] = newFeatureList;
					featureKinds[index] = listKinds(newFeatureList);
				}
				return newFeatureList;
			}

			/**
			 * Specifies the serialization order of features for a given ECLass.
			 * Subclasses should override this behavior.
			 * The default implementation simply returns the original list.
			 * 
			 * @param cls - EClass whose features need to be reordered
			 * @param featureList - the original feature list as provided by the Bpmn2Package
			 * @return a feature list that specifies the new ordering
			 */
			protected EStructuralFeature[] reorderFeatureList(EClass cls, EStructuralFeature[] featureList) {
				return featureList;
			}
			
			/**
			 * Change the serialization order of features for a given EClass. The string array "featureNames"
			 * specifies a new ordering for some or all of the features in the feature list; these names must
			 * be contiguous in the original list. For example, given the following original feature list:
			 * 
			 * "w"
			 * "x"
			 * "a"
			 * "b"
			 * "c"
			 * "d"
			 * "e"
			 * "y"
			 * "z"
			 * 
			 * The featureNames list may not contain this:
			 * 
			 * "b"
			 * "a"
			 * "e"
			 * "d"
			 * 
			 * because the two sets "b", "a" and "e", "d" are not contiguous. The correct way of specifying this is:
			 * 
			 * "b"
			 * "a"
			 * "c"
			 * "e"
			 * "d"
			 * 
			 * Alternatively, the client could call this method twice, the first time with the first set ("b" and "a")
			 * and a second time with the second set ("e" and "d).
			 * 
			 * @param cls
			 * @param featureList
			 * @param featureNames
			 * @return
			 */
			protected EStructuralFeature[] reorderFeatureList(EClass cls, EStructuralFeature[] featureList, String[] featureNames) {
				// the reordered list of features
				EStructuralFeature[] newFeatureList = new EStructuralFeature[featureList.length];
				// map of old to new array indexes
				int[] indexMap = new int[featureList.length];
				for (int i=0; i<indexMap.length; ++i)
					indexMap[i] = -1;

				int startIndex = Integer.MAX_VALUE;
				for (int i=0; i<featureList.length; ++i) {
					for (int j=0; j<featureNames.length; ++j) {
						if (featureList[i].getName().equals(featureNames[j])) {
							if (i<startIndex) {
								startIndex = i;
								break;
							}
						}
					}
				}
				
				for (int newIndex=0; newIndex<featureNames.length; ++newIndex) {
					String fn = featureNames[newIndex];
					for (int oldIndex=0; oldIndex<featureList.length; ++oldIndex) {
						EStructuralFeature f = featureList[oldIndex];
						if (f.getName().equalsIgnoreCase(fn)) {
							indexMap[oldIndex] = newIndex + startIndex;
							break;
						}
					}
				}
				
				for (int oldIndex=0; oldIndex<featureList.length; ++oldIndex) {
					EStructuralFeature f = featureList[oldIndex];
					int newIndex = indexMap[oldIndex];
					if (newIndex>=0) {
						newFeatureList[newIndex] = featureList[oldIndex];
					}
					else
						newFeatureList[oldIndex] = featureList[oldIndex];
				}
				return newFeatureList;
			}
		}
	}
	
	// TODO check this, is this the correct way to deal with this ID prefixes
	/**
	 * QName handler to make create URIs out of the fragment, which is the local part of the QName
	 * 
	 * Most other tools dont understand QNames in referencing attributes
	 * 
	 * @author drobisch
	 *
	 */
	public static class FragmentQNameURIHandler extends QNameURIHandler {

		protected BpmnXmlHelper xmlHelper;

		public FragmentQNameURIHandler(BpmnXmlHelper xmlHelper) {
			super(xmlHelper);
			this.xmlHelper = xmlHelper;
		}

		@Override
		public URI resolve(URI uri) {
			URI resolvedUri = super.resolve(uri);
			if (resolvedUri.isRelative())
				resolvedUri = resolvedUri.resolve(baseURI);
			return resolvedUri;
		}

		@Override
		public URI deresolve(URI uri) {
			String fragment = uri.fragment();
			if (fragment != null && !fragment.startsWith("/")) { //$NON-NLS-1$
				// return just fragment (i.e. without the '#') but only if local reference
				URI otherURI = uri.trimFragment();
				if (baseURI.equals(otherURI))
					return URI.createURI(fragment);
			}

			if (
					(uri.isPlatformResource() && baseURI.isPlatformResource()) ||
					(uri.isFile() && baseURI.isFile())) {
				uri = uri.deresolve(baseURI, false, true, true);
					return uri;
			}
			return super.deresolve(uri);
		}

		@Override
		public String convertQNameToUri(String qName) {
			if (qName.contains("#") || qName.contains("/")) { //$NON-NLS-1$ //$NON-NLS-2$
				// We already have an URI and not QName, e.g. URL
				return qName;
			}
			
			// Split into prefix and local part (fragment)
			String[] parts = qName.split(":"); //$NON-NLS-1$
			String prefix, fragment;
			if (parts.length == 1) {
				prefix = null;
				fragment = qName;
			} else if (parts.length == 2) {
				prefix = parts[0];
				fragment = parts[1];
			} else
				throw new IllegalArgumentException(Messages.Bpmn2ModelerResourceImpl_Illegal_QName + qName);

			if (fragment.contains(".")) { //$NON-NLS-1$
				// HACK: officially IDs can contain ".", but unfortunately
				// XmlHandler calls resolve also for xsi:schemaLocation stuff
				// and similar, that are
				// NO URIs. We must not process them.
				return qName;
			}

			boolean isTargetNamespacePrefix = false;
			try {
				isTargetNamespacePrefix = xmlHelper.isTargetNamespace(prefix);
			} catch (Exception e) {
			}

			if (!isTargetNamespacePrefix) {
				String uriString = xmlHelper.getPathForPrefix(prefix).appendFragment(fragment).toString();
				URI uri = URI.createURI(uriString);
				ResourceSet rs = ModelUtil.slightlyHackedResourceSet(xmlHelper.getResource().getResourceSet());
				Resource r = ((Bpmn2ModelerResourceSetImpl)rs).getResource(uri, true, "wsdl"); // the only problem here... //$NON-NLS-1$
				if (r instanceof WSDLResourceImpl) {
					EObject o = r.getContents().get(0);
					Definition def = (Definition)o;
					// if eReference -- operation.implementationref
					// search all of these:
					for (PortType pt : (List<PortType>)def.getEPortTypes()) {
						for (org.eclipse.wst.wsdl.Operation op : (List<org.eclipse.wst.wsdl.Operation>)pt.getEOperations()) {
							
						}
					}
					// and so on for other eReference bpmn2 types
				}
				return xmlHelper.getPathForPrefix(prefix).appendFragment(fragment).toString();
			}
			else {
				return URI.createURI("").appendFragment(fragment).toString(); //$NON-NLS-1$
			}
		}
	}
	
	public static class Bpmn2ModelerXmlHelper extends BpmnXmlHelper {

		// List of all EReferences that are defined as type="xsd:QName" in the BPMN 2.0 Schema
		// This information is not represented in the MDT BPMN2 project metamodel. 
		boolean isQNameFeature = false;
		ImportUtil importHandler = new ImportUtil();

		public Bpmn2ModelerXmlHelper(Bpmn2ResourceImpl resource) {
			super(resource);
		}		
		
		@Override
		public Object getValue(EObject eObject, EStructuralFeature eStructuralFeature) {
			Object o = super.getValue(eObject, eStructuralFeature);
			if (qnameMap.contains(eStructuralFeature)) {
				List<String> prefixes = urisToPrefixes.get(getTargetNamespace());
				if (prefixes!=null && prefixes.contains("")) //$NON-NLS-1$
					isQNameFeature = false;
				else
					isQNameFeature = true;
			}
			else
				isQNameFeature = false;
			return o;
		}

		@Override
		public String getHREF(EObject obj) {
			// convert the attribute ID references to a QName
			String s = null;
				if (ModelUtil.isStringWrapper(obj)) {
					s = ModelUtil.getStringWrapperValue(obj);
				}
			else
				s = super.getHREF(obj);
			if (isQNameFeature) {
				if (s!=null && s.contains("#")) { //$NON-NLS-1$
					// object is a reference possibly to another document
					Import imp = importHandler.findImportForObject(resource, obj);
					if (imp!=null) {
						String localname = importHandler.getLocalnameForObject(obj);
						if (localname!=null) {
							String prefix = NamespaceUtil.getPrefixForNamespace(resource, imp.getNamespace());
							if (prefix!=null) {
								s = prefix + ":" + localname; //$NON-NLS-1$
								return s;
							}
						}
					}
				}
			}
			
			return s;
		}

		public String getIDREF(EObject obj) {
			// convert the element ID references to a QName
			String s = super.getIDREF(obj);
			if (isQNameFeature && !ModelUtil.isStringWrapper(obj))
				s = convertToQName(s);
			return s;
		}

		/**
		 * Returns the targetNamespace defined in the <definitions> root element.
		 * 
		 * @return a namespace URI or null if targetNamespace is not defined.
		 */
		private String getTargetNamespace() {
			Definitions definitions = ModelUtil.getDefinitions(getResource());
			if (definitions==null)
				return null;
			return definitions.getTargetNamespace();
		}
		
		/**
		 * Get the namespace prefix for the targetNamespace.
		 * 
		 * @return null if the document does not define a targetNamespace,
		 * an empty string if there is a targetNamespace, but no prefix has been
		 * defined for it, or the prefix for the targetNamespace
		 * 
		 * Examples:
		 * 
		 * <bpmn2:definitions tns="http://eclipse.org/example" targetNamespace="http://eclipse.org/example"/>
		 *   return "tns"
		 *   
		 * <bpmn2:definitions targetNamespace="http://eclipse.org/example"/>
		 *   returns ""
		 *   
		 * <bpmn2:definitions tns="http://eclipse.org/example"/>
		 *   returns null
		 */
		private String getTargetNamespacePrefix() {
			String targetNamespace = getTargetNamespace();
			if (targetNamespace!=null && !targetNamespace.isEmpty()) {
				String prefix = getPrefix(targetNamespace);
				if (prefix==null || prefix.isEmpty()) {
					for (Entry<String, String> e : this.getPrefixToNamespaceMap().entrySet()) {
						if (targetNamespace.equals(e.getValue()) && !e.getKey().isEmpty()) {
							return e.getKey();
						}
					}
				}
				return ""; //$NON-NLS-1$
			}
			return null;
		}
		
		/**
		 * Converts the NCName "s" to a QName that maps to the targetNamespace
		 * 
		 * @param s - a non-colonized name string
		 * @return the string "s" prefixed with the NS prefix for the targetNamespace.
		 */
		private String convertToQName(String s) {
			if (s!=null && !s.contains(":")) { //$NON-NLS-1$
				String prefix = getTargetNamespacePrefix();
				if (prefix!=null && !prefix.isEmpty()) {
					s = prefix + ":" + s; //$NON-NLS-1$
				}
			}
			return s;
		}
		
		public void setValue(EObject object, EStructuralFeature feature, Object value, int position) {
			// fix some kind of bug which causes duplicate entries in objects that have
			// mutual reference lists.
			if (	object!=null
					&& feature!=null
					&& object.eClass()!=null
					&& feature == object.eClass().getEStructuralFeature(feature.getFeatureID())
			) {
				Object v = object.eGet(feature);
				if (v instanceof EObjectWithInverseEList) {
					EObjectWithInverseEList list = (EObjectWithInverseEList)v;
					if (list.contains(value)) {
						// it's already in there!
						return;
					}
				}
			}
			
//			// check if we need to change the attribute's data type:
//			if (feature instanceof EAttribute) {
//				// and only if the attribute's data type is "Object"
//				EClassifier t = feature.getEType();
//				if (t!=null && t.getInstanceClass() == Object.class) {
//					// search for the attribute in the target runtime's Custom Task and
//					// Model Extension definitions by name
//					List<Property>properties = new ArrayList<Property>();
//					TargetRuntime rt = TargetRuntime.getCurrentRuntime();
//					String className = object.eClass().getName();
//					String featureName = feature.getName();
//					for (CustomTaskDescriptor ctd : rt.getCustomTasks()) {
//						if (className.equals(ctd.getType())) {
//							properties.addAll(ctd.getProperties());
//						}
//					}
//					for (ModelExtensionDescriptor med : rt.getModelExtensions()) {
//						if (className.equals(med.getType())) {
//							properties.addAll(med.getProperties());
//						}
//					}
//					for (Property p : properties) {
//						if (p.name.equals(featureName)) {
//							String type = p.type;
//							if (type==null)
//								type = "EString"; //$NON-NLS-1$
//							EClassifier eClassifier = ModelDecorator.findEClassifier(
//									rt.getModelDescriptor().getEPackage(),type);
//							if (eClassifier instanceof EDataType) {
//								feature.setEType(eClassifier);
//							}
//							break;
//						}
//					}
//				}
//			}
			
			super.setValue(object, feature, value, position);
		}

    	@Override
		public EStructuralFeature getFeature(EClass eClass, String nsURI, String name, boolean isElement) {
    		// This fixes https://bugs.eclipse.org/bugs/show_bug.cgi?id=378296
    		// I'm still not convinced that getFeature() shouldn't simply return the feature
    		// from the given EClass instead of searching the EPackage of the Resource being
    		// loaded (if the EClass has a feature with that name of course).
    		EStructuralFeature feature = null;
    		EPackage pkg = eClass.getEPackage();
			if (pkg != Bpmn2Package.eINSTANCE &&
					pkg != BpmnDiPackage.eINSTANCE &&
					pkg != DcPackage.eINSTANCE &&
					pkg != DiPackage.eINSTANCE &&
					pkg != TargetRuntime.getCurrentRuntime().getModelDescriptor().getEPackage()) {
				feature = eClass.getEStructuralFeature(name);
			}
			if (feature==null)
				feature = super.getFeature(eClass, nsURI, name, isElement);
			return feature;
		}
	}
}
