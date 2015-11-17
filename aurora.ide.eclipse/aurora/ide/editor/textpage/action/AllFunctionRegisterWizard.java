/**
 * 
 */
package aurora.ide.editor.textpage.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mozilla.javascript.ast.StringLiteral;
import org.xml.sax.SAXException;

import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.FileUtil;
import aurora.ide.helpers.PathUtil;
import aurora.ide.javascript.Javascript4Rhino;
import aurora.ide.javascript.search.JavascriptSearchService;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractSearchService;
import aurora.ide.search.reference.MapFinderResult;
import aurora.ide.search.reference.MultiReferenceTypeFinder;
import aurora.ide.search.reference.NamedMapFinder;
import freemarker.template.TemplateException;
import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;

/**
 * @author shiliyan
 * 
 */
public class AllFunctionRegisterWizard extends Wizard {

	private WizardPage settingPage = new WizardPage("") {

		public void createControl(Composite parent) {
			parent.setLayout(new GridLayout());
			final Text m_code = createTextField(parent, "功能定义", "");
			m_code.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setFunctionDefineFilePath(m_code.getText());
					settingChanged();
				}
			});
			final Text savePath = createTextField(parent, "保存路径", "");
			savePath.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setSaveFilePath(savePath.getText());
					settingChanged();
				}
			});
			this.setControl(parent);
			this.setPageComplete(false);
		}

		private Text createTextField(Composite parent, String text, String defaultValue) {
			Composite c = new Composite(parent, SWT.NONE);
			c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			c.setLayout(layout);
			Label l = new Label(c, SWT.NONE);
			l.setText(text);
			Text t = new Text(c, SWT.BORDER);
			t.setText(defaultValue);
			t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			return t;
		}

	};

//	private IFile hostPage;

	private String functionDefineFilePath;

	private String saveFilePath;

	private List<IFile> files;

	private IContainer container;

//	public IFile getHostPage() {
//		return hostPage;
//	}
//
//	public void setHostPage(IFile hostPage) {
//		this.hostPage = hostPage;
//	}

	public AllFunctionRegisterWizard(IContainer container) {
		this.setNeedsProgressMonitor(false);
		this.setHelpAvailable(false);
		this.setWindowTitle("导出脚本");

		this.container = container;
	}

	public void addPages() {
		settingPage.setDescription("根据功能定义文件，将所选目录下所有关联的screen，svc，bm文件整理，按功能导出为功能注册脚本");
		settingPage.setTitle("导出功能注册脚本");
		addPage(settingPage);
	}

	@Override
	public boolean performFinish() {
//		if (hostPage == null)
//			return false;
		try {
			// System.out.println("======begin=======");
			List<FunctionDefine> createFunctionDefine = this.createFunctionDefine(container);
			
			if(createFunctionDefine==null){
				settingPage.setErrorMessage("配置文件无效");				
				return false;
			}
			
			int i = 0;
			for (FunctionDefine fd : createFunctionDefine) {
				files = new ArrayList<IFile>();
				files.add(fd.hostPage);
				fetchAll(fd.hostPage);
				String exportSql = null;
				try {
					exportSql = toExportSql(fd, files);
				} catch (IOException e) {
					settingPage.setErrorMessage("找不到摸版");
					e.printStackTrace();
					return false;
				} catch (SAXException e) {
					settingPage.setErrorMessage("找不到摸版");
					e.printStackTrace();
					return false;
				} catch (TemplateException e) {
					settingPage.setErrorMessage("找不到摸版");
					e.printStackTrace();
					return false;
				}
				// IDE.openEditor(AuroraPlugin.getActivePage(),
				// new StringEditorInput(exportSql, "utf-8"),
				// "org.eclipse.ui.DefaultTextEditor");
				// tofile
				i++;
				String saveFilePath2 = this.getSaveFilePath();
				save2File(saveFilePath2, fd.functionCode + ".txt", exportSql);
				// System.out.println(fd.functionCode + " " + i);

			}

		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
			e.printStackTrace();
			return false;
		} catch (ApplicationException e) {
			DialogUtil.logErrorException(e);
			e.printStackTrace();
			return false;
		}
		// System.out.println("======end=======");
		return true;
	}

	private String toExportSql(FunctionDefine fd, List<IFile> files)
			throws IOException, SAXException, TemplateException {
		RegisterSql rsql = new RegisterSql(fd.functionCode, fd.functionName, fd.functionOrder, fd.modulesCode,
				fd.modulesName, fd.hostPage);
		return rsql.build(files);
	}

	private void fetchAll(IFile host) throws CoreException, ApplicationException {
		if (PathUtil.isAuroraFile(host)) {
			CompositeMap hostMap = CacheManager.getCompositeMap(host);
			iterateAttribute(host, hostMap);
			iterateScript(host, hostMap);
		}
	}

	private void iterateScript(IFile host, CompositeMap hostMap) throws CoreException, ApplicationException {
		if (!"screen".equalsIgnoreCase(host.getFileExtension())) {
			return;
		}
		NamedMapFinder nmf = new NamedMapFinder(JavascriptSearchService.SCRIPT);
		hostMap.iterate(nmf, false);
		List<MapFinderResult> r = nmf.getResult();
		for (MapFinderResult result : r) {
			CompositeMap map = result.getMap();
			if (JavascriptSearchService.SCRIPT.equalsIgnoreCase(map.getName()) && map.getText() != null) {
				Javascript4Rhino e = new Javascript4Rhino(host, map);
				List<StringLiteral> stringLiteral = e.getStringLiteralNodes(new NullProgressMonitor());
				for (StringLiteral sl : stringLiteral) {
					String value = e.getLiteralValue(sl);
					found(host, value);
				}

			}
		}
	}

	private void iterateAttribute(IFile host, CompositeMap hostMap) throws CoreException, ApplicationException {
		MultiReferenceTypeFinder mrtf = new MultiReferenceTypeFinder(AbstractSearchService.bmReference)
				.addReferenceType(AbstractSearchService.screenReference)
				.addReferenceType(AbstractSearchService.urlReference);
		hostMap.iterate(mrtf, true);
		List<MapFinderResult> results = mrtf.getResult();
		for (MapFinderResult r : results) {
			List<Attribute> attributes = r.getAttributes();
			for (Attribute attribute : attributes) {
				String value = CompositeMapUtil.getValueIgnoreCase(attribute, r.getMap());
				if (value == null || "".equals(value))
					continue;
				found(host, value);
			}
		}
	}

	private IFile found(IFile host, String value) throws CoreException, ApplicationException {
		IFile findFile = PathUtil.findFile(host, value);
		if (findFile != null && findFile.exists()) {
			if (!files.contains(findFile)) {
				files.add(findFile);
				if (!"bm".equals(findFile.getFileExtension().toLowerCase())) {
					fetchAll(findFile);
				}
			}
		}
		return findFile;
	}

	private List<FunctionDefine> createFunctionDefine(IContainer container) {
		List<FunctionDefine> defines = new ArrayList<FunctionDefine>();
		try {
			File file = new File(this.getFunctionDefineFilePath());

			if (file.exists() == false)
				return null;

			List<String> readStringFileToList = FileUtil.readStringFileToList(new FileInputStream(file));

			for (String string : readStringFileToList) {
				String[] split = string.split("@");
				if (split.length != 6) {
//					System.out.println(string);
					continue;
				}

				try {
					FunctionDefine d = new FunctionDefine();
					d.functionCode = split[2];
					d.functionName = split[3];
					d.functionOrder = split[4];
					d.modulesCode = split[0];
					d.modulesName = split[1];
					d.hostPage = container.getFile(new Path(split[5]));
					if (d.hostPage.exists() == false) {
						System.out.println(string);
						continue;
					}
					defines.add(d);
				} catch (Exception e) {
//					System.out.println(string);
				}
				// break;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return defines;

	}

	private void save2File(String path, String name, String content) {
		try {
			FileUtil.saveToFile(new File(path, name), content, "UTF8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getFunctionDefineFilePath() {
		return functionDefineFilePath;
	}

	public void setFunctionDefineFilePath(String functionDefineFilePath) {
		this.functionDefineFilePath = functionDefineFilePath;
	}

	public String getSaveFilePath() {
		return saveFilePath;
	}

	public void setSaveFilePath(String saveFilePath) {
		this.saveFilePath = saveFilePath;
	}

	private void settingChanged() {
		boolean sf = saveFilePath != null && "".equals(saveFilePath) == false;
		boolean fd = functionDefineFilePath != null && "".equals(functionDefineFilePath) == false;
		settingPage.setPageComplete(sf && fd);

	}

	private class FunctionDefine {
		private IFile hostPage;

		private String functionCode;
		private String functionName;
		private String functionOrder;

		private String modulesCode;
		private String modulesName;

		private FunctionDefine() {

		}

	}
}
