package aurora.ide.meta.gef.designer.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.designer.BMDesigner;
import aurora.ide.meta.gef.designer.DesignerUtil;
import aurora.ide.meta.gef.designer.gen.BaseBmGenerator;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.ModelUtil;
import aurora.ide.meta.gef.designer.model.Record;

public class NewBmqWizard extends Wizard implements INewWizard {
	private BaseInfoWizardPage page1 = new BaseInfoWizardPage();
	private ExtensionWizardPage page2 = new ExtensionWizardPage();
	private IWorkbench workbench;
	private IResource resource;

	public NewBmqWizard() {
	}

	@Override
	public void addPages() {
		super.addPages();
		page1.setCurrentSelection(resource);
		addPage(page1);
		addPage(page2);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		if (selection == null
				|| !(selection.getFirstElement() instanceof IResource))
			return;
		resource = (IResource) selection.getFirstElement();
	}

	@Override
	public boolean performFinish() {
		String fullPath = page1.getFileFullPath();
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(new Path(fullPath));
		String[] input = page1.getPreInput();
		String[] userSel = page2.getUserSelection();
		CompositeMap map = createModel(input, userSel, file.getFullPath()
				.removeFileExtension().lastSegment());
		String xml = BaseBmGenerator.xml_header + map.toXML();
		try {
			byte[] bs = xml.getBytes("UTF-8");
			InputStream is = new ByteArrayInputStream(bs);
			final CreateFileOperation cfo = new CreateFileOperation(file, null,
					is, "create bm model file.");
			IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					try {
						cfo.execute(monitor,
								WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			};
			try {
				getContainer().run(true, true, op);
				IDE.openEditor(workbench.getActiveWorkbenchWindow()
						.getActivePage(), file, BMDesigner.ID, true);
				return true;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return false;
	}

	CompositeMap createModel(String[] input, String[] userSel, String name) {
		BMModel model = new BMModel();
		String pre = name;
		if (pre.length() > 3)
			pre = pre.substring(0, 3);
		model.setNamePrefix(pre + "_c");
		model.setTitle(name);
		Record r = model.getPkRecord();
		r.setName(name + "_pk");
		if (userSel.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < userSel.length - 1; i++)
				sb.append(userSel[i] + "|");
			sb.append(userSel[userSel.length - 1]);
			model.setAutoExtends(sb.toString());

		}
		for (String s : input)
			model.add(DesignerUtil.createRecord(s));
		CompositeMap map = ModelUtil.toCompositeMap(model);
		return map;
	}

}
