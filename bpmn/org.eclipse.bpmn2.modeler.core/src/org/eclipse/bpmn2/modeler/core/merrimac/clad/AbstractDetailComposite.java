/******************************************************************************* 
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 * IBM Corporation - http://dev.eclipse.org/viewcvs/viewvc.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet19.java
 *
 * @author Innar Made
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.merrimac.clad;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.modeler.core.EDataTypeConversionFactory;
import org.eclipse.bpmn2.modeler.core.EditControlProvider;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.merrimac.IConstants;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.AbstractObjectEditingDialog;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.BooleanObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ComboObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.DelegatingObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.FeatureListObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.FloatObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.IntObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.ReadonlyTextObjectEditor;
import org.eclipse.bpmn2.modeler.core.merrimac.dialogs.TextObjectEditor;
import org.eclipse.bpmn2.modeler.core.model.ModelDecorator;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.transaction.ResourceSetListener;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

/**
 * This is a base class for all Property Sheet Sections. The Composite is used to render
 * the "structural features" for an EObject, presumably a subclass of BaseElement or some
 * other BPMN2 metamodel object.
 * 
 * The Composite uses a GridLayout with 3 columns: the leftmost column is designated for a
 * label; the two rightmost columns are designated for input or editing widgets, depending
 * on the type of structural feature being rendered.
 * 
 * EAttribute and EReference type of structural features are collected and rendered within
 * a non-collapsible FormSection given the title "Attributes". List features are rendered in
 * collapsible AbstractBpmn2TableComposite table widgets. 
 * 
 * Subclasses must implement the abstract createBindings() method to construct their editing
 * widgets. These widgets are torn down and reconstructed when the editor selection changes.
 */
public abstract class AbstractDetailComposite extends ListAndDetailCompositeBase implements ResourceSetListener {

	public final String EMPTY_LABEL_PROPERTY = "empty.label"; //$NON-NLS-1$
	protected Section attributesSection = null;
	protected Composite attributesComposite = null;
	protected Font descriptionFont = null;
	protected AbstractPropertiesProvider propertiesProvider = null;
	protected StyledText descriptionText = null;
	/**
	 * Constructor for embedding this composite in an AbstractBpmn2PropertySection.
	 * This is the "normal" method of creating this composite.
	 * 
	 * @param section
	 */
	public AbstractDetailComposite(AbstractBpmn2PropertySection section) {
		super(section);
	}
	
	/**
	 * Constructor for embedding this composite in a nested property section,
	 * e.g. the AdvancedPropertySection uses this.
	 * 
	 * @param parent
	 * @param style
	 */
	public AbstractDetailComposite(Composite parent, int style) {
		super(parent,style);
	}

	/**
	 * This method is called by the property sheet tab section to update the UI
	 * after a new selection is made. Updating consists of a full teardown of the
	 * widget tree and then rebuilding it for the newly selected EObject. Since the
	 * same composite MAY be used for different EObject types, the widgets may businessObject
	 * completely different, hence the need for teardown and setup for each new selection.
	 * 
	 * @param object
	 */
	public void setBusinessObject(final EObject object) {
		super.setBusinessObject(object);
		
		cleanBindings();
		if (businessObject != null) {
			createBindings(businessObject);
			if (isEmpty())
				createEmptyLabel(businessObject);
			redrawPage();
		}
	}

	/**
	 * Tear down all child widgets
	 */
	protected void cleanBindings() {
		ModelUtil.disposeChildWidgets(this);
	}
	
	protected boolean isEmpty() {
		Control kids[] = getChildren();
		if (kids.length==0) {
			return true;
		}
		else if (kids.length==1 && EMPTY_LABEL_PROPERTY.equals(kids[0].getData())) {
			return true;
		}
		return false;
	}
	
	protected void createEmptyLabel(EObject be) {
		boolean doit = false;
		Control kids[] = getChildren();
		if (kids.length==0) {
			doit = true;
		}
		else if (kids.length==1 && EMPTY_LABEL_PROPERTY.equals(kids[0].getData())) {
			doit = true;
		}
		if (doit) {
			String text = NLS.bind(
				Messages.AbstractDetailComposite_Empty_Property_Sheet,
				be.eClass().getName());
			
			String elements = ""; //$NON-NLS-1$
			String props[] = getPropertiesProvider().getProperties();
			for (String s : props) {
				elements += "    " + s + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			text += NLS.bind(Messages.AbstractDetailComposite_Empty_Property_Sheet_Elements,elements);

			Label label = createLabel(this, text);
			label.setData(EMPTY_LABEL_PROPERTY);
		}
	}

	/**
	 * Returns the composite that is used to contain all EAttributes for the
	 * current selection. The default behavior is to construct a non-collapsible
	 * Form Section and create a Composite within that section.
	 * 
	 * @return the Composite root for the current selection's EAttributes
	 */
	public Composite getAttributesParent() {
		if (getParent() instanceof Section)
			return this;
		
		if (attributesSection==null || attributesSection.isDisposed()) {

			attributesSection = createSection(this, Messages.AbstractDetailComposite_Attributes, true);
			
			attributesSection.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 3, 1));
			attributesComposite = toolkit.createComposite(attributesSection);
			attributesSection.setClient(attributesComposite);
			attributesComposite.setLayout(new GridLayout(3,false));
		}
		return attributesComposite;
	}
	
	/**
	 * Set the title of the Attributes section
	 * @param title
	 */
	public void setTitle(String title) {
		getAttributesParent();
		if (attributesSection!=null)
			attributesSection.setText(title);
	}

	/**
	 * This method is called when setEObject is called and this should recreate
	 * all bindings and widgets for the current selection.
	 *  
	 * @param be the business object linked to the currently selected EditPart
	 * through the Graphiti DiagramEditor framework.
	 */
	public abstract void createBindings(EObject be);
	
	/**
	 * Convenience method to look up an EObject's feature by name.
	 * 
	 * @param object the EObject
	 * @param name the feature name string
	 * @return the EStructuralFeature or null if the feature does not exist for this object
	 */
	protected EStructuralFeature getFeature(EObject object, String name) {
		return getBusinessObjectDelegate().getFeature(object, name);
	}

	/**
	 * Convenience method to check if a feature is an EAttribute
	 * @param feature
	 * @return
	 */
	protected boolean isAttribute(EObject object, EStructuralFeature feature) {
		return getBusinessObjectDelegate().isAttribute(object, feature);
	}
	
	/**
	 * Convenience method to check if a feature is a containment EList
	 * @param object
	 * @param feature
	 * @return
	 */
	protected boolean isList(EObject object, EStructuralFeature feature) {
		return getBusinessObjectDelegate().isList(object, feature);
	}

	/**
	 * Convenience method to check if a feature is an EReference
	 * @param feature
	 * @return
	 */
	protected boolean isReference(EObject object, EStructuralFeature feature) {
		if (feature != null) {
			List<EStructuralFeature> anyAttributes = ModelDecorator.getAnyAttributes(object);
			for (EStructuralFeature f : anyAttributes) {
				if (f.getName().equals(feature.getName())) {
					if (f instanceof EReference)
						return true;
					break;
				}
			}
		}

		return (feature instanceof EReference);
	}

	protected Label createLabel(Composite parent, String name) {
		Label label = toolkit.createLabel(parent, name);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		return label;
	}

	protected Text createText(Composite parent, String label, String value) {
		createLabel(parent,label);
		Text text = getToolkit().createText(parent, "", style | SWT.BORDER); //$NON-NLS-1$
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		text.setLayoutData(data);
		text.setText(value==null ? "" : value); //$NON-NLS-1$
		text.setEditable(false);
		text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		text.setForeground(text.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		text.setData(AbstractObjectEditingDialog.DO_NOT_ADAPT, Boolean.TRUE);
		return text;
	}
	
	public Font getDescriptionFont() {
		if (descriptionFont==null) {
			Display display = Display.getCurrent();
		    FontData data = display.getSystemFont().getFontData()[0];
		    descriptionFont = new Font(display, data.getName(), data.getHeight() + 1, SWT.NONE);
		}
		return descriptionFont;
	}

	protected StyledText createDescription(Composite parent, String description) {
		Display display = Display.getCurrent();
		descriptionText = new StyledText(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
		descriptionText.setText(description);

	    descriptionText.setFont(getDescriptionFont());
		
		descriptionText.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		descriptionText.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		descriptionText.setData(AbstractObjectEditingDialog.DO_NOT_ADAPT , Boolean.TRUE);
		
		GridData d = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
		d.horizontalIndent = 4;
		d.verticalIndent = 4;
		d.heightHint = (int)(5.5 * getDescriptionFont().getFontData()[0].getHeight());
		d.widthHint = 100;
		descriptionText.setLayoutData(d);

		return descriptionText;
	}

	protected Section createSection(Composite parent, final String title, boolean expanded) {
		Section section = toolkit.createSection(parent,
				ExpandableComposite.TWISTIE |
				ExpandableComposite.EXPANDED |
				ExpandableComposite.TITLE_BAR);
		section.setText(title);

		// TODO: add a Close button to allow user to hide sections s/he is not interested in.
//	    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
//	    ToolBar toolbar = toolBarManager.createControl(section);
//	    ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/20/close.png");
//	    toolBarManager.add( new Action("Close", id) {
//			@Override
//			public void run() {
//				super.run();
//			}
//	    });
//	    toolBarManager.update(true);
//	    section.setTextClient(toolbar);

		final String prefKey = (getBusinessObject()==null) ?
				null :
				"section."+getBusinessObject().eClass().getName()+"."+title+"."+".expanded"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		if (prefKey!=null && preferenceStore.contains(prefKey))
			expanded = preferenceStore.getBoolean(prefKey);
		section.setExpanded(expanded);
		
		section.addExpansionListener(new IExpansionListener() {
			@Override
			public void expansionStateChanging(ExpansionEvent e) {
			}

			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				if (prefKey!=null) {
					preferenceStore.setValue(prefKey, e.getState());
				}
			}
		});
		
		return section;
	}

	protected Composite createSectionComposite(Composite parent, String title) {
		Section section = createSection(parent, title, false);
		section.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 3, 1));
		Composite composite = toolkit.createComposite(section);
		section.setClient(composite);
		composite.setLayout(new GridLayout(3,false));
		return composite;
	}
	
	protected Section createSubSection(Composite parent, String title) {
		Section section = toolkit.createSection(parent,
				ExpandableComposite.EXPANDED |
				ExpandableComposite.TITLE_BAR);
		section.setText(title);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,true, 3,1));
		return section;
	}

	// TODO: clean this mess up! Too many variations of bindAttribute()
	
	protected void bindAttribute(EObject object, String name) {
		bindAttribute(null,object,name, null);
	}

	protected void bindAttribute(EObject object, String name, String label) {
		bindAttribute(null,object,name,label);
	}
	
	protected void bindAttribute(Composite parent, EObject object, String name) {
		bindAttribute(parent,object,name,null);
	}
	
	protected void bindAttribute(Composite parent, EObject object, String name, String label) {
		EStructuralFeature feature = getFeature(object,name);
		if (isAttribute(object,feature)) {
			if (label==null)
				label = getBusinessObjectDelegate().getLabel(object, feature);
			bindAttribute(parent, object,(EAttribute)feature,label);
		}
	}

	protected void bindAttribute(EObject object, EAttribute attribute) {
		bindAttribute(null,object,attribute,getBusinessObjectDelegate().getLabel(object, attribute));
	}
	
	protected void bindAttribute(EObject object, EAttribute attribute, String label) {
		bindAttribute(null,object,attribute,label);
	}
	
	protected void bindAttribute(Composite parent, EObject object, EAttribute attribute) {
		bindAttribute(parent,object,attribute,getBusinessObjectDelegate().getLabel(object, attribute));
	}
	
	protected void bindAttribute(Composite parent, EObject object, EAttribute attribute, String label) {

		if (isModelObjectEnabled(object.eClass(), attribute)) {

			if (parent==null)
				parent = getAttributesParent();
			
			if (label==null)
				label = getBusinessObjectDelegate().getLabel(object, attribute);
			
			EClassifier eTypeClassifier = attribute.getEType();
			Class eTypeClass = eTypeClassifier.getInstanceClass();
			if (getBusinessObjectDelegate().isMultiChoice(object, attribute)) {
				ObjectEditor editor = new ComboObjectEditor(this,object,attribute);
				editor.createControl(parent,label);
			}
			else if (String.class.equals(eTypeClass)) {
				ObjectEditor editor = new TextObjectEditor(this,object,attribute);
				editor.createControl(parent,label);
			} else if (Boolean.class.equals(eTypeClass) ||
					boolean.class.equals(eTypeClass)
			) {
				ObjectEditor editor = new BooleanObjectEditor(this,object,attribute);
				editor.createControl(parent,label);
			} else if (int.class.equals(eTypeClass) ||
					Short.class.equals(eTypeClass) ||
					Integer.class.equals(eTypeClass) ||
					BigInteger.class.equals(eTypeClass) ||
					short.class.equals(eTypeClass) ||
					int.class.equals(eTypeClass) ||
					long.class.equals(eTypeClass)
			) {
				ObjectEditor editor = new IntObjectEditor(this,object,attribute);
				editor.createControl(parent,label);
			} else if (int.class.equals(eTypeClass) ||
					Float.class.equals(eTypeClass) ||
					Double.class.equals(eTypeClass) ||
					float.class.equals(eTypeClass) ||
					double.class.equals(eTypeClass)
			) {
				ObjectEditor editor = new FloatObjectEditor(this,object,attribute);
				editor.createControl(parent,label);
			} else if ("anyAttribute".equals(attribute.getName()) || //$NON-NLS-1$
					object.eGet(attribute) instanceof FeatureMap) {
				List<EStructuralFeature> features = new ArrayList<EStructuralFeature>();
				List<Entry> basicList = ((BasicFeatureMap) object.eGet(attribute)).basicList();
				for (Entry entry : basicList) {
					features.add(entry.getEStructuralFeature());
				}
				
				// add the Extension Attributes
				ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object);
				if (adapter!=null) {
					List<EStructuralFeature> extensionFeatures = adapter.getExtensionFeatures();
					for (EStructuralFeature feature : extensionFeatures) {
						if (!features.contains(feature)) {
							features.add(feature);
						}
					}
				}
				for (EStructuralFeature feature : features) {
					// ignore the ShapeStyle extension - this is already handled in the Description tab
					if (!ShapeStyle.isStyleFeature(feature)) {
						if (isAttribute(object, feature)) {
							bindAttribute(parent,object,(EAttribute)feature);
						}
						else if (isReference(object, feature)) {
							bindReference(parent,object,(EReference)feature);
						}
						else if (isList(object,feature)) {
							bindList(object,feature);
						}
					}
				}

			}
			else if (eTypeClassifier instanceof EDataType) {
				ConversionDelegate cd = EDataTypeConversionFactory.INSTANCE.createConversionDelegate(
						(EDataType)eTypeClassifier);
				if (cd instanceof EditControlProvider) {
					ObjectEditor editor = new DelegatingObjectEditor(this,object,attribute, (EditControlProvider)cd);
					editor.createControl(parent,label);
				}
			}
		}
	}
	
	protected void bindReference(Composite parent, EObject object, String name) {
		EStructuralFeature feature = getFeature(object,name);
		if (isReference(object,feature)) {
			bindReference(parent, object,(EReference)feature);
		}
	}

	protected void bindReference(EObject object, String name) {
		EStructuralFeature feature = getFeature(object,name);
		if (isReference(object,feature)) {
			bindReference(object,(EReference)feature);
		}
	}
	
	protected void bindReference(EObject object, EReference reference) {
		bindReference(null, object, reference);
	}
	
	protected void bindReference(Composite parent, EObject object, EReference reference) {
		if (isModelObjectEnabled(object.eClass(), reference)) {
			if (parent==null)
				parent = getAttributesParent();
			
			String displayName = getBusinessObjectDelegate().getLabel(object, reference);

			ObjectEditor editor = null;
			if (getBusinessObjectDelegate().isMultiChoice(object, reference)) {
				if (reference.isMany()) {
					editor = new FeatureListObjectEditor(this,object,reference);
				} else {
					editor = new ComboObjectEditor(this,object,reference);
				}
			}
			else if (getBusinessObjectDelegate().isContainmentFeature(object, reference)) {
				EClass eClass = (EClass) reference.getEType();
				// FIXME: how do we deal with lists of these things?
				Object v = getBusinessObjectDelegate().getValue(object,reference);
				EObject value = (EObject)v;
				if (value==null) {
					value = getBusinessObjectDelegate().createFeature(object,reference);
					InsertionAdapter.add(object, reference, value);
				}
				AbstractDetailComposite detailComposite = PropertiesCompositeFactory.INSTANCE.createDetailComposite(eClass.getInstanceClass(), this, SWT.NONE);
				detailComposite.setBusinessObject(value);
				detailComposite.setTitle(getBusinessObjectDelegate().getLabel(value));
			}
			else if (getBusinessObjectDelegate().canCreateNew(object, reference)) {
				editor = new ReadonlyTextObjectEditor(this,object,reference);
			}
			else {
				editor = new TextObjectEditor(this,object,reference);
			}
			if (editor!=null)
				editor.createControl(parent,displayName);
		}
	}

	protected AbstractListComposite bindList(EObject object, String name) {
		EStructuralFeature feature = getFeature(object,name);
		if (isList(object,feature)) {
			return bindList(object,feature);
		}
		return null;
	}
	
	protected AbstractListComposite bindList(EObject object, EStructuralFeature feature) {
		return bindList(object,feature,null);
	}
	
	protected AbstractListComposite bindList(EObject object, EStructuralFeature feature, EClass listItemClass) {
		AbstractListComposite tableComposite = null;
		if (isModelObjectEnabled(object.eClass(), feature) || isModelObjectEnabled(listItemClass)) {
			Class clazz = (listItemClass!=null) ?
					listItemClass.getInstanceClass() :
					feature.getEType().getInstanceClass();
			if (propertySection!=null) {
				tableComposite = PropertiesCompositeFactory.INSTANCE.createListComposite(clazz, propertySection);
			}
			else {
				tableComposite = PropertiesCompositeFactory.INSTANCE.createListComposite(clazz, this, AbstractListComposite.DEFAULT_STYLE);
			}
			
			tableComposite.setListItemClass(listItemClass);
			tableComposite.bindList(object, feature);
		}
		return tableComposite;
	}
	
	protected AbstractListComposite bindList(Composite parent, EObject object, EStructuralFeature feature, EClass listItemClass) {

		AbstractListComposite tableComposite = null;
		if (isModelObjectEnabled(object.eClass(), feature) || isModelObjectEnabled(listItemClass)) {
			Class clazz = (listItemClass!=null) ?
					listItemClass.getInstanceClass() :
					feature.getEType().getInstanceClass();
			tableComposite = PropertiesCompositeFactory.INSTANCE.createListComposite(clazz, parent, AbstractListComposite.DEFAULT_STYLE);
			tableComposite.setListItemClass(listItemClass);
			tableComposite.bindList(object, feature);
		}
		return tableComposite;
	}

	public void refresh() {
		Display.getDefault().asyncExec( new Runnable() {
			public void run() {
				List<Control>kids = new ArrayList<Control>();
				Composite parent = AbstractDetailComposite.this;
				try {
					AbstractBpmn2PropertySection section = AbstractDetailComposite.this.getPropertySection();
					if (section!=null && section.getTabbedPropertySheetPage()!=null) {
						parent = (Composite)section.getTabbedPropertySheetPage().getControl();
					}
				}
				catch (Exception e) {
				}
				// Send a notification to all listeners about this refresh event.
				// This will cause all children to be refreshed when a property tab switch happens.
				Notification n = new ENotificationImpl(null, -1, -1, 0, 0);
				getAllChildWidgets(parent, kids);
				for (Control c : kids) {
					INotifyChangedListener listener = (INotifyChangedListener)c.getData(
							IConstants.NOTIFY_CHANGE_LISTENER_KEY);
					if (listener!=null) {
						listener.notifyChanged(n);
					}
				}
			}
		});
	}
	
	public void setPropertiesProvider(AbstractPropertiesProvider provider) {
		propertiesProvider = provider;
	}
	
	public AbstractPropertiesProvider getPropertiesProvider() {
		return getPropertiesProvider(businessObject);
	}
	
	public AbstractPropertiesProvider getPropertiesProvider(final EObject object) {
		if (propertiesProvider==null) {
			return new AbstractPropertiesProvider(object) {
				public String[] getProperties() {
					List<String> list = new ArrayList<String>();
					EClass c = object.eClass();
					// add name and id attributes first (if any)
					if (c.getEStructuralFeature("name")!=null) //$NON-NLS-1$
						list.add("name"); //$NON-NLS-1$
					if (c.getEStructuralFeature("id")!=null) //$NON-NLS-1$
						list.add("id"); //$NON-NLS-1$
					for (EStructuralFeature attribute : object.eClass().getEStructuralFeatures()) {
						if (!list.contains(attribute.getName()))
							list.add(attribute.getName());
					}
					// add the extension attributes and elements
					for (String f : getExtensionProperties(object)) {
						if (!list.contains(f))
							list.add(f);
					}
					String a[] = new String[list.size()];
					list.toArray(a);
					return a;
				}
			};
		}
		return propertiesProvider;
	}
	
	public List<String> getExtensionProperties(EObject object) {
		// Get the list of extension attributes and elements
		List<String> list = new ArrayList<String>();
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object);
		if (adapter!=null) {
			List<EStructuralFeature> features = adapter.getExtensionFeatures();
			for (EStructuralFeature f : features) {
				if (!list.contains(f.getName()))
					list.add(f.getName());
			}
		}
		return list;
	}
}