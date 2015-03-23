/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 * All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features;

import java.util.Hashtable;

import org.eclipse.bpmn2.modeler.core.features.label.UpdateLabelFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * Keys used to store Graphiti Shape and Context properties.
 * <p>
 * Some figure construction and updating operations need to pass information
 * from one Graphiti Feature to another (e.g. during a CreateFeature the
 * AddFeature, UpdateFeature and LayouFeatures are invoked). This is done
 * through the Shape property list, and the {@link IContext} property list. Note
 * that Shape properties are String only, whereas IContext properties are
 * Objects, allowing greater flexibility when passing information from Feature
 * to Feature. Shape properties must be encoded into Strings if we need to
 * handle Objects.
 */
public interface GraphitiConstants {

	/**
	 * The {@link IContext} key that holds a reference to a BPMN2 model object
	 * during creation of its visual representation.
	 */
	public static final String BUSINESS_OBJECT = "businessObject"; //$NON-NLS-1$

	/**
	 * The {@link Shape} property key that identifies a Shape as a Label. Labels
	 * are managed separately from their owners and can be located either above,
	 * below, to the left or right of its owning shape. See the
	 * {@link UpdateLabelFeature} class for more information
	 **/
	public static final String LABEL_SHAPE = "label.shape"; //$NON-NLS-1$
	
	/**
	 * The {@link Shape} property key that indicates if a Label has changed and
	 * its Shape needs to be updated
	 **/
	public static final String LABEL_CHANGED = "label.changed"; //$NON-NLS-1$
	
	/**
	 * The {@link IContext} property key that holds a reference to a
	 * {@link Point} object, which represents an offset (x,y) distance a Label
	 * should be moved when its {@link Shape} is updated.
	 **/
	public static final String LABEL_OFFSET = "label.offset"; //$NON-NLS-1$
	
	/**
	 * The {@link IContext} property key that holds a reference to a
	 * {@link Hashtable}. The Hashtable maps a PictogramElement to a move delta.
	 * This is used when the orientation of a Lane or Pool is changed - each of
	 * the contained shapes are transposed by a different x/y delta, depending
	 * on their location within the Lane or Pool.
	 */
	public static final String LABEL_OFFSET_MAP = "label.offset.map"; //$NON-NLS-1$
	
	/**
	 * The {@link IContext} property key that holds a reference to a single
	 * {@link PictogramElement} object. The PE is used to identify the owner of
	 * a Label Shape.
	 **/
	public static final String PICTOGRAM_ELEMENT = "pictogram.element"; //$NON-NLS-1$
	
	/**
	 * The {@link IContext} property key that holds a reference to a List of
	 * {@link PictogramElement} objects that were created during a
	 * {@link MultiAddFeature} or {@link CompoundCreateFeature}
	 **/
	public static final String PICTOGRAM_ELEMENTS = "pictogram.elements"; //$NON-NLS-1$

	/**
	 * The {@link IContext} property key that indicates the editor is importing
	 * objects and that the {@llink PictogramElement} Create and Add Features
	 * should populate the object from BPMN DI values instead of providing
	 * defaults.
	 **/
	public static final String IMPORT_PROPERTY = "is.importing"; //$NON-NLS-1$

	/**
	 * The {@link Shape} property key that identifies this Shape as the
	 * Validation Decorator. This is an image decorator attached to the top-left
	 * corner of a BPMN2 shape visual to indicate validation errors/warnings. A
	 * ToolTip on the decorator shape displays the validation message text.
	 * Validation Decorators are created for all BPMN2 shapes, i.e. Tasks,
	 * Gateways, Events, etc.
	 **/
	public static final String VALIDATION_DECORATOR = "validation.decorator"; //$NON-NLS-1$

	/**
	 * The {@link Shape} property key that identifies this Shape as the
	 * Activity's border decoration. This is a rounded rectangle drawn in the
	 * Activity's foreground color. This shape optionally owns an {@link Image}
	 * {@link GraphicsAlgorithm} which appears inside the top-left corner and is
	 * used to identify the Activity type, i.e. a hand icon for Manual Task, a
	 * person icon for User Task, envelope icons for Send and Receive Tasks,
	 * etc.
	 **/
	public static final String ACTIVITY_BORDER = "activity.border"; //$NON-NLS-1$

	/** The Constant ACTIVITY_MOVE_PROPERTY. */
	public static final String ACTIVITY_MOVE_PROPERTY = "activity.move"; //$NON-NLS-1$

	/** The Constant SELECTION_MOVE_PROPERTY. */
	public static final String SELECTION_MOVE_PROPERTY = "selection.move"; //$NON-NLS-1$

	/** The is compensate property. */
	public final static String IS_COMPENSATE_PROPERTY = "marker.compensate"; //$NON-NLS-1$

	/** The is loop or multi instance. */
	public final static String IS_LOOP_OR_MULTI_INSTANCE = "marker.loop.or.multi"; //$NON-NLS-1$

	public final static String COMMAND_HINT = "command.hint"; //$NON-NLS-1$

	public static final String COLLECTION_PROPERTY = "isCollection"; //$NON-NLS-1$

	public static final String HIDEABLE_PROPERTY = "hideable"; //$NON-NLS-1$

	public static final String DATASTATE_PROPERTY = "datastate"; //$NON-NLS-1$

	public static final String IS_HORIZONTAL_PROPERTY = "isHorizontal"; //$NON-NLS-1$

	public static final String TOOLTIP_PROPERTY = "tooltip"; //$NON-NLS-1$

	public final static String EVENT_DEFINITION_SHAPE = "event.definition.shape";

	public static final String EVENT_ELEMENT = "event.graphics.element"; //$NON-NLS-1$

	public static final String EVENT_CIRCLE = "event.graphics.element.circle"; //$NON-NLS-1$

	/**
	 * The {@link Shape} property key that identifies this Shape as the Activity
	 * Marker container. This ContainerShape contains a number of child shapes,
	 * (see the "ACTIVITY_MARKER_..." constants, below) which are used to
	 * indicate some kind of state or behavior for a BPMN2 Activity. The
	 * children shapes are made visible or invisible depending on the
	 * corresponding Activity state attribute. The ContainerShape is drawn at
	 * the bottom-center of Activity figures.
	 */
	public static final String ACTIVITY_MARKER_CONTAINER = "activity.marker.container"; //$NON-NLS-1$

	/**
	 * The {@link Shape} property key that identifies this Shape as an Activity
	 * Marker container child shape. This shape indicates that an Activity is
	 * used for some kind of compensation processing, indicated by two
	 * left-pointing chevrons. This shape is made visible when the Activity's
	 * "isForCompensation" attribute is set to TRUE.
	 */
	public static final String ACTIVITY_MARKER_COMPENSATE = "activity.marker.compensate"; //$NON-NLS-1$

	/**
	 * The {@link Shape} property key that identifies this Shape as an Activity
	 * Marker container child shape. When this shape is visible, it indicates
	 * the Looping Characteristics for an Activity are set to "Standard". This
	 * is represented by a circular, counter-clockwise pointing arrow.
	 */
	public static final String ACTIVITY_MARKER_LC_STANDARD = "activity.marker.lc.standard"; //$NON-NLS-1$

	/**
	 * The {@link Shape} property key that identifies this Shape as an Activity
	 * Marker container child shape. When this shape is visible, it indicates
	 * the Looping Characteristics for an Activity are set to "Sequential". This
	 * is represented by three small horizontal lines.
	 */
	public static final String ACTIVITY_MARKER_LC_MULTI_SEQUENTIAL = "activity.marker.lc.multi.sequential"; //$NON-NLS-1$

	/**
	 * The {@link Shape} property key that identifies this Shape as an Activity
	 * Marker container child shape. When this shape is visible, it indicates
	 * the Looping Characteristics for an Activity are set to "Parallel". This
	 * is represented by three small vertical lines.
	 */
	public static final String ACTIVITY_MARKER_LC_MULTI_PARALLEL = "activity.marker.lc.multi.parallel"; //$NON-NLS-1$

	/**
	 * The {@link Shape} property key that identifies this Shape as an Activity
	 * Marker container child shape. When this shape is visible, it indicates
	 * the Activity is an "ad-hoc" Sub Process. This is represented by a tilde
	 * shape.
	 */
	public static final String ACTIVITY_MARKER_AD_HOC = "activity.marker.adhoc"; //$NON-NLS-1$

	/**
	 * The {@link Shape} property key that identifies this Shape as an Activity
	 * Marker container child shape. When this shape is visible, it indicates
	 * the Activity is a Sub Process shown in its collapsed state. This is
	 * represented by a small square containing a "+" sign. When the Sub Process
	 * is expanded, this shape is made invisible.
	 */
	public static final String ACTIVITY_MARKER_EXPAND = "activity.marker.expand"; //$NON-NLS-1$

	/**
	 * The {@link Shape} property key that can be used to offset the vertical
	 * position of the Activity Marker ContainerShape. This is necessary for
	 * Choreography Activities which may have one or more Participant Bands at
	 * the bottom of the Choreography shape. In this case, the Marker
	 * ContainerShape will need to be moved up to prevent it from being obscured
	 * by the Participant Band(s).
	 **/
	public static final String ACTIVITY_MARKER_OFFSET = "activity.marker.offset"; //$NON-NLS-1$

	public static final String EVENT_MARKER_CONTAINER = "event.marker.container"; //$NON-NLS-1$

	public static final String EVENT_SUBPROCESS_DECORATOR_CONTAINER = "event.subprocess.decorator.container"; //$NON-NLS-1$

	// Lane and Pool move/resize constants
	public static final String LANE_RESIZE_PROPERTY = "lane.resize"; //$NON-NLS-1$
	public static final String POOL_RESIZE_PROPERTY = "pool.resize"; //$NON-NLS-1$
	public static final String RESIZE_FIRST_LANE = "resize.first.lane"; //$NON-NLS-1$

	public final static String PARENT_CONTAINER = "parent.container";

	public static final String MULTIPLICITY_MARKER = "multiplicity.marker"; //$NON-NLS-1$
	public static final String MULTIPLICITY = "multiplicity"; //$NON-NLS-1$

	/**
	 * The key used to store the copied shape in the Paste Context. This is
	 * copied to the AddContext and picked up by the AddFeature which duplicates
	 * the copied shape's size and other attributes.
	 */
	public static final String COPIED_BPMN_SHAPE = "copied.bpmn.shape"; //$NON-NLS-1$

	/** The key used to store the copied business object in the Paste Context. */
	public static final String COPIED_BPMN_OBJECT = "copied.bpmn.object"; //$NON-NLS-1$

	/** The Constant COPY_FROM_CONTEXT. */
	public static final String COPY_FROM_CONTEXT = "copy.from.context"; //$NON-NLS-1$

	/** The Constant CUSTOM_ELEMENT_ID. */
	public final static String CUSTOM_ELEMENT_ID = "custom.element.id"; //$NON-NLS-1$

	/** 
	 * The {@link IContext} property key used by the {@link MultiUpdateFeature}
	 * to force an update of all of its contained features, regardless of
	 * whether they have determined an update is needed.
	 **/
	public final static String FORCE_UPDATE_ALL = "force.update.all"; //$NON-NLS-1$

	public static final String BOUNDARY_EVENT_RELATIVE_POS = "boundary.event.relative.pos"; //$NON-NLS-1$

	public static final String BOUNDARY_FIXPOINT_ANCHOR = "boundary.fixpoint.anchor"; //$NON-NLS-1$

	public static final String BOUNDARY_ADHOC_ANCHOR = "boundary.adhoc.anchor"; //$NON-NLS-1$

	public static final String CONNECTION_SOURCE_LOCATION = "connection.source.location"; //$NON-NLS-1$

	public static final String CONNECTION_TARGET_LOCATION = "connection.target.location"; //$NON-NLS-1$
	public static final String CONNECTION_BENDPOINTS = "connection.bendpoints"; //$NON-NLS-1$

	public static final String CONNECTION_CREATED = "connection.created"; //$NON-NLS-1$

	// values for connection points
	public static final String CONNECTION_POINT = "connection.point"; //$NON-NLS-1$

	public static final String CONNECTION_POINT_KEY = "connection.point.key"; //$NON-NLS-1$

	public static final String EVENT_SUBPROCESS_DECORATOR = "event.subprocess.decorator";

	public static final String IS_EXPANDED = "is.expanded"; //$NON-NLS-1$
	public static final String EXPANDED_SIZE = "expanded.size"; //$NON-NLS-1$
	public static final String COLLAPSED_SIZE = "collapsed.size"; //$NON-NLS-1$

	public static final String TRIGGERED_BY_EVENT = "triggered.by.event"; //$NON-NLS-1$

}
