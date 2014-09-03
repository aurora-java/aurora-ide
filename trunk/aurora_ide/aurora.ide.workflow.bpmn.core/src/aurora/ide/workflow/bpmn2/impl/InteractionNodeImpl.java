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
package aurora.ide.workflow.bpmn2.impl;

import aurora.ide.workflow.bpmn2.Bpmn2Package;
import aurora.ide.workflow.bpmn2.ConversationLink;
import aurora.ide.workflow.bpmn2.InteractionNode;

import java.util.List;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Interaction Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link aurora.ide.workflow.bpmn2.impl.InteractionNodeImpl#getIncomingConversationLinks <em>Incoming Conversation Links</em>}</li>
 *   <li>{@link aurora.ide.workflow.bpmn2.impl.InteractionNodeImpl#getOutgoingConversationLinks <em>Outgoing Conversation Links</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InteractionNodeImpl extends EObjectImpl implements InteractionNode {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InteractionNodeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Bpmn2Package.Literals.INTERACTION_NODE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<ConversationLink> getIncomingConversationLinks() {
		// TODO: implement this method to return the 'Incoming Conversation Links' reference list
		// Ensure that you remove @generated or mark it @generated NOT
		// The list is expected to implement org.eclipse.emf.ecore.util.InternalEList and org.eclipse.emf.ecore.EStructuralFeature.Setting
		// so it's likely that an appropriate subclass of org.eclipse.emf.ecore.util.EcoreEList should be used.
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List<ConversationLink> getOutgoingConversationLinks() {
		// TODO: implement this method to return the 'Outgoing Conversation Links' reference list
		// Ensure that you remove @generated or mark it @generated NOT
		// The list is expected to implement org.eclipse.emf.ecore.util.InternalEList and org.eclipse.emf.ecore.EStructuralFeature.Setting
		// so it's likely that an appropriate subclass of org.eclipse.emf.ecore.util.EcoreEList should be used.
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case Bpmn2Package.INTERACTION_NODE__INCOMING_CONVERSATION_LINKS:
			return getIncomingConversationLinks();
		case Bpmn2Package.INTERACTION_NODE__OUTGOING_CONVERSATION_LINKS:
			return getOutgoingConversationLinks();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case Bpmn2Package.INTERACTION_NODE__INCOMING_CONVERSATION_LINKS:
			return !getIncomingConversationLinks().isEmpty();
		case Bpmn2Package.INTERACTION_NODE__OUTGOING_CONVERSATION_LINKS:
			return !getOutgoingConversationLinks().isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //InteractionNodeImpl
