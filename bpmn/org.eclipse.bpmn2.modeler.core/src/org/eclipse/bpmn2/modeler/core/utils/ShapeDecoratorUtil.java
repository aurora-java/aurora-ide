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
package org.eclipse.bpmn2.modeler.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.util.IColorConstant;

public class ShapeDecoratorUtil {

	static final IGaService gaService = Graphiti.getGaService();
	static final IPeService peService = Graphiti.getPeService();

	private static final String DELETABLE_PROPERTY = "deletable"; //$NON-NLS-1$
	public static final int GATEWAY_RADIUS = 25;
	public static final int EVENT_SIZE = 36;

	public static class Envelope {
		public Rectangle rect;
		public Polyline line;
	}

	public static class Asterisk {
		public Polyline horizontal;
		public Polyline vertical;
		public Polyline diagonalDesc;
		public Polyline diagonalAsc;
	}

	public static class Compensation {
		public Polygon arrow1;
		public Polygon arrow2;
	}

	public static class Cross {
		public Polyline vertical;
		public Polyline horizontal;
	}

	public static class DiagonalCross {
		public Polyline diagonalAsc;
		public Polyline diagonalDesc;
	}

	public static class MultiInstance {
		public Polyline line1;
		public Polyline line2;
		public Polyline line3;
	}

	public static class Loop {
		public Polyline circle;
		public Polyline arrow;
	}

	public static class Expand {
		public Rectangle rect;
		public Polyline horizontal;
		public Polyline vertical;
	}

	private static int generateRatioPointValue(float originalPointValue, float ratioValue) {
		return Math.round(Float.valueOf(originalPointValue * ratioValue));
	}
	
	private static float calculateRatio(float x, float y) {
		return x / y;
	}
	
	static int getShapeHeight(Shape shape) {
		return shape.getGraphicsAlgorithm().getHeight();
	}
	
	static int getShapeWidth(Shape shape) {
		return shape.getGraphicsAlgorithm().getWidth();
	}
	
	public static Shape getContainedShape(ContainerShape container, String propertyKey) {
		Iterator<Shape> iterator = peService.getAllContainedShapes(container).iterator();
		while (iterator.hasNext()) {
			Shape shape = iterator.next();
			String property = peService.getPropertyValue(shape, propertyKey);
			if (property != null && new Boolean(property)) {
				return shape;
			}
		}
		return null;
	}
	
	public static List<PictogramElement> getContainedPictogramElements(PictogramElement container, String propertyKey) {
		List<PictogramElement> pictogramElements = new ArrayList<PictogramElement>();
		Iterator<PictogramElement> iterator = peService.getAllContainedPictogramElements(container).iterator();
		while (iterator.hasNext()) {
			PictogramElement pe = iterator.next();
			String property = peService.getPropertyValue(pe, propertyKey);
			if (property != null && new Boolean(property)) {
				pictogramElements.add(pe);
			}
		}
		return pictogramElements;
	}

	/* GATEWAY */

	public static Polygon createGateway(Shape container, final int width, final int height) {
		final int widthRadius = width / 2;
		final int heightRadius = height / 2;
		final int[] gateWayPoints = {0, heightRadius, widthRadius, 0, 2 * widthRadius, heightRadius, widthRadius, 2 * heightRadius};
		return gaService.createPolygon(container, gateWayPoints);
	}

	public static Polygon createGatewayPentagon(ContainerShape container) {
		Shape pentagonShape = peService.createShape(container, false);
		
		final int gatewayHeight = getShapeHeight(container);
		final int gatewayWidth = getShapeWidth(container);
		
		final float heightRatio = calculateRatio(gatewayHeight, Float.valueOf(GATEWAY_RADIUS * 2));
		final float widthRatio = calculateRatio(gatewayWidth, Float.valueOf(GATEWAY_RADIUS * 2));
		
		Polygon pentagon = gaService.createPolygon(pentagonShape,
				new int[] { gatewayWidth / 2, generateRatioPointValue(18, heightRatio),
							gatewayWidth / 2 + generateRatioPointValue(8, widthRatio), gatewayHeight / 2 - generateRatioPointValue(2, heightRatio),
							gatewayWidth / 2 + generateRatioPointValue(5, widthRatio), gatewayHeight / 2 + generateRatioPointValue(7, heightRatio),
							gatewayWidth / 2 - generateRatioPointValue(5, widthRatio), gatewayHeight / 2 + generateRatioPointValue(7, heightRatio),
							gatewayWidth / 2 - generateRatioPointValue(8, widthRatio), gatewayHeight / 2 - generateRatioPointValue(2, heightRatio) });
							
		peService.setPropertyValue(pentagonShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$
		return pentagon;
	}

	public static Ellipse createGatewayInnerCircle(Ellipse outer) {
		final int gatewayHeight = outer.getHeight();
		final int gatewayWidth = outer.getWidth();
		
		final float heightRatio = calculateRatio(gatewayHeight, Float.valueOf(GATEWAY_RADIUS * 2));
		final float widthRatio = calculateRatio(gatewayWidth, Float.valueOf(GATEWAY_RADIUS * 2));
		
		Float x = (5 * widthRatio) * new Float(0.8);
		Float y = (5 * heightRatio) * new Float(0.8);

		Float width = gatewayHeight * new Float(0.8);
		Float height = gatewayWidth * new Float(0.8);
		
		Ellipse ellipse = gaService.createEllipse(outer);
		gaService.setLocationAndSize(ellipse,
				 Math.round(x), Math.round(y),
				 Math.round(width), Math.round(height));
		ellipse.setFilled(false);
		ellipse.setLineWidth(1);
		peService.setPropertyValue(ellipse, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$
		return ellipse;
	}

	public static Ellipse createGatewayOuterCircle(ContainerShape container) {
		Shape ellipseShape = peService.createShape(container, false);
		Ellipse ellipse = gaService.createEllipse(ellipseShape);
		
		final int gatewayHeight = getShapeHeight(container);
		final int gatewayWidth = getShapeWidth(container);
		
		final float heightRatio = calculateRatio(gatewayHeight, Float.valueOf(GATEWAY_RADIUS * 2));
		final float widthRatio = calculateRatio(gatewayWidth, Float.valueOf(GATEWAY_RADIUS * 2));
		gaService.setLocationAndSize(ellipse,
				generateRatioPointValue(12, widthRatio),
				generateRatioPointValue(12, heightRatio),
				generateRatioPointValue(27, widthRatio),
				generateRatioPointValue(27, heightRatio));
		ellipse.setFilled(false);
		ellipse.setLineWidth(1);
		peService.setPropertyValue(ellipseShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$
		return ellipse;
	}

	public static Cross createGatewayCross(ContainerShape container) {
		Shape verticalShape = peService.createShape(container, false);
		
		final int gatewayHeight = getShapeHeight(container);
		final int gatewayWidth = getShapeWidth(container);
		
		final float heightRatio = calculateRatio(gatewayHeight, Float.valueOf(GATEWAY_RADIUS * 2));
		final float widthRatio = calculateRatio(gatewayWidth, Float.valueOf(GATEWAY_RADIUS * 2));
		
		Polyline verticalLine = gaService.createPolyline(verticalShape,
				new int[] { generateRatioPointValue(24, widthRatio), generateRatioPointValue(7, heightRatio),
							generateRatioPointValue(24, widthRatio), generateRatioPointValue(43, heightRatio) });
		verticalLine.setLineWidth(3);
		peService.setPropertyValue(verticalShape, DELETABLE_PROPERTY, "false"); //$NON-NLS-1$

		Shape horizontalShape = peService.createShape(container, false);
		
		Polyline horizontalLine = gaService.createPolyline(horizontalShape,
				new int[] { generateRatioPointValue(7, widthRatio), generateRatioPointValue(24, heightRatio),
							generateRatioPointValue(43, widthRatio), generateRatioPointValue(24, heightRatio) });
		horizontalLine.setLineWidth(3);
		peService.setPropertyValue(horizontalShape, DELETABLE_PROPERTY, "false"); //$NON-NLS-1$

		Cross cross = new Cross();
		cross.vertical = verticalLine;
		cross.horizontal = horizontalLine;
		return cross;
	}

	public static DiagonalCross createGatewayDiagonalCross(ContainerShape container) {
		final int gatewayHeight = getShapeHeight(container);
		final int gatewayWidth = getShapeWidth(container);
		
		final float heightRatio = calculateRatio(gatewayHeight, Float.valueOf(GATEWAY_RADIUS * 2));
		final float widthRatio = calculateRatio(gatewayWidth, Float.valueOf(GATEWAY_RADIUS * 2));

		Shape diagonalDescShape = peService.createShape(container, false);
		Polyline diagonalDesc = gaService.createPolyline(diagonalDescShape,
				new int[] { generateRatioPointValue(14, widthRatio), generateRatioPointValue(14, heightRatio),
							generateRatioPointValue(37, widthRatio), generateRatioPointValue(37, heightRatio) });
		diagonalDesc.setLineWidth(3);
		peService.setPropertyValue(diagonalDescShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$

		Shape diagonalAscShape = peService.createShape(container, false);
		
		Polyline diagonalAsc = gaService.createPolyline(diagonalAscShape,
				new int[] { generateRatioPointValue(37, widthRatio), generateRatioPointValue(14, heightRatio),
							generateRatioPointValue(14, widthRatio), generateRatioPointValue(37, heightRatio) });
		diagonalAsc.setLineWidth(3);
		peService.setPropertyValue(diagonalAscShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$

		DiagonalCross diagonalCross = new DiagonalCross();
		diagonalCross.diagonalDesc = diagonalDesc;
		diagonalCross.diagonalAsc = diagonalAsc;
		return diagonalCross;
	}

	public static Polygon createEventGatewayParallelCross(ContainerShape container) {
		Shape crossShape = peService.createShape(container, false);
		
		final int gatewayHeight = getShapeHeight(container);
		final int gatewayWidth = getShapeWidth(container);
		
		final float heightRatio = calculateRatio(gatewayHeight, Float.valueOf(GATEWAY_RADIUS * 2));
		final float widthRatio = calculateRatio(gatewayWidth, Float.valueOf(GATEWAY_RADIUS * 2));
		
		int n1x = generateRatioPointValue(14, widthRatio);
		int n1y = generateRatioPointValue(14, heightRatio);
		int n2x = generateRatioPointValue(22, widthRatio);
		int n2y = generateRatioPointValue(22, heightRatio);
		int n3x = generateRatioPointValue(28, widthRatio);
		int n3y = generateRatioPointValue(28, heightRatio);
		int n4x = generateRatioPointValue(36, widthRatio);
		int n4y = generateRatioPointValue(36, heightRatio);
		
		Collection<Point> points = new ArrayList<Point>();
		points.add(gaService.createPoint(n1x, n2y));
		points.add(gaService.createPoint(n2x, n2y));
		points.add(gaService.createPoint(n2x, n1y));
		points.add(gaService.createPoint(n3x, n1y));
		points.add(gaService.createPoint(n3x, n2y));
		points.add(gaService.createPoint(n4x, n2y));
		points.add(gaService.createPoint(n4x, n3y));
		points.add(gaService.createPoint(n3x, n3y));
		points.add(gaService.createPoint(n3x, n4y));
		points.add(gaService.createPoint(n2x, n4y));
		points.add(gaService.createPoint(n2x, n3y));
		points.add(gaService.createPoint(n1x, n3y));
		Polygon cross = gaService.createPolygon(crossShape, points);
		cross.setFilled(false);
		cross.setLineWidth(1);
		peService.setPropertyValue(crossShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$
		return cross;
	}

	public static Asterisk createGatewayAsterisk(ContainerShape container) {
		final int gatewayHeight = getShapeHeight(container);
		final int gatewayWidth = getShapeWidth(container);
		
		final float heightRatio = calculateRatio(gatewayHeight, Float.valueOf(GATEWAY_RADIUS * 2));
		final float widthRatio = calculateRatio(gatewayWidth, Float.valueOf(GATEWAY_RADIUS * 2));

		Shape verticalShape = peService.createShape(container, false);
		Polyline vertical = gaService.createPolyline(verticalShape,
				new int[] { generateRatioPointValue(24, widthRatio), generateRatioPointValue(7, heightRatio),
							generateRatioPointValue(24, widthRatio), generateRatioPointValue(43, heightRatio) });
		vertical.setLineWidth(3);
		peService.setPropertyValue(verticalShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$

		Shape horizontalShape = peService.createShape(container, false);
		Polyline horizontal = gaService.createPolyline(horizontalShape,
				new int[] { generateRatioPointValue(7, widthRatio), generateRatioPointValue(24, heightRatio),
							generateRatioPointValue(43, widthRatio), generateRatioPointValue(24, heightRatio) });
		horizontal.setLineWidth(3);
		peService.setPropertyValue(horizontalShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$

		Shape diagonalDescShape = peService.createShape(container, false);
		Polyline diagonalDesc = gaService.createPolyline(diagonalDescShape,
				new int[] { generateRatioPointValue(14, widthRatio), generateRatioPointValue(14, heightRatio),
							generateRatioPointValue(37, widthRatio), generateRatioPointValue(37, heightRatio) });
		diagonalDesc.setLineWidth(3);
		peService.setPropertyValue(diagonalDescShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$

		Shape diagonalAscShape = peService.createShape(container, false);
		Polyline diagonalAsc = gaService.createPolyline(diagonalAscShape,
				new int[] { generateRatioPointValue(37, widthRatio), generateRatioPointValue(14, heightRatio),
							generateRatioPointValue(14, widthRatio), generateRatioPointValue(37, heightRatio) });
		diagonalAsc.setLineWidth(3);
		peService.setPropertyValue(diagonalAscShape, DELETABLE_PROPERTY, "true"); //$NON-NLS-1$

		Asterisk a = new Asterisk();
		a.horizontal = horizontal;
		a.vertical = vertical;
		a.diagonalDesc = diagonalDesc;
		a.diagonalAsc = diagonalAsc;
		return a;
	}

	public static void clearGateway(PictogramElement element) {
		for (PictogramElement pe : getContainedPictogramElements(element, DELETABLE_PROPERTY)) {
			peService.deletePictogramElement(pe);
		}
	}

	/* EVENT */

	public static Ellipse createEventShape(Shape container, final int width, final int height) {
		Ellipse ellipse = gaService.createEllipse(container);
		gaService.setLocationAndSize(ellipse, 0, 0, width, height);
		return ellipse;
	}

	public static Envelope createEventEnvelope(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		return createEnvelope(shape,
				generateRatioPointValue(9, widthRatio),
				generateRatioPointValue(12, heightRatio),
				generateRatioPointValue(18, widthRatio),
				generateRatioPointValue(14, heightRatio));
		
	}

	public static Polygon createEventPentagon(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		return gaService.createPolygon(shape,
				new int[] { eventWidth / 2, generateRatioPointValue(7, heightRatio),
				eventWidth / 2 + generateRatioPointValue(10, widthRatio), eventHeight / 2 - generateRatioPointValue(4, heightRatio),
				eventWidth / 2 + generateRatioPointValue(7, widthRatio), eventHeight / 2 + generateRatioPointValue(10, heightRatio),
				eventWidth / 2 - generateRatioPointValue(7, widthRatio), eventHeight / 2 + generateRatioPointValue(10, heightRatio),
				eventWidth / 2 - generateRatioPointValue(10, widthRatio), eventHeight / 2 - generateRatioPointValue(4, heightRatio) });
	}

	public static Ellipse createIntermediateEventCircle(Ellipse ellipse) {
		final int eventHeight = ellipse.getHeight();
		final int eventWidth = ellipse.getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		Float x = (5 * widthRatio) * new Float(0.8);
		Float y = (5 * heightRatio) * new Float(0.8);

		Float width = eventWidth * new Float(0.8);
		Float height = eventHeight * new Float(0.8);
		
		Ellipse circle = gaService.createEllipse(ellipse);
		gaService.setLocationAndSize(circle,
				 Math.round(x), Math.round(y),
				 width.intValue(), height.intValue());
		circle.setLineWidth(1);
		circle.setFilled(false);
		return circle;
	}

	public static Image createEventImage(Shape shape, String imageId) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		Image image = gaService.createImage(shape, imageId);
		image.setProportional(true);
		gaService.setLocationAndSize(image, 
				generateRatioPointValue(8, widthRatio), generateRatioPointValue(8, heightRatio),
				generateRatioPointValue(20, widthRatio), generateRatioPointValue(20, heightRatio));
		return image;
	}

	public static Polygon createEventSignal(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));

		Polygon polygon = gaService.createPolygon(shape, 
				new int[] { generateRatioPointValue(16, widthRatio), generateRatioPointValue(4, heightRatio),
							generateRatioPointValue(28, widthRatio), generateRatioPointValue(26, heightRatio),
							generateRatioPointValue(7, widthRatio), generateRatioPointValue(26, heightRatio) });
		polygon.setLineWidth(1);
		return polygon;
	}

	public static Polygon createEventEscalation(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		int heightRadius = eventHeight / 2;
		int widthRadius = eventWidth / 2;
		
		int[] points = { widthRadius, generateRatioPointValue(8, heightRatio),
						 widthRadius + generateRatioPointValue(8, widthRatio), heightRadius + generateRatioPointValue(9, heightRatio),
						 widthRadius, heightRadius + generateRatioPointValue(2, heightRatio),
						 widthRadius - generateRatioPointValue(8, widthRatio), heightRadius + generateRatioPointValue(9, heightRatio) };
		Polygon polygon = gaService.createPolygon(shape, points);
		polygon.setLineWidth(1);
		return polygon;
	}

	public static Compensation createEventCompensation(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		Rectangle rect = gaService.createInvisibleRectangle(shape);

		int w = generateRatioPointValue(22, widthRatio);
		int h = generateRatioPointValue(18, heightRatio);
		gaService.setLocationAndSize(rect, 
				generateRatioPointValue(5, widthRatio), generateRatioPointValue(9, heightRatio), w, h);

		int _w = w / 2;
		int _h = h / 2;
		int[] pontsArrow1 = { _w, 0, _w, h, 0, _h };
		Polygon arrow1 = gaService.createPolygon(rect, pontsArrow1);

		int[] pontsArrow2 = { w, 0, w, h, w / 2, _h };
		Polygon arrow2 = gaService.createPolygon(rect, pontsArrow2);

		Compensation compensation = new Compensation();
		compensation.arrow1 = arrow1;
		compensation.arrow2 = arrow2;
		return compensation;
	}

	public static Polygon createEventLink(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		int heightRadius = eventHeight / 2;

		int[] points = { 
				generateRatioPointValue(32, widthRatio), heightRadius,
				generateRatioPointValue(23, widthRatio), heightRadius + generateRatioPointValue(11, heightRatio),
				generateRatioPointValue(23, widthRatio), heightRadius + generateRatioPointValue(6, heightRatio),
				generateRatioPointValue(5, widthRatio), heightRadius + generateRatioPointValue(6, heightRatio),
				generateRatioPointValue(5, widthRatio), heightRadius - generateRatioPointValue(6, heightRatio),
				generateRatioPointValue(23, widthRatio), heightRadius - generateRatioPointValue(6, heightRatio),
				generateRatioPointValue(23, widthRatio), heightRadius - generateRatioPointValue(11, heightRatio)};
		Polygon polygon = gaService.createPolygon(shape, points);
		polygon.setLineWidth(1);
		return polygon;
	}

	public static Polygon createEventError(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		int heightRadius = eventHeight / 2;
		int widthRadius = eventWidth / 2;
		
		int[] points = { 
				widthRadius + generateRatioPointValue(4, widthRatio), heightRadius,
				widthRadius + generateRatioPointValue(10, widthRatio), heightRadius - generateRatioPointValue(10, heightRatio),
				widthRadius + generateRatioPointValue(7, widthRatio), heightRadius + generateRatioPointValue(10, heightRatio),
				widthRadius - generateRatioPointValue(4, widthRatio), heightRadius,
				widthRadius - generateRatioPointValue(10, widthRatio), heightRadius + generateRatioPointValue(10, heightRatio),
				widthRadius - generateRatioPointValue(7, widthRatio), heightRadius - generateRatioPointValue(10, heightRatio)};
		Polygon polygon = gaService.createPolygon(shape, points);
		polygon.setLineWidth(1);
		return polygon;
	}

	public static Polygon createEventCancel(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		int heightRadius = eventHeight / 2;
		int widthRadius = eventWidth / 2;
		
		int a1 = generateRatioPointValue(9, widthRatio);
		int a2 = generateRatioPointValue(9, heightRatio);
		int b1 = generateRatioPointValue(12, widthRatio);
		int b2 = generateRatioPointValue(12, heightRatio);
		int c1 = generateRatioPointValue(4, widthRatio);
		int c2 = generateRatioPointValue(4, heightRatio);
		int[] points = { widthRadius, heightRadius - c2,
						 widthRadius + a1, heightRadius - b2,
						 widthRadius + b1, heightRadius - a2,
						 widthRadius + c1, heightRadius,
						 widthRadius + b1, heightRadius + a2,
						 widthRadius + a1, heightRadius + b2,
						 widthRadius, heightRadius + c2,
						 widthRadius - a1, heightRadius + b2,
						 widthRadius - b1, heightRadius + a2,
						 widthRadius - c1, heightRadius,
						 widthRadius - b1, heightRadius - a2,
						 widthRadius - a1, heightRadius - b2 };
		Polygon polygon = gaService.createPolygon(shape, points);
		polygon.setLineWidth(1);
		return polygon;
	}

	public static Ellipse createEventTerminate(Shape terminateShape) {
		final int eventHeight = terminateShape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = terminateShape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		Ellipse ellipse = gaService.createEllipse(terminateShape);
		gaService.setLocationAndSize(ellipse,
				generateRatioPointValue(6, widthRatio), generateRatioPointValue(6, heightRatio),
				eventWidth - generateRatioPointValue(12, widthRatio), eventHeight - generateRatioPointValue(12, heightRatio));
		ellipse.setLineWidth(1);
		ellipse.setFilled(true);
		return ellipse;
	}

	public static Ellipse createEventNotAllowed(Shape shape) {
		final int eventHeight = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int eventWidth = shape.getContainer().getGraphicsAlgorithm().getWidth();
		
		final float heightRatio = calculateRatio(eventHeight, Float.valueOf(EVENT_SIZE));
		final float widthRatio = calculateRatio(eventWidth, Float.valueOf(EVENT_SIZE));
		
		Ellipse ellipse = gaService.createEllipse(shape);
		gaService.setLocationAndSize(ellipse,
				generateRatioPointValue(6, widthRatio), generateRatioPointValue(6, heightRatio),
				eventWidth - generateRatioPointValue(12, widthRatio), eventHeight - generateRatioPointValue(12, heightRatio));
		ellipse.setLineWidth(2);
		ellipse.setFilled(false);
		ellipse.setForeground(manageColor(shape, IColorConstant.RED));

		
		int[] points = {
				generateRatioPointValue(8, widthRatio), generateRatioPointValue(12, heightRatio),
				generateRatioPointValue(28, widthRatio), generateRatioPointValue(24, heightRatio),
		};

		shape = peService.createShape(shape.getContainer(), false);
		Polyline polygon = gaService.createPolyline(shape, points);
		polygon.setLineWidth(2);
		polygon.setForeground(manageColor(shape, IColorConstant.RED));
		return ellipse;
	}

	public static Polygon createEventParallelMultiple(Shape shape) {
		final int h = shape.getContainer().getGraphicsAlgorithm().getHeight();
		final int w = shape.getContainer().getGraphicsAlgorithm().getWidth();

		int w0 = w / 5;
		int h0 = h / 5;
		int x = 2*w0;
		int y = h0;
		int[] points = {
				x, y,
				x+w0, y, 
				x+w0, y+h0,
				x+w0+w0, y+h0,
				x+w0+w0, y+h0+h0,
				x+w0, y+h0+h0,
				x+w0, y+h0+h0+h0,
				x, y+h0+h0+h0,
				x, y+h0+h0,
				x-w0, y+h0+h0,
				x-w0, y+h0,
				x, y+h0,
				x, y,
			};
		Polygon cross = gaService.createPolygon(shape, points);
		cross.setFilled(false);
		cross.setLineWidth(1);
		return cross;
	}

	public static void deleteEventShape(ContainerShape containerShape) {
		for (PictogramElement shape : containerShape.getChildren()) {
			String property = peService.getPropertyValue(shape, GraphitiConstants.EVENT_DEFINITION_SHAPE);
			if (property != null) {
				peService.deletePictogramElement(shape);
				break;
			}
		}
	}

	/* OTHER */

	public static Envelope createEnvelope(GraphicsAlgorithmContainer gaContainer, int x, int y, int w, int h) {
		Rectangle rect = gaService.createRectangle(gaContainer);
		gaService.setLocationAndSize(rect, x, y, w, h);
		rect.setFilled(false);

		Polyline line = gaService.createPolyline(rect, new int[] { 0, 0, w / 2, h / 2, w, 0 });

		Envelope envelope = new Envelope();
		envelope.rect = rect;
		envelope.line = line;

		return envelope;
	}

	public static Polygon createDataArrow(Polygon p) {
		int[] points = { 4, 8, 14, 8, 14, 4, 18, 10, 14, 16, 14, 12, 4, 12 };
		Polygon arrow = gaService.createPolygon(p, points);
		arrow.setLineWidth(1);
		return arrow;
	}

	public static final int TASK_IMAGE_SIZE = 16;

	public static final int MARKER_WIDTH = 10;
	public static final int MARKER_HEIGHT = 10;

	private static GraphicsAlgorithmContainer createActivityMarkerCompensate(ContainerShape markerContainer) {
		GraphicsAlgorithmContainer algorithmContainer = createActivityMarkerGaContainer(markerContainer,
		        GraphitiConstants.ACTIVITY_MARKER_COMPENSATE);
		Compensation compensation = createCompensation(algorithmContainer, MARKER_WIDTH, MARKER_HEIGHT);
		compensation.arrow1.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		compensation.arrow2.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		return algorithmContainer;
	}

	private static GraphicsAlgorithmContainer createActivityMarkerStandardLoop(ContainerShape markerContainer) {
		GraphicsAlgorithmContainer algorithmContainer = createActivityMarkerGaContainer(markerContainer,
				GraphitiConstants.ACTIVITY_MARKER_LC_STANDARD);

		int[] xy = { 8, 10, 10, 5, 5, 0, 0, 5, 3, 10 };
		int[] bend = { 0, 0, 3, 4, 4, 4, 4, 3, 3, 0 };
		Polyline circle = gaService.createPolyline(algorithmContainer, xy, bend);

		Loop loop = new Loop();
		loop.circle = circle;
		loop.arrow = gaService.createPolyline(algorithmContainer, new int[] { 5, 5, 5, 10, 0, 10 });
		loop.circle.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		loop.arrow.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		return algorithmContainer;
	}

	private static GraphicsAlgorithmContainer createActivityMarkerMultiParallel(ContainerShape markerContainer) {
		GraphicsAlgorithmContainer algorithmContainer = createActivityMarkerGaContainer(markerContainer,
				GraphitiConstants.ACTIVITY_MARKER_LC_MULTI_PARALLEL);
		MultiInstance multiInstance = new MultiInstance();
		multiInstance.line1 = gaService.createPolyline(algorithmContainer, new int[] { 2, 0, 2, MARKER_HEIGHT });
		multiInstance.line2 = gaService.createPolyline(algorithmContainer, new int[] { 5, 0, 5, MARKER_HEIGHT });
		multiInstance.line3 = gaService.createPolyline(algorithmContainer, new int[] { 8, 0, 8, MARKER_HEIGHT });
		multiInstance.line1.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		multiInstance.line2.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		multiInstance.line3.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		return algorithmContainer;
	}

	private static GraphicsAlgorithmContainer createActivityMarkerMultiSequential(ContainerShape markerContainer) {
		GraphicsAlgorithmContainer algorithmContainer = createActivityMarkerGaContainer(markerContainer,
		        GraphitiConstants.ACTIVITY_MARKER_LC_MULTI_SEQUENTIAL);
		MultiInstance multiInstance = new MultiInstance();
		multiInstance.line1 = gaService.createPolyline(algorithmContainer, new int[] { 0, 2, MARKER_WIDTH, 2 });
		multiInstance.line2 = gaService.createPolyline(algorithmContainer, new int[] { 0, 5, MARKER_WIDTH, 5 });
		multiInstance.line3 = gaService.createPolyline(algorithmContainer, new int[] { 0, 8, MARKER_WIDTH, 8 });
		multiInstance.line1.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		multiInstance.line2.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		multiInstance.line3.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		return algorithmContainer;
	}

	private static GraphicsAlgorithmContainer createActivityMarkerAdHoc(ContainerShape markerContainer) {
		GraphicsAlgorithmContainer algorithmContainer = createActivityMarkerGaContainer(markerContainer,
		        GraphitiConstants.ACTIVITY_MARKER_AD_HOC);
		int[] xy = { 0, 8, 3, 2, 7, 8, 10, 2 };
		int[] bend = { 0, 3, 3, 3, 3, 3, 3, 0 };
		Polyline tilde = gaService.createPolyline(algorithmContainer, xy, bend);
		tilde.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		return algorithmContainer;
	}

	private static GraphicsAlgorithmContainer createActivityMarkerExpand(ContainerShape markerContainer) {
		GraphicsAlgorithmContainer algorithmContainer = createActivityMarkerGaContainer(markerContainer,
		        GraphitiConstants.ACTIVITY_MARKER_EXPAND);

		Rectangle rect = gaService.createRectangle(algorithmContainer);
		rect.setFilled(false);
		gaService.setLocationAndSize(rect, 0, 0, 10, 10);

		Expand expand = new Expand();
		expand.rect = rect;
		expand.horizontal = gaService.createPolyline(algorithmContainer, new int[] { 0, 5, 10, 5 });
		expand.vertical = gaService.createPolyline(algorithmContainer, new int[] { 5, 0, 5, 10 });
		expand.rect.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		expand.horizontal.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		expand.vertical.setForeground(manageColor(markerContainer, StyleUtil.CLASS_FOREGROUND));
		return algorithmContainer;
	}

	
	private static ContainerShape getActivityMarker(ContainerShape container) {
		String property = peService.getPropertyValue(container, GraphitiConstants.ACTIVITY_MARKER_CONTAINER);
		if (property != null && new Boolean(property)) {
			return container;
		}
		return (ContainerShape) getContainedShape(container, GraphitiConstants.ACTIVITY_MARKER_CONTAINER);
	}

	private static ContainerShape createActivityMarker(ContainerShape container) {
		
		ContainerShape markerContainer = getActivityMarker(container);
		if (markerContainer==null) {
			// need to create a marker container first
			markerContainer = peService.createContainerShape(container, false);
			Rectangle markerInvisibleRect = gaService.createInvisibleRectangle(markerContainer);
			GraphicsAlgorithm ga = container.getGraphicsAlgorithm();
			int x = ga.getWidth() / 2;
			int y = ga.getHeight() - 10;
			int w = 50;
			int h = 10;
			gaService.setLocationAndSize(markerInvisibleRect, x, y, w, h);
			peService.setPropertyValue(markerContainer, GraphitiConstants.ACTIVITY_MARKER_CONTAINER, Boolean.toString(true));
			peService.setPropertyValue(markerContainer, GraphitiConstants.ACTIVITY_BORDER, Boolean.toString(true));

			createActivityMarkerCompensate(markerContainer);
			createActivityMarkerStandardLoop(markerContainer);
			createActivityMarkerMultiParallel(markerContainer);
			createActivityMarkerMultiSequential(markerContainer);
			createActivityMarkerAdHoc(markerContainer);
			createActivityMarkerExpand(markerContainer);
			
			// make them all invisible
			Iterator<Shape> iterator = peService.getAllContainedShapes(markerContainer).iterator();
			while (iterator.hasNext()) {
				Shape shape = iterator.next();
				shape.setVisible(false);
			}
		}
		return markerContainer;
	}

	public static void setActivityMarkerOffest(ContainerShape container, int offset) {
		peService.setPropertyValue(container, GraphitiConstants.ACTIVITY_MARKER_OFFSET, Integer.toString(offset));
	}

	public static int getActivityMarkerOffest(ContainerShape container) {
		int offset = 0;
		String s = peService.getPropertyValue(container, GraphitiConstants.ACTIVITY_MARKER_OFFSET);
		if (s!=null) {
			try {
				offset = Integer.parseInt(s);
			}
			catch (Exception e) {
			}
		}
		return offset;
	}
	
	public static void layoutActivityMarker(ContainerShape container) {

		ContainerShape markerContainer = getActivityMarker(container);
		if (markerContainer!=null) {
			int lastX = 0;
			Iterator<Shape> iterator = peService.getAllContainedShapes(markerContainer).iterator();
			while (iterator.hasNext()) {
				Shape marker = iterator.next();
				if (marker.isVisible()) {
					GraphicsAlgorithm ga = marker.getGraphicsAlgorithm();
					gaService.setLocation(ga, lastX, 0);
					lastX += ga.getWidth() + 3;
				}
			}
			
			GraphicsAlgorithm parentGa = container.getGraphicsAlgorithm();
			GraphicsAlgorithm ga = markerContainer.getGraphicsAlgorithm();
			int newWidth = parentGa.getWidth();
			int newHeight = parentGa.getHeight();
			int x = (newWidth / 2) - (lastX / 2);
			int y = newHeight - 13 - getActivityMarkerOffest(container);
			gaService.setLocation(ga, x, y);
		}
	}
	
	public static void showActivityMarker(ContainerShape container, String property) {

		ContainerShape markerContainer = getActivityMarker(container);
		if (markerContainer==null) {
			markerContainer = createActivityMarker(container);
		}
		ShapeDecoratorUtil.getContainedShape(markerContainer, property).setVisible(true);
		layoutActivityMarker(container);
	}
	
	public static void hideActivityMarker(ContainerShape container, String property) {

		ContainerShape markerContainer = getActivityMarker(container);
		if (markerContainer==null) {
			markerContainer = createActivityMarker(container);
		}
		ShapeDecoratorUtil.getContainedShape(markerContainer, property).setVisible(false);
		layoutActivityMarker(container);
	}

	public static boolean isActivityMarker(PictogramElement pe) {
		String property = peService.getPropertyValue(pe, GraphitiConstants.ACTIVITY_MARKER_CONTAINER);
		return new Boolean(property).booleanValue();
	}

	public static Shape createActivityBorder(ContainerShape containerShape, BaseElement businessObject) {
		Shape shape = peService.createShape(containerShape, false);
		RoundedRectangle rect = gaService.createRoundedRectangle(shape, 5, 5);
		StyleUtil.applyStyle(rect, businessObject);
		IDimension dim = gaService.calculateSize(containerShape.getGraphicsAlgorithm());
		gaService.setLocationAndSize(rect, 0, 0, dim.getWidth(), dim.getHeight());
		peService.setPropertyValue(shape, GraphitiConstants.ACTIVITY_BORDER, Boolean.TRUE.toString());
		link(shape, businessObject);
		return shape;
	}
	
	public static boolean isActivityBorder(PictogramElement pe) {
		String value = peService.getPropertyValue(pe, GraphitiConstants.ACTIVITY_BORDER);
		return new Boolean(value);
	}

	public static Image createActivityImage(ContainerShape containerShape, String imageId) {
		if (imageId!=null && !imageId.trim().isEmpty()) {
			GraphicsAlgorithmContainer ga = containerShape.getChildren().get(0).getGraphicsAlgorithm();
			Image img = gaService.createImage(ga, imageId.trim());
			gaService.setLocationAndSize(img, 2, 2, TASK_IMAGE_SIZE, TASK_IMAGE_SIZE);
			return img;
		}
		return null;
	}

	private static void link(PictogramElement pe, EObject object) {
		PictogramLink link = pe.getLink();
		if (link==null) {
			link = PictogramsFactory.eINSTANCE.createPictogramLink();
			pe.setLink(link);
		}
		link.getBusinessObjects().add(object);
	}

	private static Color manageColor(PictogramElement pe, IColorConstant colorConstant) {
		Diagram diagram = peService.getDiagramForPictogramElement(pe);
		return gaService.manageColor(diagram, colorConstant);
	}

	private static GraphicsAlgorithmContainer createActivityMarkerGaContainer(ContainerShape markerContainer,
	        String property) {
		GraphicsAlgorithm ga = markerContainer.getGraphicsAlgorithm();

		int totalWidth = MARKER_WIDTH;
		int parentW = ((ContainerShape) markerContainer.eContainer()).getGraphicsAlgorithm().getWidth();
		int parentH = ((ContainerShape) markerContainer.eContainer()).getGraphicsAlgorithm().getHeight();
		
		int lastX = 0;

		Iterator<Shape> iterator = peService.getAllContainedShapes(markerContainer).iterator();
		while (iterator.hasNext()) {
			Shape containedShape = (Shape) iterator.next();
			if (containedShape.isVisible()) {
				GraphicsAlgorithm containedGa = containedShape.getGraphicsAlgorithm();
				totalWidth += containedGa.getWidth();
				lastX = containedGa.getX() + containedGa.getWidth();
			}
		}

		gaService.setLocationAndSize(ga, (parentW / 2) - (totalWidth / 2), parentH-MARKER_WIDTH, totalWidth, MARKER_HEIGHT);

		Shape shape = peService.createShape(markerContainer, false);
		peService.setPropertyValue(shape, property, Boolean.toString(true));
		Rectangle invisibleRect = gaService.createInvisibleRectangle(shape);
		gaService.setLocationAndSize(invisibleRect, lastX, 0, MARKER_WIDTH, MARKER_HEIGHT);

		return invisibleRect;
	}

	private static Compensation createCompensation(GraphicsAlgorithmContainer container, int w, int h) {
		int[] xy = { 0, h / 2, w / 2, 0, w / 2, h };
		Polygon arrow1 = gaService.createPolygon(container, xy);
		arrow1.setFilled(false);

		xy = new int[] { w / 2, h / 2, w, 0, w, h };
		Polygon arrow2 = gaService.createPolygon(container, xy);
		arrow2.setFilled(false);

		Compensation compensation = new Compensation();
		compensation.arrow1 = arrow1;
		compensation.arrow2 = arrow2;

		return compensation;
	}
	
	public static boolean isValidationDecorator(PictogramElement pe) {
		String value = peService.getPropertyValue(pe, GraphitiConstants.VALIDATION_DECORATOR);
		if (new Boolean(value))
			return true;
		return false;
	}

	public static PictogramElement createValidationDecorator(ContainerShape containerShape) {
		for (PictogramElement pe : containerShape.getChildren()) {
			if (isValidationDecorator(pe))
				return pe;
		}
		PictogramElement decorator = Graphiti.getPeCreateService().createShape(containerShape, false);
		peService.setPropertyValue(decorator, GraphitiConstants.VALIDATION_DECORATOR, "true");
		Rectangle rect = Graphiti.getGaCreateService().createInvisibleRectangle(decorator);
		rect.setX(-5);
		rect.setY(-5);
		rect.setWidth(0);
		rect.setHeight(0);
		
		return decorator;
	}

	public static ContainerShape createEventSubProcessDecorator(ContainerShape subProcessShape, boolean isInterrupting) {
		ContainerShape decoratorShape = Graphiti.getPeCreateService().createContainerShape(subProcessShape, false);
		Rectangle invisibleRect = Graphiti.getGaCreateService().createInvisibleRectangle(decoratorShape);

		ContainerShape circleShape = Graphiti.getPeCreateService().createContainerShape(decoratorShape, false);
		Ellipse circle = Graphiti.getGaCreateService().createEllipse(circleShape);
		gaService.setLocationAndSize(circle, 0, 0, 20, 20);
		circle.setForeground(manageColor(decoratorShape, StyleUtil.CLASS_FOREGROUND));
		circle.setFilled(false);
		if (!isInterrupting)
			circle.setLineStyle(LineStyle.DASH);
		peService.setPropertyValue(decoratorShape, GraphitiConstants.EVENT_SUBPROCESS_DECORATOR_CONTAINER, Boolean.TRUE.toString());
		gaService.setLocationAndSize(invisibleRect, 1, 1, 20, 20);

		return decoratorShape;
	}
	
	public static boolean isEventSubProcessDecorator(PictogramElement pe) {
		String property = peService.getPropertyValue(pe, GraphitiConstants.EVENT_SUBPROCESS_DECORATOR_CONTAINER);
		if (property!=null)
			return true;
		return false;
	}
}
