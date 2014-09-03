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
 * A representation of the model object '<em><b>Sub Choreography</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link aurora.ide.workflow.bpmn2.SubChoreography#getArtifacts <em>Artifacts</em>}</li>
 * </ul>
 * </p>
 *
 * @see aurora.ide.workflow.bpmn2.Bpmn2Package#getSubChoreography()
 * @model extendedMetaData="name='tSubChoreography' kind='elementOnly'"
 * @generated
 */
public interface SubChoreography extends ChoreographyActivity,
		FlowElementsContainer {
	/**
	 * Returns the value of the '<em><b>Artifacts</b></em>' containment reference list.
	 * The list contents are of type {@link aurora.ide.workflow.bpmn2.Artifact}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Artifacts</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Artifacts</em>' containment reference list.
	 * @see aurora.ide.workflow.bpmn2.Bpmn2Package#getSubChoreography_Artifacts()
	 * @model containment="true" ordered="false"
	 *        extendedMetaData="kind='element' name='artifact' namespace='http://www.omg.org/spec/BPMN/20100524/MODEL' group='http://www.omg.org/spec/BPMN/20100524/MODEL#artifact'"
	 * @generated
	 */
	List<Artifact> getArtifacts();

} // SubChoreography
