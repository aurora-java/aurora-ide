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
 * @author Innar Made
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.diagram;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.eclipse.bpmn2.DataState;
import org.eclipse.bpmn2.ParticipantMultiplicity;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.ui.Activator;
import org.eclipse.bpmn2.modeler.ui.editor.BPMN2Editor;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.notification.DefaultNotificationService;
import org.eclipse.graphiti.notification.INotificationService;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

public class Bpmn2DiagramTypeProvider extends AbstractDiagramTypeProvider {
	private IToolBehaviorProvider[] toolBehaviorProviders;
	private INotificationService notificationService;

	public Bpmn2DiagramTypeProvider() {
		super();
		setFeatureProvider(new BPMN2FeatureProvider(this));
	}

	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		if (toolBehaviorProviders == null) {
			BPMN2Editor editor = (BPMN2Editor)getDiagramEditor();
			TargetRuntime rt = editor.getTargetRuntime();
			IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
					Activator.UI_EXTENSION_ID);
			Bpmn2ToolBehaviorProvider provider = null;
			try {
				for (IConfigurationElement e : config) {
					if (e.getName().equals("toolProvider")) { //$NON-NLS-1$
						String id = e.getAttribute("id"); //$NON-NLS-1$
						String runtimeId = e.getAttribute("runtimeId"); //$NON-NLS-1$
						if (rt!=null && rt.getId().equals(runtimeId)) {
							String className = e.getAttribute("class"); //$NON-NLS-1$
							ClassLoader cl = rt.getRuntimeExtension().getClass().getClassLoader();
							Constructor ctor = null;
							Class providerClass = Class.forName(className, true, cl);
							ctor = providerClass.getConstructor(IDiagramTypeProvider.class);
							provider = (Bpmn2ToolBehaviorProvider)ctor.newInstance(this);
							break;
						}
					}
				}
			}
			catch (Exception ex) {
				Activator.logError(ex);
			}
			
			if (provider==null)
				provider = new Bpmn2ToolBehaviorProvider(this);
			toolBehaviorProviders = new IToolBehaviorProvider[] { provider };
		}
		return toolBehaviorProviders;
	}


	public INotificationService getNotificationService() {
		if (this.notificationService == null) {
			this.notificationService = new BPMNNotificationService(this);
		}
		return this.notificationService;
	}
	
	public class BPMNNotificationService extends DefaultNotificationService {
		
		public BPMNNotificationService(IDiagramTypeProvider diagramTypeProvider) {
			super(diagramTypeProvider);
		}

		public PictogramElement[] calculateRelatedPictogramElements(Object[] changedBOs) {
			PictogramElement[] pes = super.calculateRelatedPictogramElements(changedBOs);

			final IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();
			ArrayList<PictogramElement> changedAndRelatedBOs = new ArrayList<PictogramElement>();
			for (PictogramElement pe : pes) {
				changedAndRelatedBOs.add(pe);
			}
			
			for (Object cbo : changedBOs) {
				if (cbo instanceof ParticipantMultiplicity) {
					cbo = ((ParticipantMultiplicity)cbo).eContainer();
					if (cbo==null)
						continue;
				}
				else if (cbo instanceof DataState) {
					// this requires a change in the PE's label
					cbo = ((DataState)cbo).eContainer();
				}
				final PictogramElement[] allPictogramElementsForBusinessObject = featureProvider.getAllPictogramElementsForBusinessObject(cbo);
				for (PictogramElement pe : allPictogramElementsForBusinessObject) {
					changedAndRelatedBOs.add(pe);
				}
			}
			return changedAndRelatedBOs.toArray(new PictogramElement[0]);
		}
		
		@Override
		public void updatePictogramElements(PictogramElement[] dirtyPes) {
			ArrayList<PictogramElement> updated = new ArrayList<PictogramElement>();
			for (PictogramElement pe : dirtyPes) {
				if (!updated.contains(pe))
					updated.add(pe);
			}
			super.updatePictogramElements(updated.toArray(new PictogramElement[updated.size()]));
		}
	}
}
