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

package org.eclipse.bpmn2.modeler.core.merrimac.clad;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.merrimac.IConstants;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ObjectEditingDialog;
import org.eclipse.bpmn2.modeler.core.merrimac.providers.TableCursor;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.plugin.AbstractUIPlugin;
 

/**
 * @author Bob Brodt
 *
 */
public abstract class AbstractListComposite extends ListAndDetailCompositeBase implements INotifyChangedListener {
	
	public static final int HIDE_TITLE = 1 << 18; // Hide section title - useful if this is the only thing in the PropertySheetTab
	public static final int ADD_BUTTON = 1 << 19; // show "Add" button
	public static final int REMOVE_BUTTON = 1 << 20; // show "Remove" button
	public static final int MOVE_BUTTONS = 1 << 21; // show "Up" and "Down" buttons
	public static final int EDIT_BUTTON = 1 << 23; // show "Edit..." button
	public static final int SHOW_DETAILS = 1 << 24; // create a "Details" section
	public static final int DELETE_BUTTON = 1 << 25; // show "Delete" button - this uses EcoreUtil.delete() to kill the EObject
	public static final int COMPACT_STYLE = (
			ADD_BUTTON|REMOVE_BUTTON|MOVE_BUTTONS|SHOW_DETAILS);
	public static final int DEFAULT_STYLE = (
			ADD_BUTTON|REMOVE_BUTTON|MOVE_BUTTONS|EDIT_BUTTON|SHOW_DETAILS);
	public static final int DELETE_STYLE = (
			ADD_BUTTON|DELETE_BUTTON|MOVE_BUTTONS|EDIT_BUTTON|SHOW_DETAILS);
	public static final int READ_ONLY_STYLE = (
			ADD_BUTTON|REMOVE_BUTTON|MOVE_BUTTONS);
	
	public static final int CUSTOM_STYLES_MASK = (
			HIDE_TITLE|ADD_BUTTON|REMOVE_BUTTON|MOVE_BUTTONS|EDIT_BUTTON|SHOW_DETAILS);
	public static final int CUSTOM_BUTTONS_MASK = (
			ADD_BUTTON|REMOVE_BUTTON|MOVE_BUTTONS|EDIT_BUTTON);

	protected EStructuralFeature feature;
	
	// widgets
	protected SashForm sashForm;
	protected Section tableSection;
	protected ToolBarManager tableToolBarManager;
	protected Section detailSection;
	protected ToolBarManager detailToolBarManager;
	
	protected Table table;
	protected TableViewer tableViewer;
	protected AbstractDetailComposite detailComposite;
	
	protected boolean removeIsDelete = false;
	protected Action addAction;
	protected Action removeAction;
	protected Action upAction;
	protected Action downAction;
	protected Action editAction;
	
	protected ListCompositeColumnProvider columnProvider;
	protected ListCompositeContentProvider contentProvider;
	
	public AbstractListComposite(AbstractBpmn2PropertySection section) {
		this(section,DEFAULT_STYLE);
	}
	
	public AbstractListComposite(AbstractBpmn2PropertySection section, int style) {
		super(section.getSectionRoot(), style & ~CUSTOM_STYLES_MASK);
		this.style = style;
	}
	
	public AbstractListComposite(final Composite parent, int style) {
		super(parent, style & ~CUSTOM_STYLES_MASK);
		this.style = style;
	}

	abstract public void setListItemClass(EClass clazz);
	
	abstract public EClass getListItemClass(EObject object, EStructuralFeature feature);
	
	public EClass getListItemClass() {
		EClass eclass = getListItemClass(businessObject, feature);
		if (eclass==null)
			eclass = (EClass) feature.getEType();
		return eclass;
	}
	
	public EClass getDefaultListItemClass(EObject object, EStructuralFeature feature) {
		EClass lic = getListItemClass(object,feature);
		if (lic!=null)
			return lic;
		lic = (EClass) feature.getEType();
		EClass oc = object.eClass();
		if (oc.isInstance(lic))
			return oc;
		return lic;
	}
	
	/**
	 * Create a default ColumnTableProvider if none was set in setTableProvider();
	 * @param object
	 * @param feature
	 * @return
	 */
	public ListCompositeColumnProvider getColumnProvider(EObject object, EStructuralFeature feature) {
		if (columnProvider==null) {
			final EList<EObject> list = (EList<EObject>)object.eGet(feature);
			final EClass listItemClass = getDefaultListItemClass(object, feature);

			boolean canModify;
			if (style==READ_ONLY_STYLE)
				canModify = false;
			else
				canModify = ((style & SHOW_DETAILS)==0 && (style & EDIT_BUTTON)==0)
						|| ((style & SHOW_DETAILS)!=0 && (style & EDIT_BUTTON)!=0);
			columnProvider = new ListCompositeColumnProvider(this, canModify);
			
			// default is to include property name as the first column
			EStructuralFeature nameAttribute = listItemClass.getEStructuralFeature("name"); //$NON-NLS-1$
			EStructuralFeature idAttribute = listItemClass.getEStructuralFeature("id"); //$NON-NLS-1$
			if (nameAttribute!=null)
				columnProvider.add(object, listItemClass, nameAttribute);

			List<EStructuralFeature> added = new ArrayList<EStructuralFeature>();
			for (EAttribute a1 : listItemClass.getEAllAttributes()) {
				if ("anyAttribute".equals(a1.getName())) { //$NON-NLS-1$
					List<EStructuralFeature> anyAttributes = new ArrayList<EStructuralFeature>();
					// are there any actual "anyAttribute" instances we can look at
					// to get the feature names and types from?
					// TODO: enhance the table to dynamically allow creation of new
					// columns which will be added to the "anyAttributes"
					for (EObject instance : list) {
						if (listItemClass.isInstance(instance)) {
							Object o = instance.eGet(a1);
							if (o instanceof BasicFeatureMap) {
								BasicFeatureMap map = (BasicFeatureMap)o;
								for (Entry entry : map) {
									EStructuralFeature f1 = entry.getEStructuralFeature();
									if (f1 instanceof EAttribute && !anyAttributes.contains(f1)) {
										columnProvider.add(object, listItemClass, f1);
										anyAttributes.add(f1);
										added.add(a1);
									} 
								}
							}
						}
					}
				}
				else if (FeatureMap.Entry.class.equals(a1.getEType().getInstanceClass())) {
					// TODO: how do we handle these?
					if (a1 instanceof EAttribute) {
						columnProvider.add(object, listItemClass, a1);
						added.add(a1);
					}
				}
				else {
					
					if (a1!=nameAttribute) {
						if (a1!=idAttribute || getPreferences().getShowIdAttribute()) {
							columnProvider.add(object, listItemClass, a1);
							added.add(a1);
						}
					}
				}
			}
			// FIXME:
			// add the extension attributes and elements
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(listItemClass);
			if (adapter!=null) {
				List<EStructuralFeature> features = adapter.getFeatures();
				for (EStructuralFeature f : features) {
					if (!added.contains(f))
						columnProvider.add(object, listItemClass, f);
				}
			}
			if (columnProvider.getColumns().size()==0) {
				if (idAttribute!=null)
					columnProvider.addRaw(object, idAttribute);
			}
		}
		return columnProvider;
	}

	public ListCompositeColumnProvider getColumnProvider() {
		return columnProvider;
	}
	
	/**
	 * Override this to create your own Details section. This composite will be displayed
	 * in a twistie section whenever the user selects an item from the table. The section
	 * is automatically hidden when the table is collapsed.
	 * @param eClass
	 * @param parent
	 * @param style TODO
	 * 
	 * @return
	 */
	abstract public AbstractDetailComposite createDetailComposite(Class eClass, Composite parent, int style);
	
	public ListCompositeContentProvider getContentProvider(EObject object, EStructuralFeature feature, EList<EObject>list) {
		if (contentProvider==null)
			contentProvider = new ListCompositeContentProvider(this, object, feature, list);
		return contentProvider;
	}
	
	/**
	 * Add a new list item. 
	 * @param object
	 * @param feature
	 * @return the new item to be added to the list, or null if item creation failed
	 */
	abstract protected EObject addListItem(EObject object, EStructuralFeature feature);

	/**
	 * Edit the currently selected list item. 
	 * @param object
	 * @param feature
	 * @return the selected item if edit was successful, null if not
	 */
	abstract protected EObject editListItem(EObject object, EStructuralFeature feature);
	
	/**
	 * Remove a list item (does not delete it from the model.) 
	 * @param object
	 * @param feature
	 * @param item
	 * @return the item that follows the one removed, or null if the removed item was at the bottom of the list
	 */
	abstract protected Object removeListItem(EObject object, EStructuralFeature feature, int index);
	
	/**
	 * Remove an item from the list and delete it from model. 
	 * @param object
	 * @param feature
	 * @param index
	 * @return the item that follows the one deleted, or null if the deleted item was at the bottom of the list
	 */
	abstract protected Object deleteListItem(EObject object, EStructuralFeature feature, int index);
	
	/**
	 * Move the currently selected item up in the list.
	 * @param object
	 * @param feature
	 * @param index
	 * @return the selected item if it was moved, null if the item is already at the top of the list.
	 */
	abstract protected Object moveListItemUp(EObject object, EStructuralFeature feature, int index);

	/**
	 * Move the currently selected item down in the list.
	 * @param object
	 * @param feature
	 * @param index
	 * @return the selected item if it was moved, null if the item is already at the bottom of the list.
	 */
	abstract protected Object moveListItemDown(EObject object, EStructuralFeature feature, int index);

	protected int[] buildIndexMap(EObject object, EStructuralFeature feature) {
		EList<EObject> list = (EList<EObject>)object.eGet(feature);
		EClass listItemClass = getListItemClass(object,feature);
		int[] map = null;
		if (listItemClass!=null) {
			int[] tempMap = new int[list.size()];
			int index = 0;
			int realIndex = 0;
			for (EObject o : list) {
				EClass ec = o.eClass();
				boolean isSubType = ec.getESuperTypes().contains(listItemClass);
				if (ec == listItemClass || isSubType) {
					tempMap[index] = realIndex;
					++index;
				}
				++realIndex;
			}
			map = new int[index];
			for (int i=0; i<index; ++i)
				map[i] = tempMap[i];
		}
		else {
			map = new int[list.size()];
			for (int i=0; i<map.length; ++i)
				map[i] = i;
		}
		return map;
	}
	
	public void setTitle(String title) {
		if (tableSection!=null)
			tableSection.setText(title);
	}
	
	public void bindList(final EObject theobject, final EStructuralFeature thefeature) {
		if (!(theobject.eGet(thefeature) instanceof EList<?>)) {
			return;
		}
//		Class<?> clazz = thefeature.getEType().getInstanceClass();
//		if (!EObject.class.isAssignableFrom(clazz)) {
//			return;
//		}

		setBusinessObject(theobject);
		this.feature = thefeature;
		final EList<EObject> list = (EList<EObject>)businessObject.eGet(feature);
		final EClass listItemClass = getDefaultListItemClass(businessObject,feature);
		String label = getBusinessObjectDelegate().getLabel(listItemClass);
		final String prefName = "list."+listItemClass.getName()+".expanded"; //$NON-NLS-1$ //$NON-NLS-2$
		
		////////////////////////////////////////////////////////////
		// Collect columns to be displayed and build column provider
		////////////////////////////////////////////////////////////
		if (createColumnProvider(businessObject, feature) <= 0) {
			dispose();
			return;
		}

		////////////////////////////////////////////////////////////
		// SashForm contains the table section and a possible
		// details section
		////////////////////////////////////////////////////////////
		if ((style & HIDE_TITLE)==0 || (style & SHOW_DETAILS)!=0) {
			// display title in the table section and/or show a details section
			// SHOW_DETAILS forces drawing of a section title
			sashForm = new SashForm(this, SWT.NONE);
			sashForm.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
			
			tableSection = createListSection(sashForm,label);
			
			if ((style & SHOW_DETAILS)!=0) {
				detailSection = createDetailSection(sashForm, label);
				detailSection.addExpansionListener(new IExpansionListener() {
					
					@Override
					public void expansionStateChanging(ExpansionEvent e) {
						if (!e.getState()) {
							detailSection.setVisible(false);
							if (editAction!=null)
								editAction.setChecked(false);
						}
					}
	
					@Override
					public void expansionStateChanged(ExpansionEvent e) {
						redrawPage();
					}
				});
				
				sashForm.setWeights(new int[] { 50, 50 });
			}					
			else
				sashForm.setWeights(new int[] { 100 });
		}
		else {
			tableSection = createListSection(sashForm,label);
		}
		
		tableSection.addExpansionListener(new IExpansionListener() {
			
			@Override
			public void expansionStateChanging(ExpansionEvent e) {
				if (!e.getState() && detailSection!=null) {
					detailSection.setVisible(false);
				}
			}

			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				preferenceStore.setValue(prefName, e.getState());
				redrawPage();
			}
		});
		
		////////////////////////////////////////////////////////////
		// Create table viewer and cell editors
		////////////////////////////////////////////////////////////
		tableViewer = new TableViewer(table);
		columnProvider.createTableLayout(table);
		columnProvider.setTableViewer(tableViewer);
		
		tableViewer.setLabelProvider(columnProvider);
		tableViewer.setCellModifier(columnProvider);
		tableViewer.setContentProvider(getContentProvider(businessObject,feature,list));
		tableViewer.setColumnProperties(columnProvider.getColumnProperties());
		tableViewer.setCellEditors(columnProvider.createCellEditors(table));

		////////////////////////////////////////////////////////////
		// Create handlers
		////////////////////////////////////////////////////////////
		table.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				Point p = new Point(e.x,e.y);
				TableItem item = table.getItem(p);
				if (item==null) {
					if (addAction!=null && addAction.isEnabled()) {
						addAction.run();
					}
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		if ((style & SHOW_DETAILS)!=0) { // && (style & EDIT_BUTTON)==0) {
			tableViewer.addDoubleClickListener( new IDoubleClickListener() {
				@Override
				public void doubleClick(DoubleClickEvent event) {
					showDetails(true);
				}
			});
		}
		tableViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				boolean enable = !event.getSelection().isEmpty();
				if ((style & SHOW_DETAILS)!=0) {
					if (detailSection!=null && detailSection.isVisible())
						showDetails(true);
//					else if ((style & EDIT_BUTTON)==0)
//						showDetails(true);
				}
				if (removeAction!=null)
					removeAction.setEnabled(enable);
				if (editAction!=null)
					editAction.setEnabled(enable);
				if (upAction!=null && downAction!=null) {
					int i = table.getSelectionIndex();
					if (i>0)
						upAction.setEnabled(true);
					else
						upAction.setEnabled(false);
					if (i>=0 && i<table.getItemCount()-1)
						downAction.setEnabled(true);
					else
						downAction.setEnabled(false);
				}
			}
		});
		
		tableViewer.setInput(list);
		
		// a TableCursor allows navigation of the table with keys
		TableCursor.create(table, tableViewer);
		redrawPage();
		
		boolean expanded = preferenceStore.getBoolean(prefName);
		if (expanded && tableSection!=null)
			tableSection.setExpanded(true);
	}
	
	public void setBusinessObject(EObject object) {
		super.setBusinessObject(object);
		showDetails(false);
	}
	
	private void showDetails(boolean enable) {
		if (detailSection==null)
			return;
		
		boolean useDialog = preferenceStore.getBoolean(Bpmn2Preferences.PREF_USE_POPUP_DIALOG_FOR_LISTS);
		
		if (useDialog) {
			// Use a popup dialog instead of the old "sliding detail panel"
			if (enable) {
				IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
				if (selection.getFirstElement() instanceof EObject) {
					EObject o = (EObject)selection.getFirstElement();
					ObjectEditingDialog dialog = new ObjectEditingDialog(getDiagramEditor(), o);
					// Our clients can provide their own Detail panels, so we'll pass along a IPropertiesCompositeFactory
					// to the ObjectEditingDialog and delegate construction of the Detail Composite to our clients.
					IPropertiesCompositeFactory factory = new IPropertiesCompositeFactory() {
						@Override
						public AbstractDetailComposite createDetailComposite(Class eClass, AbstractBpmn2PropertySection section) {
							return null;
						}
						@Override
						public AbstractDetailComposite createDetailComposite(Class eClass, Composite parent, int style) {
							// this is the only one that's required!
							return AbstractListComposite.this.createDetailComposite(eClass, parent, style);
						}
						@Override
						public AbstractListComposite createListComposite(Class eClass, AbstractBpmn2PropertySection section) {
							return null;
						}
						@Override
						public AbstractListComposite createListComposite(Class eClass, Composite parent, int style) {
							return null;
						}
						@Override
						public AbstractDialogComposite createDialogComposite(EClass eClass, Composite parent, int style) {
							return null;
						}
					};
					
					dialog.setCompositeFactory(factory);
					dialog.open();
				}
			}
		}
		else {
			if (enable) {
	
				IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
				if (selection.getFirstElement() instanceof EObject) {
					EObject o = (EObject)selection.getFirstElement();
					
					if (detailComposite!=null)
						detailComposite.dispose();
					detailComposite = createDetailComposite(o.eClass().getInstanceClass(), detailSection, 0);
					detailSection.setClient(detailComposite);
					toolkit.adapt(detailComposite);
	
					String label = getBusinessObjectDelegate().getLabel(o);
					detailSection.setText(label+Messages.AbstractListComposite_Details);
					((AbstractDetailComposite)detailComposite).setBusinessObject(o);
					enable = !detailComposite.isEmpty();
					detailSection.setExpanded(enable);
					if (!enable && editAction!=null)
						editAction.setEnabled(enable);

			    	Notification n = new ENotificationImpl((InternalEObject) o, 0, null, null, null, false);
					this.validate(n);
				}
			}
			
			detailSection.setVisible(enable);
			detailSection.setExpanded(enable);
			if (editAction!=null)
				editAction.setChecked(enable);

			sashForm.setWeights(new int[] { 40, 60 });
			Control parent = getParent();
			while (parent!=null) {
				if (parent instanceof AbstractListComposite) {
					((AbstractListComposite)parent).sashForm.setWeights(new int[] { 30, 70 });
				}
				parent = parent.getParent();
			}

			final EList<EObject> list = (EList<EObject>)businessObject.eGet(feature);
			tableViewer.setInput(list);

			redrawPage();
		}
	}
	
	/**
	 * @param theobject
	 * @param thefeature
	 * @return
	 */
	protected int createColumnProvider(EObject theobject, EStructuralFeature thefeature) {
		if (columnProvider==null) {
			EClass listItemClass = getDefaultListItemClass(theobject,thefeature);
			columnProvider = getColumnProvider(theobject, thefeature);
			// remove disabled columns
			List<TableColumn> removed = new ArrayList<TableColumn>();
			for (TableColumn tc : (List<TableColumn>)columnProvider.getColumns()) {
				if (tc.feature!=null) {
					if (!"id".equals(tc.feature.getName())) { //$NON-NLS-1$
						if (!isModelObjectEnabled(listItemClass, tc.feature)) {
							removed.add(tc);
						}
					}
				}
			}
			if (removed.size()>0) {
				for (TableColumn tc : removed)
					columnProvider.remove(tc);
			}
		}
		return columnProvider.getColumns().size();
	}

	private Section createListSection(Composite parent, String label) {
		final Section section = toolkit.createSection(parent,
				ExpandableComposite.TWISTIE |
				ExpandableComposite.COMPACT |
				ExpandableComposite.TITLE_BAR);
		section.setText(label+Messages.AbstractListComposite_List);

		final Composite tableComposite = toolkit.createComposite(section, SWT.NONE);
		section.setClient(tableComposite);
		tableComposite.setLayout(new GridLayout(1, false));
		
		table = toolkit.createTable(tableComposite, SWT.FULL_SELECTION | SWT.V_SCROLL);
		final GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		gridData.heightHint = 100;
		gridData.widthHint = 50;
		table.setLayoutData(gridData);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// make the table resizing behave a little better:
		// adjust table columns so they are all equal width,
		// grow and shrink table height based on number of rows
		tableComposite.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = tableComposite.getClientArea();
				ScrollBar vBar = table.getVerticalBar();
				int vBarSize = vBar.isVisible() ? vBar.getSize().x : 0;
				ScrollBar hBar = table.getHorizontalBar();
				int hBarSize = hBar.isVisible() ? hBar.getSize().y : 0;
				Rectangle trim = table.computeTrim(0,0,0,0);
				int width = area.width - trim.width - vBarSize;
				int remainingWidth = width;
				int columnCount = table.getColumnCount();
				// adjust number of visible rows
				int rowCount = table.getItemCount();
				if (rowCount<2)
					rowCount = 2;
				if (rowCount>8)
					rowCount = 8;
				int height = trim.height + table.getHeaderHeight() + hBarSize + rowCount * table.getItemHeight();
				gridData.heightHint = height;
				gridData.widthHint = 50;
				table.setSize(area.width, area.height);
				for (int index=0; index<columnCount; ++index) {
					org.eclipse.swt.widgets.TableColumn tc = table.getColumn(index);
					if (index==columnCount-1)
						tc.setWidth(remainingWidth + 7);
					else
						tc.setWidth(width/columnCount);
					remainingWidth -= tc.getWidth();
				}
			}
		});
		
	    tableToolBarManager = new ToolBarManager(SWT.FLAT);
	    ToolBar toolbar = tableToolBarManager.createControl(section);

	    ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/20/add.png"); //$NON-NLS-1$
		if ((style & ADD_BUTTON)!=0) {
			addAction = new Action(Messages.AbstractListComposite_Add, id) {
				@Override
				public void run() {
					super.run();
					editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
						@Override
						protected void doExecute() {
							EObject newItem = addListItem(businessObject,feature);
							if (newItem!=null) {
								final EList<EObject> list = (EList<EObject>)businessObject.eGet(feature);
								tableViewer.setInput(list);
								tableViewer.setSelection(new StructuredSelection(newItem));
								showDetails(true);
							}
						}
					});
				}
			};
			addAction.setId("add"); //$NON-NLS-1$
			tableToolBarManager.add(addAction);
		}
		
		if ((style & DELETE_BUTTON)!=0 || (style & REMOVE_BUTTON)!=0) {
			
			if ((style & DELETE_BUTTON)!=0) {
				removeIsDelete = true;
				id = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/20/delete.png"); //$NON-NLS-1$
			}
			else {
				id = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/20/remove.png"); //$NON-NLS-1$
			}
			removeAction = new Action(removeIsDelete ? Messages.AbstractListComposite_Delete : Messages.AbstractListComposite_Remove, id) {
				@Override
				public void run() {
					super.run();
					showDetails(false);
					editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
						@Override
						protected void doExecute() {
                            final EList<EObject> list = (EList<EObject>)businessObject.eGet(feature);
                            int i = tableViewer.getTable().getSelectionIndex();
							Object item;
							if (removeIsDelete)
								item = deleteListItem(businessObject,feature,i);
							else
								item = removeListItem(businessObject,feature,i);
							
							tableViewer.setInput(list);
							if (item!=null) {
								if (i>=list.size())
									i = list.size() - 1;
								if (i>=0)
									tableViewer.setSelection(new StructuredSelection(item));
							}
							
							Display.getDefault().asyncExec( new Runnable() {
								@Override
								public void run() {
									showDetails(false);
								}
							});
						}
					});
				}
			};
			removeAction.setId("remove"); //$NON-NLS-1$
			tableToolBarManager.add(removeAction);
			removeAction.setEnabled(false);
		}
		
		if ((style & MOVE_BUTTONS)!=0) {
			id = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/20/up.png"); //$NON-NLS-1$
			upAction = new Action(Messages.AbstractListComposite_Move_Up, id) {
				@Override
				public void run() {
					super.run();
					editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
						@Override
						protected void doExecute() {
                            final EList<EObject> list = (EList<EObject>)businessObject.eGet(feature);
                            int i = tableViewer.getTable().getSelectionIndex();
							Object item = moveListItemUp(businessObject,feature,i);
							tableViewer.setInput(list);
							tableViewer.setSelection(new StructuredSelection(item));
						}
					});
				}
			};
			upAction.setId("up"); //$NON-NLS-1$
			tableToolBarManager.add(upAction);
			upAction.setEnabled(false);
	
			id = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/20/down.png"); //$NON-NLS-1$
			downAction = new Action(Messages.AbstractListComposite_Move_Down, id) {
				@Override
				public void run() {
					super.run();
					editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
						@Override
						protected void doExecute() {
                            final EList<EObject> list = (EList<EObject>)businessObject.eGet(feature);
                            int i = tableViewer.getTable().getSelectionIndex();
							Object item = moveListItemDown(businessObject,feature,i);
							tableViewer.setInput(list);
							tableViewer.setSelection(new StructuredSelection(item));
						}
					});
				}
			};
			downAction.setId("down"); //$NON-NLS-1$
			tableToolBarManager.add(downAction);
			downAction.setEnabled(false);
		}
		
		if ((style & EDIT_BUTTON)!=0) {
			id = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/20/edit.png"); //$NON-NLS-1$
			editAction = new Action(Messages.AbstractListComposite_Edit, id) {
				@Override
				public void run() {
					super.run();
					if ((style & SHOW_DETAILS)!=0) {
						if (!editAction.isChecked()) {
							showDetails(true);
						}
						else {
							showDetails(false);
						}
					}
					else {
						editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
							@Override
							protected void doExecute() {
								EObject newItem = editListItem(businessObject,feature);
								if (newItem!=null) {
									final EList<EObject> list = (EList<EObject>)businessObject.eGet(feature);
									tableViewer.setInput(list);
									tableViewer.setSelection(new StructuredSelection(newItem));
								}
							}
						});
					}
				}
			};
			editAction.setId("edit"); //$NON-NLS-1$
			tableToolBarManager.add(editAction);
			editAction.setEnabled(false);
		}
		
		tableToolBarManager.update(true);
	    section.setTextClient(toolbar);
	    
	    // hook a resource change listener to this Table Control
	    table.setData(IConstants.NOTIFY_CHANGE_LISTENER_KEY,this);
	    
	    return section;
	}
	
	protected Section createDetailSection(Composite parent, String label) {
		Section section = toolkit.createSection(parent,
				ExpandableComposite.EXPANDED |
				ExpandableComposite.TITLE_BAR);
		section.setText(label+Messages.AbstractListComposite_Details);
		section.setVisible(false);

	    detailToolBarManager = new ToolBarManager(SWT.FLAT);
	    ToolBar toolbar = detailToolBarManager.createControl(section);
	    ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/20/close.png"); //$NON-NLS-1$
	    detailToolBarManager.add( new Action(Messages.AbstractListComposite_Close, id) {
			@Override
			public void run() {
				super.run();
				showDetails(false);
			}
	    });
	    detailToolBarManager.update(true);
	    section.setTextClient(toolbar);
	    return section;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notifyChanged(Notification notification) {
		EList<EObject> table = (EList<EObject>)businessObject.eGet(feature);
		Object n = notification.getNotifier();
		// if the table contains the notifier, or if this notification is coming from
		// AbstractDetailComposite.refresh(), then set the new input into the table
		if (table.contains(n) || notification.getEventType() == -1) {
			tableViewer.setInput(table);
			tableViewer.refresh(true);
			return; // quick exit before the exhaustive search that follows
		}
		if (n instanceof EObject) {
			HashSet<Object> visited = new HashSet<Object>(); 
			if (refreshIfNeededRecursive((EObject)n, table, visited))
				return;
		}
	}
	
	public ToolBarManager getToolBarManager() {
		return tableToolBarManager;
	}
	
	@SuppressWarnings("rawtypes")
	private boolean refreshIfNeededRecursive(EObject value, EList<EObject> table, HashSet<Object> visited) {
		for (EStructuralFeature f : value.eClass().getEAllStructuralFeatures()) {
			try {
				Object v = value.eGet(f);
				if (!visited.contains(v)) {
					visited.add(v);
					if (v instanceof List) {
						if (!((List)v).isEmpty())
							if (refreshIfNeededRecursive((List)v, table, visited))
								return true;
					}
					else if (v instanceof EObject) {
						if (refreshIfNeeded((EObject)v, table))
							return true;
					}
				}
			}
			catch (Exception e) {
				// some getters may throw exceptions - ignore those
			}
		}
		return refreshIfNeeded(value, table);
	}

	static int count = 0;
	@SuppressWarnings("rawtypes")
	private boolean refreshIfNeededRecursive(List list, EList<EObject> table, HashSet<Object> visited) {
		for (Object v : list) {
			if (!visited.contains(v)) {
				visited.add(v);
				if (v instanceof List) {
					if (refreshIfNeededRecursive((List)v, table, visited))
						return true;
				}
				else if (v instanceof EObject) {
					if (refreshIfNeededRecursive((EObject)v, table, visited))
						return true;
				}
			}
		}
		return false;
	}

	private boolean refreshIfNeeded(EObject value, EList<EObject> table) {
		if (table.contains(value) && tableViewer!=null) {
			tableViewer.setInput(table);
			return true;
		}
		return false;
	}
	
	public void setVisible (boolean visible) {
		super.setVisible(visible);
		Object data = getLayoutData();
		if (data instanceof GridData) {
			((GridData)data).exclude = !visible;
		}
	}
}
