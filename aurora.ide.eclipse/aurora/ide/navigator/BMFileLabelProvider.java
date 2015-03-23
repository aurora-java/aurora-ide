package aurora.ide.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.model.IWorkbenchAdapter;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraPlugin;
import aurora.ide.bm.BMUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.ImagesUtils;
import aurora.ide.helpers.LocaleMessage;

public class BMFileLabelProvider extends LabelProvider
// implements ICommonLabelProvider
{

	public BMFileLabelProvider() {
		super();
	}

	public final int LimitDescLength = 15;

	public String getText(Object element) {
		IResource resource = resolveObject(element);
		if (resource != null) {
			String resourceName = resource.getName();
			if (!resourceName.toLowerCase().endsWith(
					"." + AuroraConstant.BMFileExtension)) {
				return resourceName;
			}
			try {
				String text = getBMDescription(resource);
				if (text != null) {
					resourceName = resourceName + text;
				}
				return resourceName;
			} catch (final ApplicationException e) {
				return resourceName;
			}
		}
		return element == null ? "" : element.toString();//$NON-NLS-1$
	}

	private ResourceManager resourceManager;

	private ResourceManager getResourceManager() {
		if (resourceManager == null) {
			resourceManager = new LocalResourceManager(
					JFaceResources.getResources());
		}

		return resourceManager;
	}

	protected final Object getAdapter(Object sourceObject) {
		Class<?> adapterType = IWorkbenchAdapter.class;
		if (sourceObject == null) {
			return null;
		}
		if (adapterType.isInstance(sourceObject)) {
			return sourceObject;
		}

		if (sourceObject instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) sourceObject;

			Object result = adaptable.getAdapter(adapterType);
			if (result != null) {
				// Sanity-check
				Assert.isTrue(adapterType.isInstance(result));
				return result;
			}
		}

		if (!(sourceObject instanceof PlatformObject)) {
			Object result = Platform.getAdapterManager().getAdapter(
					sourceObject, adapterType);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public Image getImage(Object element) {

		IResource resource = resolveObject(element);
		if (resource == null)
			return null;

		if ("bm".equalsIgnoreCase(resource.getFileExtension())) {
			try {
				if (isHasOperation(resource)) {
					return ImagesUtils.getImage("sql.gif");
				}
			} catch (final ApplicationException e) {
				return ImagesUtils.getImage("bm.gif");
			}
			return ImagesUtils.getImage("bm.gif");
		}

		if (element instanceof IResource) {
			// obtain the base image by querying the element
			IWorkbenchAdapter adapter = (IWorkbenchAdapter) getAdapter(element);
			if (adapter == null) {
				return null;
			}
			ImageDescriptor descriptor = adapter.getImageDescriptor(element);
			if (descriptor == null) {
				return null;
			}

			return (Image) getResourceManager().get(descriptor);
		}
		return null;

		// if(resource instanceof IFolder){
		// return
		// AuroraPlugin.getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER).createImage();
		// }
		// String resourceName = resource.getName();
		// if(!resourceName.toLowerCase().endsWith("."+AuroraConstant.BMFileExtension)){
		// return
		// AuroraPlugin.getImageDescriptor(ISharedImages.IMG_OBJ_FILE).createImage();
		// }
		// try {
		// if(isHasOperation(resource)){
		// return
		// AuroraPlugin.getImageDescriptor(LocaleMessage.getString("sql.icon")).createImage();
		// }
		// } catch (final ApplicationException e) {
		// return
		// AuroraPlugin.getImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK).createImage();
		// }
		// return
		// AuroraPlugin.getImageDescriptor(LocaleMessage.getString("bm.icon")).createImage();
	}

	private IResource resolveObject(Object element) {
		IResource resource = null;
		if (element instanceof BMFile) {
			BMFile file = (BMFile) element;
			resource = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(file.getPath());
		}
		if (element instanceof IResource) {
			resource = (IResource) element;
		}
		return resource;
	}

	private String getBMDescription(IResource file) throws ApplicationException {
		String bmDesc = BMUtil.getBMDescription(file);
		if (bmDesc == null)
			return null;
		if (LimitDescLength >= bmDesc.length())
			return formatDesc(bmDesc);
		else {
			return formatDesc(bmDesc.substring(0, LimitDescLength - 3) + "...");
		}
	}

	private boolean isHasOperation(IResource file) throws ApplicationException {
		String OperationsNode = "operations";
		CompositeMap bmData = AuroraResourceUtil.loadFromResource(file);
		if (bmData == null)
			return false;
		return bmData.getChild(OperationsNode) != null;
	}

	private String formatDesc(String desc) {
		if (desc == null)
			return null;
		else {
			return " {" + desc + "}";
		}
	}
}