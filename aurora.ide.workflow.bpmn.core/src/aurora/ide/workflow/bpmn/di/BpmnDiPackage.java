/**
 * <copyright>
 * 
 * Copyright (c) 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Reiner Hille-Doering (SAP AG) - initial API and implementation and/or initial documentation
 * 
 * </copyright>
 */
package aurora.ide.workflow.bpmn.di;

import aurora.ide.workflow.bpmn.dd.di.DiPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see aurora.ide.workflow.bpmn.di.BpmnDiFactory
 * @model kind="package"
 * @generated
 */
public interface BpmnDiPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "di";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.omg.org/spec/BPMN/20100524/DI-XMI";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "bpmndi";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	BpmnDiPackage eINSTANCE = aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl
			.init();

	/**
	 * The meta object id for the '{@link aurora.ide.workflow.bpmn.di.impl.DocumentRootImpl <em>Document Root</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see aurora.ide.workflow.bpmn.di.impl.DocumentRootImpl
	 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getDocumentRoot()
	 * @generated
	 */
	int DOCUMENT_ROOT = 0;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__MIXED = 0;

	/**
	 * The feature id for the '<em><b>XMLNS Prefix Map</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__XMLNS_PREFIX_MAP = 1;

	/**
	 * The feature id for the '<em><b>XSI Schema Location</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = 2;

	/**
	 * The feature id for the '<em><b>BPMN Diagram</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__BPMN_DIAGRAM = 3;

	/**
	 * The feature id for the '<em><b>BPMN Edge</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__BPMN_EDGE = 4;

	/**
	 * The feature id for the '<em><b>BPMN Label</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__BPMN_LABEL = 5;

	/**
	 * The feature id for the '<em><b>BPMN Label Style</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__BPMN_LABEL_STYLE = 6;

	/**
	 * The feature id for the '<em><b>BPMN Plane</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__BPMN_PLANE = 7;

	/**
	 * The feature id for the '<em><b>BPMN Shape</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__BPMN_SHAPE = 8;

	/**
	 * The number of structural features of the '<em>Document Root</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT_FEATURE_COUNT = 9;

	/**
	 * The meta object id for the '{@link aurora.ide.workflow.bpmn.di.impl.BPMNDiagramImpl <em>BPMN Diagram</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see aurora.ide.workflow.bpmn.di.impl.BPMNDiagramImpl
	 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getBPMNDiagram()
	 * @generated
	 */
	int BPMN_DIAGRAM = 1;

	/**
	 * The feature id for the '<em><b>Documentation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_DIAGRAM__DOCUMENTATION = DiPackage.DIAGRAM__DOCUMENTATION;

	/**
	 * The feature id for the '<em><b>Owned Style</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_DIAGRAM__OWNED_STYLE = DiPackage.DIAGRAM__OWNED_STYLE;

	/**
	 * The feature id for the '<em><b>Root Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_DIAGRAM__ROOT_ELEMENT = DiPackage.DIAGRAM__ROOT_ELEMENT;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_DIAGRAM__ID = DiPackage.DIAGRAM__ID;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_DIAGRAM__NAME = DiPackage.DIAGRAM__NAME;

	/**
	 * The feature id for the '<em><b>Resolution</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_DIAGRAM__RESOLUTION = DiPackage.DIAGRAM__RESOLUTION;

	/**
	 * The feature id for the '<em><b>Plane</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_DIAGRAM__PLANE = DiPackage.DIAGRAM_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Label Style</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_DIAGRAM__LABEL_STYLE = DiPackage.DIAGRAM_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>BPMN Diagram</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_DIAGRAM_FEATURE_COUNT = DiPackage.DIAGRAM_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link aurora.ide.workflow.bpmn.di.impl.BPMNEdgeImpl <em>BPMN Edge</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see aurora.ide.workflow.bpmn.di.impl.BPMNEdgeImpl
	 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getBPMNEdge()
	 * @generated
	 */
	int BPMN_EDGE = 2;

	/**
	 * The feature id for the '<em><b>Owning Diagram</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE__OWNING_DIAGRAM = DiPackage.LABELED_EDGE__OWNING_DIAGRAM;

	/**
	 * The feature id for the '<em><b>Owning Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE__OWNING_ELEMENT = DiPackage.LABELED_EDGE__OWNING_ELEMENT;

	/**
	 * The feature id for the '<em><b>Owned Element</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE__OWNED_ELEMENT = DiPackage.LABELED_EDGE__OWNED_ELEMENT;

	/**
	 * The feature id for the '<em><b>Model Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE__MODEL_ELEMENT = DiPackage.LABELED_EDGE__MODEL_ELEMENT;

	/**
	 * The feature id for the '<em><b>Style</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE__STYLE = DiPackage.LABELED_EDGE__STYLE;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE__ID = DiPackage.LABELED_EDGE__ID;

	/**
	 * The feature id for the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE__SOURCE = DiPackage.LABELED_EDGE__SOURCE;

	/**
	 * The feature id for the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE__TARGET = DiPackage.LABELED_EDGE__TARGET;

	/**
	 * The feature id for the '<em><b>Waypoint</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE__WAYPOINT = DiPackage.LABELED_EDGE__WAYPOINT;

	/**
	 * The feature id for the '<em><b>Owned Label</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE__OWNED_LABEL = DiPackage.LABELED_EDGE__OWNED_LABEL;

	/**
	 * The feature id for the '<em><b>Label</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE__LABEL = DiPackage.LABELED_EDGE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Bpmn Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE__BPMN_ELEMENT = DiPackage.LABELED_EDGE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Message Visible Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE__MESSAGE_VISIBLE_KIND = DiPackage.LABELED_EDGE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Source Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE__SOURCE_ELEMENT = DiPackage.LABELED_EDGE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Target Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE__TARGET_ELEMENT = DiPackage.LABELED_EDGE_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>BPMN Edge</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_EDGE_FEATURE_COUNT = DiPackage.LABELED_EDGE_FEATURE_COUNT + 5;

	/**
	 * The meta object id for the '{@link aurora.ide.workflow.bpmn.di.impl.BPMNLabelImpl <em>BPMN Label</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see aurora.ide.workflow.bpmn.di.impl.BPMNLabelImpl
	 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getBPMNLabel()
	 * @generated
	 */
	int BPMN_LABEL = 3;

	/**
	 * The feature id for the '<em><b>Owning Diagram</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_LABEL__OWNING_DIAGRAM = DiPackage.LABEL__OWNING_DIAGRAM;

	/**
	 * The feature id for the '<em><b>Owning Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_LABEL__OWNING_ELEMENT = DiPackage.LABEL__OWNING_ELEMENT;

	/**
	 * The feature id for the '<em><b>Owned Element</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_LABEL__OWNED_ELEMENT = DiPackage.LABEL__OWNED_ELEMENT;

	/**
	 * The feature id for the '<em><b>Model Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_LABEL__MODEL_ELEMENT = DiPackage.LABEL__MODEL_ELEMENT;

	/**
	 * The feature id for the '<em><b>Style</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_LABEL__STYLE = DiPackage.LABEL__STYLE;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_LABEL__ID = DiPackage.LABEL__ID;

	/**
	 * The feature id for the '<em><b>Bounds</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_LABEL__BOUNDS = DiPackage.LABEL__BOUNDS;

	/**
	 * The feature id for the '<em><b>Label Style</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_LABEL__LABEL_STYLE = DiPackage.LABEL_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>BPMN Label</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_LABEL_FEATURE_COUNT = DiPackage.LABEL_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link aurora.ide.workflow.bpmn.di.impl.BPMNLabelStyleImpl <em>BPMN Label Style</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see aurora.ide.workflow.bpmn.di.impl.BPMNLabelStyleImpl
	 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getBPMNLabelStyle()
	 * @generated
	 */
	int BPMN_LABEL_STYLE = 4;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_LABEL_STYLE__ID = DiPackage.STYLE__ID;

	/**
	 * The feature id for the '<em><b>Font</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_LABEL_STYLE__FONT = DiPackage.STYLE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>BPMN Label Style</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_LABEL_STYLE_FEATURE_COUNT = DiPackage.STYLE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link aurora.ide.workflow.bpmn.di.impl.BPMNPlaneImpl <em>BPMN Plane</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see aurora.ide.workflow.bpmn.di.impl.BPMNPlaneImpl
	 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getBPMNPlane()
	 * @generated
	 */
	int BPMN_PLANE = 5;

	/**
	 * The feature id for the '<em><b>Owning Diagram</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLANE__OWNING_DIAGRAM = DiPackage.PLANE__OWNING_DIAGRAM;

	/**
	 * The feature id for the '<em><b>Owning Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLANE__OWNING_ELEMENT = DiPackage.PLANE__OWNING_ELEMENT;

	/**
	 * The feature id for the '<em><b>Owned Element</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLANE__OWNED_ELEMENT = DiPackage.PLANE__OWNED_ELEMENT;

	/**
	 * The feature id for the '<em><b>Model Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLANE__MODEL_ELEMENT = DiPackage.PLANE__MODEL_ELEMENT;

	/**
	 * The feature id for the '<em><b>Style</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLANE__STYLE = DiPackage.PLANE__STYLE;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLANE__ID = DiPackage.PLANE__ID;

	/**
	 * The feature id for the '<em><b>Plane Element</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLANE__PLANE_ELEMENT = DiPackage.PLANE__PLANE_ELEMENT;

	/**
	 * The feature id for the '<em><b>Bpmn Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLANE__BPMN_ELEMENT = DiPackage.PLANE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>BPMN Plane</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_PLANE_FEATURE_COUNT = DiPackage.PLANE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link aurora.ide.workflow.bpmn.di.impl.BPMNShapeImpl <em>BPMN Shape</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see aurora.ide.workflow.bpmn.di.impl.BPMNShapeImpl
	 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getBPMNShape()
	 * @generated
	 */
	int BPMN_SHAPE = 6;

	/**
	 * The feature id for the '<em><b>Owning Diagram</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__OWNING_DIAGRAM = DiPackage.LABELED_SHAPE__OWNING_DIAGRAM;

	/**
	 * The feature id for the '<em><b>Owning Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__OWNING_ELEMENT = DiPackage.LABELED_SHAPE__OWNING_ELEMENT;

	/**
	 * The feature id for the '<em><b>Owned Element</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__OWNED_ELEMENT = DiPackage.LABELED_SHAPE__OWNED_ELEMENT;

	/**
	 * The feature id for the '<em><b>Model Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__MODEL_ELEMENT = DiPackage.LABELED_SHAPE__MODEL_ELEMENT;

	/**
	 * The feature id for the '<em><b>Style</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__STYLE = DiPackage.LABELED_SHAPE__STYLE;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__ID = DiPackage.LABELED_SHAPE__ID;

	/**
	 * The feature id for the '<em><b>Bounds</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__BOUNDS = DiPackage.LABELED_SHAPE__BOUNDS;

	/**
	 * The feature id for the '<em><b>Owned Label</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__OWNED_LABEL = DiPackage.LABELED_SHAPE__OWNED_LABEL;

	/**
	 * The feature id for the '<em><b>Label</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__LABEL = DiPackage.LABELED_SHAPE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Bpmn Element</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__BPMN_ELEMENT = DiPackage.LABELED_SHAPE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Choreography Activity Shape</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__CHOREOGRAPHY_ACTIVITY_SHAPE = DiPackage.LABELED_SHAPE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Is Expanded</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__IS_EXPANDED = DiPackage.LABELED_SHAPE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Is Horizontal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__IS_HORIZONTAL = DiPackage.LABELED_SHAPE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Is Marker Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__IS_MARKER_VISIBLE = DiPackage.LABELED_SHAPE_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Is Message Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__IS_MESSAGE_VISIBLE = DiPackage.LABELED_SHAPE_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Participant Band Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE__PARTICIPANT_BAND_KIND = DiPackage.LABELED_SHAPE_FEATURE_COUNT + 7;

	/**
	 * The number of structural features of the '<em>BPMN Shape</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BPMN_SHAPE_FEATURE_COUNT = DiPackage.LABELED_SHAPE_FEATURE_COUNT + 8;

	/**
	 * The meta object id for the '{@link aurora.ide.workflow.bpmn.di.MessageVisibleKind <em>Message Visible Kind</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see aurora.ide.workflow.bpmn.di.MessageVisibleKind
	 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getMessageVisibleKind()
	 * @generated
	 */
	int MESSAGE_VISIBLE_KIND = 7;

	/**
	 * The meta object id for the '{@link aurora.ide.workflow.bpmn.di.ParticipantBandKind <em>Participant Band Kind</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see aurora.ide.workflow.bpmn.di.ParticipantBandKind
	 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getParticipantBandKind()
	 * @generated
	 */
	int PARTICIPANT_BAND_KIND = 8;

	/**
	 * Returns the meta object for class '{@link aurora.ide.workflow.bpmn.di.DocumentRoot <em>Document Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Document Root</em>'.
	 * @see aurora.ide.workflow.bpmn.di.DocumentRoot
	 * @generated
	 */
	EClass getDocumentRoot();

	/**
	 * Returns the meta object for the attribute list '{@link aurora.ide.workflow.bpmn.di.DocumentRoot#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see aurora.ide.workflow.bpmn.di.DocumentRoot#getMixed()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EAttribute getDocumentRoot_Mixed();

	/**
	 * Returns the meta object for the map '{@link aurora.ide.workflow.bpmn.di.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
	 * @see aurora.ide.workflow.bpmn.di.DocumentRoot#getXMLNSPrefixMap()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_XMLNSPrefixMap();

	/**
	 * Returns the meta object for the map '{@link aurora.ide.workflow.bpmn.di.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XSI Schema Location</em>'.
	 * @see aurora.ide.workflow.bpmn.di.DocumentRoot#getXSISchemaLocation()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_XSISchemaLocation();

	/**
	 * Returns the meta object for the containment reference '{@link aurora.ide.workflow.bpmn.di.DocumentRoot#getBPMNDiagram <em>BPMN Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>BPMN Diagram</em>'.
	 * @see aurora.ide.workflow.bpmn.di.DocumentRoot#getBPMNDiagram()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_BPMNDiagram();

	/**
	 * Returns the meta object for the containment reference '{@link aurora.ide.workflow.bpmn.di.DocumentRoot#getBPMNEdge <em>BPMN Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>BPMN Edge</em>'.
	 * @see aurora.ide.workflow.bpmn.di.DocumentRoot#getBPMNEdge()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_BPMNEdge();

	/**
	 * Returns the meta object for the containment reference '{@link aurora.ide.workflow.bpmn.di.DocumentRoot#getBPMNLabel <em>BPMN Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>BPMN Label</em>'.
	 * @see aurora.ide.workflow.bpmn.di.DocumentRoot#getBPMNLabel()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_BPMNLabel();

	/**
	 * Returns the meta object for the containment reference '{@link aurora.ide.workflow.bpmn.di.DocumentRoot#getBPMNLabelStyle <em>BPMN Label Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>BPMN Label Style</em>'.
	 * @see aurora.ide.workflow.bpmn.di.DocumentRoot#getBPMNLabelStyle()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_BPMNLabelStyle();

	/**
	 * Returns the meta object for the containment reference '{@link aurora.ide.workflow.bpmn.di.DocumentRoot#getBPMNPlane <em>BPMN Plane</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>BPMN Plane</em>'.
	 * @see aurora.ide.workflow.bpmn.di.DocumentRoot#getBPMNPlane()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_BPMNPlane();

	/**
	 * Returns the meta object for the containment reference '{@link aurora.ide.workflow.bpmn.di.DocumentRoot#getBPMNShape <em>BPMN Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>BPMN Shape</em>'.
	 * @see aurora.ide.workflow.bpmn.di.DocumentRoot#getBPMNShape()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_BPMNShape();

	/**
	 * Returns the meta object for class '{@link aurora.ide.workflow.bpmn.di.BPMNDiagram <em>BPMN Diagram</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>BPMN Diagram</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNDiagram
	 * @generated
	 */
	EClass getBPMNDiagram();

	/**
	 * Returns the meta object for the containment reference '{@link aurora.ide.workflow.bpmn.di.BPMNDiagram#getPlane <em>Plane</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Plane</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNDiagram#getPlane()
	 * @see #getBPMNDiagram()
	 * @generated
	 */
	EReference getBPMNDiagram_Plane();

	/**
	 * Returns the meta object for the containment reference list '{@link aurora.ide.workflow.bpmn.di.BPMNDiagram#getLabelStyle <em>Label Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Label Style</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNDiagram#getLabelStyle()
	 * @see #getBPMNDiagram()
	 * @generated
	 */
	EReference getBPMNDiagram_LabelStyle();

	/**
	 * Returns the meta object for class '{@link aurora.ide.workflow.bpmn.di.BPMNEdge <em>BPMN Edge</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>BPMN Edge</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNEdge
	 * @generated
	 */
	EClass getBPMNEdge();

	/**
	 * Returns the meta object for the containment reference '{@link aurora.ide.workflow.bpmn.di.BPMNEdge#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Label</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNEdge#getLabel()
	 * @see #getBPMNEdge()
	 * @generated
	 */
	EReference getBPMNEdge_Label();

	/**
	 * Returns the meta object for the reference '{@link aurora.ide.workflow.bpmn.di.BPMNEdge#getBpmnElement <em>Bpmn Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Bpmn Element</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNEdge#getBpmnElement()
	 * @see #getBPMNEdge()
	 * @generated
	 */
	EReference getBPMNEdge_BpmnElement();

	/**
	 * Returns the meta object for the attribute '{@link aurora.ide.workflow.bpmn.di.BPMNEdge#getMessageVisibleKind <em>Message Visible Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Message Visible Kind</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNEdge#getMessageVisibleKind()
	 * @see #getBPMNEdge()
	 * @generated
	 */
	EAttribute getBPMNEdge_MessageVisibleKind();

	/**
	 * Returns the meta object for the reference '{@link aurora.ide.workflow.bpmn.di.BPMNEdge#getSourceElement <em>Source Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Source Element</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNEdge#getSourceElement()
	 * @see #getBPMNEdge()
	 * @generated
	 */
	EReference getBPMNEdge_SourceElement();

	/**
	 * Returns the meta object for the reference '{@link aurora.ide.workflow.bpmn.di.BPMNEdge#getTargetElement <em>Target Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target Element</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNEdge#getTargetElement()
	 * @see #getBPMNEdge()
	 * @generated
	 */
	EReference getBPMNEdge_TargetElement();

	/**
	 * Returns the meta object for class '{@link aurora.ide.workflow.bpmn.di.BPMNLabel <em>BPMN Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>BPMN Label</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNLabel
	 * @generated
	 */
	EClass getBPMNLabel();

	/**
	 * Returns the meta object for the reference '{@link aurora.ide.workflow.bpmn.di.BPMNLabel#getLabelStyle <em>Label Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Label Style</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNLabel#getLabelStyle()
	 * @see #getBPMNLabel()
	 * @generated
	 */
	EReference getBPMNLabel_LabelStyle();

	/**
	 * Returns the meta object for class '{@link aurora.ide.workflow.bpmn.di.BPMNLabelStyle <em>BPMN Label Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>BPMN Label Style</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNLabelStyle
	 * @generated
	 */
	EClass getBPMNLabelStyle();

	/**
	 * Returns the meta object for the containment reference '{@link aurora.ide.workflow.bpmn.di.BPMNLabelStyle#getFont <em>Font</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Font</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNLabelStyle#getFont()
	 * @see #getBPMNLabelStyle()
	 * @generated
	 */
	EReference getBPMNLabelStyle_Font();

	/**
	 * Returns the meta object for class '{@link aurora.ide.workflow.bpmn.di.BPMNPlane <em>BPMN Plane</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>BPMN Plane</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNPlane
	 * @generated
	 */
	EClass getBPMNPlane();

	/**
	 * Returns the meta object for the reference '{@link aurora.ide.workflow.bpmn.di.BPMNPlane#getBpmnElement <em>Bpmn Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Bpmn Element</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNPlane#getBpmnElement()
	 * @see #getBPMNPlane()
	 * @generated
	 */
	EReference getBPMNPlane_BpmnElement();

	/**
	 * Returns the meta object for class '{@link aurora.ide.workflow.bpmn.di.BPMNShape <em>BPMN Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>BPMN Shape</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNShape
	 * @generated
	 */
	EClass getBPMNShape();

	/**
	 * Returns the meta object for the containment reference '{@link aurora.ide.workflow.bpmn.di.BPMNShape#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Label</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNShape#getLabel()
	 * @see #getBPMNShape()
	 * @generated
	 */
	EReference getBPMNShape_Label();

	/**
	 * Returns the meta object for the reference '{@link aurora.ide.workflow.bpmn.di.BPMNShape#getBpmnElement <em>Bpmn Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Bpmn Element</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNShape#getBpmnElement()
	 * @see #getBPMNShape()
	 * @generated
	 */
	EReference getBPMNShape_BpmnElement();

	/**
	 * Returns the meta object for the reference '{@link aurora.ide.workflow.bpmn.di.BPMNShape#getChoreographyActivityShape <em>Choreography Activity Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Choreography Activity Shape</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNShape#getChoreographyActivityShape()
	 * @see #getBPMNShape()
	 * @generated
	 */
	EReference getBPMNShape_ChoreographyActivityShape();

	/**
	 * Returns the meta object for the attribute '{@link aurora.ide.workflow.bpmn.di.BPMNShape#isIsExpanded <em>Is Expanded</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Expanded</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNShape#isIsExpanded()
	 * @see #getBPMNShape()
	 * @generated
	 */
	EAttribute getBPMNShape_IsExpanded();

	/**
	 * Returns the meta object for the attribute '{@link aurora.ide.workflow.bpmn.di.BPMNShape#isIsHorizontal <em>Is Horizontal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Horizontal</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNShape#isIsHorizontal()
	 * @see #getBPMNShape()
	 * @generated
	 */
	EAttribute getBPMNShape_IsHorizontal();

	/**
	 * Returns the meta object for the attribute '{@link aurora.ide.workflow.bpmn.di.BPMNShape#isIsMarkerVisible <em>Is Marker Visible</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Marker Visible</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNShape#isIsMarkerVisible()
	 * @see #getBPMNShape()
	 * @generated
	 */
	EAttribute getBPMNShape_IsMarkerVisible();

	/**
	 * Returns the meta object for the attribute '{@link aurora.ide.workflow.bpmn.di.BPMNShape#isIsMessageVisible <em>Is Message Visible</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Message Visible</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNShape#isIsMessageVisible()
	 * @see #getBPMNShape()
	 * @generated
	 */
	EAttribute getBPMNShape_IsMessageVisible();

	/**
	 * Returns the meta object for the attribute '{@link aurora.ide.workflow.bpmn.di.BPMNShape#getParticipantBandKind <em>Participant Band Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Participant Band Kind</em>'.
	 * @see aurora.ide.workflow.bpmn.di.BPMNShape#getParticipantBandKind()
	 * @see #getBPMNShape()
	 * @generated
	 */
	EAttribute getBPMNShape_ParticipantBandKind();

	/**
	 * Returns the meta object for enum '{@link aurora.ide.workflow.bpmn.di.MessageVisibleKind <em>Message Visible Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Message Visible Kind</em>'.
	 * @see aurora.ide.workflow.bpmn.di.MessageVisibleKind
	 * @generated
	 */
	EEnum getMessageVisibleKind();

	/**
	 * Returns the meta object for enum '{@link aurora.ide.workflow.bpmn.di.ParticipantBandKind <em>Participant Band Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Participant Band Kind</em>'.
	 * @see aurora.ide.workflow.bpmn.di.ParticipantBandKind
	 * @generated
	 */
	EEnum getParticipantBandKind();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	BpmnDiFactory getBpmnDiFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link aurora.ide.workflow.bpmn.di.impl.DocumentRootImpl <em>Document Root</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see aurora.ide.workflow.bpmn.di.impl.DocumentRootImpl
		 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getDocumentRoot()
		 * @generated
		 */
		EClass DOCUMENT_ROOT = eINSTANCE.getDocumentRoot();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOCUMENT_ROOT__MIXED = eINSTANCE.getDocumentRoot_Mixed();

		/**
		 * The meta object literal for the '<em><b>XMLNS Prefix Map</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__XMLNS_PREFIX_MAP = eINSTANCE
				.getDocumentRoot_XMLNSPrefixMap();

		/**
		 * The meta object literal for the '<em><b>XSI Schema Location</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = eINSTANCE
				.getDocumentRoot_XSISchemaLocation();

		/**
		 * The meta object literal for the '<em><b>BPMN Diagram</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__BPMN_DIAGRAM = eINSTANCE
				.getDocumentRoot_BPMNDiagram();

		/**
		 * The meta object literal for the '<em><b>BPMN Edge</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__BPMN_EDGE = eINSTANCE
				.getDocumentRoot_BPMNEdge();

		/**
		 * The meta object literal for the '<em><b>BPMN Label</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__BPMN_LABEL = eINSTANCE
				.getDocumentRoot_BPMNLabel();

		/**
		 * The meta object literal for the '<em><b>BPMN Label Style</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__BPMN_LABEL_STYLE = eINSTANCE
				.getDocumentRoot_BPMNLabelStyle();

		/**
		 * The meta object literal for the '<em><b>BPMN Plane</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__BPMN_PLANE = eINSTANCE
				.getDocumentRoot_BPMNPlane();

		/**
		 * The meta object literal for the '<em><b>BPMN Shape</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__BPMN_SHAPE = eINSTANCE
				.getDocumentRoot_BPMNShape();

		/**
		 * The meta object literal for the '{@link aurora.ide.workflow.bpmn.di.impl.BPMNDiagramImpl <em>BPMN Diagram</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see aurora.ide.workflow.bpmn.di.impl.BPMNDiagramImpl
		 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getBPMNDiagram()
		 * @generated
		 */
		EClass BPMN_DIAGRAM = eINSTANCE.getBPMNDiagram();

		/**
		 * The meta object literal for the '<em><b>Plane</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BPMN_DIAGRAM__PLANE = eINSTANCE.getBPMNDiagram_Plane();

		/**
		 * The meta object literal for the '<em><b>Label Style</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BPMN_DIAGRAM__LABEL_STYLE = eINSTANCE
				.getBPMNDiagram_LabelStyle();

		/**
		 * The meta object literal for the '{@link aurora.ide.workflow.bpmn.di.impl.BPMNEdgeImpl <em>BPMN Edge</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see aurora.ide.workflow.bpmn.di.impl.BPMNEdgeImpl
		 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getBPMNEdge()
		 * @generated
		 */
		EClass BPMN_EDGE = eINSTANCE.getBPMNEdge();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BPMN_EDGE__LABEL = eINSTANCE.getBPMNEdge_Label();

		/**
		 * The meta object literal for the '<em><b>Bpmn Element</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BPMN_EDGE__BPMN_ELEMENT = eINSTANCE
				.getBPMNEdge_BpmnElement();

		/**
		 * The meta object literal for the '<em><b>Message Visible Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BPMN_EDGE__MESSAGE_VISIBLE_KIND = eINSTANCE
				.getBPMNEdge_MessageVisibleKind();

		/**
		 * The meta object literal for the '<em><b>Source Element</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BPMN_EDGE__SOURCE_ELEMENT = eINSTANCE
				.getBPMNEdge_SourceElement();

		/**
		 * The meta object literal for the '<em><b>Target Element</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BPMN_EDGE__TARGET_ELEMENT = eINSTANCE
				.getBPMNEdge_TargetElement();

		/**
		 * The meta object literal for the '{@link aurora.ide.workflow.bpmn.di.impl.BPMNLabelImpl <em>BPMN Label</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see aurora.ide.workflow.bpmn.di.impl.BPMNLabelImpl
		 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getBPMNLabel()
		 * @generated
		 */
		EClass BPMN_LABEL = eINSTANCE.getBPMNLabel();

		/**
		 * The meta object literal for the '<em><b>Label Style</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BPMN_LABEL__LABEL_STYLE = eINSTANCE
				.getBPMNLabel_LabelStyle();

		/**
		 * The meta object literal for the '{@link aurora.ide.workflow.bpmn.di.impl.BPMNLabelStyleImpl <em>BPMN Label Style</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see aurora.ide.workflow.bpmn.di.impl.BPMNLabelStyleImpl
		 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getBPMNLabelStyle()
		 * @generated
		 */
		EClass BPMN_LABEL_STYLE = eINSTANCE.getBPMNLabelStyle();

		/**
		 * The meta object literal for the '<em><b>Font</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BPMN_LABEL_STYLE__FONT = eINSTANCE.getBPMNLabelStyle_Font();

		/**
		 * The meta object literal for the '{@link aurora.ide.workflow.bpmn.di.impl.BPMNPlaneImpl <em>BPMN Plane</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see aurora.ide.workflow.bpmn.di.impl.BPMNPlaneImpl
		 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getBPMNPlane()
		 * @generated
		 */
		EClass BPMN_PLANE = eINSTANCE.getBPMNPlane();

		/**
		 * The meta object literal for the '<em><b>Bpmn Element</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BPMN_PLANE__BPMN_ELEMENT = eINSTANCE
				.getBPMNPlane_BpmnElement();

		/**
		 * The meta object literal for the '{@link aurora.ide.workflow.bpmn.di.impl.BPMNShapeImpl <em>BPMN Shape</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see aurora.ide.workflow.bpmn.di.impl.BPMNShapeImpl
		 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getBPMNShape()
		 * @generated
		 */
		EClass BPMN_SHAPE = eINSTANCE.getBPMNShape();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BPMN_SHAPE__LABEL = eINSTANCE.getBPMNShape_Label();

		/**
		 * The meta object literal for the '<em><b>Bpmn Element</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BPMN_SHAPE__BPMN_ELEMENT = eINSTANCE
				.getBPMNShape_BpmnElement();

		/**
		 * The meta object literal for the '<em><b>Choreography Activity Shape</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BPMN_SHAPE__CHOREOGRAPHY_ACTIVITY_SHAPE = eINSTANCE
				.getBPMNShape_ChoreographyActivityShape();

		/**
		 * The meta object literal for the '<em><b>Is Expanded</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BPMN_SHAPE__IS_EXPANDED = eINSTANCE
				.getBPMNShape_IsExpanded();

		/**
		 * The meta object literal for the '<em><b>Is Horizontal</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BPMN_SHAPE__IS_HORIZONTAL = eINSTANCE
				.getBPMNShape_IsHorizontal();

		/**
		 * The meta object literal for the '<em><b>Is Marker Visible</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BPMN_SHAPE__IS_MARKER_VISIBLE = eINSTANCE
				.getBPMNShape_IsMarkerVisible();

		/**
		 * The meta object literal for the '<em><b>Is Message Visible</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BPMN_SHAPE__IS_MESSAGE_VISIBLE = eINSTANCE
				.getBPMNShape_IsMessageVisible();

		/**
		 * The meta object literal for the '<em><b>Participant Band Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BPMN_SHAPE__PARTICIPANT_BAND_KIND = eINSTANCE
				.getBPMNShape_ParticipantBandKind();

		/**
		 * The meta object literal for the '{@link aurora.ide.workflow.bpmn.di.MessageVisibleKind <em>Message Visible Kind</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see aurora.ide.workflow.bpmn.di.MessageVisibleKind
		 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getMessageVisibleKind()
		 * @generated
		 */
		EEnum MESSAGE_VISIBLE_KIND = eINSTANCE.getMessageVisibleKind();

		/**
		 * The meta object literal for the '{@link aurora.ide.workflow.bpmn.di.ParticipantBandKind <em>Participant Band Kind</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see aurora.ide.workflow.bpmn.di.ParticipantBandKind
		 * @see aurora.ide.workflow.bpmn.di.impl.BpmnDiPackageImpl#getParticipantBandKind()
		 * @generated
		 */
		EEnum PARTICIPANT_BAND_KIND = eINSTANCE.getParticipantBandKind();

	}

} //BpmnDiPackage
