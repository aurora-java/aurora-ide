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

package org.eclipse.bpmn2.modeler.core.merrimac.dialogs;

import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;

public class FeatureEditingDialog extends ObjectEditingDialog {

	protected EStructuralFeature feature;
	protected EObject newObject;
	protected boolean createNew = false;

	public FeatureEditingDialog(DiagramEditor editor, EObject object, EStructuralFeature feature, EObject value) {
		super(editor, object, (EClass)feature.getEType());
		this.feature = feature;
		this.newObject = value;
	}

	public FeatureEditingDialog(DiagramEditor editor, EObject object, EStructuralFeature feature) {
		super(editor, object, (EClass)feature.getEType());
		this.feature = feature;
		this.newObject = (EObject) object.eGet(feature);
	}

	public void create() {
		startTransaction();
		if (newObject==null) {
			// create the new object
			createNew = true;
			if (featureEType==null) {
				ModelSubclassSelectionDialog dialog = new ModelSubclassSelectionDialog(editor, object, feature);
				if (dialog.open()==Window.OK){
					featureEType = (EClass)dialog.getResult()[0];
					newObject = createNewObject(object, feature, featureEType);
				}
				else
					cancel = true;
			}
			else
				newObject = createNewObject(object, feature, featureEType);
		}
		else if (featureEType==null) {
			if (newObject instanceof EObject)
				featureEType = newObject.eClass();
			else if (feature.getEType() instanceof EClass)
				featureEType = (EClass)feature.getEType();
		}
		if (newObject==null)
			cancel = true;
		
		if (cancel) {
			rollbackTransaction();
		}
		else {
			super.create();
		}
	}
	
	protected EObject createNewObject(final EObject object, final EStructuralFeature feature, final EClass eclass) {
		final EObject[] result = new EObject[1];
		final TransactionalEditingDomain domain = (TransactionalEditingDomainImpl)editor.getEditingDomain();
		if (domain!=null) {
			domain.getCommandStack().execute(new RecordingCommand(domain) {
				@Override
				protected void doExecute() {
					result[0] = Bpmn2ModelerFactory.createFeature(object, feature, eclass);
				}
			});
		}
		else {
			result[0] = Bpmn2ModelerFactory.createFeature(object, feature, eclass);
		}
		return result[0];
	}

	@Override
	protected String getPreferenceKey() {
		return super.getPreferenceKey() + "." + feature.getName(); //$NON-NLS-1$
	}
	
	@Override
	protected String getTitle() {
		if (createNew)
			title = NLS.bind(Messages.FeatureEditingDialog_Create, ExtendedPropertiesProvider.getLabel(newObject));
		else
			title = NLS.bind(Messages.FeatureEditingDialog_Edit, ExtendedPropertiesProvider.getLabel(newObject));
		return title;
	}
	
	public void aboutToOpen() {
		dialogContent.setData(newObject);
	}
	
	@Override
	public int open() {
		int result = super.open();
		if (result!=Window.OK){
			undoCreateNewObject();
		}
		return result;
	}

	private void undoCreateNewObject() {
		if (createNew && newObject!=null) {
			ModelUtil.unsetID(newObject, object.eResource());
			final TransactionalEditingDomain domain = (TransactionalEditingDomainImpl)editor.getEditingDomain();
			if (domain!=null) {
				if (domain.getCommandStack().canUndo()) {
					domain.getCommandStack().undo();
				}
			}
			else {
				EcoreUtil.delete(newObject, true);
			}
		}
		newObject = null;
	}
	
	@Override
	protected void cancelPressed() {
		super.cancelPressed();
		undoCreateNewObject();
	}
	
	public EObject getNewObject() {
		return newObject;
	}
}