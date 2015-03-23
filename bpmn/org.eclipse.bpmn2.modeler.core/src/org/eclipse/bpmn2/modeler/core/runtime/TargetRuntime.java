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
package org.eclipse.bpmn2.modeler.core.runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.modeler.core.IBpmn2RuntimeExtension;
import org.eclipse.bpmn2.modeler.core.LifecycleEvent;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerResourceImpl;
import org.eclipse.bpmn2.modeler.core.model.ModelDecorator;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle;
import org.eclipse.bpmn2.modeler.core.runtime.ModelExtensionDescriptor.Property;
import org.eclipse.bpmn2.modeler.core.utils.ErrorDialog;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.graphiti.ui.editor.DiagramEditor;


/**
 * Target Runtime Extension Descriptor class for Target Runtime definition.
 * Instances of this class correspond to <runtime> extension elements in the extension's plugin.xml
 * See the description of the "runtime" element in the org.eclipse.bpmn2.modeler.runtime extension point schema.
 */
public class TargetRuntime extends BaseRuntimeExtensionDescriptor implements IRuntimeExtensionDescriptor {

	public static final String EXTENSION_NAME = "runtime"; //$NON-NLS-1$

	// extension point ID for Target Runtimes
	public static final String RUNTIME_EXTENSION_ID = "org.eclipse.bpmn2.modeler.runtime"; //$NON-NLS-1$
	public static final String DEFAULT_RUNTIME_ID = "org.eclipse.bpmn2.modeler.runtime.none"; //$NON-NLS-1$
	// ID for BPMN2 specific problem markers
	public static final String BPMN2_MARKER_ID = "org.eclipse.bpmn2.modeler.core.problemMarker"; //$NON-NLS-1$
	
	// our cached registry of target runtimes contributed by other plugins
	private static List<TargetRuntime> targetRuntimes;
	// the Target Runtime for the currently active BPMN2 Editor
	private static TargetRuntime currentRuntime;
	
	// the Target Runtime properties
	private String name;
	private String[] versions;
	private String description;
	private IBpmn2RuntimeExtension runtimeExtension;
	private String problemMarkerId;
	
	// the lists of Extension Descriptors defined in the extension plugin's plugin.xml
	protected List<ModelDescriptor> modelDescriptors;
	protected List<PropertyTabDescriptor> propertyTabDescriptors;
	protected List<CustomTaskDescriptor> customTaskDescriptors;
	protected List<ModelExtensionDescriptor> modelExtensionDescriptors;
	protected List<ModelEnablementDescriptor> modelEnablementDescriptors;
//	protected ModelEnablementDescriptor defaultModelEnablementDescriptors;
	protected List<PropertyExtensionDescriptor> propertyExtensionDescriptors;
	protected List<FeatureContainerDescriptor> featureContainerDescriptors;
	protected List<ToolPaletteDescriptor> toolPaletteDescriptors;
	protected List<ShapeStyle> shapeStyles;
	protected List<DataTypeDescriptor> dataTypeDescriptors;
	protected List<TypeLanguageDescriptor> typeLanguageDescriptors;
	protected List<ExpressionLanguageDescriptor> expressionLanguageDescriptors;
	protected List<ServiceImplementationDescriptor> serviceImplementationDescriptors;

	// all of the extension descriptor classes in the order in which they need to be processed
	static Class extensionDescriptorClasses[] = {
		TargetRuntime.class,
		ModelDescriptor.class,
		DataTypeDescriptor.class,
		PropertyTabDescriptor.class,
		ModelExtensionDescriptor.class,
		CustomTaskDescriptor.class,
		ModelEnablementDescriptor.class,
		ToolPaletteDescriptor.class,
		PropertyExtensionDescriptor.class,
		FeatureContainerDescriptor.class,
		TypeLanguageDescriptor.class,
		ExpressionLanguageDescriptor.class,
		ServiceImplementationDescriptor.class,
		ShapeStyle.class,
	};

	/**
	 * Target Runtime Construction with a ConfigurationElement.
	 * This initializes all of our Target Runtime properties (name, ID, implementation class, etc.)
	 * 
	 * @param e - an IConfigurationElement defined in a plugin.xml
	 */
	public TargetRuntime(IConfigurationElement e) {
		super(e);
		name = e.getAttribute("name"); //$NON-NLS-1$
		String s = e.getAttribute("versions"); //$NON-NLS-1$
		if (s!=null) {
			versions = s.split("[, ]"); //$NON-NLS-1$
		}
		description = e.getAttribute("description"); //$NON-NLS-1$
		try {
			setRuntimeExtension((IBpmn2RuntimeExtension) e.createExecutableExtension("class")); //$NON-NLS-1$
		} catch (CoreException e1) {
			e1.printStackTrace();
		} //$NON-NLS-1$

		// add validation problem marker IDs
		IContributor contributor = e.getDeclaringExtension().getContributor();
		IConfigurationElement[] markers = Platform.getExtensionRegistry().getConfigurationElementsFor(
				"org.eclipse.core.resources.markers"); //$NON-NLS-1$
		for (IConfigurationElement m : markers) {
			if (m.getDeclaringExtension().getContributor() == contributor) {
				problemMarkerId = m.getDeclaringExtension().getUniqueIdentifier();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.runtime.IRuntimeExtensionDescriptor#getExtensionName()
	 */
	@Override
	public String getExtensionName() {
		return EXTENSION_NAME;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.runtime.IRuntimeExtensionDescriptor#setRuntime(org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime)
	 */
	@Override
	public void setRuntime(TargetRuntime targetRuntime) {
		targetRuntimes.add(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.runtime.IRuntimeExtensionDescriptor#getRuntime()
	 */
	@Override
	public TargetRuntime getRuntime() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.runtime.IRuntimeExtensionDescriptor#getConfigFile()
	 */
	@Override
	public IFile getConfigFile() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.runtime.IRuntimeExtensionDescriptor#setConfigFile(org.eclipse.core.resources.IFile)
	 */
	@Override
	public void setConfigFile(IFile configFile) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.runtime.IRuntimeExtensionDescriptor#dispose()
	 */
	@Override
	public void dispose() {
	}
	
	/**
	 * Load and initialize all RuntimeExtension classes for all Target Runtimes
	 * that have the same ID as ours.
	 * 
	 * @param editor - the DiagramEditor initializing us.
	 */
	public void notify(LifecycleEvent event) {
		for (TargetRuntime rt : targetRuntimes) {
			if (rt.getId().equals(this.id)) {
				rt.getRuntimeExtension().notify(event);
			}
		}
	}
	
	/*
	 * Target Runtime property accessors
	 */
	
	/**
	 * Returns the Target Runtime's unique ID string.
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the Target Runtime's name for use in UI components
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the Target Runtime's version number strings for use in UI components
	 * 
	 * @return
	 */
	public String[] getVersions() {
		return versions;
	}
	
	/**
	 * Returns the Target Runtime's descriptive text for use in UI components
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the extension plugin class that implements the Target Runtime behavior
	 * defined by the IBpmn2RuntimeExtension interface.
	 * 
	 * @return
	 */
	public IBpmn2RuntimeExtension getRuntimeExtension() {
		return runtimeExtension;
	}

	/**
	 * Sets the extension plugin's IBpmn2RuntimeExtension implementation class.
	 * 
	 * @param runtimeExtension
	 */
	public void setRuntimeExtension(IBpmn2RuntimeExtension runtimeExtension) {
		this.runtimeExtension = runtimeExtension;
	}

	/**
	 * Returns the Target Runtime's Model Descriptor which defines the EMF extension model.
	 * 
	 * @return
	 */
	public ModelDescriptor getModelDescriptor() {
		if (getModelDescriptors().size()==0)
			return null;
		return getModelDescriptors().get(0);
	}
	
	public void setModelDescriptor(ModelDescriptor md) {
		getModelDescriptors().clear();
		getModelDescriptors().add(md);
	}

	public String getProblemMarkerId() {
		if (problemMarkerId==null)
			return BPMN2_MARKER_ID;
		return problemMarkerId;
	}

	/*
	 * Helper methods for access to global Target Runtime data
	 */
	
	/**
	 * Fetch the TargetRuntime for the given ID string
	 * 
	 * @param id
	 * @return
	 */
	public static TargetRuntime getRuntime(String id) {
		if (targetRuntimes == null) {
			return null;
		}
		
		for (TargetRuntime rt : targetRuntimes) {
			if (rt.id.equals(id))
				return rt;
		}
		return null;
	}
	
	/**
	 * Set the current TargetRuntime.
	 * This is called by a BPMN2 Editor when it becomes the active editor.
	 * 
	 * @param rt
	 */
	public static void setCurrentRuntime(TargetRuntime rt) {
		currentRuntime = rt;
	}
	
	/**
	 * Return the current TargetRuntime.
	 * This can be used by any UI component that belongs to the currently active BPMN2 Editor
	 * 
	 * @return
	 */
	public static TargetRuntime getCurrentRuntime() {
		if (currentRuntime==null)
			return getDefaultRuntime();
		return currentRuntime;
	}
	
	/**
	 * Returns the "None" TargetRuntime definition.
	 * 
	 * @return
	 */
	public static TargetRuntime getDefaultRuntime() {
		return getRuntime(DEFAULT_RUNTIME_ID);
	}
	
	/**
	 * Returns the first TargetRuntime which is not the "None", or "default" runtime.
	 * If there are no other TargetRuntime extension plugins loaded, this returns the default runtime.
	 * 
	 * @return
	 */
	public static String getFirstNonDefaultId(){
		String runtimeId = null;
		int nonDefaultRuntimeCount = 0;
		
		if (TargetRuntime.createTargetRuntimes() == null) {
			return TargetRuntime.DEFAULT_RUNTIME_ID;
		}
		
		for (TargetRuntime rt :TargetRuntime.createTargetRuntimes()) {
			if (!rt.getId().equals(TargetRuntime.DEFAULT_RUNTIME_ID)){
				nonDefaultRuntimeCount++;
				runtimeId = rt.getId();
			}
		}
		
		if (nonDefaultRuntimeCount == 1 && runtimeId != null){
			return runtimeId;
		}else{
			return TargetRuntime.DEFAULT_RUNTIME_ID;
		}
	}
	
	public void registerExtensionResourceFactory(ResourceSet resourceSet) {
		resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put(
				Bpmn2ModelerResourceImpl.BPMN2_CONTENT_TYPE_ID, getModelDescriptor().getResourceFactory());
	}
	
	public static List<TargetRuntime> createTargetRuntimes() {
		if (targetRuntimes==null) {
			// load runtimes contributions from other plugins
			targetRuntimes = new ArrayList<TargetRuntime>();
			
			IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(RUNTIME_EXTENSION_ID);
			
			try {
				loadExtensions(null, elements, null);
			}
			catch (Exception e) {
				ErrorDialog dlg = new ErrorDialog(org.eclipse.bpmn2.modeler.core.runtime.Messages.TargetRuntime_Config_Error, e);
				dlg.show();
			}

			// All done parsing configuration elements
			// now go back and fix up some things...
			for (TargetRuntime rt : targetRuntimes) {
				
				if (rt.getModelDescriptor()==null) {
					rt.setModelDescriptor( getDefaultRuntime().getModelDescriptor() ); 
				}
				for (ToolPaletteDescriptor tp : rt.getToolPaletteDescriptors()) {
					tp.sortCategories();
				}
			}
			
			CustomTaskImageProvider.registerAvailableImages();
		}
		return targetRuntimes;
	}
	
	static TargetRuntime getRuntime(IConfigurationElement e) {
		TargetRuntime rt = getRuntime( getRuntimeId(e) );
		if (rt==null) {
			if (currentRuntime!=null)
				rt = currentRuntime;
			else
				rt = getDefaultRuntime();
		}
		return rt;
	}
	
	static String getRuntimeId(IConfigurationElement e) {
		String id = null;
		if (EXTENSION_NAME.equals(e.getName()))
			id = e.getAttribute("id"); //$NON-NLS-1$
		else {
			id = e.getAttribute("runtimeId"); //$NON-NLS-1$
			// this extension does not define a runtimeId, so get it from the containing
			// plugin.xml's <runtime> definition
			if (id==null) {
				for (IConfigurationElement ep : e.getDeclaringExtension().getConfigurationElements()) {
					if (EXTENSION_NAME.equals(ep.getName())) {
						id = ep.getAttribute("id"); //$NON-NLS-1$
						break;
					}
				}
			}
		}
		return id;
	}

	public List<TargetRuntime> getTargetRuntimes() {
		return createTargetRuntimes();
	}

	/*
	 * Custom Task convenience methods
	 */
	public CustomTaskDescriptor getCustomTask( String id ) {
		Iterator<CustomTaskDescriptor> ctIter = customTaskDescriptors.iterator();
		while (ctIter.hasNext()) {
			CustomTaskDescriptor ctd = ctIter.next();
			if (ctd.getId().equalsIgnoreCase(id)) 
				return ctd;
		}
		return null;
	}

	public boolean customTaskExists ( String id ) {
		Iterator<CustomTaskDescriptor> ctIter = customTaskDescriptors.iterator();
		while (ctIter.hasNext()) {
			CustomTaskDescriptor ctd = ctIter.next();
			if (ctd.getId().equalsIgnoreCase(id)) 
				return true;
		}
		return false;
	}
	
	public void addCustomTask(CustomTaskDescriptor ct) {
		getCustomTaskDescriptors().add(ct);
		ct.targetRuntime = this;
	}
	
	// FIXME: {@see ICustomElementFeatureContainer#getId(EObject)}
	public String getCustomTaskId(EObject object) {
		for (CustomTaskDescriptor ctd : getCustomTaskDescriptors()) {
			String id = ctd.getFeatureContainer().getId(object);
			if (ctd.getId().equals(id))
				return id;
		}
		return null;
	}
	
	/*
	 * Model Extension convenience methods
	 */
	public void addModelExtension(ModelExtensionDescriptor me) {
		getModelExtensionDescriptors().add(me);
	}
	
	/*
	 * Property Extension convenience methods
	 */
	public void addPropertyExtension(PropertyExtensionDescriptor me) {
		getPropertyExtensionDescriptors().add(me);
	}

	public PropertyExtensionDescriptor getPropertyExtension(Class clazz) {
		for (PropertyExtensionDescriptor ped : getPropertyExtensionDescriptors()) {
			String className = clazz.getName();
			if (className.equals(ped.type))
				return ped;
			// well, that didn't work...
			// The "type" name should be the BPMN2 element's interface definition;
			// if it's an implementation class name, try to convert it to its
			// interface name.
			className = className.replaceFirst("\\.impl\\.", "."); //$NON-NLS-1$ //$NON-NLS-2$
			className = className.replaceFirst("Impl$", ""); //$NON-NLS-1$ //$NON-NLS-2$
			if (className.equals(ped.type))
				return ped;
		}
		return null;
	}
	
	/*
	 * Feature Container Extension convenience methods
	 */
	public void addFeatureContainer(FeatureContainerDescriptor me) {
		getFeatureContainerDescriptors().add(me);
	}

	public FeatureContainerDescriptor getFeatureContainer(EClass clazz) {
		for (FeatureContainerDescriptor fcd : getFeatureContainerDescriptors()) {
			String className = clazz.getInstanceClassName();
			if (className.equals(fcd.type))
				return fcd;
			// well, that didn't work...
			// The "type" name should be the BPMN2 element's interface definition;
			// if it's an implementation class name, try to convert it to its
			// interface name.
			className = className.replaceFirst("\\.impl\\.", "."); //$NON-NLS-1$ //$NON-NLS-2$
			className = className.replaceFirst("Impl$", ""); //$NON-NLS-1$ //$NON-NLS-2$
			if (className.equals(fcd.type))
				return fcd;
		}
		return null;
	}
	
	/*
	 * Model Enablement Extension convenience methods
	 */
	public ModelEnablementDescriptor getModelEnablements(EObject object)
	{
		// TODO: At some point the separation of "Core" and "UI" plugins is going to become
		// an unmanageable problem: I am having to resort to using DiagramEditor.getAdapter()
		// more and more just to get things done.
		// Think about either reorganizing these two plugins, or simply combining them...
		TargetRuntime rt = this;
		DiagramEditor diagramEditor = ModelUtil.getEditor(object);
		if (diagramEditor!=null) {
			rt = (TargetRuntime) diagramEditor.getAdapter(TargetRuntime.class);
		}
		List<ModelEnablementDescriptor> meds = rt.getModelEnablementDescriptors();
		if (meds.size()>0)
			return meds.get(0);
		return null;
	}
	
	public List<ModelEnablementDescriptor>  getModelEnablements()
	{
		List<ModelEnablementDescriptor> list = new ArrayList<ModelEnablementDescriptor>();
		for (ModelEnablementDescriptor me : getModelEnablementDescriptors()) {
			list.add(me);
		}
		return list;
	}
	
	public ModelEnablementDescriptor getModelEnablements(String profileId)
	{
		if (profileId!=null && profileId.isEmpty())
			profileId = null;
		
		for (ModelEnablementDescriptor me : getModelEnablementDescriptors()) {
			if (profileId==null || profileId.equalsIgnoreCase(me.getId()))
				return me;
		}
		if (this != getDefaultRuntime()) {
			// fall back to enablements from Default Runtime
			return getDefaultRuntime().getModelEnablements(profileId);
		}
		return null;
	}
	
	public void addModelEnablements(ModelEnablementDescriptor me) {
		getModelEnablementDescriptors().add(me);
	}
	
	/*
	 * Tool Palette Extension convenience methods
	 */
	public ToolPaletteDescriptor getToolPalette(EObject object)
	{
		DiagramEditor diagramEditor = ModelUtil.getEditor(object);
		return (ToolPaletteDescriptor) diagramEditor.getAdapter(ToolPaletteDescriptor.class);
	}
	
	public ToolPaletteDescriptor getToolPalette(String profileId) {
		ToolPaletteDescriptor defaultToolPalette = null;
		// search from the end of the ToolPaletteDescriptors list so that
		// we'll find the most recently defined ToolPalette, which may be
		// in a .bpmn2config file
		List<ToolPaletteDescriptor> allToolPalettes = getToolPaletteDescriptors();
		for (int i = allToolPalettes.size() - 1; i >= 0; --i) {
			ToolPaletteDescriptor tp = allToolPalettes.get(i);
			if (profileId == null)
				return tp;
			for (String p : tp.getProfileIds()) {
				if (profileId.equalsIgnoreCase(p))
					return tp;
			}
			if (defaultToolPalette==null)
				defaultToolPalette = tp;
		}

		if (defaultToolPalette != null)
			return defaultToolPalette;

		if (this != getDefaultRuntime()) {
			// fall back to toolPalettes from Default Runtime
			return getDefaultRuntime().getToolPalette(profileId);
		}
		return null;
	}
	
	public void addToolPalette(ToolPaletteDescriptor tp) {
		getToolPaletteDescriptors().add(tp);
	}

	/*
	 * Property Tab Extension convenience methods
	 */
	private void addAfterTab(ArrayList<PropertyTabDescriptor> list, PropertyTabDescriptor tab) {
		
		createTargetRuntimes();
		String afterTab = tab.getAfterTab();
		if (afterTab!=null && !afterTab.isEmpty() && !afterTab.equals("top")) { //$NON-NLS-1$
			String id = tab.getId();
			for (TargetRuntime rt : targetRuntimes) {
				for (PropertyTabDescriptor td : rt.getPropertyTabDescriptors()) {
					if (tab!=td) {
						if (td.getId().equals(afterTab) || td.isReplacementForTab(afterTab)) {
							addAfterTab(list,td);
							if (rt==this || rt==TargetRuntime.getDefaultRuntime()) {
								if (!list.contains(td))
									list.add(td);
							}
						}
					}
				}
			}
		}
	}

	public void addPropertyTabDescriptor(PropertyTabDescriptor td) {
		getPropertyTabDescriptors().add(td);
	}

	public static PropertyTabDescriptor findPropertyTabDescriptor(String id) {
		for (TargetRuntime rt : TargetRuntime.createTargetRuntimes()) {
			PropertyTabDescriptor tab = rt.getPropertyTabDescriptor(id);
			if (tab!=null)
				return tab;
		}
		return null;
	}
	
	public PropertyTabDescriptor getPropertyTabDescriptor(String id) {
		for (PropertyTabDescriptor tab : getPropertyTabDescriptors()) {
			if (tab.getId().equals(id))
				return tab;
		}
		return null;
	}
	
	public List<PropertyTabDescriptor> buildPropertyTabDescriptors() {
		ArrayList<PropertyTabDescriptor> list = new ArrayList<PropertyTabDescriptor>();
		for (PropertyTabDescriptor tab : getPropertyTabDescriptors()) {
			addAfterTab(list, tab);
			if (!list.contains(tab))
				list.add(tab);
		}
		
		return list;
	}

	/**
	 * Gets the default Type Language for this Target Runtime. If the Target Runtime does
	 * not define its own Type Languages, use the Default Target Runtime.
	 * 
	 * @return the Type Language URI
	 */
	public String getTypeLanguage() {
		if (getTypeLanguageDescriptors().size()>0) {
			return getTypeLanguageDescriptors().get(0).getUri();
		}
		// extension plugin does not specify a type language, so use default
		return TargetRuntime.getDefaultRuntime().getTypeLanguage();
	}
	
	public TypeLanguageDescriptor getTypeLanguageDescriptor(String uri) {
		if (uri!=null) {
			for (TypeLanguageDescriptor tld : getTypeLanguageDescriptors()) {
				if (uri.equals(tld.getUri()))
					return tld;
			}
		}
		return null;
	}

	/**
	 * Gets the default Expression Language for this Target Runtime. If the Target Runtime does
	 * not define its own Expression Languages, use the Default Target Runtime.
	 * 
	 * @return the Expression Language URI
	 */
	public String getExpressionLanguage() {
		if (getExpressionLanguageDescriptors().size()>0) {
			return getExpressionLanguageDescriptors().get(0).getUri();
		}
		// extension plugin does not specify an expression language, so use default
		return TargetRuntime.getDefaultRuntime().getExpressionLanguage();
	}

	public ExpressionLanguageDescriptor getExpressionLanguageDescriptor(String uri) {
		if (uri!=null) {
			for (ExpressionLanguageDescriptor tld : getExpressionLanguageDescriptors()) {
				if (uri.equals(tld.getUri()))
					return tld;
			}
		}
		return null;
	}
	
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof TargetRuntime) {
			if (id!=null && id.equals(((TargetRuntime)arg0).getId()))
				return true;
		}
		else if (arg0 instanceof String) {
			return ((String)arg0).equals(id);
		}
		return super.equals(arg0);
	}

	/*
	 * Runtime Extension Descriptor handling
	 */
	public static void loadExtensions(TargetRuntime targetRuntime, IConfigurationElement[] elements, IFile file) throws TargetRuntimeConfigurationException {

		TargetRuntime oldCurrentRuntime = currentRuntime;
		currentRuntime = targetRuntime;
		try {
			ConfigurationElementSorter.sort(elements);

			unloadExtensions(file);
			
			for (IConfigurationElement e : elements) {
				currentRuntime = getRuntime(e);
				createRuntimeExtensionDescriptor(currentRuntime,e,file);
			}
		}
		finally {
			currentRuntime = oldCurrentRuntime;
		}
	}

	public static void unloadExtensions(IFile file) {
		if (file != null) {
			List<IRuntimeExtensionDescriptor> disposed = new ArrayList<IRuntimeExtensionDescriptor>();

			for (TargetRuntime rt : targetRuntimes) {
				for (Class c : extensionDescriptorClasses) {
					String name = getExtensionNameForClass(c);
					for (IRuntimeExtensionDescriptor d : rt.getRuntimeExtensionDescriptors(name)) {
						if (file.equals(d.getConfigFile())) {
							disposed.add(d);
						}
					}
				}
			}
			for (IRuntimeExtensionDescriptor d : disposed) {
				d.dispose();
			}
		}

	}

	public static IRuntimeExtensionDescriptor createRuntimeExtensionDescriptor(TargetRuntime rt, IConfigurationElement e, IFile file) throws TargetRuntimeConfigurationException {
		IRuntimeExtensionDescriptor d = null;
		try {
			Class c = getClassForExtensionName(e.getName());
			Constructor ctor = c.getConstructor(IConfigurationElement.class);
			d = (IRuntimeExtensionDescriptor)ctor.newInstance(e);
			d.setRuntime(rt);
			d.setConfigFile(file);
		}
		catch (Exception ex) {
			throw new TargetRuntimeConfigurationException(rt, ex);
		}
		return d;
	}
	
	public List<IRuntimeExtensionDescriptor> getRuntimeExtensionDescriptors(String name) {
		List<IRuntimeExtensionDescriptor> result = new ArrayList<IRuntimeExtensionDescriptor>();
		try {
			Class c = getClassForExtensionName(name);
			Method m = TargetRuntime.class.getMethod("get"+c.getSimpleName()+"s"); //$NON-NLS-1$ //$NON-NLS-2$
			result = (List<IRuntimeExtensionDescriptor>) m.invoke(this);
		}
		catch (Exception ex) {
			throw new TargetRuntimeConfigurationException(this, ex);
		}
		return result;
	}
	
	public static Class getClassForExtensionName(String name) {
		try {
			Class clazz = null;
			for (int i=0; i<extensionDescriptorClasses.length; ++i) {
				Class c = extensionDescriptorClasses[i];
				Field field = c.getField("EXTENSION_NAME"); //$NON-NLS-1$
				String n = (String) field.get(null);
				if (name.equals(n)) {
					return c;
				}
			}
		}
		catch (Exception ex) {
			throw new TargetRuntimeConfigurationException(null, ex);
		}
		return null;
	}
	
	public static String getExtensionNameForClass(Class clazz) {
		try {
			Field field = clazz.getField("EXTENSION_NAME"); //$NON-NLS-1$
			return (String) field.get(null);
		}
		catch (Exception ex) {
			throw new TargetRuntimeConfigurationException(null, ex);
		}
	}
	
	/*
	 * List Accessors for all Runtime Extension Descriptors
	 */
	public List<CustomTaskDescriptor> getCustomTaskDescriptors()
	{
		if (customTaskDescriptors==null) {
			customTaskDescriptors = new ArrayList<CustomTaskDescriptor>();
		}
		return customTaskDescriptors;
	}
	
	public List<ModelExtensionDescriptor> getModelExtensionDescriptors()
	{
		if (modelExtensionDescriptors==null) {
			modelExtensionDescriptors = new ArrayList<ModelExtensionDescriptor>();
		}
		return modelExtensionDescriptors;
	}
	
	public List<ModelExtensionDescriptor> getAllModelExtensionDescriptors()
	{
		List<ModelExtensionDescriptor> list = new ArrayList<ModelExtensionDescriptor>();
		list.addAll(getCustomTaskDescriptors());
		list.addAll(getModelExtensionDescriptors());
		return list;
	}

	public ModelExtensionDescriptor getModelExtensionDescriptor(EObject object) {
		EClass eClass = (EClass) ((object instanceof EClass) ? object : object.eClass());
		
		for (ModelExtensionDescriptor md : getModelExtensionDescriptors()) {
			String type = eClass.getName();
			if (md.getType().equals(type))
				return md;
			for (EClass ec : eClass.getESuperTypes()) {
				type = ec.getName();
				if (md.getType().equals(type))
					return md;
				ModelExtensionDescriptor md2 = getModelExtensionDescriptor(ec);
				if (md2!=null)
					return md2;
			}
		}
		return null;
	}
	
	/**
	 * Returns a list of model extension types and their features. A filter can
	 * be used to select only BPMN2 model elements, plugin model elements or
	 * both.
	 * 
	 * @param filter selects which elements to return: 0 = all, 1 = BPMN2 model
	 *            extensions only, 2 = plugin model extension elements only
	 * @return a list of EClass objects, and their lists of EStructuralFeatures.
	 */
	public Hashtable<EClass, List<EStructuralFeature>> getModelExtensions(int filter) {
		Hashtable<EClass, List<EStructuralFeature>> list = new Hashtable<EClass, List<EStructuralFeature>>();
		for (CustomTaskDescriptor ctd : getCustomTaskDescriptors()) {
			getModelExtensions(filter, ctd, list);
		}
		for (ModelExtensionDescriptor med : getModelExtensionDescriptors()) {
			getModelExtensions(filter, med, list);
		}
		if (filter==2) { // plugin extension elements only
			ModelDescriptor md = getModelDescriptor();
			if (md.getEPackage() != Bpmn2Package.eINSTANCE) {
				for (EClassifier ec : md.getEPackage().getEClassifiers()) {
					if (ec.getName().equals("DocumentRoot"))
						continue;
					if (ec instanceof EClass) {
						EClass eClass = (EClass)ec;
						List<EStructuralFeature> features = list.get(eClass);
						if (features==null) {
							features = new ArrayList<EStructuralFeature>();
							list.put(eClass,features);
						}
						for (EStructuralFeature f : eClass.getEStructuralFeatures()) {
							features.add(f);
						}
					}
				}
			}
		}
		return list;
	}

	private void getModelExtensions(int filter, ModelExtensionDescriptor med, Hashtable<EClass, List<EStructuralFeature>> list) {
		String type = med.getType();
		EClassifier bpmn2type  = Bpmn2Package.eINSTANCE.getEClassifier(type);
		if (filter==1) { // BPMN2 elements only
			if (bpmn2type==null)
				return;
		}
		if (filter==2) { // plugin extension elements only
			if (bpmn2type!=null)
				return;
		}
		EClass eClass = med.createEClass(type);
		List<EStructuralFeature> features = list.get(eClass);
		if (features==null) {
			features = new ArrayList<EStructuralFeature>();
			list.put(eClass,features);
		}
		for (Property p : med.getProperties()) {
			EStructuralFeature feature = med.createEFeature(eClass, p);
			if (bpmn2type instanceof EClass) {
				// ignore structural features that are already defined in
				// the BPMN2 package. These <property> elements are used
				// only for initialization of these features and should not
				// be considered as model extensions.
				if (((EClass) bpmn2type).getEStructuralFeature(p.name)!=null)
					continue;
			}
			if (feature!=null && !features.contains(feature))
				features.add(feature);
			for (Object v : p.getValues()) {
				if (v instanceof Property) {
					getModelExtensions(med, (Property)v, list);
				}
			}
		}
		if (features.isEmpty())
			list.remove(eClass);
	}
	
	private void getModelExtensions(ModelExtensionDescriptor med, Property p,  Hashtable<EClass, List<EStructuralFeature>> list) {
		if (p.parent!=null) {
			EClass eClass = med.getModelDecorator().getEClass(p.parent.type);
			if (eClass!=null) {
				List<EStructuralFeature> features = list.get(eClass);
				if (features==null) {
					features = new ArrayList<EStructuralFeature>();
					list.put(eClass,features);
				}
				EStructuralFeature feature = med.createEFeature(eClass, p);
				if (feature!=null && !features.contains(feature))
					features.add(feature);
				for (Object v : p.getValues()) {
					if (v instanceof Property) {
						getModelExtensions(med, (Property)v, list);
					}
				}
			}
		}
	}
	
	public List<PropertyExtensionDescriptor> getPropertyExtensionDescriptors()
	{
		if (propertyExtensionDescriptors==null) {
			propertyExtensionDescriptors = new ArrayList<PropertyExtensionDescriptor>();
		}
		return propertyExtensionDescriptors;
	}
	
	public List<FeatureContainerDescriptor> getFeatureContainerDescriptors()
	{
		if (featureContainerDescriptors==null) {
			featureContainerDescriptors = new ArrayList<FeatureContainerDescriptor>();
		}
		return featureContainerDescriptors;
	}
	
	public List<ModelEnablementDescriptor> getModelEnablementDescriptors()
	{
		if (modelEnablementDescriptors==null) {
			modelEnablementDescriptors = new ArrayList<ModelEnablementDescriptor>();
		}
		return modelEnablementDescriptors;
	}

	public List<ToolPaletteDescriptor> getToolPaletteDescriptors()
	{
		if (toolPaletteDescriptors==null) {
			toolPaletteDescriptors = new ArrayList<ToolPaletteDescriptor>();
		}
		return toolPaletteDescriptors;
	}
	
	public List<ShapeStyle> getShapeStyles() {
		if (shapeStyles==null) {
			shapeStyles = new ArrayList<ShapeStyle>();
		}
		return shapeStyles;
	}
	
	public List<PropertyTabDescriptor> getPropertyTabDescriptors() {
		if (propertyTabDescriptors==null)
			propertyTabDescriptors = new ArrayList<PropertyTabDescriptor>();
		return propertyTabDescriptors;
	}
	
	public List<ModelDescriptor> getModelDescriptors() {
		if (modelDescriptors==null)
			modelDescriptors = new ArrayList<ModelDescriptor>();
		return modelDescriptors;
	}

	public List<DataTypeDescriptor> getDataTypeDescriptors() {
		if (dataTypeDescriptors==null)
			dataTypeDescriptors = new ArrayList<DataTypeDescriptor>();
		return dataTypeDescriptors;
	}
	
	public List<TypeLanguageDescriptor> getTypeLanguageDescriptors() {
		if (typeLanguageDescriptors==null)
			typeLanguageDescriptors = new ArrayList<TypeLanguageDescriptor>();
		return typeLanguageDescriptors;
	}
	
	public List<ExpressionLanguageDescriptor> getExpressionLanguageDescriptors() {
		if (expressionLanguageDescriptors==null)
			expressionLanguageDescriptors = new ArrayList<ExpressionLanguageDescriptor>();
		return expressionLanguageDescriptors;
	}
	
	public List<ServiceImplementationDescriptor> getServiceImplementationDescriptors() {
		if (serviceImplementationDescriptors==null)
			serviceImplementationDescriptors = new ArrayList<ServiceImplementationDescriptor>();
		return serviceImplementationDescriptors;
	}
	
	public static class ConfigurationElementSorter {
		public static void sort(IConfigurationElement[] elements) {
			Arrays.sort(elements, new Comparator<IConfigurationElement>() {
				@Override
				public int compare(IConfigurationElement e0, IConfigurationElement e1) {
					return rank(e1) - rank(e0);
				}
				
				int rank(IConfigurationElement e) {
					int rank = 0;
					try {
						String name = e.getName();
						Class clazz = null;
						for (int i=0; i<extensionDescriptorClasses.length; ++i) {
							Class c = extensionDescriptorClasses[i];
							Field field = c.getField("EXTENSION_NAME"); //$NON-NLS-1$
							String n = (String) field.get(null);
							if (name.equals(n)) {
								rank = extensionDescriptorClasses.length - i;
								clazz = c;
								break;
							}
						}
						rank *= 2;
						String id = getRuntimeId(e);
						if (DEFAULT_RUNTIME_ID.equals(id))
							rank += 1;
					}
					catch (Exception ex) {
						throw new TargetRuntimeConfigurationException(null, ex);
					}
					return rank;
				}
			});
		}
	}
}
