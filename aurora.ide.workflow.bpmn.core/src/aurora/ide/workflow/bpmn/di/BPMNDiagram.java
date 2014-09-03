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

import aurora.ide.workflow.bpmn.dd.di.Diagram;

import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>BPMN Diagram</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link aurora.ide.workflow.bpmn.di.BPMNDiagram#getPlane <em>Plane</em>}</li>
 *   <li>{@link aurora.ide.workflow.bpmn.di.BPMNDiagram#getLabelStyle <em>Label Style</em>}</li>
 * </ul>
 * </p>
 *
 * @see aurora.ide.workflow.bpmn.di.BpmnDiPackage#getBPMNDiagram()
 * @model extendedMetaData="name='BPMNDiagram' kind='elementOnly'"
 * @generated
 */
public interface BPMNDiagram extends Diagram {
	/**
	 * Returns the value of the '<em><b>Plane</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Plane</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Plane</em>' containment reference.
	 * @see #setPlane(BPMNPlane)
	 * @see aurora.ide.workflow.bpmn.di.BpmnDiPackage#getBPMNDiagram_Plane()
	 * @model containment="true" required="true" ordered="false"
	 *        extendedMetaData="kind='element' name='BPMNPlane' namespace='http://www.omg.org/spec/BPMN/20100524/DI'"
	 * @generated
	 */
	BPMNPlane getPlane();

	/**
	 * Sets the value of the '{@link aurora.ide.workflow.bpmn.di.BPMNDiagram#getPlane <em>Plane</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Plane</em>' containment reference.
	 * @see #getPlane()
	 * @generated
	 */
	void setPlane(BPMNPlane value);

	/**
	 * Returns the value of the '<em><b>Label Style</b></em>' containment reference list.
	 * The list contents are of type {@link aurora.ide.workflow.bpmn.di.BPMNLabelStyle}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Label Style</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Label Style</em>' containment reference list.
	 * @see aurora.ide.workflow.bpmn.di.BpmnDiPackage#getBPMNDiagram_LabelStyle()
	 * @model containment="true" ordered="false"
	 *        extendedMetaData="kind='element' name='BPMNLabelStyle' namespace='http://www.omg.org/spec/BPMN/20100524/DI'"
	 * @generated
	 */
	List<BPMNLabelStyle> getLabelStyle();

} // BPMNDiagram
