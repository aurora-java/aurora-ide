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
package org.eclipse.bpmn2.modeler.core.features;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

/**
 * This is the Graphiti Feature Container class for {@link BPMNDiagram}
 * elements. Currently, it is only used to contribute context menu actions for
 * Connection Routing.
 */
public class BPMNDiagramFeatureContainer extends BaseElementFeatureContainer {

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.BaseElementFeatureContainer#getApplyObject(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public Object getApplyObject(IContext context) {
		if (context instanceof ICustomContext) {
			PictogramElement[] pes = ((ICustomContext) context).getPictogramElements();
			if (pes.length==1)
				return BusinessObjectUtil.getFirstElementOfType(pes[0], BPMNDiagram.class);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.BaseElementFeatureContainer#canApplyTo(java.lang.Object)
	 */
	@Override
	public boolean canApplyTo(Object o) {
		return o instanceof BPMNDiagram;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IShapeFeatureContainer#getCreateFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public ICreateFeature getCreateFeature(IFeatureProvider fp) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getAddFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getUpdateFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.BaseElementFeatureContainer#getDirectEditingFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IDirectEditingFeature getDirectEditingFeature(IFeatureProvider fp) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getLayoutFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IShapeFeatureContainer#getMoveFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IMoveShapeFeature getMoveFeature(IFeatureProvider fp) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IShapeFeatureContainer#getResizeFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IResizeShapeFeature getResizeFeature(IFeatureProvider fp) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getDeleteFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider fp) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.BaseElementFeatureContainer#getCustomFeatures(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		return new ICustomFeature[] {
				new EnableConnectionRoutingFeature(fp),
				new LayoutConnectionsFeature(fp)
			};
	}

	/**
	 * Context menu action to globally enable or disable the Manhattan
	 * Connection Router.
	 */
	public class EnableConnectionRoutingFeature extends AbstractCustomFeature {

		/** The User Preferences. */
		Bpmn2Preferences preferences;
		
		/**
		 * Instantiates a new connection routing custom feature.
		 *
		 * @param fp the Feature Provider
		 */
		public EnableConnectionRoutingFeature(IFeatureProvider fp) {
			super(fp);
			Diagram diagram = fp.getDiagramTypeProvider().getDiagram();
			EObject bo = BusinessObjectUtil.getBusinessObjectForPictogramElement(diagram);
			preferences = Bpmn2Preferences.getInstance(bo);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
		 */
		@Override
		public boolean canExecute(ICustomContext context) {
			return true;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#isAvailable(org.eclipse.graphiti.features.context.IContext)
		 */
		@Override
		public boolean isAvailable(IContext context) {
			return true;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
		 */
		@Override
		public String getName() {
			if (preferences.getEnableConnectionRouting())
				return Messages.BPMNDiagramFeatureContainer_Disable_Name;
			return Messages.BPMNDiagramFeatureContainer_Enable_Name;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
		 */
		@Override
		public String getDescription() {
			return Messages.BPMNDiagramFeatureContainer_Disable_Enable_Description;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
		 */
		@Override
		public void execute(ICustomContext context) {
			boolean enabled = preferences.getEnableConnectionRouting();
			preferences.setEnableConnectionRouting(!enabled);
		}
	}
	
	/**
	 * Context menu action to force automatic Connection Routing on all connections in
	 * the Diagram.
	 */
	public class LayoutConnectionsFeature extends AbstractCustomFeature {

		/** indicates if changes were made as a result of updating all connections. */
		boolean hasDoneChanges = false;
		
		/**
		 * Instantiates a new layout connections feature.
		 *
		 * @param fp the fp
		 */
		public LayoutConnectionsFeature(IFeatureProvider fp) {
			super(fp);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
		 */
		@Override
		public String getName() {
			return Messages.BPMNDiagramFeatureContainer_Reroute_All_Name;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
		 */
		@Override
		public String getDescription() {
			return Messages.BPMNDiagramFeatureContainer_Reroute_All_Description;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
		 */
		@Override
		public boolean canExecute(ICustomContext context) {
			PictogramElement[] pes = context.getPictogramElements();
			EObject businessObject = BusinessObjectUtil.getBusinessObjectForPictogramElement(pes[0]);
			return businessObject instanceof BPMNDiagram;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.features.impl.AbstractFeature#hasDoneChanges()
		 */
		@Override
		public boolean hasDoneChanges() {
			return hasDoneChanges;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
		 */
		@Override
		public void execute(ICustomContext context) {
			PictogramElement[] pes = context.getPictogramElements();
			EObject businessObject = BusinessObjectUtil.getBusinessObjectForPictogramElement(pes[0]);
			BPMNDiagram bpmnDiagram = (BPMNDiagram)businessObject;
			Diagram diagram = getFeatureProvider().getDiagramTypeProvider().getDiagram();
			
			// NOTE: this only operates on the current BPMNDiagram tab. If, for example, the contents
			// of a SubProcess are contained in a different BPMNDiagram (i.e. a different tab of the
			// multi-page editor), those connections will NOT be affected.
			for (DiagramElement de : bpmnDiagram.getPlane().getPlaneElement()) {
				if (de instanceof BPMNEdge) {
					BaseElement be = ((BPMNEdge)de).getBpmnElement();
					for (PictogramElement pe : Graphiti.getLinkService().getPictogramElements(diagram, be)) {
						if (pe instanceof Connection) {
							// force the default routing to happen
							if (FeatureSupport.updateConnection(getFeatureProvider(),
									(Connection)pe, true))
								hasDoneChanges = true;
						}
					}
				}
			}
		}
	}
}
