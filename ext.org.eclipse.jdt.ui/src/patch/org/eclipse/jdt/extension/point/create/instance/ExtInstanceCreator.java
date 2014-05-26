package patch.org.eclipse.jdt.extension.point.create.instance;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import patch.org.eclipse.jdt.internal.ui.JavaPlugin;

public class ExtInstanceCreator {
	private static final List<String> unsupportList = new ArrayList<String>() {
		{
			this.add("org.eclipse.jdt.internal.ui.text.java.SWTTemplateCompletionProposalComputer");
			this.add("org.eclipse.jdt.internal.ui.text.java.TemplateCompletionProposalComputer");
			this.add("org.eclipse.pde.api.tools.ui.internal.completion.APIToolsJavadocCompletionProposalComputer");
		}
	};

	private static boolean isSupport(String clazz) {
		return unsupportList.contains(clazz) == false;
	}

	public static Object createInstance(IConfigurationElement element,
			String attClass) {
		String attribute = element.getAttribute(attClass);
		Class<?> loadClass;
		try {
			if (isSupport(attribute) == false) {
				return null;
			}
			System.out.println(attribute);
			loadClass = JavaPlugin.getDefault().getBundle()
					.loadClass("ext." + attribute);
			return loadClass.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			JavaPlugin.log(new Status(IStatus.ERROR, JavaPlugin.getPluginId(),
					0,
					"Cant Create Aurora Ext.JDT Class " + "ext." + attribute,
					null));
			// try {
			// loadClass = JavaPlugin.getDefault().getBundle()
			// .loadClass(attribute);
			// } catch (ClassNotFoundException e1) {
			// e1.printStackTrace();
			// }
		} catch (InstantiationException e) {
			e.printStackTrace();
			JavaPlugin.log(new Status(IStatus.ERROR, JavaPlugin.getPluginId(),
					0,
					"Cant Create Aurora Ext.JDT Class " + "ext." + attribute,
					null));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			JavaPlugin.log(new Status(IStatus.ERROR, JavaPlugin.getPluginId(),
					0,
					"Cant Create Aurora Ext.JDT Class " + "ext." + attribute,
					null));
		}
		return null;
	}

}
