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
package aurora.ide.workflow.bpmn.dd.di;

import aurora.ide.workflow.bpmn.dd.dc.Point;

import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Edge</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link aurora.ide.workflow.bpmn.dd.di.Edge#getSource <em>Source</em>}</li>
 *   <li>{@link aurora.ide.workflow.bpmn.dd.di.Edge#getTarget <em>Target</em>}</li>
 *   <li>{@link aurora.ide.workflow.bpmn.dd.di.Edge#getWaypoint <em>Waypoint</em>}</li>
 * </ul>
 * </p>
 *
 * @see aurora.ide.workflow.bpmn.dd.di.DiPackage#getEdge()
 * @model abstract="true"
 *        extendedMetaData="name='Edge' kind='elementOnly'"
 * @generated
 */
public interface Edge extends DiagramElement {
	/**
	 * Returns the value of the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Source</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' reference.
	 * @see aurora.ide.workflow.bpmn.dd.di.DiPackage#getEdge_Source()
	 * @model transient="true" changeable="false" derived="true" ordered="false"
	 * @generated
	 */
	DiagramElement getSource();

	/**
	 * Returns the value of the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target</em>' reference.
	 * @see aurora.ide.workflow.bpmn.dd.di.DiPackage#getEdge_Target()
	 * @model transient="true" changeable="false" derived="true" ordered="false"
	 * @generated
	 */
	DiagramElement getTarget();

	/**
	 * Returns the value of the '<em><b>Waypoint</b></em>' containment reference list.
	 * The list contents are of type {@link aurora.ide.workflow.bpmn.dd.dc.Point}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Waypoint</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Waypoint</em>' containment reference list.
	 * @see aurora.ide.workflow.bpmn.dd.di.DiPackage#getEdge_Waypoint()
	 * @model containment="true" lower="2"
	 *        extendedMetaData="kind='element' name='waypoint' namespace='http://www.omg.org/spec/DD/20100524/DI'"
	 * @generated
	 */
	List<Point> getWaypoint();

} // Edge
