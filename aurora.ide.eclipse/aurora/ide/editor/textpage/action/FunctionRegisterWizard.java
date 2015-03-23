/**
 * 
 */
package aurora.ide.editor.textpage.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
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
import org.eclipse.ui.ide.IDE;
import org.mozilla.javascript.ast.StringLiteral;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.schema.Attribute;
import aurora.ide.AuroraPlugin;
import aurora.ide.editor.editorInput.StringEditorInput;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.PathUtil;
import aurora.ide.helpers.StringUtil;
import aurora.ide.javascript.Javascript4Rhino;
import aurora.ide.javascript.search.JavascriptSearchService;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.AbstractSearchService;
import aurora.ide.search.reference.MapFinderResult;
import aurora.ide.search.reference.MultiReferenceTypeFinder;
import aurora.ide.search.reference.NamedMapFinder;
import freemarker.template.TemplateException;

/**
 * @author shiliyan
 * 
 */
public class FunctionRegisterWizard extends Wizard {

	private static final String DEFAULT_FUNCTION_ORDER = "10";

	private WizardPage settingPage = new WizardPage("") {

		public void createControl(Composite parent) {
			parent.setLayout(new GridLayout());
			final Text m_code = createTextField(parent, "模块Code",
					getModulesCode());
			m_code.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setModulesCode(m_code.getText());
				}
			});
			final Text m_name = createTextField(parent, "模块Name",
					getModulesName());
			m_name.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setModulesName(m_name.getText());
				}
			});

			Label label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			final Text f_code = createTextField(parent, "功能Code",
					getFunctionCode());
			f_code.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setFunctionCode(f_code.getText());
				}
			});
			final Text f_name = createTextField(parent, "功能Name",
					getFunctionName());
			f_name.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setFunctionName(f_name.getText());
				}
			});
			final Text f_order = createTextField(parent, "功能Order",
					getFunctionOrder());
			f_order.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					setFunctionOrder(f_order.getText());
				}
			});
			this.setControl(parent);
			this.setPageComplete(true);
		}

		private Text createTextField(Composite parent, String text,
				String defaultValue) {
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

	private IFile hostPage;

	private String functionCode;
	private String functionName;
	private String functionOrder;

	private String modulesCode;
	private String modulesName;

	private List<IFile> files;

	public String getFunctionCode() {
		if (functionCode != null)
			return functionCode.toUpperCase();
		return "";
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getFunctionOrder() {
		return functionOrder;
	}

	public void setFunctionOrder(String functionOrder) {
		this.functionOrder = functionOrder;
	}

	public String getModulesCode() {
		if (modulesCode != null)
			return modulesCode.toUpperCase();
		return "";
	}

	public void setModulesCode(String modulesCode) {
		this.modulesCode = modulesCode;
	}

	public String getModulesName() {
		return modulesName;
	}

	public void setModulesName(String modulesName) {
		this.modulesName = modulesName;
	}

	public IFile getHostPage() {
		return hostPage;
	}

	public void setHostPage(IFile hostPage) {
		this.hostPage = hostPage;
	}

	public FunctionRegisterWizard(IFile hostPage) {
		this.setNeedsProgressMonitor(false);
		this.setHelpAvailable(false);
		this.setWindowTitle("导出");
		this.hostPage = hostPage;
		this.setDialogSettings(AuroraPlugin.getDefault()
				.getDialogSettingsSection("FunctionRegisterWizard"));
		this.setFunctionCode(this.getDefaultFunctionCode());
		this.setFunctionName(this.getDefaultFunctionName());
		this.setFunctionOrder(this.getDefaultFunctionOrder());
		this.setModulesCode(this.getDefaultModuleCode());
		this.setModulesName(this.getDefaultModuleName());
	}

	private String getDefaultFunctionOrder() {
		String functionCode = this.getFunctionCode();
		if (!StringUtil.isBlank(functionCode)) {
			int length = functionCode.length();
			if (length - 4 > 0) {
				String substring = functionCode.substring(length - 4);
				try {
					Integer integer = Integer.valueOf(substring);
					return String.valueOf(integer);
				} catch (NumberFormatException e) {
				}
			}
		}
		return DEFAULT_FUNCTION_ORDER;
	}

	private String getDefaultModuleName() {
		IDialogSettings dialogSettings = this.getDialogSettings();
		String modulesCode = this.getModulesCode();
		if (!StringUtil.isBlank(modulesCode)) {
			String string = dialogSettings.get(modulesCode);
			return StringUtil.isBlank(string) ? modulesCode : string;
		}
		return modulesCode;
	}

	private String getDefaultFunctionName() {
		IDialogSettings dialogSettings = this.getDialogSettings();
		String functionCode = this.getFunctionCode();
		if (!StringUtil.isBlank(functionCode)) {
			String string = dialogSettings.get(functionCode);
			return StringUtil.isBlank(string) ? functionCode : string;
		}
		return functionCode;
	}

	private String getDefaultModuleCode() {
		if (hostPage != null) {
			return hostPage.getParent().getParent().getName();
		}
		return "NONE";
	}

	private String getDefaultFunctionCode() {
		if (hostPage != null) {
			return hostPage.getParent().getName();
		}
		return "NONE";
	}

	public void addPages() {
		settingPage.setDescription("将当前文件关联的screen，svc，bm文件整理，导出为功能注册脚本");
		settingPage.setTitle("导出功能注册脚本");
		addPage(settingPage);
	}

	@Override
	public boolean performFinish() {
		if (hostPage == null)
			return false;
		try {
			IDialogSettings dialogSettings = this.getDialogSettings();
			dialogSettings.put(this.getFunctionCode(), this.getFunctionName());
			dialogSettings.put(this.getModulesCode(), this.getModulesName());
			files = new ArrayList<IFile>();
			files.add(hostPage);
			fetchAll(hostPage);
			String exportSql = null;
			try {
				exportSql = toExportSql();
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
			IDE.openEditor(AuroraPlugin.getActivePage(), new StringEditorInput(
					exportSql,"utf-8"), "org.eclipse.ui.DefaultTextEditor");
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
			e.printStackTrace();
			return false;
		} catch (ApplicationException e) {
			DialogUtil.logErrorException(e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private String toExportSql() throws IOException, SAXException, TemplateException {
		RegisterSql rsql = new RegisterSql(this.getFunctionCode(),
				this.getFunctionName(), this.getFunctionOrder(),
				this.getModulesCode(), this.getModulesName(), hostPage);
		return rsql.build(files);
	}

	private void fetchAll(IFile host) throws CoreException,
			ApplicationException {
		if (PathUtil.isAuroraFile(host)) {
			CompositeMap hostMap = CacheManager.getCompositeMap(host);
			iterateAttribute(host, hostMap);
			iterateScript(host, hostMap);
		}
	}

	private void iterateScript(IFile host, CompositeMap hostMap)
			throws CoreException, ApplicationException {
		if (!"screen".equalsIgnoreCase(host.getFileExtension())) {
			return;
		}
		NamedMapFinder nmf = new NamedMapFinder(JavascriptSearchService.SCRIPT);
		hostMap.iterate(nmf, false);
		List<MapFinderResult> r = nmf.getResult();
		for (MapFinderResult result : r) {
			CompositeMap map = result.getMap();
			if (JavascriptSearchService.SCRIPT.equalsIgnoreCase(map.getName())
					&& map.getText() != null) {
				Javascript4Rhino e = new Javascript4Rhino(host, map);
				List<StringLiteral> stringLiteral = e
						.getStringLiteralNodes(new NullProgressMonitor());
				for (StringLiteral sl : stringLiteral) {
					String value = e.getLiteralValue(sl);
					found(host, value);
				}

			}
		}
	}

	private void iterateAttribute(IFile host, CompositeMap hostMap)
			throws CoreException, ApplicationException {
		MultiReferenceTypeFinder mrtf = new MultiReferenceTypeFinder(
				AbstractSearchService.bmReference).addReferenceType(
				AbstractSearchService.screenReference).addReferenceType(
				AbstractSearchService.urlReference);
		hostMap.iterate(mrtf, true);
		List<MapFinderResult> results = mrtf.getResult();
		for (MapFinderResult r : results) {
			List<Attribute> attributes = r.getAttributes();
			for (Attribute attribute : attributes) {
				String value = CompositeMapUtil.getValueIgnoreCase(attribute,
						r.getMap());
				if (value == null || "".equals(value))
					continue;
				found(host, value);
			}
		}
	}

	private IFile found(IFile host, String value) throws CoreException,
			ApplicationException {
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

}
