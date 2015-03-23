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
package org.eclipse.bpmn2.modeler.core.features.containers;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.AbstractLayoutBpmn2ShapeFeature;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

public class LayoutContainerFeature extends AbstractLayoutBpmn2ShapeFeature {

	private final static IGaService gaService = Graphiti.getGaService();

	private static class SiblingLaneComparator implements Comparator<Shape> {
		@Override
		public int compare(Shape o1, Shape o2) {
			Lane l1 = BusinessObjectUtil.getFirstElementOfType(o1, Lane.class);
			Lane l2 = BusinessObjectUtil.getFirstElementOfType(o2, Lane.class);

			if (l1 != null && l2 != null && l1.eContainer().equals(l2.eContainer())) {
				int y1 = o1.getGraphicsAlgorithm().getY();
				int y2 = o2.getGraphicsAlgorithm().getY();
				return new Integer(y1).compareTo(y2);
			}
			return 0;
		}
	}

	public LayoutContainerFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		PictogramElement pe = context.getPictogramElement();
		if (!(pe instanceof ContainerShape)) {
			return false;
		}
		return FeatureSupport.isParticipant(pe) || FeatureSupport.isLane(pe);
	}

	@Override
	public boolean layout(ILayoutContext context) {
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		ContainerShape rootContainer = FeatureSupport.getRootContainer(containerShape);
		
		resizeContainersRecursively(rootContainer);
		postResizeFixLenghts(rootContainer);
		
		FeatureSupport.updateLabel(getFeatureProvider(), containerShape, null);
		if (rootContainer != containerShape)
			FeatureSupport.updateLabel(getFeatureProvider(), rootContainer, null);
		
		DIUtils.updateDIShape(rootContainer);
		
		for (PictogramElement pe : FeatureSupport.getPoolAndLaneDescendants(rootContainer)) {
			if (pe instanceof Connection) {
				FeatureSupport.updateConnection(getFeatureProvider(), (Connection)pe, true);
			}
		}

		return true;
	}

	/**
	 * Check if the given Shape and Business Object can be resized.
	 * 
	 * @param currentBo
	 * @param s
	 * @param bo
	 * @return
	 */
	private boolean canResize(BaseElement currentBo, Shape s, Object bo) {
		return s instanceof ContainerShape &&
			(bo instanceof Lane || bo instanceof Participant) &&
			!bo.equals(currentBo);
	}

	private void postResizeFixLenghts(ContainerShape root) {
		
		BaseElement elem = BusinessObjectUtil.getFirstElementOfType(root, BaseElement.class);
		GraphicsAlgorithm ga = root.getGraphicsAlgorithm();
		int width = ga.getWidth() - 30;
		int height = ga.getHeight() - 30;
		boolean horz = FeatureSupport.isHorizontal(root);

		for (Shape s : root.getChildren()) {
			Object bo = BusinessObjectUtil.getFirstElementOfType(s, BaseElement.class);
			if (canResize(elem, s, bo)) {
				GraphicsAlgorithm childGa = s.getGraphicsAlgorithm();
				if (horz)
					gaService.setSize(childGa, width, childGa.getHeight());
				else
					gaService.setSize(childGa, childGa.getWidth(), height);
				DIUtils.updateDIShape(s);
				postResizeFixLenghts((ContainerShape) s);
			}
		}
		DIUtils.updateDIShape(root);
	}

	private Dimension resizeContainer(ContainerShape container) {
		BaseElement elem = BusinessObjectUtil.getFirstElementOfType(container, BaseElement.class);
		int height = 0;
		int width = container.getGraphicsAlgorithm().getWidth() - 30;
		boolean horz = FeatureSupport.isHorizontal(container);
		if (horz) {
			height = 0;
			width = container.getGraphicsAlgorithm().getWidth() - 30;
		} else {
			width = 0;
			height = container.getGraphicsAlgorithm().getHeight() - 30;
		}

		EList<Shape> children = container.getChildren();
		ECollections.sort(children, new SiblingLaneComparator());
		for (Shape s : children) {
			Object bo = BusinessObjectUtil.getFirstElementOfType(s, BaseElement.class);
			if (canResize(elem, s, bo)) {
				GraphicsAlgorithm ga = s.getGraphicsAlgorithm();
				if (horz) {
					gaService.setLocation(ga, 30, height);
					height += ga.getHeight() - 1;
					if (ga.getWidth() >= width) {
						width = ga.getWidth();
					} else {
						gaService.setSize(ga, width, ga.getHeight());
					}
				} else {
					gaService.setLocation(ga, width, 30);
					width += ga.getWidth() - 1;
					if (ga.getHeight() >= height) {
						height = ga.getHeight();
					} else {
						gaService.setSize(ga, ga.getWidth(), height);
					}
				}
			}
		}

		GraphicsAlgorithm ga = container.getGraphicsAlgorithm();

		if (horz) {
			if (height == 0) {
				return new Dimension(ga.getWidth(), ga.getHeight());
			} else {
				int newWidth = width + 30;
				int newHeight = height + 1;
				gaService.setSize(ga, newWidth, newHeight);

				for (Shape s : children) {
					if (FeatureSupport.isLabelShape(s))
						continue;
					GraphicsAlgorithm childGa = s.getGraphicsAlgorithm();
					if (childGa instanceof Polyline) {
						// this is the separator line in a Pool
						Polyline line = (Polyline) childGa;
						Point p0 = line.getPoints().get(0);
						Point p1 = line.getPoints().get(1);
						p0.setX(30);
						p0.setY(0);
						p1.setX(30);
						p1.setY(newHeight);
					}
				}

				return new Dimension(newWidth, newHeight);
			}
		} else {
			if (width == 0) {
				return new Dimension(ga.getWidth(), ga.getHeight());
			} else {
				int newWidth = width + 1;
				int newHeight = height + 30;
				gaService.setSize(ga, newWidth, newHeight);

				for (Shape s : children) {
					if (FeatureSupport.isLabelShape(s))
						continue;
					GraphicsAlgorithm childGa = s.getGraphicsAlgorithm();
					if (childGa instanceof Polyline) {
						// this is the separator line in a Pool
						Polyline line = (Polyline) childGa;
						Point p0 = line.getPoints().get(0);
						Point p1 = line.getPoints().get(1);
						p0.setX(0);
						p0.setY(30);
						p1.setX(newWidth);
						p1.setY(30);
					}
				}

				return new Dimension(newWidth, newHeight);
			}
		}
	}

	private Dimension resizeContainersRecursively(ContainerShape root) {
		BaseElement elem = BusinessObjectUtil.getFirstElementOfType(root, BaseElement.class);
		List<Dimension> dimensions = new ArrayList<Dimension>();
		int foundContainers = 0;
		boolean horz = FeatureSupport.isHorizontal(root);

		List<Shape> children = new ArrayList<Shape>();
		children.addAll(root.getChildren());
		for (Shape s : children) {
			if (FeatureSupport.isLabelShape(s))
				continue;
			Object bo = BusinessObjectUtil.getFirstElementOfType(s, BaseElement.class);
			if (canResize(elem, s, bo)) {
				foundContainers += 1;
				Dimension d = resizeContainersRecursively((ContainerShape) s);
				if (d != null) {
					dimensions.add(d);
				}
			}
		}

		if (dimensions.isEmpty()) {
			GraphicsAlgorithm ga = root.getGraphicsAlgorithm();
			for (Shape s : children) {
				if (FeatureSupport.isLabelShape(s))
					continue;
				GraphicsAlgorithm childGa = s.getGraphicsAlgorithm();
				if (childGa instanceof Polyline) {
					Polyline line = (Polyline) childGa;
					Point p0 = line.getPoints().get(0);
					Point p1 = line.getPoints().get(1);
					if (horz) {
						p0.setX(30);
						p0.setY(0);
						p1.setX(30);
						p1.setY(ga.getHeight());
					} else {
						p0.setX(0);
						p0.setY(30);
						p1.setX(ga.getWidth());
						p1.setY(30);
					}
				}
			}
			return new Dimension(ga.getWidth(), ga.getHeight());
		}

		if (foundContainers > 0) {
			return resizeContainer(root);
		}

		return getMaxDimension(horz, dimensions);
	}

	private Dimension getMaxDimension(boolean horz, List<Dimension> dimensions) {
		if (dimensions.isEmpty()) {
			return null;
		}
		int height = 0;
		int width = 0;
	
		if (horz) {
			for (Dimension d : dimensions) {
				height += d.height;
				if (d.width > width) {
					width = d.width;
				}
			}
		}
		else {
			for (Dimension d : dimensions) {
				width += d.width;
				if (d.height > height) {
					height = d.height;
				}
			}
		}
		return new Dimension(width, height);
	}
}
