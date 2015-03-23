/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.runtime;

import java.net.URL;

import org.eclipse.bpmn2.modeler.core.runtime.ToolPaletteDescriptor.CategoryDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.ToolPaletteDescriptor.ToolDescriptor;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.internal.GraphitiUIPlugin;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

/**
 * Image provider class for our Custom Task extensions.
 * 
 * TODO: In Kepler, this may change at which time we can register these icons
 * in the plugin.xml as a Graphiti extension point. If this doesn't happen,
 * we should probably consider pushing image registration up to the core editor.
 * 
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=366452
 * @author bbrodt
 */
public class CustomTaskImageProvider {
	
	public final static String ICONS_FOLDER = "icons/"; //$NON-NLS-1$
	public final static String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId("BPMN2"); //$NON-NLS-1$

	// Sneaky tip: The values of this enum correspond to the subfolder names in "icons"
	public enum IconSize {
		SMALL("small"), //$NON-NLS-1$
		LARGE("large"); //$NON-NLS-1$
		String value;
		IconSize(String value) {
			this.value = value;
		}
	}
	private static boolean registered = false;

	public CustomTaskImageProvider(Package pluginPackage) {
		
		super();
	}

	public static void registerAvailableImages() {
		if (!registered) {
			for (TargetRuntime rt : TargetRuntime.createTargetRuntimes()) {
				for (CustomTaskDescriptor ctd : rt.getCustomTaskDescriptors()) {
					String icon = ctd.getIcon();
					if (icon!=null)
						registerImage(ctd, icon);
				}
				for (ToolPaletteDescriptor tp : rt.getToolPaletteDescriptors()) {
					for (CategoryDescriptor cd : tp.getCategories()) {
						for (ToolDescriptor td : cd.getTools()) {
							String icon = td.getIcon();
							if (icon!=null)
								registerImage(rt, icon);
						}
					}
				}
			}
			registered = true;
		}
	}

	public static Image createImage(CustomTaskDescriptor ctd, GraphicsAlgorithmContainer ga, String icon, IconSize size) {
		// To create an image of a specific size, use the "huge" versions
		// to prevent pixelation when stretching a small image
		String imageId = ctd.getImageId(icon, size); 
		if (imageId != null) {
			Image img = Graphiti.getGaService().createImage(ga, imageId);
			img.setProportional(false);
			return img;
		}
		return null;
	}

	public static Image createImage(CustomTaskDescriptor ctd, GraphicsAlgorithmContainer ga, String icon, int w, int h) {
		// To create an image of a specific size, use the "huge" versions
		// to prevent pixelation when stretching a small image
		String imageId = ctd.getImageId(icon, IconSize.LARGE); 
		if (imageId != null) {
			Image img = Graphiti.getGaService().createImage(ga, imageId);
			img.setProportional(false);
			img.setWidth(w);
			img.setHeight(h);
			img.setStretchH(true);
			img.setStretchV(true);
			return img;
		}
		return null;
	}

	protected static void registerImage(CustomTaskDescriptor ctd, String icon) {
		for (IconSize size : IconSize.values()) {
			String imageId = ctd.getImageId(icon,size); 
			if (imageId != null) {
				String filename = ctd.getImagePath(icon,size);
				URL url = ctd.getFeatureContainer().getClass().getClassLoader().getResource(filename);
				ImageDescriptor descriptor =  ImageDescriptor.createFromURL(url);
				registerImage(imageId, descriptor);
			}
		}
	}

	public static void registerImage(String imageId, ImageDescriptor image) {
		ImageRegistry imageRegistry = GraphitiUIPlugin.getDefault().getImageRegistry();
		imageId = providerId + "||" + imageId; //$NON-NLS-1$
		if (imageRegistry.get(imageId) == null)
			imageRegistry.put(imageId, image);
	}
	
	public static Image createImage(TargetRuntime rt, GraphicsAlgorithmContainer ga, String icon, IconSize size) {
		// To create an image of a specific size, use the "huge" versions
		// to prevent pixelation when stretching a small image
		String imageId = getImageId(rt,icon, size); 
		if (imageId != null) {
			Image img = Graphiti.getGaService().createImage(ga, imageId);
			img.setProportional(false);
			return img;
		}
		return null;
	}

	public static Image createImage(TargetRuntime rt, GraphicsAlgorithmContainer ga, String icon, int w, int h) {
		// To create an image of a specific size, use the "huge" versions
		// to prevent pixelation when stretching a small image
		String imageId = getImageId(rt,icon, IconSize.LARGE); 
		if (imageId != null) {
			Image img = Graphiti.getGaService().createImage(ga, imageId);
			img.setProportional(false);
			img.setWidth(w);
			img.setHeight(h);
			img.setStretchH(true);
			img.setStretchV(true);
			return img;
		}
		return null;
	}

	protected static void registerImage(TargetRuntime rt, String icon) {
		for (IconSize size : IconSize.values()) {
			String imageId = getImageId(rt,icon,size); 
			if (imageId != null) {
				String filename = getImagePath(rt,icon,size);
				URL url = rt.getRuntimeExtension().getClass().getClassLoader().getResource(filename);
				ImageDescriptor descriptor =  ImageDescriptor.createFromURL(url);
				registerImage(imageId, descriptor);
			}
		}
	}
	

	public static String getImageId(TargetRuntime rt, String icon, IconSize size) {
		if (icon != null && icon.trim().length() > 0) {
			String prefix = rt.getRuntimeExtension().getClass().getPackage().getName();
			return prefix + "." + icon.trim() + "." + size.value; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}
	
	public static String getImagePath(TargetRuntime rt, String icon, IconSize size) {
		if (icon != null && icon.trim().length() > 0) {
			String prefix = rt.getRuntimeExtension().getClass().getPackage().getName();
			return CustomTaskImageProvider.ICONS_FOLDER + size.value + "/" + icon.trim(); //$NON-NLS-1$
		}
		return null;
	}
}
