package aurora.ide.pkg.wizard;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.ide.undo.CreateFileOperation;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;

public class CreateAuroraPkgWizard extends Wizard {
	AuroraPkgWizardPage page;
	private IFolder initFolder;

	public CreateAuroraPkgWizard(IFolder folder) {
		this.initFolder = folder;
		setWindowTitle("Aurora Package 向导");
		page = new AuroraPkgWizardPage();
		page.setInitFolder(folder);
	}

	@Override
	public void addPages() {
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		String pkgName = page.getPkgName();
		IFolder pkgFolder = initFolder.getFolder(pkgName);
		IFolder configOFolder = pkgFolder.getFolder("config");
		for (String fn : page.getSelection()) {
			IFile file = configOFolder.getFile(fn + ".xml");
			CompositeMap map = createContent(fn);
			writeToFile(file, map);
		}
		return true;
	}

	protected CompositeMap createContent(String name) {
		CompositeMap map = new CommentCompositeMap(name);
		if ("instance".equals(name)) {
			map.setName("instance-config");
			map.setNameSpace("u", "uncertain.pkg");
		} else if ("package".equals(name)) {
			map.put("name", page.getPkgName());
		}
		return map;
	}

	private void writeToFile(IFile file, CompositeMap map) {
		String str = AuroraResourceUtil.xml_decl + map.toXML();
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(
					str.getBytes(AuroraConstant.ENCODING));
			CreateFileOperation cfo = new CreateFileOperation(file, null, is,
					"create " + file.getName());
			cfo.execute(null, null);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
