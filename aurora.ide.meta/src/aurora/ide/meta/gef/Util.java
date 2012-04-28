package aurora.ide.meta.gef;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
import uncertain.schema.Attribute;
import aurora.ide.builder.BuildContext;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.search.cache.CacheManager;

public class Util {
	/**
	 * 
	 * return editor type
	 * */
	public static String getType(CompositeMap field) {

		if (isQueryName(field)) {
			CompositeMap fieldByName = findFieldMapByQueryName(field);
			if (fieldByName != null) {
				return getType(fieldByName);
			}
		}

		String object = getCompositeValue("defaultEditor", field);
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

	public static CompositeMap findFieldMapByQueryName(CompositeMap field) {
		String fn = field.getString("name", "");
		String endS = getQueryFieldEndString(fn);
		if (endS != null) {
			BMCompositeMap bm = new BMCompositeMap(field.getRoot());
			CompositeMap fieldByName = bm.getFieldByName(fn.replace(endS, ""));
			return fieldByName;
		}
		return null;
	}

	public static boolean isQueryName(CompositeMap field) {
		return "query-field".equals(field.getName())
				&& field.get("name") != null;
	}

	private static String getQueryFieldEndString(String fn) {
		String suffix = "_from";
		if (fn.endsWith(suffix))
			return suffix;
		suffix = "_to";
		if (fn.endsWith(suffix))
			return suffix;
		return null;
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

	public static Dataset findDataset(Container container) {
		if (container == null)
			return null;
		boolean useParentBM = isUseParentBM(container);
		if (useParentBM) {
			return findDataset(container.getParent());
		}
		Dataset dataset = container.getDataset();
		return dataset;
	}

	public static boolean isUseParentBM(Container container) {
		if (Container.SECTION_TYPE_QUERY.equals(container.getSectionType())
				|| Container.SECTION_TYPE_RESULT.equals(container
						.getSectionType())) {
			return false;
		}
		return true;
	}

	public static String getPrompt(CompositeMap field) {
		String endS = "";
		if (isQueryName(field)) {
			CompositeMap qf = field;
			String fn = qf.getString("name", "");
			endS = getQueryFieldEndString(fn);
			field = findFieldMapByQueryName(field);
		}
		
		String result = field != null ? field.getString("prompt", "prompt")
				: "prompt:";
		if ("_from".equals(endS)) {
			return result + "从";
		}
		if ("_to".equals(endS)) {
			return result + "到";
		}
		return result;
	}

	public static String getRefFieldSourcePrompt(IProject auroraProject,
			CompositeMap field) {

		if (!"ref-field".equals(field.getName())) {
			return null;
		}
		String relationName = getValueIgnoreCase(field, "relationName");
		if (relationName == null) {
			return null;
		}
		BMCompositeMap c_bm = new BMCompositeMap(field.getRoot());
		CompositeMap relationMap = null;
		List<CompositeMap> relations = c_bm.getRelations();
		for (CompositeMap r : relations) {
			if (relationName.equals(r.getString("name"))) {
				relationMap = r;
			}
		}
		if (relationMap == null) {
			return null;
		}
		String bmPath = getValueIgnoreCase(relationMap, "refmodel");
		IFile resource = ResourceUtil.getBMFile(auroraProject,
				bmPath == null ? "" : bmPath);
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			CompositeMap map;
			try {
				map = CacheManager.getWholeBMCompositeMap(file);
				BMCompositeMap bm = new BMCompositeMap(map);
				String source = getValueIgnoreCase(field, "sourceField");
				CompositeMap fieldByName = bm
						.getFieldByName(source == null ? "" : source);
				if (fieldByName != null) {
					return getPrompt(fieldByName);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String getValueIgnoreCase(Attribute a, BuildContext bc) {
		return getValueIgnoreCase(a, bc.map);
	}

	public static String getValueIgnoreCase(Attribute a, CompositeMap cMap) {
		String name = a.getName();
		return getValueIgnoreCase(cMap, name);
	}

	public static String getValueIgnoreCase(CompositeMap cMap, String name) {
		Set keySet = cMap.keySet();
		for (Object object : keySet) {
			if (object instanceof String
					&& ((String) object).equalsIgnoreCase(name)) {
				return cMap.get(object).toString();
			}
		}
		return null;
	}

}
