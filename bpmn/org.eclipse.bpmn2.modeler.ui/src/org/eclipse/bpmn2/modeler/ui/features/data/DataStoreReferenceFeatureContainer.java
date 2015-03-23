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
package org.eclipse.bpmn2.modeler.ui.features.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.DataStore;
import org.eclipse.bpmn2.DataStoreReference;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature;
import org.eclipse.bpmn2.modeler.core.features.AbstractCreateFlowElementFeature;
import org.eclipse.bpmn2.modeler.core.features.BaseElementFeatureContainer;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.MultiUpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.data.MoveDataFeature;
import org.eclipse.bpmn2.modeler.core.features.label.AddShapeLabelFeature;
import org.eclipse.bpmn2.modeler.core.features.label.UpdateLabelFeature;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.model.ModelHandler;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.StyleUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.ITargetContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.ui.internal.util.ui.PopupMenu;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class DataStoreReferenceFeatureContainer extends BaseElementFeatureContainer {

	@Override
	public boolean canApplyTo(Object o) {
		return super.canApplyTo(o) && o instanceof DataStoreReference;
	}

	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return null;
	}

	@Override
	public ICreateFeature getCreateFeature(IFeatureProvider fp) {
		return new CreateDataStoreReferenceFeature(fp);
	}

	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddDataStoreReferenceFeature(fp);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		MultiUpdateFeature multiUpdate = new MultiUpdateFeature(fp);
		multiUpdate.addFeature(new UpdateItemAwareElementFeature<DataStoreReference>(fp));
		multiUpdate.addFeature(new UpdateLabelFeature(fp));
		return multiUpdate;
	}

	@Override
	public IMoveShapeFeature getMoveFeature(IFeatureProvider fp) {
		return new MoveDataFeature(fp);
	}

	@Override
	public IResizeShapeFeature getResizeFeature(IFeatureProvider fp) {
		return new DefaultResizeShapeFeature(fp) {
			@Override
			public boolean canResizeShape(IResizeShapeContext context) {
				return false;
			}
		};
	}

	public class AddDataStoreReferenceFeature extends AbstractBpmn2AddFeature<DataStoreReference> {
		public AddDataStoreReferenceFeature(IFeatureProvider fp) {
			super(fp);
		}

		public IAddFeature getAddLabelFeature(IFeatureProvider fp) {
			return new AddShapeLabelFeature(fp);
		}

		@Override
		public boolean canAdd(IAddContext context) {
			return true;
		}

		@Override
		public PictogramElement add(IAddContext context) {
			DataStoreReference businessObject = getBusinessObject(context);

			int width = getWidth(context);
			int height = getHeight(context);

			ContainerShape containerShape = peService.createContainerShape(context.getTargetContainer(), true);
			Rectangle invisibleRect = gaService.createInvisibleRectangle(containerShape);
			gaService.setLocationAndSize(invisibleRect, context.getX(), context.getY(), width, height);

			int whalf = width / 2;

			int[] xy = { 0, 10, whalf, 20, width, 10, width, height - 10, whalf, height, 0, height - 10 };
			int[] bend = { 0, 0, whalf, whalf, 0, 0, 0, 0, whalf, whalf, 0, 0 };
			Polygon polygon = gaService.createPolygon(invisibleRect, xy, bend);
			polygon.setFilled(true);
			// polygon.setForeground(manageColor(StyleUtil.CLASS_FOREGROUND));

			StyleUtil.applyStyle(polygon, businessObject);

			xy = new int[] { 0, 14, whalf, 24, width, 14 };
			bend = new int[] { 0, 0, whalf, whalf, 0, 0 };
			Polyline line1 = gaService.createPolyline(invisibleRect, xy, bend);
			line1.setForeground(manageColor(StyleUtil.CLASS_FOREGROUND));

			xy = new int[] { 0, 18, whalf, 28, width, 18 };
			Polyline line2 = gaService.createPolyline(invisibleRect, xy, bend);
			line2.setForeground(manageColor(StyleUtil.CLASS_FOREGROUND));

			xy = new int[] { 0, 11, whalf, 0, width, 11 };
			Polyline lineTop = gaService.createPolyline(invisibleRect, xy, bend);
			lineTop.setForeground(manageColor(StyleUtil.CLASS_FOREGROUND));
			boolean isImport = context.getProperty(GraphitiConstants.IMPORT_PROPERTY) != null;
			createDIShape(containerShape, businessObject, !isImport);
			
			// hook for subclasses to inject extra code
			((AddContext)context).setWidth(width);
			((AddContext)context).setHeight(height);
			decorateShape(context, containerShape, businessObject);

			peService.createChopboxAnchor(containerShape);
			AnchorUtil.addFixedPointAnchors(containerShape, invisibleRect);

			return containerShape;
		}

		protected boolean isHorizontal(ITargetContext context) {
			return true;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#getBusinessObjectType()
		 */
		@Override
		public Class getBusinessObjectType() {
			return DataStoreReference.class;
		}
	}

	public static class CreateDataStoreReferenceFeature extends AbstractCreateFlowElementFeature<DataStoreReference> {

		private static ILabelProvider labelProvider = new ILabelProvider() {

			public void removeListener(ILabelProviderListener listener) {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void dispose() {

			}

			public void addListener(ILabelProviderListener listener) {

			}

			public String getText(Object element) {
				if (((DataStore) element).getId() == null)
					return ((DataStore) element).getName();
				return NLS.bind(Messages.DataStoreReferenceFeatureContainer_Ref, ((DataStore) element).getName());
			}

			public Image getImage(Object element) {
				return null;
			}

		};

		public CreateDataStoreReferenceFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public String getStencilImageId() {
			return ImageProvider.IMG_16_DATA_STORE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.bpmn2.modeler.core.features.AbstractCreateFlowElementFeature
		 * #getFlowElementClass()
		 */
		@Override
		public EClass getBusinessObjectClass() {
			return Bpmn2Package.eINSTANCE.getDataStoreReference();
		}

		@Override
		public DataStoreReference createBusinessObject(ICreateContext context) {
			// NOTE: this is slightly different from DataObject/DataObjectReference:
			// Both DataObject and DataObjectReference instances are contained in some FlowElementContainer
			// (e.g. a Process) whereas DataStore instances are contained in the root element "Definitions".
			// This means that whenever the user creates a "Data Store" (using DND from the tool palette),
			// it necessarily means that a DataStoreReference is created and added to the FlowElementContainer
			// which is the target of the ICreateContext. In addition, if the new DataStoreReference refers
			// to a new DataStore, one is created and added to Definitions.
			changesDone = true;
				ModelHandler mh = ModelHandler.getInstance(getDiagram());

			DataStoreReference dataStoreRef = Bpmn2ModelerFactory.create(DataStoreReference.class);
				DataStore dataStore = Bpmn2ModelerFactory.create(DataStore.class);
				String oldName = dataStore.getName();
				dataStore.setName(Messages.DataStoreReferenceFeatureContainer_New);
				dataStore.setId(null);

				List<DataStore> dataStoreList = new ArrayList<DataStore>();
				dataStoreList.add(dataStore);
				TreeIterator<EObject> iter = mh.getDefinitions().eAllContents();
				while (iter.hasNext()) {
					EObject obj = iter.next();
					if (obj instanceof DataStore)
						dataStoreList.add((DataStore) obj);
				}

				DataStore result = dataStore;
				if (dataStoreList.size() > 1) {
					PopupMenu popupMenu = new PopupMenu(dataStoreList, labelProvider);
					changesDone = popupMenu.show(Display.getCurrent().getActiveShell());
					if (changesDone) {
						result = (DataStore) popupMenu.getResult();
					}
					else {
						EcoreUtil.delete(dataStore);
					EcoreUtil.delete(dataStoreRef);
					dataStoreRef = null;
					}
				}
			else
				changesDone = true;

				if (changesDone) {
					if (result == dataStore) { // the new one
						mh.addRootElement(dataStore);
						ModelUtil.setID(dataStore);
						dataStore.setName(oldName);
					dataStoreRef.setName(dataStore.getName());
					} else
					dataStoreRef.setName(
							NLS.bind(
								Messages.DataStoreReferenceFeatureContainer_Default_Name,
								result.getName()
							)
						);
	
				dataStoreRef.setDataStoreRef(result);
				ModelUtil.setID(dataStoreRef, mh.getResource());
				putBusinessObject(context, dataStoreRef);
				}
				
			return dataStoreRef;
		}
	}

	@Override
	public IDeleteFeature getDeleteFeature(IFeatureProvider context) {
		return null;
	}
}