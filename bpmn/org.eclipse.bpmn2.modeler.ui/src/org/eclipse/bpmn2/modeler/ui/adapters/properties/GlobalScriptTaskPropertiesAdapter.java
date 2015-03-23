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

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.GlobalScriptTask;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Bob Brodt
 *
 */
public class GlobalScriptTaskPropertiesAdapter extends ExtendedPropertiesAdapter<GlobalScriptTask> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public GlobalScriptTaskPropertiesAdapter(AdapterFactory adapterFactory, GlobalScriptTask object) {
		super(adapterFactory, object);


    	EStructuralFeature ref = Bpmn2Package.eINSTANCE.getGlobalScriptTask_Script();
    	setFeatureDescriptor(ref,
			new FeatureDescriptor<GlobalScriptTask>(this,object,ref) {
    		
	    		@Override
				public boolean isMultiLine() {
					return true;
				}

				@Override
	    		public String getTextValue() {
					if (object.getScript()==null)
						return ""; //$NON-NLS-1$
					return object.getScript();
	    		}
    	});
    	
    	ref = Bpmn2Package.eINSTANCE.getGlobalScriptTask_ScriptLanguage();
    	setFeatureDescriptor(ref,
			new FeatureDescriptor<GlobalScriptTask>(this,object,ref) {
    		
	    		@Override
	    		public String getTextValue() {
					if (object.getScriptLanguage()==null)
						return ""; //$NON-NLS-1$
					return object.getScriptLanguage();
	    		}
    	});
	}

}
