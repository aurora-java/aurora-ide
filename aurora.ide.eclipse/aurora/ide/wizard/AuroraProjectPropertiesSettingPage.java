package aurora.ide.wizard;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import aurora.ide.builder.ResourceUtil;
import aurora.ide.project.AuroraProject;
import aurora.ide.swt.util.GridLayoutUtil;
import aurora.ide.swt.util.TextField;
import aurora.ide.swt.util.WidgetFactory;

public class AuroraProjectPropertiesSettingPage extends WizardPage {

	private IProject project;

	private String webPath;
	private String bmPath;

	private TextField web;

	private TextField bm;

	protected AuroraProjectPropertiesSettingPage(String pageName,
			IProject project) {
		super(pageName);
		this.setTitle("Aurora工程属性设置");
		this.setMessage("请设置Web主目录和BM的classes目录。");
		this.project = project;
	}

	public void createControl(Composite parent) {

		Composite p = new Composite(parent, SWT.NONE);
		p.setLayout(GridLayoutUtil.COLUMN_LAYOUT_3);
		web = WidgetFactory.createTextButtonField(p, "WEB主目录", "浏览");
		bm = WidgetFactory.createTextButtonField(p, "BM主目录", "浏览");
		web.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setWebPath(web.getText().getText());
				validatePage();
			}
		});
		bm.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setBmPath(bm.getText().getText());
				validatePage();
			}
		});
		web.addButtonClickListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String path = openFolderSelectionDialog("选择WEB主目录");
				if (null != path) {
					web.setText(path);
					web.getText().setFocus();
				}
			}
		});
		bm.addButtonClickListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String path = openFolderSelectionDialog("选择BM主目录");
				if (null != path) {
					bm.setText(path);
					bm.getText().setFocus();
				}
			}
		});

		init();
		this.setControl(p);
	}

	protected String openFolderSelectionDialog(String msg) {
		AuroraProject ap = new AuroraProject(project);
		return ap.openFolderSelectionDialog(msg, this.getShell());
	}

	private void validatePage() {
		if (null == getWebPath() || "".equals(getWebPath())) {
			setError("请输入WEB主目录");
			return;
		}
		if (null == getBmPath() || "".equals(getBmPath())) {
			setError("请输入BM主目录");
			return;
		}
		Path webPath = new Path(getWebPath());
		if (webPath.segmentCount() == 1) {
			setError("WEB主目录不存在");
			return;
		}
		if (webPath.isRoot() || webPath.isEmpty()
				|| webPath.hasTrailingSeparator()) {
			setError("WEB主目录不存在");
			return;
		}
		IFolder webHome = project.getParent().getFolder(webPath);
		if (webHome.exists() == false) {
			setError("WEB主目录不存在");
			return;
		}
		Path bmpath = new Path(getBmPath());
		if (bmpath.isRoot() || bmpath.isEmpty()
				|| bmpath.hasTrailingSeparator()) {
			setError("BM主目录不存在");
			return;
		}

		if (bmpath.segmentCount() == 1) {
			setError("BM主目录不存在");
			return;
		}
		IFolder bmHome = project.getParent().getFolder(bmpath);
		if (bmHome.exists() == false) {
			setError("BM主目录不存在");
			return;
		}
		if (webHome.getFolder("WEB-INF").exists() == false) {
			setError("WEB-INF目录不存在");
			return;
		}
		if ("classes".equals(bmHome.getName()) == false) {
			setError("BM主目录必须为classes目录");
			return;
		}

		setError(null);

	}

	private void setError(String err) {
		this.setPageComplete(null == err);
		this.setErrorMessage(err);
	}

	private void init() {
		ArrayList<IFolder> webInfs = ResourceUtil.findAllWebInf(project);
		if (webInfs.size() > 1) {
			this.setMessage("请注意工程中存在多个WEB-INF目录！");
		} else if (webInfs.size() == 0) {
			this.setError("工程不包含WEB-INF目录，无法设置为Aurora工程。");
		} else {
			IFolder webInf = webInfs.get(0);
			IPath webHome = webInf.getParent().getFullPath();
			web.setText(webHome.toString());
			String bmPath = webInf.getFolder("classes").getFullPath()
					.toString();
			bm.setText(bmPath);
		}

	}

	public String getWebPath() {
		return webPath;
	}

	public void setWebPath(String webPath) {
		this.webPath = webPath;
	}

	public String getBmPath() {
		return bmPath;
	}

	public void setBmPath(String bmPath) {
		this.bmPath = bmPath;
	}

}
