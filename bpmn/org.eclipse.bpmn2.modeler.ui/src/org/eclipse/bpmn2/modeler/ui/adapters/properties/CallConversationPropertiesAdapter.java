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

package org.eclipse.bpmn2.modeler.ui.adapters.properties;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.CallConversation;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.Import;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.utils.ImportUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Bob Brodt
 *
 */
public class CallConversationPropertiesAdapter extends ExtendedPropertiesAdapter<CallConversation> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public CallConversationPropertiesAdapter(AdapterFactory adapterFactory, CallConversation object) {
		super(adapterFactory, object);
		
    	final EStructuralFeature ref = Bpmn2Package.eINSTANCE.getCallConversation_CalledCollaborationRef();
    	setFeatureDescriptor(ref,
    		new RootElementRefFeatureDescriptor<CallConversation>(this,object,ref) {
			
				@Override
				public Hashtable<String, Object> getChoiceOfValues() {
					Hashtable<String, Object> choices = new Hashtable<String,Object>();
					String label;
					Definitions defs = ModelUtil.getDefinitions(object);
	
					// first add all local Collaborations
					List<Collaboration> localCollaborations = ModelUtil.getAllRootElements(defs, Collaboration.class);
					for (Collaboration elem : localCollaborations) {
						label = ExtendedPropertiesProvider.getTextValue(elem) + " (" + elem.getId() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
						choices.put(label, elem);
					}
					
					// add Collaborations defined in imports
					ImportUtil importer = new ImportUtil();
					List<Import> imports = defs.getImports();
					for (Import imp : imports) {
						if (ImportUtil.IMPORT_TYPE_BPMN2.equals(imp.getImportType())) {
							// load the bpmn file and look for Collaborations
							Object importedObject = importer.loadImport(imp);
							if (importedObject instanceof DocumentRoot) {
								Definitions importDefs = ((DocumentRoot)importedObject).getDefinitions();
								for (RootElement elem : importDefs.getRootElements()) {
									if (elem instanceof Collaboration) {
										label = ExtendedPropertiesProvider.getTextValue(elem) + " (" + imp.getLocation() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
										choices.put(label, elem);
									}
								}
							}
						}
					}
	
					// remove the Call Collaboration's owning Collaboration (can't make recursive calls?)
					EObject parent = object.eContainer();
					while (parent!=null && !(parent instanceof RootElement)) {
						parent = parent.eContainer();
					}
					List<String> keys = new ArrayList<String>();
					for (Entry<String, Object> entry : choices.entrySet()) {
						if (entry.getValue() == parent)
							keys.add(entry.getKey());
					}
					for (String key : keys)
						choices.remove(key);
					
					return choices;
				}
    		}
    	);
	}

}
