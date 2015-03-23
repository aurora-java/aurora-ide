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
package org.eclipse.bpmn2.modeler.core.utils;

import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.mm.PropertyContainer;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeService;

public class BoundaryEventPositionHelper {

	public static class PositionOnLine {

		public enum LineType {
			X, Y, XY, UNKNOWN
		}

		public enum LocationType {
			TOP, TOP_LEFT, TOP_RIGHT, BOTTOM, BOTTOM_LEFT, BOTTOM_RIGHT, LEFT, RIGHT, UNKNOWN
		}

		private LineType lineType;
		private LocationType locationType;

		public PositionOnLine(LineType lineType, LocationType locationType) {
			this.lineType = lineType;
			this.locationType = locationType;
		}

		public PositionOnLine(boolean alongLeftX, boolean alongRightX, boolean alongTopY, boolean alongBottomY) {
			boolean alongX = alongLeftX || alongRightX;
			boolean alongY = alongTopY || alongBottomY;

			if (alongX && alongY) {
				lineType = LineType.XY;
			} else if (alongX) {
				lineType = LineType.X;
			} else if (alongY) {
				lineType = LineType.Y;
			} else {
				lineType = LineType.UNKNOWN;
			}

			if (lineType != null) {
				switch (lineType) {
				case XY:
					if (alongLeftX) {
						if (alongTopY) {
							locationType = LocationType.TOP_LEFT;
						} else {
							locationType = LocationType.BOTTOM_LEFT;
						}
					} else if (alongRightX) {
						if (alongTopY) {
							locationType = LocationType.TOP_RIGHT;
						} else {
							locationType = LocationType.BOTTOM_RIGHT;
						}
					}
					break;
				case X:
					if (alongLeftX) {
						locationType = LocationType.LEFT;
					} else {
						locationType = LocationType.RIGHT;
					}
					break;
				case Y:
					if (alongTopY) {
						locationType = LocationType.TOP;
					} else {
						locationType = LocationType.BOTTOM;
					}
					break;
				default:
					locationType = LocationType.UNKNOWN;
					break;
				}
			}

			if (locationType == null) {
				locationType = LocationType.UNKNOWN;
			}
		}

		public LineType getLineType() {
			return lineType;
		}

		public LocationType getLocationType() {
			return locationType;
		}

		public boolean isLegalPosition() {
			return lineType != LineType.UNKNOWN && locationType != LocationType.UNKNOWN;
		}

		public static PositionOnLine fromString(String s) {
			if (s == null) {
				return null;
			}
			String[] arr = s.split(":"); //$NON-NLS-1$
			if (!arr[0].equals(PositionOnLine.class.getSimpleName().toLowerCase())) {
				return null;
			}

			return new PositionOnLine(LineType.valueOf(arr[1]), LocationType.valueOf(arr[2]));
		}

		@Override
		public String toString() {
			String prefix = PositionOnLine.class.getSimpleName().toLowerCase();
			String lineType = this.lineType.toString();
			String locationType = this.locationType.toString();
			String colon = ":"; //$NON-NLS-1$
			return new StringBuilder().append(prefix).append(colon).append(lineType).append(colon).append(locationType)
			        .toString();
		}
	}
	public static boolean canCreateEventAt(ICreateContext context, GraphicsAlgorithm ga, int padding) {
		return canCreateEventAt(context.getX(), context.getY(), ga.getWidth(), ga.getHeight(), padding);
	}

	public static boolean canCreateEventAt(int x, int y, GraphicsAlgorithm ga, int padding) {
		return canCreateEventAt(x, y, ga.getWidth(), ga.getHeight(), padding);
	}

	public static boolean canCreateEventAt(int x, int y, int w, int h, int padding) {
		int x1 = 0;
		int x2 = 0 + padding;
		int x3 = w - padding;
		int x4 = w;

		int y1 = 0;
		int y2 = 0 + padding;
		int y3 = h - padding;
		int y4 = h;

		if ((y >= y1 && y <= y2) || (y >= y3 && y <= y4)) {
			return true;
		}
		if ((x >= x1 && x <= x2) || (x >= x3 && x <= x4)) {
			return true;
		}
		return false;
	}

	public static boolean canMoveTo(IMoveShapeContext context, Diagram diagram) {
		IPeService peService = Graphiti.getPeService();

		int x = context.getX();
		int y = context.getY();

		if (!(context.getTargetContainer() instanceof Diagram)) {
			ILocation loc = peService.getLocationRelativeToDiagram(context.getTargetContainer());
			x += loc.getX();
			y += loc.getY();
		}

		BoundaryEvent event = BusinessObjectUtil.getFirstElementOfType(context.getShape(), BoundaryEvent.class);
		GraphicsAlgorithm eventGa = context.getShape().getGraphicsAlgorithm();
		ContainerShape activityContainer = (ContainerShape) BusinessObjectUtil.getFirstBaseElementFromDiagram(diagram,
		        event.getAttachedToRef());
		GraphicsAlgorithm activityGa = activityContainer.getGraphicsAlgorithm();
		ILocation activityLoc = peService.getLocationRelativeToDiagram(activityContainer);

		if (!activityContainer.equals(context.getTargetContainer())) {
			return false;
		}

		PositionOnLine pos = getPositionOnLine(x, y, eventGa.getWidth(), eventGa.getHeight(), activityLoc.getX(),
		        activityLoc.getY(), activityGa.getWidth(), activityGa.getHeight());
		return pos.isLegalPosition();
	}

	public static PositionOnLine getPositionOnLineUsingBPMNShape(Shape eventShape, Shape activityShape) {
		BPMNShape event = BusinessObjectUtil.getFirstElementOfType(eventShape, BPMNShape.class);
		Bounds eventBounds = event.getBounds();
		BPMNShape activity = BusinessObjectUtil.getFirstElementOfType(activityShape, BPMNShape.class);
		Bounds activityBounds = activity.getBounds();

		return getPositionOnLine((int) eventBounds.getX(), (int) eventBounds.getY(), (int) eventBounds.getWidth(),
		        (int) eventBounds.getHeight(), (int) activityBounds.getX(), (int) activityBounds.getY(),
		        (int) activityBounds.getWidth(), (int) activityBounds.getHeight());
	}

	public static PositionOnLine getPositionOnLineUsingAbsoluteCoordinates(Shape eventShape, Shape activityShape) {
		IPeService peService = Graphiti.getPeService();
		GraphicsAlgorithm eventGa = eventShape.getGraphicsAlgorithm();
		ILocation eventLoc = peService.getLocationRelativeToDiagram(eventShape);
		GraphicsAlgorithm activityGa = activityShape.getGraphicsAlgorithm();
		ILocation activityLoc = peService.getLocationRelativeToDiagram(activityShape);

		return getPositionOnLine(eventLoc.getX(), eventLoc.getY(), eventGa.getWidth(), eventGa.getHeight(),
		        activityLoc.getX(), activityLoc.getY(), activityGa.getWidth(), activityGa.getHeight());
	}

	public static PositionOnLine getPositionOnLine(int eventX, int eventY, int eventW, int eventH, int activityX,
	        int activityY, int activityW, int activityH) {

		int x = eventX + eventW / 2;
		int y = eventY + eventH / 2;

		int x1 = activityX;
		int x2 = x1 + 10;
		int x3 = x1 + activityW - 10;
		int x4 = x1 + activityW + 10;

		int y1 = activityY;
		int y2 = y1 + 10;
		int y3 = y1 + activityH - 10;
		int y4 = y1 + activityH + 10;

		boolean alongLeftX = x >= x1 && x <= x2;
		boolean alongRightX = x >= x3 && x <= x4;
		boolean alongTopY = y >= y1 && y <= y2;
		boolean alongBottomY = y >= y3 && y <= y4;

		PositionOnLine pos = new PositionOnLine(alongLeftX, alongRightX, alongTopY, alongBottomY);

		return pos;
	}

	public static void assignPositionOnLineProperty(PropertyContainer propertyContainer, PositionOnLine pos) {
		IPeService peService = Graphiti.getPeService();
		peService.setPropertyValue(propertyContainer, GraphitiConstants.BOUNDARY_EVENT_RELATIVE_POS, pos.toString());
	}

	public static PositionOnLine getPositionOnLineProperty(PropertyContainer propertyContainer) {
		IPeService peService = Graphiti.getPeService();
		String value = peService.getPropertyValue(propertyContainer, GraphitiConstants.BOUNDARY_EVENT_RELATIVE_POS);
		return PositionOnLine.fromString(value);
	}
}