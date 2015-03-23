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
package org.eclipse.bpmn2.modeler.core.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CallChoreography;
import org.eclipse.bpmn2.CancelEventDefinition;
import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.InteractionNode;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.SubChoreography;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.eclipse.bpmn2.Transaction;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectPropertyProvider;
import org.eclipse.bpmn2.modeler.core.runtime.ModelEnablementDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.ModelExtensionDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.navigator.ResourceNavigator;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;


public class Bpmn2Preferences implements IResourceChangeListener, IPropertyChangeListener {
	public final static String PREF_TARGET_RUNTIME = "target.runtime"; //$NON-NLS-1$
	public final static String PREF_TARGET_RUNTIME_LABEL = Messages.Bpmn2Preferences_Target_Runtime;
	public final static String PREF_SHOW_ADVANCED_PROPERTIES = "show.advanced.properties"; //$NON-NLS-1$
	public final static String PREF_SHOW_ADVANCED_PROPERTIES_LABEL = Messages.Bpmn2Preferences_Show_Advanced_Properties;
	public final static String PREF_SHOW_DESCRIPTIONS = "show.descriptions"; //$NON-NLS-1$
	public final static String PREF_SHOW_DESCRIPTIONS_LABEL = Messages.Bpmn2Preferences_Show_Descriptions;
	public final static String PREF_TOOL_PALETTE = "tool.palette"; //$NON-NLS-1$
	public final static String PREF_TOOL_PROFILE = "tool.profile"; //$NON-NLS-1$
	public final static String PREF_MODEL_ENABLEMENT = "model.enablement"; //$NON-NLS-1$
	public final static String PREF_IS_HORIZONTAL = "is.horizontal"; //$NON-NLS-1$
	public final static String PREF_IS_HORIZONTAL_LABEL = Messages.Bpmn2Preferences_Horizontal;
	
	public final static String PREF_IS_EXPANDED = "is.expanded"; //$NON-NLS-1$
	public final static String PREF_IS_EXPANDED_LABEL = Messages.Bpmn2Preferences_Expand;
	public final static String PREF_IS_MESSAGE_VISIBLE = "is.message.visible"; //$NON-NLS-1$
	public final static String PREF_IS_MESSAGE_VISIBLE_LABEL = Messages.Bpmn2Preferences_Message_Visible;
	public final static String PREF_IS_MARKER_VISIBLE = "is.marker.visible"; //$NON-NLS-1$
	public final static String PREF_IS_MARKER_VISIBLE_LABEL = Messages.Bpmn2Preferences_Marker_Visible;
	public final static String PREF_SAVE_BPMNLABELS = "save.bpmnlabels"; //$NON-NLS-1$
	public final static String PREF_SAVE_BPMNLABELS_LABEL = Messages.Bpmn2Preferences_Save_BPMNLabels;
	
	public final static String PREF_SHAPE_STYLE = "shape.style"; //$NON-NLS-1$

	public final static String PREF_CONNECTION_TIMEOUT = "connection.timeout"; //$NON-NLS-1$
	public final static String PREF_CONNECTION_TIMEOUT_LABEL = Messages.Bpmn2Preferences_Timeout;

	public final static String PREF_USE_POPUP_DIALOG_FOR_LISTS = "popup.detail.dialog"; //$NON-NLS-1$
	public final static String PREF_USE_POPUP_DIALOG_FOR_LISTS_LABEL = Messages.Bpmn2Preferences_Use_Popup_Dialog_For_Lists;

	public final static String PREF_POPUP_CONFIG_DIALOG = "popup.config.dialog"; //$NON-NLS-1$
	public final static String PREF_POPUP_CONFIG_DIALOG_LABEL = Messages.Bpmn2Preferences_Config_Dialog;
	
	public final static String PREF_POPUP_CONFIG_DIALOG_FOR_ACTIVITIES = "popup.config.dialog.for.activities"; //$NON-NLS-1$
	public final static String PREF_POPUP_CONFIG_DIALOG_FOR_ACTIVITIES_LABEL = Messages.Bpmn2Preferences_Activities;
	public final static String PREF_POPUP_CONFIG_DIALOG_FOR_GATEWAYS = "popup.config.dialog.for.gateways"; //$NON-NLS-1$
	public final static String PREF_POPUP_CONFIG_DIALOG_FOR_GATEWAYS_LABEL = Messages.Bpmn2Preferences_Gateways;
	public final static String PREF_POPUP_CONFIG_DIALOG_FOR_EVENTS = "popup.config.dialog.for.events"; //$NON-NLS-1$
	public final static String PREF_POPUP_CONFIG_DIALOG_FOR_EVENTS_LABEL = Messages.Bpmn2Preferences_Events;
	public final static String PREF_POPUP_CONFIG_DIALOG_FOR_EVENT_DEFS = "popup.config.dialog.for.event.defs"; //$NON-NLS-1$
	public final static String PREF_POPUP_CONFIG_DIALOG_FOR_EVENT_DEFS_LABEL = Messages.Bpmn2Preferences_Event_Definitions;
	public final static String PREF_POPUP_CONFIG_DIALOG_FOR_DATA_DEFS = "popup.config.dialog.for.data.defs"; //$NON-NLS-1$
	public final static String PREF_POPUP_CONFIG_DIALOG_FOR_DATA_DEFS_LABEL = Messages.Bpmn2Preferences_Data_Items;
	public final static String PREF_POPUP_CONFIG_DIALOG_FOR_CONTAINERS = "popup.config.dialog.for.containers"; //$NON-NLS-1$
	public final static String PREF_POPUP_CONFIG_DIALOG_FOR_CONTAINERS_LABEL = Messages.Bpmn2Preferences_Containers;

	public final static String PREF_SHOW_ID_ATTRIBUTE = "show.id.attribute"; //$NON-NLS-1$
	public final static String PREF_SHOW_ID_ATTRIBUTE_LABEL = Messages.Bpmn2Preferences_Show_ID_Attribute;
	public final static String PREF_CHECK_PROJECT_NATURE = "check.project.nature"; //$NON-NLS-1$
	public final static String PREF_CHECK_PROJECT_NATURE_LABEL = Messages.Bpmn2Preferences_Check_Project_Nature;
	public final static String PREF_SIMPLIFY_LISTS = "simplify.lists"; //$NON-NLS-1$
	public final static String PREF_SIMPLIFY_LISTS_LABEL = Messages.Bpmn2Preferences_Simplify_Lists;
	public final static String PREF_DO_CORE_VALIDATION = "do.core.validation"; //$NON-NLS-1$
	public final static String PREF_DO_CORE_VALIDATION_LABEL = Messages.Bpmn2Preferences_Do_Core_Validation;
	public final static String PREF_PROPAGATE_GROUP_CATEGORIES = "propagate.group.categories"; //$NON-NLS-1$
	public final static String PREF_PROPAGATE_GROUP_CATEGORIES_LABEL = Messages.Bpmn2Preferences_Propagate_Group_Categories;
	public final static String PREF_ALLOW_MULTIPLE_CONNECTIONS = "allow.multiple.connections"; //$NON-NLS-1$
	public final static String PREF_ALLOW_MULTIPLE_CONNECTIONS_LABEL = Messages.Bpmn2Preferences_Allow_Mutliple_Connections;
	public final static String PREF_SERVICE_IMPLEMENTATIONS = "service.implementations"; //$NON-NLS-1$

	public final static String PREF_RESOLVE_EXTERNALS = "resolve.externals"; //$NON-NLS-1$
	public final static String PREF_RESOLVE_EXTERNALS_LABEL = Messages.Bpmn2Preferences_Resolve_Externals;
	
	private static Hashtable<IProject,Bpmn2Preferences> projectPreferenceCacheMap = null;
	private static Bpmn2Preferences instancePreferenceCache = null;
	private static IProject activeProject;
	private static ListenerList preferenceChangeListeners;
	private static IPreferenceStore preferenceStore;

	private IProject project;
	private boolean useProjectPreferences;
	private IEclipsePreferences projectPreferences;
	private static IEclipsePreferences instancePreferences;
	private static IEclipsePreferences defaultPreferences;
	private boolean cached;
	private boolean dirty;
	
	public enum BPMNDIAttributeDefault {
		USE_DI_VALUE,
		DEFAULT_TRUE,
		ALWAYS_TRUE,
		ALWAYS_FALSE
	};
	
	private TargetRuntime targetRuntime;
	private boolean showAdvancedPropertiesTab;
	private boolean showDescriptions;
	private boolean showIdAttribute;
	private boolean checkProjectNature;
	private boolean simplifyLists;
	private boolean usePopupDialogForLists;
	private boolean doCoreValidation;
	private boolean propagateGroupCategories;
	private boolean allowMultipleConnections;
	private BPMNDIAttributeDefault isHorizontal;
	private BPMNDIAttributeDefault isExpanded;
	private BPMNDIAttributeDefault isMessageVisible;
	private BPMNDIAttributeDefault isMarkerVisible;
	private boolean saveBPMNLabels;
	private int connectionTimeout;
	private int popupConfigDialog;
	private boolean popupConfigDialogFor[] = new boolean[6];
	private int resolveExternals;

	private HashMap<String, ShapeStyle> shapeStyles = new HashMap<String, ShapeStyle>();

	private Bpmn2Preferences(IProject project) {
		this.project = project;
		
		IPreferencesService preferenceService = Platform.getPreferencesService();
		if (preferenceStore==null)
			preferenceStore = Activator.getDefault().getPreferenceStore();
		if (instancePreferences==null)
			instancePreferences = (IEclipsePreferences) preferenceService.getRootNode().node(InstanceScope.SCOPE).node(Activator.PLUGIN_ID);
		if (defaultPreferences==null)
			defaultPreferences = (IEclipsePreferences) preferenceService.getRootNode().node(DefaultScope.SCOPE).node(Activator.PLUGIN_ID);

		if (project != null) {
			projectPreferences = (IEclipsePreferences) preferenceService.getRootNode().node(ProjectScope.SCOPE).node(project.getName()).node(Activator.PLUGIN_ID);
			preferenceStore.addPropertyChangeListener(this);
			
			try {
				projectPreferences.sync();
			}
			catch (Exception e) {
			}
		}
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

		loadDefaults();
		cache();
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// various preference instance getters
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Return the Preferences for the currently active project. This should be used
	 * with caution: the active project is set by the BPMN2Editor, so this should only
	 * be used in a context that is known to have an active editor.
	 * 
	 * @return project preferences
	 */
	public static Bpmn2Preferences getInstance() {
		return getInstance(getActiveProject());
	}
	
	/**
	 * Return the Preferences for the project containing the EMF Resource
	 * 
	 * @param resource
	 * @return project preferences
	 */
	public static Bpmn2Preferences getInstance(EObject object) {
		return getInstance(ObjectPropertyProvider.getResource(object));
	}
	
	public static Bpmn2Preferences getInstance(Resource resource) {
		if (resource!=null)
			return getInstance(resource.getURI());
		return getInstance();
	}
	
	/**
	 * Return the Preferences for the project containing the EMF Resource specified
	 * by the resource URI. This must be a Platform URI.
	 * 
	 * @param resourceURI
	 * @return project preferences
	 */
	public static Bpmn2Preferences getInstance(URI resourceURI) {
		String filename = resourceURI.trimFragment().toPlatformString(true);
		if (filename==null) {
			return getInstance();
		}
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		if (root==null) {
			return getInstance();
		}
		IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(filename);
		if (res==null) {
			return getInstance();
		}
		IProject project = res.getProject();
		return getInstance(project);
			
	}
	
	/**
	 * Return the Preferences for the given project.
	 * 
	 * @param project
	 * @return project preferences
	 */
	public static Bpmn2Preferences getInstance(IProject project) {
		Bpmn2Preferences pref = null;
		if (project==null) {
			if (instancePreferenceCache==null) {
				// Create a cache for the Instance Preference Store.
				instancePreferenceCache = new Bpmn2Preferences(null);
			}
			pref = instancePreferenceCache;
		}
		else {
			if (projectPreferenceCacheMap==null) {
				// Create a map that will hold the Project Preference Store cache objects.
				projectPreferenceCacheMap = new Hashtable<IProject,Bpmn2Preferences>();
			}
			pref = projectPreferenceCacheMap.get(project);
			if (pref==null) {
				// Create a cache for the Project Preference Store.
				pref = new Bpmn2Preferences(project);
				pref.reload();
				projectPreferenceCacheMap.put(project, pref);
			}
		}
		return pref;
	}
	
	public static List<Bpmn2Preferences> getInstances(TargetRuntime rt) {
		Assert.isNotNull(rt);
		String id = rt.getId();
		List<Bpmn2Preferences> prefs = new ArrayList<Bpmn2Preferences>();
		if (instancePreferenceCache!=null && instancePreferenceCache.targetRuntime.getId().equals(id))
			prefs.add(instancePreferenceCache);
		if (projectPreferenceCacheMap!=null) {
			for (Entry<IProject, Bpmn2Preferences> entry : projectPreferenceCacheMap.entrySet()) {
				Bpmn2Preferences pref = entry.getValue();
				if (pref.targetRuntime.getId().equals(id))
					prefs.add(pref);
			}
			
		}
		return prefs;
	}
	
	public void useProjectPreferences() {
		Assert.isNotNull(projectPreferences);
		useProjectPreferences = true;
	}
	
	public void dispose() {
		if (project!=null)
			projectPreferenceCacheMap.remove(project);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		preferenceStore.removePropertyChangeListener(this);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// Cache initialization
	////////////////////////////////////////////////////////////////////////////////

	private void loadDefaults() {
		if (defaultPreferences.get(PREF_TARGET_RUNTIME, null)==null) {
			String rid = TargetRuntime.getFirstNonDefaultId();
			defaultPreferences.put(PREF_TARGET_RUNTIME, rid);
			defaultPreferences.putBoolean(PREF_SHOW_ADVANCED_PROPERTIES, false);
			defaultPreferences.putBoolean(PREF_CHECK_PROJECT_NATURE, true);
			defaultPreferences.putBoolean(PREF_SIMPLIFY_LISTS, true);
			defaultPreferences.putBoolean(PREF_USE_POPUP_DIALOG_FOR_LISTS, false);
			defaultPreferences.putBoolean(PREF_SHOW_DESCRIPTIONS, true);
			defaultPreferences.put(PREF_IS_HORIZONTAL, BPMNDIAttributeDefault.DEFAULT_TRUE.name());
			defaultPreferences.put(PREF_IS_EXPANDED, BPMNDIAttributeDefault.ALWAYS_TRUE.name());
			defaultPreferences.put(PREF_IS_MESSAGE_VISIBLE, BPMNDIAttributeDefault.ALWAYS_TRUE.name());
			defaultPreferences.put(PREF_IS_MARKER_VISIBLE, BPMNDIAttributeDefault.DEFAULT_TRUE.name());
			defaultPreferences.putBoolean(PREF_SAVE_BPMNLABELS, true);

			defaultPreferences.putInt(PREF_POPUP_CONFIG_DIALOG, 0); // tri-state checkbox
			defaultPreferences.putBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_ACTIVITIES, false);
			defaultPreferences.putBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_GATEWAYS, false);
			defaultPreferences.putBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_EVENTS, false);
			defaultPreferences.putBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_EVENT_DEFS, false);
			defaultPreferences.putBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_DATA_DEFS, false);
			defaultPreferences.putBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_CONTAINERS, false);
			defaultPreferences.putBoolean(PREF_DO_CORE_VALIDATION, false);
			defaultPreferences.putBoolean(PREF_PROPAGATE_GROUP_CATEGORIES, true);
			defaultPreferences.putBoolean(PREF_ALLOW_MULTIPLE_CONNECTIONS, false);

			defaultPreferences.putInt(PREF_CONNECTION_TIMEOUT, 60000);
			defaultPreferences.putInt(PREF_RESOLVE_EXTERNALS, 2);
			
			for (TargetRuntime rt : TargetRuntime.createTargetRuntimes()) {
				loadDefaults(rt, PREF_TOOL_PROFILE);
				loadDefaults(rt, PREF_MODEL_ENABLEMENT);
				loadDefaults(rt, PREF_SHAPE_STYLE);
			}
		}
	}

	public void loadDefaults(TargetRuntime rt, String key) {
		if (key.equals(PREF_TOOL_PROFILE)) {
			String defaultProfile = null;
			for (ModelEnablementDescriptor med : rt.getModelEnablements()) {
				String path = getToolProfilePath(rt);
				Preferences prefs = defaultPreferences.node(path);
				if (defaultProfile == null)
					prefs.putBoolean(defaultProfile = med.getId(), true);
				else
					prefs.putBoolean(med.getId(), false);
			}
		}
		else if (key.equals(PREF_MODEL_ENABLEMENT)) {
			if (rt.getModelEnablements().size()==0) {
				String path = getModelEnablementsPath(rt, null);
				Preferences prefs = defaultPreferences.node(path);
				for (Entry<EClass, List<EStructuralFeature>> e : rt.getModelExtensions(0).entrySet()) {
					for (EStructuralFeature f : e.getValue()) {
						String s = e.getKey().getName() + "." + f.getName();
						prefs.putBoolean(s, Boolean.TRUE);
					}
				}
				ModelEnablements me = new ModelEnablements(rt,"default");
				me.setEnabledAll(true);
				for (String s : me.getAllEnabled())
					prefs.putBoolean(s, Boolean.TRUE);
			}
			else {
				for (ModelEnablementDescriptor med : rt.getModelEnablements()) {
					String path = getModelEnablementsPath(rt, med.getId());
					Preferences prefs = defaultPreferences.node(path);
					for (String s : med.getAllEnabled()) {
						prefs.putBoolean(s, Boolean.TRUE);
					}
					for (Entry<EClass, List<EStructuralFeature>> e : rt.getModelExtensions(0).entrySet()) {
						for (EStructuralFeature f : e.getValue()) {
							String s = e.getKey().getName() + "." + f.getName();
							prefs.putBoolean(s, Boolean.TRUE);
						}
					}
				}
			}
		}
		else if (key.equals(PREF_SHAPE_STYLE)) {
			// Use ShapeStyles defined in the Default Target Runtime if an extension does not provide its own. 
			List<ShapeStyle> defaultShapeStyles = TargetRuntime.getDefaultRuntime().getShapeStyles();
			String path = getShapeStylePath(rt);
			Preferences prefs = defaultPreferences.node(path);
			for (ShapeStyle ss : rt.getShapeStyles()) {
				String value = ShapeStyle.encode(ss);
				prefs.put(ss.getObject(), value);
			}
			if (rt!=TargetRuntime.getDefaultRuntime()) {
				for (ShapeStyle ss : defaultShapeStyles) {
					String value = ShapeStyle.encode(ss);
					prefs.put(ss.getObject(), value);
				}
			}
		}
	}

	public void unloadDefaults(TargetRuntime rt, String key) {
		if (key.equals(PREF_TOOL_PROFILE)) {
			for (ModelEnablementDescriptor med : rt.getModelEnablements()) {
				String path = getToolProfilePath(rt);
				Preferences prefs = defaultPreferences.node(path);
				prefs.remove(med.getId());
			}
		}
		else if (key.equals(PREF_MODEL_ENABLEMENT)) {
			for (ModelEnablementDescriptor med : rt.getModelEnablements()) {
				String path = getModelEnablementsPath(rt, med.getId());
				Preferences prefs = defaultPreferences.node(path);
				for (String s : med.getAllEnabled()) {
					prefs.remove(s);
				}
			}
			String path = getModelEnablementsPath(rt, null);
			Preferences prefs = defaultPreferences.node(path);
			for (Entry<EClass, List<EStructuralFeature>> e : rt.getModelExtensions(0).entrySet()) {
				for (EStructuralFeature f : e.getValue()) {
					String s = e.getKey().getName() + "." + f.getName();
					prefs.remove(s);
				}
			}
		}
		else if (key.equals(PREF_SHAPE_STYLE)) {
			// Use ShapeStyles defined in the Default Target Runtime if an extension does not provide its own. 
			String path = getShapeStylePath(rt);
			Preferences prefs = defaultPreferences.node(path);
			if (rt!=TargetRuntime.getDefaultRuntime()) {
				for (ShapeStyle ss : rt.getShapeStyles()) {
					prefs.remove(ss.getObject());
				}
			}
		}
	}
	
	private boolean shouldSaveToProjectPreference(String key) {
		if (projectPreferences!=null) {
			if (useProjectPreferences) {
				// if saving to project preferences, set the preference node
				projectPreferences.node(key);
				return true;
			}
			// otherwise only save to project preferences if the key already exists.
			try {
				return projectPreferences.nodeExists(key);
			}
			catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void reload() {
		cached = false;
		cache();
	}
	
	private void cache() {
		if (!cached) {
			// cache all preferences as Bpmn2Preferences instance variables for faster access
			String id = get(PREF_TARGET_RUNTIME,TargetRuntime.getFirstNonDefaultId());
			if (id==null || id.isEmpty())
				id = TargetRuntime.getFirstNonDefaultId();
			targetRuntime = TargetRuntime.getRuntime(id);
			showAdvancedPropertiesTab = getBoolean(PREF_SHOW_ADVANCED_PROPERTIES, false);
			showDescriptions = getBoolean(PREF_SHOW_DESCRIPTIONS, false);
			showIdAttribute = getBoolean(PREF_SHOW_ID_ATTRIBUTE, false);
			checkProjectNature = getBoolean(PREF_CHECK_PROJECT_NATURE, true);
			simplifyLists = getBoolean(PREF_SIMPLIFY_LISTS, true);
			usePopupDialogForLists = getBoolean(PREF_USE_POPUP_DIALOG_FOR_LISTS, false);
			isHorizontal = getBPMNDIAttributeDefault(PREF_IS_HORIZONTAL, BPMNDIAttributeDefault.USE_DI_VALUE);
			isExpanded = getBPMNDIAttributeDefault(PREF_IS_EXPANDED, BPMNDIAttributeDefault.USE_DI_VALUE);
			isMessageVisible = getBPMNDIAttributeDefault(PREF_IS_MESSAGE_VISIBLE, BPMNDIAttributeDefault.USE_DI_VALUE);
			isMarkerVisible = getBPMNDIAttributeDefault(PREF_IS_MARKER_VISIBLE, BPMNDIAttributeDefault.USE_DI_VALUE);
			saveBPMNLabels = getBoolean(PREF_SAVE_BPMNLABELS, true);
			connectionTimeout = getInt(PREF_CONNECTION_TIMEOUT, 60000); //$NON-NLS-1$
			resolveExternals = getInt(PREF_RESOLVE_EXTERNALS, 2); //$NON-NLS-1$
			
			popupConfigDialog = getInt(PREF_POPUP_CONFIG_DIALOG, 0); // tri-state checkbox
			popupConfigDialogFor[0] = getBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_ACTIVITIES, false);
			popupConfigDialogFor[1] = getBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_GATEWAYS, false);
			popupConfigDialogFor[2] = getBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_EVENTS, false);
			popupConfigDialogFor[3] = getBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_EVENT_DEFS, false);
			popupConfigDialogFor[4] = getBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_DATA_DEFS, false);
			popupConfigDialogFor[5] = getBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_CONTAINERS, false);

			doCoreValidation = getBoolean(PREF_DO_CORE_VALIDATION, false);
			propagateGroupCategories = getBoolean(PREF_PROPAGATE_GROUP_CATEGORIES, true);
			allowMultipleConnections = getBoolean(PREF_ALLOW_MULTIPLE_CONNECTIONS, false);
			
			cached = true;
		}
	}
	
	public synchronized void flush() throws BackingStoreException {
		if (dirty) {
			if (useProjectPreferences) {
				if (projectPreferences!=null) {
					projectPreferences.flush();
				}
			}
			else {
				put(PREF_TARGET_RUNTIME,getRuntime().getId());
				putBoolean(PREF_SHOW_ADVANCED_PROPERTIES, showAdvancedPropertiesTab);
				putBoolean(PREF_SHOW_DESCRIPTIONS, showDescriptions);
				putBoolean(PREF_SHOW_ID_ATTRIBUTE, showIdAttribute);
				putBoolean(PREF_CHECK_PROJECT_NATURE, checkProjectNature);
				putBoolean(PREF_SIMPLIFY_LISTS, simplifyLists);
				putBoolean(PREF_USE_POPUP_DIALOG_FOR_LISTS, usePopupDialogForLists);
				setBPMNDIAttributeDefault(PREF_IS_HORIZONTAL, isHorizontal);
	
				setBPMNDIAttributeDefault(PREF_IS_EXPANDED, isExpanded);
				setBPMNDIAttributeDefault(PREF_IS_MESSAGE_VISIBLE, isMessageVisible);
				setBPMNDIAttributeDefault(PREF_IS_MARKER_VISIBLE, isMarkerVisible);
				putBoolean(PREF_SAVE_BPMNLABELS, saveBPMNLabels);
				
				putInt(PREF_CONNECTION_TIMEOUT, connectionTimeout);
				putInt(PREF_RESOLVE_EXTERNALS, resolveExternals);
	
				putInt(PREF_POPUP_CONFIG_DIALOG, popupConfigDialog);
				putBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_ACTIVITIES, popupConfigDialogFor[0]);
				putBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_GATEWAYS, popupConfigDialogFor[1]);
				putBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_EVENTS, popupConfigDialogFor[2]);
				putBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_EVENT_DEFS, popupConfigDialogFor[3]);
				putBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_DATA_DEFS, popupConfigDialogFor[4]);
				putBoolean(PREF_POPUP_CONFIG_DIALOG_FOR_CONTAINERS, popupConfigDialogFor[5]);
				putBoolean(PREF_DO_CORE_VALIDATION, doCoreValidation);
				putBoolean(PREF_PROPAGATE_GROUP_CATEGORIES, propagateGroupCategories);
				putBoolean(PREF_ALLOW_MULTIPLE_CONNECTIONS, allowMultipleConnections);
			}
		}
		
		// prevent concurrent modification of the shapeStyle cache
		HashMap<String, ShapeStyle> ssCache = new HashMap<String, ShapeStyle>();
		ssCache.putAll(shapeStyles);
		for (Entry<String, ShapeStyle> entry : ssCache.entrySet()) {
			ShapeStyle ss = entry.getValue();
			if (ss.isDirty())
				setShapeStyle(entry.getKey(), ss);
		}
		
		instancePreferences.flush();

		dirty = false;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// Getters and setters for Graphical Styles (Colors, Fonts, etc.)
	////////////////////////////////////////////////////////////////////////////////
	
	public String getShapeStyleId(EObject object) {
		return getShapeStyleId(getRuntime(), object);
	}
	
	public static String getShapeStyleId(TargetRuntime rt, EObject object) {
		try {
			Class clazz = Class.forName(object.eClass().getInstanceClassName());
			return getShapeStyleKey(rt, clazz);
		} catch (ClassNotFoundException e) {
			return getShapeStyleKey(rt, object.getClass());
		}
	}
	
	public static String getShapeStyleKey(TargetRuntime rt, Class clazz) {
		return getShapeStyleKey(rt, clazz.getSimpleName());
	}
	
	public static String getShapeStyleKey(TargetRuntime rt, String name) {
		return getShapeStylePath(rt) + "/" + name; //$NON-NLS-1$
	}
	
	public static String getShapeStylePath(TargetRuntime rt) {
		return PREF_SHAPE_STYLE + "/" + rt.getId(); //$NON-NLS-1$
	}

	public ShapeStyle getShapeStyle(EObject object) {
		Class clazz;
		try {
			clazz = Class.forName(object.eClass().getInstanceClassName());
			return getShapeStyle(clazz);
		} catch (ClassNotFoundException e) {
			return getShapeStyle(object.getClass());
		}
	}
	
	public ShapeStyle getShapeStyle(Class clazz) {
		return getShapeStyle(clazz.getSimpleName());
	}
	
	public ShapeStyle getShapeStyle(ShapeStyle.Category category) {
		return getShapeStyle(category.name());
	}
	
	public ShapeStyle getShapeStyle(String name) {
		ShapeStyle ss = shapeStyles.get(name);
		if (ss==null) {
			if (instancePreferenceCache!=null) {
				// check the Instance Preferences cache first
				if (instancePreferenceCache.shapeStyles.containsKey(name)) {
					ss = new ShapeStyle( instancePreferenceCache.shapeStyles.get(name) );
				}
				else {
					// this has not been cached yet: check if contained
					// in Instance Preference Store
					String path = getShapeStylePath(TargetRuntime.getDefaultRuntime());
					Preferences prefs = instancePreferences.node(path);
					String value = prefs.get(name,"");
					if (!value.isEmpty()) {
						// found! save it in Instance Preferences cache
						ss = ShapeStyle.decode(value);
						instancePreferenceCache.shapeStyles.put(name,ss);
					}
					else {
						// check if this key is in Default Preferences store
						prefs = defaultPreferences.node(path);
						value = prefs.get(name,"");
						if (!value.isEmpty()) {
							ss = ShapeStyle.decode(value);
						}
					}
				}
			}
			if (ss==null) {
				String key = getShapeStyleKey(getRuntime(), name);
				String value = get(key, ""); //$NON-NLS-1$
				if (value.isEmpty())
					ss = new ShapeStyle();
				else
					ss = ShapeStyle.decode(value);
			}
			ss.setObject(name);
			ss.setRuntime(targetRuntime);
			shapeStyles.put(name, ss);
		}
		return ss;
	}

	public void setShapeStyle(EObject object, ShapeStyle ss) {
		Class clazz;
		try {
			clazz = Class.forName(object.eClass().getInstanceClassName());
			setShapeStyle(clazz, ss);
		} catch (ClassNotFoundException e) {
			setShapeStyle(object.getClass(), ss);
		}
	}
	
	public void setShapeStyle(Class clazz, ShapeStyle style) {
		setShapeStyle(clazz.getSimpleName(), style);
	}
	
	public void setShapeStyle(String name, ShapeStyle style) {
		if (style.isDirty()) {
			shapeStyles.put(name, style);
			if (instancePreferenceCache!=null) {
				instancePreferenceCache.shapeStyles.put(name, style);
			}
			String key = getShapeStyleKey(getRuntime(), name);
			String value = ShapeStyle.encode(style);
			put(key, value);
			style.setDirty(false);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// Getters and setters for Tool Profiles
	////////////////////////////////////////////////////////////////////////////////

	public static String getToolProfilePath(TargetRuntime rt) {
		return PREF_TOOL_PROFILE + "/" + rt.getId(); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public String getDefaultToolProfile(TargetRuntime rt) {
		try {
			Preferences prefs = null;
			String path = getToolProfilePath(rt);
			if (projectPreferences!=null && projectPreferences.nodeExists(path))
				prefs = projectPreferences.node(path);
			else if (instancePreferences.nodeExists(path))
				prefs = instancePreferences.node(path);
			else if (defaultPreferences.nodeExists(path))
				prefs = defaultPreferences.node(path);

			if (prefs!=null) {
				for (String p : prefs.keys()) {
					if (prefs.getBoolean(p, false))
						return p;
				}
			}
		}
		catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return ""; //$NON-NLS-1$
	}
	
	public boolean setDefaultToolProfile(TargetRuntime rt, String profileId) {
		boolean result = false;
		if (profileId!=null && !profileId.isEmpty()) {
			try {
				Preferences prefs = null;
				String path = getToolProfilePath(rt);
				String keys[] = null;
				if (projectPreferences!=null && useProjectPreferences) {
					if (!projectPreferences.nodeExists(path))
						keys = defaultPreferences.node(path).keys();
					prefs = projectPreferences.node(path);
				}
				else {
					if (!instancePreferences.nodeExists(path))
						keys = defaultPreferences.node(path).keys();
					prefs = instancePreferences.node(path);
				}
	
				if (keys==null)
					keys = prefs.keys();

				for (String p : keys) {
					if (profileId.equals(p)) {
						prefs.putBoolean(p, true);
						result = true;
					}
					else {
						prefs.putBoolean(p, false);
					}
				}
				
				firePreferenceEvent(prefs, path, null, profileId);
			}
			catch (BackingStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public ModelEnablementDescriptor createToolProfile(TargetRuntime rt, String profileId, String profileName, String description) {
		ModelEnablementDescriptor med = null;
		boolean createNew = false;
		if (profileId!=null && !profileId.isEmpty()) {
			try {
				Preferences prefs = null;
				String path = getToolProfilePath(rt);
				String keys[] = null;
				boolean populate = false;
				if (projectPreferences!=null && useProjectPreferences) {
					if (!projectPreferences.nodeExists(path)) {
						populate = true;
						keys = defaultPreferences.node(path).keys();
					}
					prefs = projectPreferences.node(path);
				}
				else {
					if (!instancePreferences.nodeExists(path)) {
						keys = defaultPreferences.node(path).keys();
						populate = true;
					}
					prefs = instancePreferences.node(path);
				}
	
				if (keys==null)
					keys = prefs.keys();

				createNew = true;
				for (String p : keys) {
					if (profileId.equals(p)) {
						if (rt.getModelEnablements(profileId)!=null)
							createNew = false;
					}
					if (populate)
						prefs.putBoolean(p, false);
				}
				if (createNew) {
					med = new ModelEnablementDescriptor(rt, profileId);
					med.setProfileName(profileName);
					med.setDescription(description);
					rt.getModelEnablementDescriptors().add(med);
					prefs.putBoolean(profileId, true);
					firePreferenceEvent(prefs, path, null, profileId);
				}
			}
			catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
		return med;
	}

	public boolean deleteToolProfile(TargetRuntime rt, String profileId) {
		boolean result = false;
		if (profileId!=null && !profileId.isEmpty()) {
			try {
				Preferences prefs = null;
				String path = getToolProfilePath(rt);
				String keys[] = null;
				boolean populate = false;
				if (projectPreferences!=null && useProjectPreferences) {
					if (!projectPreferences.nodeExists(path)) {
						keys = defaultPreferences.node(path).keys();
						populate = true;
					}
					prefs = projectPreferences.node(path);
				}
				else {
					if (!instancePreferences.nodeExists(path)) {
						keys = defaultPreferences.node(path).keys();
						populate = true;
					}
					prefs = instancePreferences.node(path);
				}
	
				if (keys==null)
					keys = prefs.keys();
	
				for (String p : keys) {
					if (populate)
						prefs.putBoolean(p, false);
					if (profileId.equals(p)) {
						result = true;
						prefs.remove(p);
					}
				}
				if (result && prefs.keys().length>0) {
					prefs.putBoolean(prefs.keys()[0], true);
					firePreferenceEvent(prefs, path, profileId, null);
				}
			}
			catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public String[] getAllToolProfiles(TargetRuntime rt) {
		List<String> profiles = new ArrayList<String>();
		try {
			Preferences prefs = null;
			String path = getToolProfilePath(rt);
			if (projectPreferences!=null && projectPreferences.nodeExists(path))
				prefs = projectPreferences.node(path);
			else if (instancePreferences.nodeExists(path))
				prefs = instancePreferences.node(path);
			else if (defaultPreferences.nodeExists(path))
				prefs = defaultPreferences.node(path);
		
			if (prefs!=null) {
				for (String profileId : prefs.keys()) {
					profiles.add(profileId);
					if (rt.getModelEnablements(profileId) == null) {
						getModelEnablements(rt, profileId);
					}
				}
				Collections.sort(profiles);
			}
		}
		catch (BackingStoreException e) {
			e.printStackTrace();
		}
		
		return profiles.toArray(new String[profiles.size()]);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// Getters and setters for Model Enablements
	////////////////////////////////////////////////////////////////////////////////

	public static String getModelEnablementsPath(TargetRuntime rt, String profileId) {
		if (profileId==null || profileId.isEmpty())
			profileId = "default";
		return PREF_MODEL_ENABLEMENT + "/" + rt.getId() + "/" + profileId; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public ModelEnablements getModelEnablements(String profileId) {
		return getModelEnablements(getRuntime(), profileId);
	}
	
	public ModelEnablements getModelEnablements(TargetRuntime rt, String profileId) {
		ModelEnablements me = new ModelEnablements(rt, profileId);
		try {
			Preferences prefs = null;
			String path = getModelEnablementsPath(rt, profileId);
			if (projectPreferences!=null && projectPreferences.nodeExists(path))
				prefs = projectPreferences.node(path);
			else if (instancePreferences.nodeExists(path))
				prefs = instancePreferences.node(path);
			else if (defaultPreferences.nodeExists(path))
				prefs = defaultPreferences.node(path);
			
			if (prefs!=null) {
				boolean create = false;
				ModelEnablementDescriptor med = rt.getModelEnablements(profileId);
				if (med==null) {
					String profileName = prefs.get("name","Unnamed Profile");
					String description = prefs.get("description","");
					med = createToolProfile(rt, profileId, profileName, description);
					create = true;
				}

				me.setEnabledAll(false);
				for (String k : prefs.keys()) {
					if (prefs.getBoolean(k, false)) {
						me.setEnabled(k, true);
						if (create) {
							med.setEnabled(k, true);
						}
					}
				}
			}
		}
		catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return me;
	}
	
	public boolean setModelEnablements(TargetRuntime rt, String profileId, ModelEnablements me) {
		if (me!=null && me.size()>0) {
			try {
				Preferences prefs = null;
				String path = getModelEnablementsPath(rt, profileId);
				if (projectPreferences!=null && useProjectPreferences) {
					prefs = projectPreferences.node(path);
					prefs.removeNode();
					prefs = projectPreferences.node(path);
				}
				else {
					prefs = instancePreferences.node(path);
					prefs.removeNode();
					prefs = instancePreferences.node(path);
				}
			
				ModelEnablementDescriptor med = rt.getModelEnablements(profileId);
				if (med!=null) {
					prefs.put("name", med.getProfileName());
					prefs.put("description", med.getDescription());
				}
				for (String s : me.getAllEnabled()) {
					prefs.putBoolean(s, true);
				}
				firePreferenceEvent(prefs, path, null, profileId);
				return true;
			}
			catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// Getters and setters for miscellaneous preferences
	////////////////////////////////////////////////////////////////////////////////

	public TargetRuntime getRuntime() {
		if (targetRuntime==null) {
			targetRuntime = TargetRuntime.getDefaultRuntime();
			Display.getDefault().asyncExec( new Runnable() {
				@Override
				public void run() {
					String id = get(PREF_TARGET_RUNTIME,TargetRuntime.getFirstNonDefaultId());
					if (id==null || id.isEmpty())
						id = TargetRuntime.getFirstNonDefaultId();

					targetRuntime = TargetRuntime.getDefaultRuntime();
					MessageDialog.openError(
						Display.getDefault().getActiveShell(),
						Messages.Bpmn2Preferences_No_Runtime_Plugin_Title,
						NLS.bind(
							Messages.Bpmn2Preferences_No_Runtime_Plugin_Message,
							id,
							targetRuntime.getDescription()
						)
					);
				}
				
			});
					
		}
		return targetRuntime;
	}

	public void setRuntime(TargetRuntime rt) {
		Assert.isTrue(rt!=null);
		put(PREF_TARGET_RUNTIME, rt.getId());
		targetRuntime = rt;
	}
	
	public boolean getShowAdvancedPropertiesTab() {
		return showAdvancedPropertiesTab;
	}
	
	public void setShowAdvancedPropertiesTab(boolean show) {
		putBoolean(PREF_SHOW_ADVANCED_PROPERTIES, show);
		showAdvancedPropertiesTab = show;
	}
	
	public boolean getShowDescriptions() {
		return showDescriptions;
	}
	
	public void setShowDescriptions(boolean show) {
		putBoolean(PREF_SHOW_DESCRIPTIONS, show);
		showDescriptions = show;
	}
	
	public boolean getShowIdAttribute() {
		return showIdAttribute;
	}
	
	public void setShowIdAttribute(boolean show) {
		putBoolean(PREF_SHOW_ID_ATTRIBUTE, show);
		showIdAttribute = show;
	}
	
	public boolean getCheckProjectNature() {
		return checkProjectNature;
	}
	
	public void setCheckProjectNature(boolean show) {
		putBoolean(PREF_CHECK_PROJECT_NATURE, show);
		checkProjectNature = show;
	}
	
	public boolean getSimplifyLists() {
		return simplifyLists;
	}
	
	public void setSimplifyLists(boolean simplify) {
		putBoolean(PREF_SIMPLIFY_LISTS,simplify);
		simplifyLists = simplify;
	}
	
	public boolean getUsePopupDialogForLists() {
		return usePopupDialogForLists;
	}
	
	public void setUsePopupDialogForLists(boolean enable) {
		putBoolean(PREF_USE_POPUP_DIALOG_FOR_LISTS,enable);
		usePopupDialogForLists = enable;
	}
	
	public boolean getShowPopupConfigDialog(Object context) {
		if (popupConfigDialog!=0) {
			if (context instanceof Task || context instanceof ChoreographyActivity) {
				return popupConfigDialogFor[0];
			}
			if (context instanceof Gateway) {
				return popupConfigDialogFor[1];
			}
			if (context instanceof Event) {
				return popupConfigDialogFor[2];
			}
			if (context instanceof EventDefinition) {
				if (context instanceof CancelEventDefinition || context instanceof TerminateEventDefinition)
					return false; // these have no additional attributes
				return popupConfigDialogFor[3];
			}
			if (context instanceof ItemAwareElement || context instanceof Message) {
				return popupConfigDialogFor[4];
			}
			if (context instanceof InteractionNode || context instanceof FlowElementsContainer) {
				return popupConfigDialogFor[5];
			}
		}
		return false;
	}
	
	public boolean hasPopupConfigDialog(Object context) {
		if (context instanceof Activity) {
			return true;
		}
		if (context instanceof Gateway) {
			return true;
		}
		if (context instanceof Event) {
			return true;
		}
		if (context instanceof EventDefinition) {
			if (context instanceof CancelEventDefinition || context instanceof TerminateEventDefinition)
				return false; // these have no additional attributes
			return true;
		}
		if (context instanceof ItemAwareElement || context instanceof Message) {
			return true;
		}
		if (context instanceof InteractionNode
				|| context instanceof FlowElementsContainer
				|| context instanceof CallChoreography) {
			return true;
		}
		// TODO: figure out a better way: does everything have configurable attributes now?
		return true;
	}
	
	public void setShowPopupConfigDialog(Object context, boolean value) {
		putInt(PREF_POPUP_CONFIG_DIALOG,  value ? 1 : 0);
		popupConfigDialog = value ? 1 : 0;
	}
	
	public boolean getDoCoreValidation() {
		return doCoreValidation;
	}
	
	public void setDoCoreValidation(boolean enable) {
		putBoolean(PREF_DO_CORE_VALIDATION,enable);
		doCoreValidation = enable;
	}
	
	public boolean getPropagateGroupCategories() {
		return propagateGroupCategories;
	}
	
	public void setPropagateGroupCategories(boolean enable) {
		putBoolean(PREF_PROPAGATE_GROUP_CATEGORIES,enable);
		propagateGroupCategories = enable;
	}
	
	public boolean getAllowMultipleConnections() {
		return allowMultipleConnections;
	}
	
	public void setAllowMultipleConnections(boolean enable) {
		putBoolean(PREF_ALLOW_MULTIPLE_CONNECTIONS,enable);
		allowMultipleConnections = enable;
	}

	public boolean isHorizontalDefault() {
		return isHorizontal==BPMNDIAttributeDefault.ALWAYS_TRUE ||
				isHorizontal==BPMNDIAttributeDefault.DEFAULT_TRUE;
	}

	public BPMNDIAttributeDefault getIsHorizontal() {
		return isHorizontal;
	}
	
	public void setIsHorizontal(BPMNDIAttributeDefault value) {
		setBPMNDIAttributeDefault(PREF_IS_HORIZONTAL, value);
		this.isHorizontal = value;
	}

	public boolean isExpandedDefault() {
		return isExpanded==BPMNDIAttributeDefault.ALWAYS_TRUE ||
				isExpanded==BPMNDIAttributeDefault.DEFAULT_TRUE;
	}

	public BPMNDIAttributeDefault getIsExpanded() {
		return isExpanded;
	}

	public void setIsExpanded(BPMNDIAttributeDefault value) {
		setBPMNDIAttributeDefault(PREF_IS_EXPANDED, value);
		this.isExpanded = value;
	}

	public BPMNDIAttributeDefault getIsMessageVisible() {
		return isMessageVisible;
	}

	public void setIsMessageVisible(BPMNDIAttributeDefault value) {
		setBPMNDIAttributeDefault(PREF_IS_MESSAGE_VISIBLE, value);
		this.isMessageVisible = value;
	}

	public BPMNDIAttributeDefault getIsMarkerVisible() {
		return isMarkerVisible;
	}

	public void setIsMarkerVisible(BPMNDIAttributeDefault value) {
		setBPMNDIAttributeDefault(PREF_IS_MARKER_VISIBLE, value);
		this.isMarkerVisible = value;
	}
	
	public boolean getSaveBPMNLabels() {
		return saveBPMNLabels;
	}
	
	public void setSaveBPMNLabels(boolean enable) {
		putBoolean(PREF_SAVE_BPMNLABELS,enable);
		saveBPMNLabels = enable;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	
	public void setConnectionTimeout(int value) {
		putInt(PREF_CONNECTION_TIMEOUT, value);
		connectionTimeout = value;
	}

	public int getResolveExternals() {
		return resolveExternals;
	}
	
	public void setResolveExternals(int value) {
		putInt(PREF_RESOLVE_EXTERNALS, value);
		resolveExternals = value;
	}
	
	// this is temporary until the connection routing has been proven reliable
	static boolean enableConnectionRouting = true;
	public boolean getEnableConnectionRouting() {
		return enableConnectionRouting;
	}
	
	public void setEnableConnectionRouting(boolean enable) {
		this.enableConnectionRouting = enable;
	}

	public static String[] getBPMNDIAttributeDefaultChoices() {
		BPMNDIAttributeDefault[] values = BPMNDIAttributeDefault.values();
		String[] choices = new String[values.length];
		int i = 0;
		for (BPMNDIAttributeDefault v : values) {
			String text = Messages.Bpmn2Preferences_None;
			switch (v) {
			case USE_DI_VALUE:
				text = Messages.Bpmn2Preferences_False_if_not_set;
				break;
			case DEFAULT_TRUE:
				text = Messages.Bpmn2Preferences_True_if_not_set;
				break;
			case ALWAYS_TRUE:
				text = Messages.Bpmn2Preferences_Always_true;
				break;
			case ALWAYS_FALSE:
				text = Messages.Bpmn2Preferences_Always_False;
				break;
			}
			choices[i++] = text;
		}
		return choices;
	}
	
	public static String[][] getBPMNDIAttributeDefaultChoicesAndValues() {
		String[] choices = getBPMNDIAttributeDefaultChoices();
		BPMNDIAttributeDefault[] values = BPMNDIAttributeDefault.values();
		String[][] choicesAndValues = new String[choices.length][2];
		int i = 0;
		for (BPMNDIAttributeDefault v : values) {
			choicesAndValues[i][0] = choices[i];
			choicesAndValues[i][1] = v.name();
			++i;
		}
		return choicesAndValues;
	}
	
	/**
	 * Applies preference defaults to a BPMNShape object. The <code>attribs</code> map should contain
	 * only those attributes that are set on the BPMNShape object (as read from the bpmn XML file).
	 * This is used to determine the appropriate default values for certain optional attributes, e.g.
	 * isHorizontal, isExpanded, etc.
	 * 
	 * @param bpmnShape - the BPMNShape object whose attributes are to be set
	 * @param attribs - map of BPMN DI attributes currently set on the BPMNShape object. May be null.
	 * @see getIsHorizontal(), getIsExpanded(), getIsMessageVisible() and getIsMarkerVisible()
	 */
	public void applyBPMNDIDefaults(BPMNShape bpmnShape, Map<String,String>attribs) {
		boolean isHorizontalSet = false;
		boolean isExpandedSet = false;
		boolean isMessageVisibleSet = false;
		boolean isMarkerVisibleSet = false;
		boolean choreographyActivityShapeSet = false;
		
		if (attribs != null) {
			for (Entry<String, String> entry : attribs.entrySet()) {
				String name = entry.getKey();
				if ("isHorizontal".equals(name)) { //$NON-NLS-1$
					isHorizontalSet = true;
				}
				if ("isExpanded".equals(name)) { //$NON-NLS-1$
					isExpandedSet = true;
				}
				if ("isMessageVisible".equals(name)) { //$NON-NLS-1$
					isMessageVisibleSet = true;
				}
				if ("isMarkerVisible".equals(name)) { //$NON-NLS-1$
					isMarkerVisibleSet = true;
				}
				if ("choreographyActivityShape".equals(name)) { //$NON-NLS-1$
					choreographyActivityShapeSet = true;
				}
			}
		}
		
		BaseElement be = bpmnShape.getBpmnElement();
		
		// isHorizontal only applies to Pools and Lanes, not Participant bands
		if (!isHorizontalSet) {
			if ((be instanceof Participant && !choreographyActivityShapeSet) || be instanceof Lane) {
				boolean horz = isHorizontalDefault();
				bpmnShape.setIsHorizontal(horz);
			}
		}
		else {
			if ((be instanceof Participant && !choreographyActivityShapeSet) || be instanceof Lane) {
				BPMNDIAttributeDefault df = getIsHorizontal();
				switch(df) {
				case ALWAYS_TRUE:
					bpmnShape.setIsHorizontal(true);
					break;
				case ALWAYS_FALSE:
					bpmnShape.setIsHorizontal(false);
					break;
				}

			}
		}
		
		// isExpanded only applies to activity containers (SubProcess, AdHocSubProcess, etc.)
		if (!isExpandedSet) {
			if (be instanceof  SubProcess ||
					be instanceof AdHocSubProcess ||
					be instanceof Transaction ||
					be instanceof SubChoreography ||
					be instanceof CallActivity ||
					be instanceof CallChoreography) {
				boolean value = false;
				BPMNDIAttributeDefault df = getIsExpanded();
				switch(df) {
				case ALWAYS_TRUE:
				case DEFAULT_TRUE:
					value = true;
					break;
				case ALWAYS_FALSE:
				case USE_DI_VALUE:
					value = false;
				}
				bpmnShape.setIsExpanded(value);
			}
		}
		else {
			if (be instanceof  SubProcess ||
					be instanceof AdHocSubProcess ||
					be instanceof Transaction ||
					be instanceof SubChoreography ||
					be instanceof CallActivity ||
					be instanceof CallChoreography) {
				BPMNDIAttributeDefault df = getIsExpanded();
				switch(df) {
				case ALWAYS_TRUE:
					bpmnShape.setIsExpanded(true);
					break;
				case ALWAYS_FALSE:
					bpmnShape.setIsExpanded(false);
					break;
				}
			}
		}
		
		// isMessageVisible only applies to Participant Bands
		if (!isMessageVisibleSet) {
			if (be instanceof Participant && choreographyActivityShapeSet) {
				boolean value = false;
				BPMNDIAttributeDefault df = getIsMessageVisible();
				switch(df) {
				case ALWAYS_TRUE:
				case DEFAULT_TRUE:
					value = true;
					break;
				case ALWAYS_FALSE:
				case USE_DI_VALUE:
					value = false;
				}
				bpmnShape.setIsMessageVisible(value);
			}
		}
		else {
			if (be instanceof Participant && choreographyActivityShapeSet) {
				BPMNDIAttributeDefault df = getIsMessageVisible();
				switch(df) {
				case ALWAYS_TRUE:
					bpmnShape.setIsMessageVisible(true);
					break;
				case ALWAYS_FALSE:
					bpmnShape.setIsMessageVisible(false);
					break;
				}
			}
		}
		
		// isMarkerVisible only applies to ExclusiveGateway
		if (!isMarkerVisibleSet) {
			if (be instanceof ExclusiveGateway) {
				BPMNDIAttributeDefault df = getIsMarkerVisible();
				switch(df) {
				case ALWAYS_TRUE:
				case DEFAULT_TRUE:
					bpmnShape.setIsMarkerVisible(true);
					break;
				case ALWAYS_FALSE:
				case USE_DI_VALUE:
					bpmnShape.setIsMarkerVisible(false);
					break;
				}
			}
		}
		else {
			if (be instanceof ExclusiveGateway) {
				BPMNDIAttributeDefault df = getIsMarkerVisible();
				switch(df) {
				case ALWAYS_TRUE:
					bpmnShape.setIsMarkerVisible(true);
					break;
				case ALWAYS_FALSE:
					bpmnShape.setIsMarkerVisible(false);
					break;
				}
			}
		}
	}

	public void setToDefault(String key) {
		try {
			if (key.startsWith(PREF_TOOL_PROFILE)) {
				Preferences prefs = instancePreferences.node(key);
				prefs.removeNode();
			}
			else if (key.startsWith(PREF_MODEL_ENABLEMENT)) {
				Preferences prefs = instancePreferences.node(key);
				prefs.removeNode();
			}
			else if (key.startsWith(PREF_SHAPE_STYLE)) {
				shapeStyles.clear();
				Preferences prefs = instancePreferences.node(key);
				instancePreferenceCache = null;
				prefs.removeNode();
			}
			else if (key.startsWith(PREF_TARGET_RUNTIME)) {
				if (shouldSaveToProjectPreference(PREF_TARGET_RUNTIME)) {
					projectPreferences.put(PREF_TARGET_RUNTIME, TargetRuntime.DEFAULT_RUNTIME_ID);
				}
			}
			else {
				if (projectPreferences!=null)
					projectPreferences.remove(key);
				instancePreferences.remove(key);
			}
		}
		catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	// Listeners
	////////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		int type = event.getType();
		if (type==IResourceChangeEvent.PRE_CLOSE) {
			try {
				flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
			dispose();
		}
		if (type==IResourceChangeEvent.PRE_DELETE)
			dispose();
	}
	
	public void addPreferenceChangeListener(IPreferenceChangeListener listener) {
		if (preferenceChangeListeners == null)
			preferenceChangeListeners = new ListenerList();
		preferenceChangeListeners.add(listener);
	}
	
	public void removePreferenceChangeListener(IPreferenceChangeListener listener) {
		if (preferenceChangeListeners == null)
			return;
		preferenceChangeListeners.remove(listener);
		if (preferenceChangeListeners.size() == 0)
			preferenceChangeListeners = null;
	}
	
	protected void firePreferenceEvent(Preferences node, String key, Object oldValue, Object newValue) {
		if (preferenceChangeListeners == null)
			return;
		if (oldValue==null) {
			if (newValue==null)
				return;
		}
		else {
			if (oldValue.equals(newValue))
				return;
		}
		Object[] listeners = preferenceChangeListeners.getListeners();
		final String absolutePath = node.absolutePath() + "/" + key; //$NON-NLS-1$
		final PreferenceChangeEvent event = new PreferenceChangeEvent(node, absolutePath, oldValue, newValue);
		for (int i = 0; i < listeners.length; i++) {
			final IPreferenceChangeListener listener = (IPreferenceChangeListener) listeners[i];
			ISafeRunnable job = new ISafeRunnable() {
				public void handleException(Throwable exception) {
					// already logged in Platform#run()
				}

				public void run() throws Exception {
					listener.preferenceChange(event);
				}
			};
			SafeRunner.run(job);
		}
	}
	
	// TODO: use CNF for indigo & future - keep ResourceNavigator for backward compatibility
	public static IProject getActiveProject() {
		if (activeProject!=null)
			return activeProject;
		
		IWorkbench workbench = PlatformUI.getWorkbench(); 
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window!=null) {
			IWorkbenchPage page = window.getActivePage();
			if (page!=null) {
				IViewPart[] parts = page.getViews();
		
				for (int i = 0; i < parts.length; i++) {
					if (parts[i] instanceof ResourceNavigator) {
						ResourceNavigator navigator = (ResourceNavigator) parts[i];
						StructuredSelection sel = (StructuredSelection) navigator.getTreeViewer().getSelection();
						IResource resource = (IResource) sel.getFirstElement();
						if (resource!=null) {
							activeProject = resource.getProject();
							break;
						}
					}
				}
			}
		}
		return activeProject;
	}

	public static void setActiveProject(IProject project) {
		activeProject = project;
	}

	////////////////////////////////////////////////////////////////////////////////
	// General getters and setters
	////////////////////////////////////////////////////////////////////////////////
	/**
	 * Helper class that parses a Preferences path string into a node path and key.
	 * This can then be used to get or set the preference using the search order:
	 * 
	 *    Project Preferences - only used if explicitly set using {@link Bpmn2Preferences#useProjectPreferences()}
	 *    Instance Preferences - used when setting a Preference; used when getting if the node and key exist
	 *    Default Preferences - used only when getting a Preference
	 */
	private class PreferencesHelper {
		public IEclipsePreferences root;
		public Preferences node;
		public String key;
		public String path;
		private boolean set;
		
		public PreferencesHelper(String key, boolean set) {
			this.set = set;
			try {
				path = ""; //$NON-NLS-1$
				if (set) {
					int i = key.lastIndexOf("/"); //$NON-NLS-1$
					if (i>0) {
						path = key.substring(0, i);
						this.key = key = key.substring(i+1);
						if (shouldSaveToProjectPreference(key))
							root = projectPreferences;
						else
							root = instancePreferences;
						node = root.node(path);
					}
					else {
						this.key = key;
						if (shouldSaveToProjectPreference(key))
							node = root = projectPreferences;
						else
							node = root = instancePreferences;
					}		
				}
				else {
					int i = key.lastIndexOf("/"); //$NON-NLS-1$
					if (i>0) {
						path = key.substring(0, i);
						this.key = key = key.substring(i+1);
						if (projectPreferences!=null && projectPreferences.nodeExists(path) && keyExists(projectPreferences.node(path),key)) {
							root = projectPreferences;
						}
						else if (instancePreferences.nodeExists(path) && keyExists(instancePreferences.node(path),key)) {
							root = instancePreferences;
						}
						else if (defaultPreferences.nodeExists(path) && keyExists(defaultPreferences.node(path),key)) {
							root = defaultPreferences;
						}
						if (root!=null)
							node = root.node(path);
					}
					else {
						this.key = key;
						if (projectPreferences!=null && keyExists(projectPreferences,key))
							node = root = projectPreferences;
						else if (keyExists(instancePreferences,key))
							node = root = instancePreferences;
						else if (keyExists(defaultPreferences,key))
							node = root = defaultPreferences;
					}
				}
			}
			catch (BackingStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public Preferences unset() {
			node.remove(key);
			if (!path.isEmpty()) {
				node = root.node(path + "/" + key); //$NON-NLS-1$
			}
			return node;
		}
		
		public String getString(String defaultValue) {
			Assert.isTrue(!set);
			if (node!=null)
				return node.get(key, defaultValue);
			return defaultValue;
		}
		
		public void put(String value) {
			Assert.isTrue(set && node!=null);
			String oldValue = node.get(key, null);
			node.put(key, value);
			firePreferenceEvent(node, key, oldValue, value);
			dirty = true;
		}
		
		public Boolean getBoolean(boolean defaultValue) {
			return Boolean.parseBoolean( getString("false") ); //$NON-NLS-1$
		}
		
		public void putBoolean(boolean value) {
			put(Boolean.toString(value));
		}
		
		public int getInt(int defaultValue) {
			return Integer.parseInt( getString("0") ); //$NON-NLS-1$
		}
		
		public void putInt(int value) {
			put(Integer.toString(value));
		}
		
		private boolean keyExists(Preferences prefs, String key) {
			try {
				for (String k : prefs.keys()) {
					if (k.equals(key)) {
						return true;
					}
				}
			}
			catch (BackingStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		PreferencesHelper helper = new PreferencesHelper(key, false);
		return helper.getBoolean(defaultValue);
	}
	
	public void putBoolean(String key, boolean value) {
		PreferencesHelper helper = new PreferencesHelper(key, true);
		helper.putBoolean(value);
	}

	public int getInt(String key, int defaultValue) {
		PreferencesHelper helper = new PreferencesHelper(key, false);
		return helper.getInt(defaultValue);
	}
	
	public void putInt(String key, int value) {
		PreferencesHelper helper = new PreferencesHelper(key, true);
		helper.putInt(value);
	}
	
	public String get(String key, String defaultValue) {
		PreferencesHelper helper = new PreferencesHelper(key, false);
		return helper.getString(defaultValue);
	}
	
	public void put(String key, String value) {
		PreferencesHelper helper = new PreferencesHelper(key, true);
		helper.put(value);

	}

	public Hashtable<String,String> getServiceImplementations() {
		String value = get(PREF_SERVICE_IMPLEMENTATIONS, ""); //$NON-NLS-1$
		Hashtable<String,String> impls = new Hashtable<String,String>();
		for (String s : value.split("\t")) { //$NON-NLS-1$
			if (!s.isEmpty()) {
				String a[] = s.split(";");
				if (a.length>1)
					impls.put(a[0], a[1]);
				else
					impls.put(a[0], a[0]);
			}
		}
		return impls;
	}
	
	private void putServiceImplementations(Hashtable<String,String> impls) {
		String value = ""; //$NON-NLS-1$
		Iterator<Entry<String, String>> iter = impls.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			if (!entry.getKey().isEmpty()) {
				value += entry.getKey() + ";" + entry.getValue();
				if (iter.hasNext())
					value += "\t"; //$NON-NLS-1$
			}
		}
		put(PREF_SERVICE_IMPLEMENTATIONS, value);
	}
	
	public void addServiceImplementation(String name, String uri) {
		Hashtable<String,String> impls = getServiceImplementations();
		if (!impls.contains(name)) {
			impls.put(name, uri);
			putServiceImplementations(impls);
		}
	}
	
	public void removeServiceImplementation(String name) {
		Hashtable<String,String> impls = getServiceImplementations();
		if (impls.containsKey(name)) {
			impls.remove(name);
			putServiceImplementations(impls);
		}
	}
	
	public BPMNDIAttributeDefault getBPMNDIAttributeDefault(String key, BPMNDIAttributeDefault defaultValue) {
		PreferencesHelper helper = new PreferencesHelper(key, false);
		String str = helper.getString(defaultValue.name());
		return BPMNDIAttributeDefault.valueOf(str);
	}
	
	public void setBPMNDIAttributeDefault(String key, BPMNDIAttributeDefault value) {
		PreferencesHelper helper = new PreferencesHelper(key, true);
		helper.put(value.name());
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		firePreferenceEvent(instancePreferences, event.getProperty(), event.getOldValue(), event.getNewValue());
	
		// notify all other Bpmn2Preferences instances (if any)
		if (projectPreferenceCacheMap!=null) {
			for (Entry<IProject, Bpmn2Preferences> entry : projectPreferenceCacheMap.entrySet()) {
				Bpmn2Preferences pref = entry.getValue();
				if (pref!=this)
					pref.firePreferenceEvent(instancePreferences, event.getProperty(), event.getOldValue(), event.getNewValue());
			}
		}
	}
}
