/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.preferences;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle.LabelPosition;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle.Category;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle.RoutingStyle;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.Messages;
import org.eclipse.bpmn2.modeler.ui.diagram.Bpmn2FeatureMap;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;


@SuppressWarnings({"nls", "rawtypes"})
public class Bpmn2EditorAppearancePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	@SuppressWarnings("serial")
	class ShapeStyleCategoryList extends LinkedHashMap<String, Object> {
		public Category key;
		
		public ShapeStyleCategoryList(Category key) {
			this.key = key;
		}
	}
	@SuppressWarnings("serial")
	class ShapeStyleList extends LinkedHashMap<Class, ShapeStyle> {
		public Category key;
		
		public ShapeStyleList(Category key) {
			this.key = key;
		}
	}

	Bpmn2Preferences preferences;
	TreeViewer elementsTreeViewer;
	List<Class> allElements;
	Group colorGroup;
	Group labelGroup;
	Composite colorEditors;
	Composite container;
	Hashtable<Object, ShapeStyle> allShapeStyles;
	ShapeStyleCategoryList categories;
	ShapeStyleCategoryList shapesList = new ShapeStyleCategoryList(Category.SHAPES);
	ShapeStyleList connectionShapeStyles = new ShapeStyleList(Category.CONNECTIONS);
	ShapeStyleList eventShapeStyles = new ShapeStyleList(Category.EVENTS);
	ShapeStyleList gatewayShapeStyles = new ShapeStyleList(Category.GATEWAYS);
	ShapeStyleList taskShapeStyles = new ShapeStyleList(Category.TASKS);
	ShapeStyleList globalTaskShapeStyles = new ShapeStyleList(Category.GLOBAL_TASKS);
	ShapeStyleList subProcessShapeStyles = new ShapeStyleList(Category.SUBPROCESS);
	ShapeStyleList choreographyShapeStyles = new ShapeStyleList(Category.CHOREOGRAPHY);
	ShapeStyleList conversationShapeStyles = new ShapeStyleList(Category.CONVERSATION);
	ShapeStyleList swimLanesShapeStyles = new ShapeStyleList(Category.SWIMLANES);
	ShapeStyleList dataShapeStyles = new ShapeStyleList(Category.DATA);
	ShapeStyleList artifactShapeStyles = new ShapeStyleList(Category.ARTIFACTS);
	Object currentSelection;
	ColorShapeStyleEditor shapeBackground;
	ColorShapeStyleEditor shapePrimarySelectedColor;
	ColorShapeStyleEditor shapeSecondarySelectedColor;
	ColorShapeStyleEditor shapeForeground;
	CheckboxShapeStyleEditor useDefaultSize;
	Button applyToAllChildren;
	IntegerShapeStyleEditor defaultWidth;
	IntegerShapeStyleEditor defaultHeight;
	FontShapeStyleEditor labelFont;
	ColorShapeStyleEditor labelForeground;
	RoutingStyleShapeStyleEditor routingStyleViewer;
	LabelLocationShapeStyleEditor labelLocationViewer;
	BEListLabelProvider labelProvider;
	
	public Bpmn2EditorAppearancePreferencePage() {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		preferences = Bpmn2Preferences.getInstance();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		allElements = new ArrayList<Class>();
		allElements.addAll(Bpmn2FeatureMap.CONNECTIONS);
		allElements.addAll(Bpmn2FeatureMap.EVENTS);
		allElements.addAll(Bpmn2FeatureMap.GATEWAYS);
		allElements.addAll(Bpmn2FeatureMap.TASKS);
		allElements.addAll(Bpmn2FeatureMap.GLOBAL_TASKS);
		allElements.addAll(Bpmn2FeatureMap.SUBPROCESS);
		allElements.addAll(Bpmn2FeatureMap.CHOREOGRAPHY);
		allElements.addAll(Bpmn2FeatureMap.CONVERSATION);
		allElements.addAll(Bpmn2FeatureMap.SWIMLANES);
		allElements.addAll(Bpmn2FeatureMap.ALL_DATA);
		allElements.addAll(Bpmn2FeatureMap.ARTIFACTS);
		Collections.sort(allElements, new Comparator<Class>() {

			@Override
			public int compare(Class arg0, Class arg1) {
				return arg0.getSimpleName().compareTo(arg1.getSimpleName());
			}
			
		});
	}

	@Override
	protected Control createContents(Composite parent) {
		
		GridLayout layout = (GridLayout)parent.getLayout();
		GridData gd;
		
		container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		container.setLayout(new GridLayout(2, true));
        
		final Group elementsGroup = new Group(container, SWT.NONE);
		elementsGroup.setText(Messages.Bpmn2EditorPreferencePage_GraphicalElements_Group);
        gd = new GridData(SWT.FILL,SWT.FILL,true,true,1,1);
		elementsGroup.setLayoutData(gd);
		elementsGroup.setLayout(new GridLayout(1,false));
        
        elementsTreeViewer = new TreeViewer(elementsGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        final Tree elementsTree = elementsTreeViewer.getTree();
        gd = new GridData(SWT.FILL,SWT.TOP,true,true,1,1);
        elementsTree.setLayoutData(gd);
        
        elementsTreeViewer.setContentProvider(new BEListContentProvider());
        labelProvider = new BEListLabelProvider();
        elementsTreeViewer.setLabelProvider(labelProvider);
		parent.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				GridData gd = (GridData) elementsGroup.getLayoutData();
				gd.heightHint = 500;
				gd = (GridData) elementsTreeViewer.getTree().getLayoutData();
				gd.heightHint = 500;
				container.layout();
			}
		});
        
		Composite colorAndLabelComposite = new Composite(container, SWT.NONE);
		colorAndLabelComposite.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		colorAndLabelComposite.setLayout(new GridLayout(1,false));
		
		colorGroup = new Group(colorAndLabelComposite, SWT.NONE);
		colorGroup.setText(Messages.Bpmn2EditorPreferencePage_Colors_Group);
		colorGroup.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		colorGroup.setLayout(new GridLayout(1,false));

        colorEditors = new Composite(colorGroup, SWT.NONE);
        colorEditors.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
        layout = new GridLayout(1,false);
        layout.verticalSpacing = 0;
        colorEditors.setLayout(layout);
        colorEditors.setFont(parent.getFont());
//        colorEditors.setVisible(false);

		shapeBackground = new ColorShapeStyleEditor(colorEditors, ShapeStyle.SS_SHAPE_BACKGROUND,
				Messages.Bpmn2EditorPreferencePage_Fill_Color_Label);
		shapeBackground.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IColorConstant c = shapeBackground.getValue();
				ShapeStyle ss = allShapeStyles.get(currentSelection);
				if (currentSelection instanceof Class) {
					// update secondary colors
					ss.setDefaultColors(c);
					shapePrimarySelectedColor.setValue(ss.getShapePrimarySelectedColor());
					shapeSecondarySelectedColor.setValue(ss.getShapeSecondarySelectedColor());
					shapeForeground.setValue(ss.getShapeForeground());
					labelForeground.setValue(ss.getLabelForeground());
				}
			}
    	});
		
		shapeForeground = new ColorShapeStyleEditor(colorEditors, ShapeStyle.SS_SHAPE_FOREGROUND,
				Messages.Bpmn2EditorPreferencePage_Foreground_Color_Label);

		shapePrimarySelectedColor = new ColorShapeStyleEditor(colorEditors, ShapeStyle.SS_SHAPE_PRIMARY_SELECTION,
				Messages.Bpmn2EditorPreferencePage_Selected_Color_Label);

		shapeSecondarySelectedColor = new ColorShapeStyleEditor(colorEditors, ShapeStyle.SS_SHAPE_SECONDARY_SELECTION,
				Messages.Bpmn2EditorPreferencePage_MultiSelected_Color_Label);


		defaultWidth = new IntegerShapeStyleEditor(colorEditors, ShapeStyle.SS_DEFAULT_WIDTH,"");
		gd = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd.horizontalIndent = 5;
		gd.verticalIndent = 10;
		defaultWidth.setLayoutData(gd);

		defaultHeight = new IntegerShapeStyleEditor(colorEditors, ShapeStyle.SS_DEFAULT_HEIGHT,"");
		gd = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd.horizontalIndent = 5;
		gd.verticalIndent = 10;
		defaultHeight.setLayoutData(gd);
		
		useDefaultSize = new CheckboxShapeStyleEditor(colorEditors, ShapeStyle.SS_USE_DEFAULT_SIZE, "");
		gd = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd.horizontalIndent = 5;
		gd.verticalIndent = 10;
		useDefaultSize.setLayoutData(gd);

        Composite routingStyleComposite = new Composite(colorEditors, SWT.NONE);
        routingStyleComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));
        layout = new GridLayout(2,false);
        routingStyleComposite.setLayout(layout);

		routingStyleViewer = new RoutingStyleShapeStyleEditor(colorEditors,
				Messages.Bpmn2EditorPreferencePage_Routing_Style_Label);
		routingStyleViewer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		labelGroup = new Group(colorAndLabelComposite, SWT.NONE);
		labelGroup.setText("Labels");
		labelGroup.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		labelGroup.setLayout(new GridLayout(1,false));
		labelForeground = new ColorShapeStyleEditor(labelGroup, ShapeStyle.SS_LABEL_FOREGROUND,
				Messages.Bpmn2EditorPreferencePage_Label_Color_Label);
		showControl(labelForeground, false);
		labelFont = new FontShapeStyleEditor(labelGroup, ShapeStyle.SS_LABEL_FONT,
				Messages.Bpmn2EditorPreferencePage_Label_Font_Label);
		showControl(labelFont, false);

		labelLocationViewer = new LabelLocationShapeStyleEditor(labelGroup,
				Messages.Bpmn2EditorPreferencePage_Label_Location_Label);
		labelLocationViewer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		
		applyToAllChildren = new Button(colorAndLabelComposite, SWT.CHECK);
		applyToAllChildren.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		applyToAllChildren.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				configureAll();
			}
		});
		showControl(applyToAllChildren, false);

		loadStyleEditors();

		configureForShapes(null);
		configureAll();

        elementsTreeViewer.addSelectionChangedListener(new BEListSelectionChangedListener());

        return container;
	}
	
	private void applyToAll(ShapeStyle theShapeStyle) {
		if (applyToAllChildren!=null && applyToAllChildren.getSelection()
				&& currentSelection instanceof Category) {
			List<ShapeStyle> ssl = new ArrayList<ShapeStyle>();
			switch ((Category)currentSelection) {
			case CONNECTIONS:
				ssl.addAll(connectionShapeStyles.values());
				break;
			case SHAPES:
				ssl.addAll(eventShapeStyles.values());
				ssl.addAll(gatewayShapeStyles.values());
				ssl.addAll(taskShapeStyles.values());
				ssl.addAll(globalTaskShapeStyles.values());
				ssl.addAll(subProcessShapeStyles.values());
				ssl.addAll(choreographyShapeStyles.values());
				ssl.addAll(conversationShapeStyles.values());
				ssl.addAll(swimLanesShapeStyles.values());
				ssl.addAll(dataShapeStyles.values());
				ssl.addAll(artifactShapeStyles.values());
				
				ssl.add(allShapeStyles.get(Category.EVENTS));
				ssl.add(allShapeStyles.get(Category.GATEWAYS));
				ssl.add(allShapeStyles.get(Category.TASKS));
				ssl.add(allShapeStyles.get(Category.GLOBAL_TASKS));
				ssl.add(allShapeStyles.get(Category.SUBPROCESS));
				ssl.add(allShapeStyles.get(Category.CHOREOGRAPHY));
				ssl.add(allShapeStyles.get(Category.CONVERSATION));
				ssl.add(allShapeStyles.get(Category.SWIMLANES));
				ssl.add(allShapeStyles.get(Category.DATA));
				ssl.add(allShapeStyles.get(Category.ARTIFACTS));
				break;
			case CANVAS:
				break;
			case DATA:
				ssl.addAll(dataShapeStyles.values());
				break;
			case EVENTS:
				ssl.addAll(eventShapeStyles.values());
				break;
			case GATEWAYS:
				ssl.addAll(gatewayShapeStyles.values());
				break;
			case GRID:
				break;
			case ARTIFACTS:
				ssl.addAll(artifactShapeStyles.values());
				break;
			case TASKS:
				ssl.addAll(taskShapeStyles.values());
				break;
			case GLOBAL_TASKS:
				ssl.addAll(globalTaskShapeStyles.values());
				break;
			case SUBPROCESS:
				ssl.addAll(subProcessShapeStyles.values());
				break;
			case CHOREOGRAPHY:
				ssl.addAll(choreographyShapeStyles.values());
				break;
			case CONVERSATION:
				ssl.addAll(conversationShapeStyles.values());
				break;
			case SWIMLANES:
				ssl.addAll(swimLanesShapeStyles.values());
				break;
			case NONE:
				break;
			}
			
			for (ShapeStyle ss : ssl) {
				ss.applyChanges(theShapeStyle);
			}
		}
	}
	
	private void loadStyleEditors() {
		if (allShapeStyles == null) {
			allShapeStyles = new Hashtable<Object, ShapeStyle>();
			
			shapesList.put(Category.EVENTS.toString(), eventShapeStyles);
			shapesList.put(Category.GATEWAYS.toString(), gatewayShapeStyles);
			shapesList.put(Category.TASKS.toString(), taskShapeStyles);
			shapesList.put(Category.GLOBAL_TASKS.toString(), globalTaskShapeStyles);
			shapesList.put(Category.SUBPROCESS.toString(), subProcessShapeStyles);
			shapesList.put(Category.CHOREOGRAPHY.toString(), choreographyShapeStyles);
			shapesList.put(Category.CONVERSATION.toString(), conversationShapeStyles);
			shapesList.put(Category.SWIMLANES.toString(), swimLanesShapeStyles);
			shapesList.put(Category.DATA.toString(), dataShapeStyles);
			shapesList.put(Category.ARTIFACTS.toString(), artifactShapeStyles);

			categories = new ShapeStyleCategoryList(Category.NONE);
			categories.put(Category.CONNECTIONS.toString(), connectionShapeStyles);
			categories.put(Category.SHAPES.toString(), shapesList);

			ShapeStyle ss;
			for (Class c : allElements) {
				ss = new ShapeStyle( preferences.getShapeStyle(c) );
				allShapeStyles.put(c, ss);
				
				if (Bpmn2FeatureMap.CONNECTIONS.contains(c))
					connectionShapeStyles.put(c, ss);
				if (Bpmn2FeatureMap.EVENTS.contains(c))
					eventShapeStyles.put(c, ss);
				if (Bpmn2FeatureMap.GATEWAYS.contains(c))
					gatewayShapeStyles.put(c, ss);
				if (Bpmn2FeatureMap.TASKS.contains(c))
					taskShapeStyles.put(c, ss);
				if (Bpmn2FeatureMap.GLOBAL_TASKS.contains(c))
					globalTaskShapeStyles.put(c, ss);
				if (Bpmn2FeatureMap.SUBPROCESS.contains(c))
					subProcessShapeStyles.put(c, ss);
				if (Bpmn2FeatureMap.CHOREOGRAPHY.contains(c))
					choreographyShapeStyles.put(c, ss);
				if (Bpmn2FeatureMap.CONVERSATION.contains(c))
					conversationShapeStyles.put(c, ss);
				if (Bpmn2FeatureMap.SWIMLANES.contains(c))
					swimLanesShapeStyles.put(c, ss);
				if (Bpmn2FeatureMap.ALL_DATA.contains(c))
					dataShapeStyles.put(c, ss);
				if (Bpmn2FeatureMap.ARTIFACTS.contains(c))
					artifactShapeStyles.put(c, ss);
				
				if (Activator.getDefault().isDebugging()) {
					System.out.println("\t\t<"+ShapeStyle.STYLE_OBJECT + " object=\"" + c.getSimpleName() + "\" "
							+ ShapeStyle.STYLE_SHAPE_FOREGROUND+"=\"" + ShapeStyle.colorToString(ss.getShapeForeground()) + "\" "
							+ ShapeStyle.STYLE_SHAPE_BACKGROUND+"=\"" + ShapeStyle.colorToString(ss.getShapeBackground()) + "\" "
							+ ShapeStyle.STYLE_LABEL_FONT+"=\"" + ShapeStyle.fontToString(ss.getLabelFont()) + "\" "
							+ ShapeStyle.STYLE_LABEL_FOREGROUND+"=\"" + ShapeStyle.colorToString(ss.getLabelForeground()) + "\" "
							+ ShapeStyle.STYLE_LABEL_BACKGROUND+"=\"" + ShapeStyle.colorToString(ss.getLabelBackground()) + "\" "
							+ ShapeStyle.STYLE_LABEL_POSITION+"=\"" + ss.getLabelPosition().name() + "\" "
							+ ShapeStyle.STYLE_ROUTING_STYLE+"=\"" + ss.getRoutingStyle().name() + "\" "
							+ ShapeStyle.STYLE_USE_DEFAULT_SIZE+"=\"" + ss.getUseDefaultSize() + "\" "
							+ ShapeStyle.STYLE_DEFAULT_HEIGHT+"=\"" + ss.getDefaultHeight() + "\" "
							+ ShapeStyle.STYLE_DEFAULT_WIDTH+"=\"" + ss.getDefaultWidth() + "\" "
							+ "/>"); //$NON-NLS-1$
				}
			}
			for (Category key : Category.values()) {
				ss = new ShapeStyle( preferences.getShapeStyle(key) );
				allShapeStyles.put(key, ss);
			}
			
			ss = new ShapeStyle( preferences.getShapeStyle(Category.CANVAS) );
			categories.put(Category.CANVAS.toString(), Category.CANVAS);

			ss = new ShapeStyle( preferences.getShapeStyle(Category.GRID) );
			categories.put(Category.GRID.toString(), Category.GRID);

			currentSelection = null;
			elementsTreeViewer.setInput(categories);
			elementsTreeViewer.setSelection(null);
		}

		Object key = null;
		ShapeStyle ss = null;
		if (currentSelection instanceof Class) {
			key = currentSelection;
			ss = allShapeStyles.get((Class)key);
		}
		else if (currentSelection instanceof Category) {
			key = currentSelection;
			ss = allShapeStyles.get((Category)key);
		}			

		if (ss!=null) {
			shapeForeground.setValue(ss.getShapeForeground());
			shapeBackground.setValue(ss.getShapeBackground());
			shapePrimarySelectedColor.setValue(ss.getShapePrimarySelectedColor());
			shapeSecondarySelectedColor.setValue(ss.getShapeSecondarySelectedColor());
			useDefaultSize.setValue(ss.getUseDefaultSize());
			defaultWidth.setValue(ss.getDefaultWidth());
			defaultHeight.setValue(ss.getDefaultHeight());
			labelFont.setValue(ss.getLabelFont());
			labelForeground.setValue(ss.getLabelForeground());
			labelLocationViewer.setValue(ss.getLabelPosition());

			if (Bpmn2FeatureMap.CONNECTIONS.contains(key) || key == Category.CONNECTIONS) {
				configureForConnections(ss);
			}
			else if (key == Category.CANVAS) {
				configureForCanvas(ss);
			}
			else if (key == Category.GRID) {
				configureForGrid(ss);
			}
			else {
				configureForShapes(ss);
			}
			container.layout();
		}
		else {
			showControl(labelFont,false);
			showControl(labelForeground,false);
		}
	}

	private void configureAll() {
		boolean enabled = (currentSelection != null);
		if (	currentSelection instanceof Category &&
				currentSelection!=Category.CANVAS &&
				currentSelection!=Category.GRID) { 
			showControl(applyToAllChildren, true);
			applyToAllChildren.setText("Apply changes to all "+((Category)currentSelection).toString());
			enabled = applyToAllChildren.getSelection();
		}
		else
			showControl(applyToAllChildren, false);

		enableComposite(colorGroup, enabled);
		enableComposite(labelGroup, enabled);
		showControl(labelLocationViewer, hasLabel());
	}
	
	private boolean hasLabel() {
		if (currentSelection instanceof Class) {
			Class c = (Class) currentSelection;
			if (
					Bpmn2FeatureMap.TASKS.contains(c) ||
					Bpmn2FeatureMap.GLOBAL_TASKS.contains(c) ||
					Bpmn2FeatureMap.SUBPROCESS.contains(c) ||
					Bpmn2FeatureMap.CHOREOGRAPHY.contains(c) ||
					Bpmn2FeatureMap.SWIMLANES.contains(c) ||
					Bpmn2FeatureMap.ARTIFACTS.contains(c))
				return false;
		}
		else if (currentSelection instanceof Category) {
			Category k = (Category) currentSelection;
			if (	k==Category.TASKS ||
					k==Category.GLOBAL_TASKS ||
					k==Category.SUBPROCESS ||
					k==Category.CHOREOGRAPHY ||
					k==Category.SWIMLANES ||
					k==Category.CANVAS ||
					k==Category.GRID) 
				return false;
		}

		return true;
	}
	
	private void configureForConnections(ShapeStyle ss) {
		showControl(shapeForeground,true);
		showControl(shapeBackground,false);
		showControl(shapePrimarySelectedColor,false);
		showControl(shapeSecondarySelectedColor,false);
		showControl(routingStyleViewer,true);
		routingStyleViewer.setValue(ss.getRoutingStyle());
		showControl(labelGroup,true);
		showControl(labelFont,true);
		showControl(labelForeground,true);
	
		// NOTE: BPMN2 does not define a width for connections
		showControl(useDefaultSize,false);
		showControl(defaultWidth,true);
		showControl(defaultHeight,false);

		configureAll();
	}
	
	private void configureForShapes(ShapeStyle ss) {
		showControl(shapeForeground,true);
		showControl(shapeBackground,true);
		showControl(shapePrimarySelectedColor,true);
		showControl(shapeSecondarySelectedColor,true);
		showControl(routingStyleViewer,false);
		showControl(labelGroup,true);
		showControl(labelFont,true);
		showControl(labelForeground,true);
		
		useDefaultSize.setText(Messages.Bpmn2EditorPreferencePage_UseDefaultSize);
		showControl(useDefaultSize,true);
		defaultWidth.setText(Messages.Bpmn2EditorPreferencePage_DefaultWidth);
		showControl(defaultWidth,true);
		defaultHeight.setText(Messages.Bpmn2EditorPreferencePage_DefaultHeight);
		showControl(defaultHeight,true);
		
		configureAll();
	}
	
	private void configureForCanvas(ShapeStyle ss) {
		showControl(shapeForeground,false);
		showControl(shapeBackground,true);
		showControl(shapePrimarySelectedColor,false);
		showControl(shapeSecondarySelectedColor,false);
		showControl(routingStyleViewer,false);
		showControl(labelGroup,false);
		showControl(labelFont,false);
		showControl(labelForeground,false);
		
		showControl(useDefaultSize,false);
		showControl(defaultWidth,false);
		showControl(defaultHeight,false);
		
		configureAll();
	}
	
	private void configureForGrid(ShapeStyle ss) {
		showControl(shapeForeground,true);
		showControl(shapeBackground,false);
		showControl(shapePrimarySelectedColor,false);
		showControl(shapeSecondarySelectedColor,false);
		showControl(routingStyleViewer,false);
		showControl(labelGroup,false);
		showControl(labelFont,false);
		showControl(labelForeground,false);
		
		useDefaultSize.setText(Messages.Bpmn2EditorPreferencePage_SnapToGrid);
		showControl(useDefaultSize,true);
		defaultWidth.setText(Messages.Bpmn2EditorPreferencePage_GridWidth);
		showControl(defaultWidth,true);
		defaultHeight.setText(Messages.Bpmn2EditorPreferencePage_GridHeight);
		showControl(defaultHeight,true);
		
		configureAll();
	}
	
	private void showControl(Control control, boolean visible) {
		if (control!=null && !control.isDisposed()) {
			control.setVisible(visible);
			((GridData)control.getLayoutData()).exclude = !visible;
		}
	}
	
	private void enableComposite(Composite composite, boolean enabled) {
		if (composite!=null && !composite.isDisposed()) {
			for (Control c : composite.getChildren()) {
				c.setEnabled(enabled);
				if (c instanceof Composite) {
					enableComposite((Composite)c, enabled);
				}
			}
		}
	}
	
	@Override
	protected void performDefaults() {
		try {
			preferences.setToDefault(Bpmn2Preferences.PREF_SHAPE_STYLE);
			allShapeStyles = null;
			loadStyleEditors();
			preferences.flush();
		}
		catch(Exception e) {
		}
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		for (Entry<Object, ShapeStyle> entry : allShapeStyles.entrySet()) {
			Object key = entry.getKey();
			if (key instanceof Class)
				key = ((Class)key).getSimpleName();
			preferences.setShapeStyle(key.toString(), entry.getValue());
		}
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return super.performOk();
	}

	private class BEListContentProvider implements ITreeContentProvider {

		ShapeStyleCategoryList categories;
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		@Override
		public void dispose() {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput instanceof ShapeStyleCategoryList) {
				categories = (ShapeStyleCategoryList) newInput;
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Entry) {
				Entry entry = (Entry)inputElement;
				if (entry.getKey() instanceof Category) {
					
				}
			}
			if (inputElement instanceof ShapeStyleCategoryList) {
				ShapeStyleCategoryList categories = (ShapeStyleCategoryList)inputElement;
				return categories.entrySet().toArray();
			}
			if (inputElement instanceof ShapeStyleList) {
				ShapeStyleList shapeStyles = (ShapeStyleList)inputElement;
				return shapeStyles.keySet().toArray();
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof Entry) {
				Entry entry = (Entry) parentElement;
				Object value = entry.getValue();
				if (value instanceof ShapeStyleCategoryList) {
					return ((ShapeStyleCategoryList)value).entrySet().toArray();
				}
				else if (value instanceof ShapeStyleList) {
					return ((ShapeStyleList)value).entrySet().toArray();
				}
			}
			else if (parentElement instanceof ShapeStyleCategoryList) {
				return ((ShapeStyleCategoryList)parentElement).entrySet().toArray();
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}
		
		@Override
		public boolean hasChildren(Object element) {
			return getChildren(element) != null;
		}
		
	}

	private class BEListLabelProvider extends LabelProvider {

		private Hashtable<Object,String> classNameMap = new Hashtable<Object,String>();
		
		@Override
		public String getText(Object element) {
			if (element instanceof Entry) {
				Entry entry = (Entry)element;
				if (entry.getKey() instanceof String)
					return (String) entry.getKey();
				if (entry.getKey() instanceof Class) {
					String text = classNameMap.get((Class)entry.getKey());
					if (text!=null)
						return text;
					return ModelUtil.toCanonicalString( ((Class)entry.getKey()).getSimpleName() );
				}
			}
			return element.toString();
		}
		
		public void setText(Object c, String t) {
			classNameMap.put(c, t);
		}
	}
	
	private class BEListSelectionChangedListener implements ISelectionChangedListener {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection sel = (IStructuredSelection) elementsTreeViewer.getSelection();
			if (sel != null) {
				Object element = sel.getFirstElement();
				if (element != null) {
					if (element instanceof Entry) {
						Entry entry = (Entry) element;
						Object key = entry.getKey();
						Object value = entry.getValue();
						if (value instanceof ShapeStyleCategoryList) {
							colorEditors.setVisible(true);
							currentSelection = ((ShapeStyleCategoryList)value).key;
						}
						else if (value instanceof ShapeStyleList) {
							colorEditors.setVisible(true);
							currentSelection = ((ShapeStyleList)value).key;
						}
						else if (value instanceof Category) {
							colorEditors.setVisible(true);
							currentSelection = (Category)value;
						}
						else if (key instanceof Class) {
							colorEditors.setVisible(true);
							currentSelection = key;
						}
						else {
							colorEditors.setVisible(false);
							currentSelection = null;
						}
					}
				}
				else {
//					colorEditors.setVisible(true);
					configureAll();
				}

				loadStyleEditors();
			}
		}
		
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// ShapeStyle Editors
	////////////////////////////////////////////////////////////////////////////////
	
	private abstract class ShapeStyleEditor extends Composite {

		public ShapeStyleEditor(Composite parent, int style, final int ssMask) {
	    	super(parent, style);
	    	
	    	Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
			    	addSelectionListener( new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							if (currentSelection!=null) {
								Object value = getValue();
								ShapeStyle ss = allShapeStyles.get(currentSelection);
								ss.setValue(ssMask, value);
								if (currentSelection instanceof Category) {
									applyToAll(ss);
								}
							}
						}
			    	});
				}
	    	});
	    }
		
	    public abstract void addSelectionListener (SelectionListener listener);
	    public abstract Object getValue();
	}
	
	private class ColorShapeStyleEditor extends ShapeStyleEditor {
		private ColorSelector colorSelector;
	    private Label selectorLabel;
	    private List<SelectionListener> listeners;
	    
	    public ColorShapeStyleEditor(Composite parent, int ssMask, String labelText) {
	    	super(parent, SWT.NONE, ssMask);
	    	this.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
	    	this.setLayout(new GridLayout(2, false));

	    	selectorLabel = new Label(this, SWT.LEFT);
	    	selectorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	    	selectorLabel.setFont(parent.getFont());
	    	selectorLabel.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                	selectorLabel = null;
                }
            });
	    	selectorLabel.setText(labelText);
	    	
	    	colorSelector = new ColorSelector(this);
	    	colorSelector.getButton().setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
	    }
	    
	    @Override
	    public void addSelectionListener(final SelectionListener listener) {
	    	colorSelector.addListener(new IPropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent e) {
					Event event = new Event();
					event.widget = colorSelector.getButton();
					SelectionEvent se = new SelectionEvent(event);
					se.data = getValue();
					listener.widgetSelected(se);
				}
	    	});
//	    	colorSelector.getButton().addSelectionListener(listener);
	    }
	    
	    @Override
		public IColorConstant getValue() {
			return ShapeStyle.RGBToColor(colorSelector.getColorValue());
		}
		
		public void setValue(IColorConstant c) {
			RGB rgb = ShapeStyle.colorToRGB(c);
			colorSelector.setColorValue(rgb);
		}
	}
	
	private class FontShapeStyleEditor extends ShapeStyleEditor {

	    /**
	     * The change font button, or <code>null</code> if none
	     * (before creation and after disposal).
	     */
	    private Button changeFontButton = null;

	    /**
	     * Font data for the chosen font button, or <code>null</code> if none.
	     */
	    private FontData[] selectedFont;

	    /**
	     * The label that displays the selected font, or <code>null</code> if none.
	     */
	    private Label previewLabel;
	    private Label selectorLabel;

	    public FontShapeStyleEditor(Composite parent, int ssMask, String labelText) {
	    	super(parent, SWT.NONE, ssMask);
	    	this.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
	    	this.setLayout(new GridLayout(3, false));

	    	selectorLabel = new Label(this, SWT.LEFT);
	    	selectorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	    	selectorLabel.setFont(parent.getFont());
	    	selectorLabel.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                	selectorLabel = null;
                }
            });
	    	selectorLabel.setText(labelText);

	    	previewLabel = new Label(this, SWT.LEFT);
	    	previewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            previewLabel.setFont(parent.getFont());
            previewLabel.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    previewLabel = null;
                }
            });
	    	
            changeFontButton = new Button(this, SWT.PUSH);
            changeFontButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			changeFontButton.setText(Messages.Bpmn2EditorPreferencePage_Change_Button);
            changeFontButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    FontDialog fontDialog = new FontDialog(changeFontButton
                            .getShell());
                    if (selectedFont != null) {
						fontDialog.setFontList(selectedFont);
					}
                    FontData font = fontDialog.open();
                    if (font != null) {
                        FontData[] oldFont = selectedFont;
                        if (oldFont == null) {
							oldFont = JFaceResources.getDefaultFont().getFontData();
						}
                        setSelectedFont(font);
//                        fireValueChanged(VALUE, oldFont[0], font);
                    }

                }
            });
            changeFontButton.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    changeFontButton = null;
                }
            });
            changeFontButton.setFont(parent.getFont());

	    }

		@Override
		public void addSelectionListener(SelectionListener listener) {
			changeFontButton.addSelectionListener(listener);
		}

		@Override
	    public Font getValue() {
	    	if (selectedFont!=null && selectedFont.length>0)
	    		return ShapeStyle.fontDataToFont(selectedFont[0]);
	    	return null;
	    }

	    public void setValue(Font f) {
	    	setSelectedFont(ShapeStyle.fontToFontData(f));
	    }
	    
	    private void setSelectedFont(FontData fd) {

	        FontData[] bestFont = JFaceResources.getFontRegistry().filterData(
	        		new FontData[]{fd}, previewLabel.getDisplay());

	        //if we have nothing valid do as best we can
	        if (bestFont == null) {
				bestFont = getDefaultFontData();
			}

	        //Now cache this value in the receiver
	        this.selectedFont = bestFont;

	        if (previewLabel != null) {
	            previewLabel.setText(StringConverter.asString(selectedFont[0]));
	        }
	    }

	    /**
	     * Get the system default font data.
	     * @return FontData[]
	     */
	    private FontData[] getDefaultFontData() {
	        return previewLabel.getDisplay().getSystemFont().getFontData();
	    }
	}

	private class IntegerShapeStyleEditor extends ShapeStyleEditor {
		private Label label;
		private Text text;
		
		public IntegerShapeStyleEditor(Composite parent, final int ssMask, String labelText) {
	    	super(parent, SWT.NONE, ssMask);
	    	this.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
	    	this.setLayout(new GridLayout(2, true));

	    	label = new Label(this, SWT.LEFT);
	    	label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	    	label.setFont(parent.getFont());
	    	label.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                	label = null;
                }
            });
	    	label.setText(labelText);
	    	
	    	text = new Text(this, SWT.BORDER);
	    	text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			text.addVerifyListener(new VerifyListener() {
				@Override
				public void verifyText(VerifyEvent e) {
					String string = e.text;
					char[] chars = new char[string.length()];
					string.getChars(0, chars.length, chars, 0);
					for (int i = 0; i < chars.length; i++) {
						if (!('0' <= chars[i] && chars[i] <= '9')) {
							e.doit = false;
							return;
						}
					}
				}
			});
		}

		public void setText(String labelText) {
	    	label.setText(labelText);
		}
		
		@Override
		public void addSelectionListener(final SelectionListener listener) {
			text.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					Event event = new Event();
					event.widget = text;
					SelectionEvent se = new SelectionEvent(event);
					se.data = getValue();
					listener.widgetSelected(se);
				}
			});
		}

		@Override
		public Integer getValue() {
			if (text!=null) {
				return Integer.parseInt(text.getText());
			}
			return -1;
		}

		public void setValue(int value) {
			if (text!=null) {
				text.setText(Integer.toString(value, 10));
			}
		}
	}

	private class CheckboxShapeStyleEditor extends ShapeStyleEditor {
		private Button checkbox;
		
		public CheckboxShapeStyleEditor(Composite parent, final int ssMask, String labelText) {
	    	super(parent, SWT.NONE, ssMask);
	    	this.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
	    	this.setLayout(new GridLayout(1, true));
	    	
	    	checkbox = new Button(this, SWT.CHECK);
	    	checkbox.setText(labelText);
	    	checkbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		}

		public void setText(String labelText) {
			if (checkbox!=null)
				checkbox.setText(labelText);
		}
		
		@Override
		public void addSelectionListener(final SelectionListener listener) {
			checkbox.addSelectionListener(listener);
		}

		@Override
		public Boolean getValue() {
			if (checkbox!=null) {
				return new Boolean(checkbox.getSelection());
			}
			return false;
		}

		public void setValue(boolean value) {
			if (checkbox!=null) {
				checkbox.setSelection(value);
			}
		}
	}
	
	private class LabeledComboShapeStyleEditor extends ShapeStyleEditor {
		protected Label label;
		protected Combo combo;

		public LabeledComboShapeStyleEditor (Composite parent, int ssMask, String text) {
			super(parent, SWT.NONE, ssMask);
			setLayout(new GridLayout(2,false));
			
	        label = new Label(this, SWT.LEFT);
			label.setText(text);
			label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

			combo = new Combo(this, SWT.READ_ONLY);
			combo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		}

		public void add(String text, Object value) {
			combo.add(text);
			combo.setData(text, value);
		}
		
		@Override
		public void addSelectionListener(SelectionListener listener) {
			combo.addSelectionListener(listener);
		}
		
		@Override
		public Object getValue() {
			int index = combo.getSelectionIndex();
			if ( index>=0) {
				return combo.getData(combo.getItem(index));
			}
			return null;
		}
		
		public void setValue(Object value) {
			int index = 0;
			for (String s : combo.getItems()) {
				Object data = combo.getData(s);
				if (data.equals(value)) {
					combo.select(index);
					break;
				}
				++index;
			}

		}
	}

	private class LabelLocationShapeStyleEditor extends LabeledComboShapeStyleEditor {

		public LabelLocationShapeStyleEditor(Composite parent, String text) {
			super(parent, ShapeStyle.SS_LABEL_POSITION, text);
			for (LabelPosition p : LabelPosition.values())
				add(p.toString(), p);
		}

		public LabelPosition getValue() {
			LabelPosition value = (LabelPosition) super.getValue();
			if (value==null)
				value = LabelPosition.SOUTH;
			return value;
		}
	}

	private class RoutingStyleShapeStyleEditor extends LabeledComboShapeStyleEditor {
		
		public RoutingStyleShapeStyleEditor(Composite parent, String text) {
			super(parent, ShapeStyle.SS_ROUTING_STYLE, text);
			for (RoutingStyle p : RoutingStyle.values())
				add(p.toString(), p);
		}

		public RoutingStyle getValue() {
			RoutingStyle value = (RoutingStyle) super.getValue();
			if (value==null)
				value = RoutingStyle.Manhattan;
			return value;
		}
	}
}
