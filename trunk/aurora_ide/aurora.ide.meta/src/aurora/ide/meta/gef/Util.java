package aurora.ide.meta.gef;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import aurora.ide.meta.gef.editors.models.Input;

public class Util {
	/**
	 * 
	 * return editor type
	 * */
	public static String getType(CompositeMap field) {
		String object = field.getString("defaultEditor");
		if (supportEditor(object) != null) {
			return object;
		} else {

			if ("java.lang.Long".equals(field.getString("datatype"))) {
				return Input.NUMBER;
			}
			if ("java.lang.String".equals(field.getString("datatype"))) {
				return Input.TEXT;
			}
			if ("java.util.Date".equals(field.getString("datatype"))) {
				return Input.CAL;
			}
		}
		return Input.TEXT;
	}

	public static String supportEditor(String object) {
		for (String t : Input.INPUT_TYPES) {
			if (t.equalsIgnoreCase(object))
				return t;
		}
		return null;
	}

	static public IEditorPart getActiveEditor(IWorkbenchWindow fWorkbenchWindow) {
		IWorkbenchPage activePage = fWorkbenchWindow.getActivePage();
		if (activePage != null) {
			IEditorPart activeEditor = activePage.getActiveEditor();
			IWorkbenchPart activePart = activePage.getActivePart();
			if (activeEditor == activePart)
				return activeEditor;
		}
		return null;
	}

	static public IWorkbenchWindow getActiveWorkbenchWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	static public ISelection getSelection(IWorkbenchWindow window) {
		return window.getSelectionService().getSelection();
	}

	public static String[] evaluateEnclosingProject(
			IWorkbenchWindow fWorkbenchWindow) {
		return evaluateEnclosingProject(getSelection(fWorkbenchWindow),
				getActiveEditor(fWorkbenchWindow));
	}

	public static String evaluateEnclosingProject(IAdaptable adaptable) {
		IProject project = (IProject) adaptable.getAdapter(IProject.class);
		if (project == null) {
			IResource resource = (IResource) adaptable
					.getAdapter(IResource.class);
			if (resource != null) {
				project = resource.getProject();
			}
		}
		if (project != null && project.isAccessible()) {
			return project.getName();
		}
		return null;
	}

	public static String[] evaluateEnclosingProject(ISelection selection,
			IEditorPart activeEditor) {
		// always use the editor if active
		if (activeEditor != null) {
			String name = evaluateEnclosingProject(activeEditor
					.getEditorInput());
			if (name != null) {
				return new String[] { name };
			}
		} else if (selection instanceof IStructuredSelection) {
			HashSet res = new HashSet();
			for (Iterator iter = ((IStructuredSelection) selection).iterator(); iter
					.hasNext();) {
				Object curr = iter.next();
				if (curr instanceof IWorkingSet) {
					IWorkingSet workingSet = (IWorkingSet) curr;
					if (workingSet.isAggregateWorkingSet()
							&& workingSet.isEmpty()) {
						IProject[] projects = ResourcesPlugin.getWorkspace()
								.getRoot().getProjects();
						for (int i = 0; i < projects.length; i++) {
							IProject proj = projects[i];
							if (proj.isOpen()) {
								res.add(proj.getName());
							}
						}
					} else {
						IAdaptable[] elements = workingSet.getElements();
						for (int i = 0; i < elements.length; i++) {
							String name = evaluateEnclosingProject(elements[i]);
							if (name != null) {
								res.add(name);
							}
						}
					}
				} else if (curr instanceof IAdaptable) {
					String name = evaluateEnclosingProject((IAdaptable) curr);
					if (name != null) {
						res.add(name);
					}
				}
			}
			if (!res.isEmpty()) {
				return (String[]) res.toArray(new String[res.size()]);
			}
		}
		return new String[0];
	}

	public static String getCompositeValue(String key, CompositeMap map) {
		Set keySet = map.keySet();
		for (Object object : keySet) {
			if (key.equalsIgnoreCase(object.toString())) {
				return map.getString(object);
			}
		}
		return null;
	}

}
