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
 * @author Ivar Meikas
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features.label;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.DataState;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNLabel;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2UpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle.LabelPosition;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.graphiti.ui.services.IUiLayoutService;

public class UpdateLabelFeature extends AbstractBpmn2UpdateFeature {

	public UpdateLabelFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		if (bo instanceof BaseElement) {
			return hasLabel((BaseElement)bo);
		}
		return false;
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		IReason reason = super.updateNeeded(context);
		if (reason.toBoolean())
			return reason;

		PictogramElement ownerPE = FeatureSupport.getLabelOwner(context);

		BaseElement element = (BaseElement) BusinessObjectUtil.getFirstElementOfType(ownerPE, BaseElement.class);

		Shape labelShape = FeatureSupport.getLabelShape(ownerPE);
		if (labelShape != null) {

			if (Graphiti.getPeService().getPropertyValue(labelShape, GraphitiConstants.LABEL_CHANGED) != null) {
				return Reason.createTrueReason(Messages.UpdateLabelFeature_LabelChanged);
			}

			String newLabel = getLabelString(element);
			if (newLabel == null || newLabel.isEmpty())
				newLabel = ""; //$NON-NLS-1$
			AbstractText text = (AbstractText) labelShape.getGraphicsAlgorithm();
			String oldLabel = text.getValue();
			if (oldLabel == null || oldLabel.isEmpty())
				oldLabel = ""; //$NON-NLS-1$

			if (!newLabel.equals(oldLabel))
				return Reason.createTrueReason(Messages.UpdateLabelFeature_TextChanged);
			
		}
		return Reason.createFalseReason();
	}

	@Override
	public boolean update(IUpdateContext context) {
		PictogramElement pe = FeatureSupport.getLabelOwner(context);
		Point offset = (Point) context.getProperty(GraphitiConstants.LABEL_OFFSET);
		boolean isAdding = isAddingLabel(context);
		adjustLabelLocation(pe, isAdding, offset);
		return true;
	}

	protected boolean isAddingLabel(IContext context) {
		return context.getProperty(GraphitiConstants.PICTOGRAM_ELEMENTS) != null
				|| context.getProperty(GraphitiConstants.PICTOGRAM_ELEMENT) != null
				|| context.getProperty(GraphitiConstants.IMPORT_PROPERTY) != null;
	}

	protected boolean hasLabel(BaseElement element) {
		return ModelUtil.hasName(element);
	}

	protected String getLabelString(BaseElement element) {
		/*
		 * Unfortunately this needs to be aware of ItemAwareElements, which have
		 * a Data State (the Data State needs to appear below the element's
		 * label in []) The UpdateLabelFeature is checked in
		 * BPMN2FeatureProvider AFTER the Update Feature for Data Objects is
		 * executed - this wipes out the Label provided by
		 * ItemAwareElementUpdateFeature.
		 */
		String label = ModelUtil.getName(element);
		if (element instanceof ItemAwareElement) {
			DataState state = ((ItemAwareElement) element).getDataState();
			if (state != null && state.getName() != null) {
				return label + "\n[" + state.getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return label;
	}

	protected int getLabelWrapWidth(PictogramElement ownerPE) {
		int w = GraphicsUtil.calculateSize(ownerPE).getWidth();
		return w>=80 ? w : 80; 
	}
	
	protected int[] wrapText(AbstractText ga, String text, int wrapWidth) {
		Font font = ga.getFont();
		List<String> ss = new ArrayList<String>();
		int start = 0;
		for (int end=0; end<text.length(); ++end) {
			char c = text.charAt(end);
			if (c==' ') {
				ss.add(text.substring(start, end+1));
				start = end+1;
			}
			else if (c=='\n') {
				ss.add(text.substring(start, end+1));
				start = end+1;
			}
		}
		if (start<text.length())
			ss.add(text.substring(start));
		String words[] = ss.toArray(new String[ss.size()]);
		IDimension dim = calculateTextSize(text, font);
		int totalHeight = dim.getHeight();
		int totalWidth = dim.getWidth();
		int height = totalHeight;
		int width = 0;
		String line = "";
		String nextword = "";
		for (int i=0; i<words.length; ++i) {
			line += words[i];
			if (i<words.length-1)
				nextword = words[i+1];
			else
				nextword = "";
			dim = calculateTextSize(line + nextword, font);
			if (dim.getWidth()>wrapWidth) {
				height += dim.getHeight();
				dim = calculateTextSize(line, font);
				if (dim.getWidth()>width)
					width = dim.getWidth();
				line = "";
			}
			else if (dim.getWidth()>width)
				width = dim.getWidth();
		}
		if (width==0)
			width = totalWidth;
		return new int[] {height, width};
	}
	
	private IDimension calculateTextSize(String text, Font font) {
		IDimension dim = GraphitiUi.getUiLayoutService().calculateTextSize(text, font);
		if (text.endsWith("\n"))
			dim.setHeight(2*dim.getHeight());
		return dim;
	}
	
	protected Rectangle getLabelBounds(PictogramElement pe, boolean isAddingLabel, Point offset) {

		PictogramElement ownerPE = FeatureSupport.getLabelOwner(pe);
		Shape labelShape = FeatureSupport.getLabelShape(pe);
		if (labelShape != null) {
			AbstractText labelGA = (AbstractText) labelShape.getGraphicsAlgorithm();
			BaseElement element = (BaseElement) BusinessObjectUtil.getFirstElementOfType(ownerPE, BaseElement.class);
			String text = getLabelString(element);
			if (text == null) {
				text = ""; //$NON-NLS-1$
			}

			// Get the absolute location of the owner. If the owner is a
			// Connection use the Connection midpoint.
			ILocation ownerLoc = ownerPE instanceof Connection ? Graphiti.getPeLayoutService().getConnectionMidpoint(
					(Connection) ownerPE, 0.5) : Graphiti.getPeService().getLocationRelativeToDiagram((Shape) ownerPE);
			IDimension ownerSize = GraphicsUtil.calculateSize(ownerPE);
			ILocation labelLoc = Graphiti.getPeService().getLocationRelativeToDiagram(labelShape);
			int x = 0;
			int y = 0;
			int w = getLabelWidth(labelGA);
			int h = getLabelHeight(labelGA);
			int wrapWidth = getLabelWrapWidth(ownerPE);
			if (wrapWidth>0 && w > wrapWidth) {
				int hw[] = wrapText(labelGA, text, wrapWidth);
				h = hw[0];
				w = hw[1];
			}
			
			LabelPosition hpos = getHorizontalLabelPosition(labelGA);
			LabelPosition vpos = getVerticalLabelPosition(labelGA);

			if (isAddingLabel) {
				BPMNLabel bpmnLabel = null;
				if (ownerPE instanceof Connection) {
					BPMNEdge bpmnEdge = DIUtils.findBPMNEdge(element);
					if (bpmnEdge!=null)
						bpmnLabel = bpmnEdge.getLabel();
				} else {
					BPMNShape bpmnShape = DIUtils.findBPMNShape(element);
					if (bpmnShape!=null)
						bpmnLabel = bpmnShape.getLabel();
				}
				Bounds bounds = bpmnLabel == null ? null : bpmnLabel.getBounds();

				if (bounds == null) {
					/*
					 * The edge or shape does not have a BPMNLabel so treat the
					 * label normally, that is adjust its location according to
					 * the User Preferences. In this case force the relative
					 * location of the label to be below the shape or connection
					 * in case User Preferences allow labels to be moved
					 * manually.
					 */
					isAddingLabel = false;
					if (hpos == LabelPosition.MOVABLE) {
						vpos = hpos = LabelPosition.SOUTH;
					}
				} else {
					int bw = (int) bounds.getWidth();
					int bh = (int) bounds.getHeight();
					/*
					 * The size provided in BPMNLabel for this Label shape is
					 * not sufficient to hold all of the text at the selected
					 * Font. Recalculate the Label bounds using the selected
					 * preferences.
					 */
					if (bw < w || bh < h) {
						isAddingLabel = false;
					}
					else {
						x = (int) bounds.getX();
						y = (int) bounds.getY();
						w = bw;
						h = bh;
					}
				}
			}

			if (!isAddingLabel && !text.isEmpty()) {
				// calculate X coordinate
				switch (hpos) {
				case NORTH:
				case SOUTH:
				case TOP:
				case CENTER:
				case BOTTOM:
					// X coordinate for these positions are all the same
					x = ownerLoc.getX() + (ownerSize.getWidth() - w)/2;
					break;
				case WEST:
					x = ownerLoc.getX() - w - LabelFeatureContainer.LABEL_MARGIN;
					break;
				case EAST:
					x = ownerLoc.getX() + ownerSize.getWidth() + LabelFeatureContainer.LABEL_MARGIN;
					break;
				case LEFT:
					x = ownerLoc.getX() + LabelFeatureContainer.LABEL_MARGIN;
					break;
				case RIGHT:
					x = ownerLoc.getX() + ownerSize.getWidth() - w - LabelFeatureContainer.LABEL_MARGIN;
					break;
				case MOVABLE:
					x = (int) labelLoc.getX();
					y = (int) labelLoc.getY();
					if (offset != null) {
						x += offset.getX();
						y += offset.getY();
					}
					break;
				}

				// calculate Y coordinate
				switch (vpos) {
				case NORTH:
					y = ownerLoc.getY() - h - LabelFeatureContainer.LABEL_MARGIN/2;
					break;
				case SOUTH:
					y = ownerLoc.getY() + ownerSize.getHeight();
					break;
				case TOP:
					y = ownerLoc.getY() + LabelFeatureContainer.LABEL_MARGIN / 2;
					break;
				case CENTER:
					y = ownerLoc.getY() + (ownerSize.getHeight() - h)/2;
					break;
				case BOTTOM:
					y = ownerLoc.getY() + ownerSize.getHeight() - h - LabelFeatureContainer.LABEL_MARGIN / 2;
					break;
				case WEST:
				case EAST:
				case LEFT:
				case RIGHT:
					// Y coordinate for these positions are all the same
					y = ownerLoc.getY() + (ownerSize.getHeight() - h)/2;
					break;
				case MOVABLE:
					break;
				}
			}
			if (ownerPE instanceof Connection) {
				x -= ownerLoc.getX();
				y -= ownerLoc.getY();
			}
			
			return new Rectangle(x,y,w,h);
		}

		return null;
	}
	
	protected void adjustLabelLocation(PictogramElement pe, boolean isAddingLabel, Point offset) {

		PictogramElement ownerPE = FeatureSupport.getLabelOwner(pe);
		Shape labelShape = FeatureSupport.getLabelShape(pe);
		if (labelShape != null) {
			AbstractText labelGA = (AbstractText) labelShape.getGraphicsAlgorithm();
			BaseElement element = (BaseElement) BusinessObjectUtil.getFirstElementOfType(pe, BaseElement.class);
			String text = getLabelString(element);
			if (text == null) {
				text = ""; //$NON-NLS-1$
			}
			if (!text.equals(labelGA.getValue()))
				labelGA.setValue(text);

			Rectangle bounds = getLabelBounds(labelShape, isAddingLabel, offset);
			int x = bounds.x;
			int y = bounds.y;
			int w = bounds.width;
			int h = bounds.height;
			// move and resize the label shape and set the new size of the Text GA
			// make sure the label shape is a child of the same ContainerShape
			// as its owner.
			if (!(ownerPE instanceof Connection)) {
				// this is only valid if the owner is not a Connection;
				// Connection labels are always contained in the Diagram
				// just like the Connection itself.
				ContainerShape container = getTargetContainer(ownerPE);
				if (labelShape.eContainer() != container) {
					container.getChildren().add(labelShape);
				}
			}
			GraphicsUtil.setLocationRelativeToDiagram(labelShape, x, y);
			Graphiti.getGaService().setSize(labelGA, w, h);
			if (ownerPE instanceof Shape) {
				// Note that it's not necessary to send Connection Labels
				// to the front because they are children of Connections
				// which are in the Connection Layer, which is always on
				// top of the Figure Layer.
				Graphiti.getPeService().sendToFront(labelShape);
			}

			// if the label is owned by a connection, its location will always be
			// relative to the connection midpoint so we have to get the absolute
			// location for the BPMNLabel coordinates.
			ILocation absloc = Graphiti.getPeService().getLocationRelativeToDiagram(labelShape);
			DIUtils.updateDILabel(ownerPE, absloc.getX(), absloc.getY(), w, h);
			labelShape.setVisible(!text.isEmpty());
			Graphiti.getPeService().removeProperty(labelShape, GraphitiConstants.LABEL_CHANGED);
		}
	}
	
	protected ContainerShape getTargetContainer(PictogramElement ownerPE) {
		return (ContainerShape) ownerPE.eContainer();
	}
	
	protected int getLabelWidth(AbstractText text) {
		return getLabelSize(text).width;
	}

	protected int getLabelHeight(AbstractText text) {
		return getLabelSize(text).height;
	}

	protected Dimension getLabelSize(AbstractText text) {
		int width = 0;
		int height = 0;
		if (text.getValue() != null && !text.getValue().isEmpty()) {
			String[] strings = text.getValue().split(LabelFeatureContainer.LINE_BREAK);
			for (String string : strings) {
				IDimension dim = GraphitiUi.getUiLayoutService().calculateTextSize(string, text.getFont());
				if (dim.getWidth() > width) {
					width = dim.getWidth();
				}
				height += dim.getHeight();
			}
		}
		// TODO: the zoom level influences the effective font size which determines the text extents.
		// Need to figure this stuff out because labels are truncated at high zoom levels.
		// The org.eclipse.graphiti.ui.internal.parts.directedit.GFDirectEditManager#updateScaledFont()
		// does this for SWT Text widgets.
//		GraphicalViewer viewer = (GraphicalViewer) ((IAdaptable)getDiagramEditor()).getAdapter(GraphicalViewer.class);
//		ZoomManager zoomMgr = (ZoomManager) viewer.getProperty(ZoomManager.class.toString());
//		if (zoomMgr!=null) {
//			width = (int)(width * zoomMgr.getZoom());
//			height = (int)(height * zoomMgr.getZoom());
//		}
		return new Dimension(width, height);
	}
	
	/**
	 * Get the position of the label relative to its owning figure for the given
	 * BaseElement as defined in the User Preferences.
	 * 
	 * Overrides will provide their own relative positions for, e.g. Tasks and
	 * TextAnnotations.
	 * @param text the BaseElement that is represented by the graphical
	 *            figure.
	 * 
	 * @return a ShapeStyle LabelPosition relative location indicator.
	 */
	protected LabelPosition getLabelPosition(AbstractText text) {
		PictogramElement pe = FeatureSupport.getLabelOwner(text);
		BaseElement element = BusinessObjectUtil.getFirstBaseElement(pe);
		ShapeStyle ss = ShapeStyle.getShapeStyle(element);
		return ss.getLabelPosition();
	}
	
	protected LabelPosition getHorizontalLabelPosition(AbstractText text) {
		return getLabelPosition(text);
	}
	
	protected LabelPosition getVerticalLabelPosition(AbstractText text) {
		return getLabelPosition(text);
	}
}