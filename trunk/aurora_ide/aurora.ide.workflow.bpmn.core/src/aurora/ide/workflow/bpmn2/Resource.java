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
package aurora.ide.workflow.bpmn2;

import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Resource</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link aurora.ide.workflow.bpmn2.Resource#getResourceParameters <em>Resource Parameters</em>}</li>
 *   <li>{@link aurora.ide.workflow.bpmn2.Resource#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see aurora.ide.workflow.bpmn2.Bpmn2Package#getResource()
 * @model extendedMetaData="name='tResource' kind='elementOnly'"
 * @generated
 */
public interface Resource extends RootElement {
	/**
	 * Returns the value of the '<em><b>Resource Parameters</b></em>' containment reference list.
	 * The list contents are of type {@link aurora.ide.workflow.bpmn2.ResourceParameter}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Resource Parameters</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resource Parameters</em>' containment reference list.
	 * @see aurora.ide.workflow.bpmn2.Bpmn2Package#getResource_ResourceParameters()
	 * @model containment="true" ordered="false"
	 *        extendedMetaData="kind='element' name='resourceParameter' namespace='http://www.omg.org/spec/BPMN/20100524/MODEL'"
	 * @generated
	 */
	List<ResourceParameter> getResourceParameters();

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see aurora.ide.workflow.bpmn2.Bpmn2Package#getResource_Name()
	 * @model required="true" ordered="false"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link aurora.ide.workflow.bpmn2.Resource#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // Resource
