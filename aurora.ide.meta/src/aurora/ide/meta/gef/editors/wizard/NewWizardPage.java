package aurora.ide.meta.gef.editors.wizard;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionValidator;
import org.xml.sax.SAXException;

import aurora.ide.meta.gef.editors.template.Template;
import aurora.ide.meta.gef.editors.template.parse.TemplateParse;

public class NewWizardPage extends WizardPage {
	private Text txtPath;
	private Text txtFile;
	private Label lblTpName;
	private Canvas canvas;
	private Button btnRight;
	private Button btnLeft;

	private IProject metaProject;
	private IFolder metaFolder;
	private java.util.List<Template> templates = new ArrayList<Template>();
	private Template template;
	private int index;

	public NewWizardPage() {
		super("aurora.wizard.new.Page");
		setTitle("meta文件向导");
		setDescription("通过模板创建meta文件");
		setPageComplete(false);
		IResource r = null;
		try {
			ISelection obj = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
			if (obj instanceof StructuredSelection) {
				StructuredSelection ts = (StructuredSelection) obj;
				if (!ts.isEmpty() && (ts.getFirstElement() instanceof IResource)) {
					r = (IResource) ts.getFirstElement();
					if (r.getProject().hasNature("aurora.ide.meta.nature") && (r instanceof IFolder) && r.getFullPath().toString().indexOf("screen") != -1) {
						metaFolder = (IFolder) r;
					}
				}
			}
		} catch (NullPointerException e1) {
			r = null;
		} catch (CoreException e) {
			e.printStackTrace();
		}
		try {
			if (r == null || (r != null && !r.getProject().hasNature("aurora.ide.meta.nature"))) {
				r = (IResource) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
			}
		} catch (NullPointerException e) {
			r = null;
		} catch (CoreException e) {
			e.printStackTrace();
		}
		try {
			if (r != null && r.getProject().hasNature("aurora.ide.meta.nature")) {
				metaProject = r.getProject();
				initTemplates();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public String getPath() {
		return txtPath.getText().trim();
	}

	public String getFileName() {
		String fileName = txtFile.getText().trim();
		if (fileName.length() > 0 && fileName.indexOf(".") == -1) {
			fileName = fileName + ".meta";
		}
		return fileName;
	}

	public IProject getMetaProject() {
		return metaProject;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTextFieldPart(composite);
		createImagePart(composite);

		if (template != null) {
			((SettingWizardPage) getNextPage()).setTemplate(template);
			lblTpName.setText(template.getName());
			btnRight.setEnabled(true);
		}
	}

	private void createTextFieldPart(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lblPath = new Label(composite, SWT.NONE);
		lblPath.setText("目录");
		txtPath = new Text(composite, SWT.BORDER);
		txtPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button btnBrower = new Button(composite, SWT.NONE);
		btnBrower.setText("  浏览...  ");

		Label lblFile = new Label(composite, SWT.NONE);
		lblFile.setText("文件名");
		txtFile = new Text(composite, SWT.BORDER);
		txtFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(composite, SWT.NONE);
		if (metaFolder != null) {
			txtPath.setText(metaFolder.getFullPath().toString());
		} else if (metaProject != null) {
			txtPath.setText(metaProject.getName() + "/screen");
		}

		txtPath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				try {
					dialogChanged();
					if (metaProject == null) {
						templates.clear();
						btnLeft.setEnabled(false);
					} else {
						initTemplates();
					}
					index = 0;
				} catch (CoreException e1) {
					e1.printStackTrace();
				}
				refresh();
			}
		});

		txtFile.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				try {
					dialogChanged();
				} catch (CoreException e1) {
					e1.printStackTrace();
				}
			}
		});

		btnBrower.addSelectionListener(new ClickButtonBrower());
	}

	private void createImagePart(Composite parent) {
		Group composite = new Group(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setText("模板");

		canvas = new Canvas(composite, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		gd.heightHint = 240;
		canvas.setLayoutData(gd);
		canvas.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		btnLeft = new Button(composite, SWT.NONE);
		btnLeft.setText("Previous");
		btnLeft.setEnabled(false);
		gd = new GridData();
		gd.widthHint = 80;
		btnLeft.setLayoutData(gd);

		lblTpName = new Label(composite, SWT.CENTER);
		lblTpName.setText("");
		lblTpName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		btnRight = new Button(composite, SWT.NONE);
		btnRight.setText("Next");
		btnRight.setEnabled(false);
		gd = new GridData();
		gd.horizontalAlignment = GridData.END;
		gd.widthHint = 80;
		btnRight.setLayoutData(gd);

		btnLeft.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				index--;
				btnRight.setEnabled(true);
				if (index <= 0) {
					index = 0;
					refresh();
					btnLeft.setEnabled(false);
				} else {
					refresh();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnRight.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				index++;
				btnLeft.setEnabled(true);
				if (index >= templates.size() - 1) {
					index = templates.size() - 1;
					refresh();
					btnRight.setEnabled(false);
				} else {
					refresh();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (template != null && template.getIcon() != null) {
					try {
						IFolder folder = metaProject.getFolder("template/thumbnails");
						if (folder.getFile(template.getIcon()).exists()) {
							Image image = new Image(getShell().getDisplay(), folder.getFile(template.getIcon()).getContents());
							e.gc.drawImage(image, (e.width - image.getImageData().width) / 2, (e.height - image.getImageData().height) / 2);
							image.dispose();
						}
					} catch (NullPointerException e1) {
						// TODO
					} catch (CoreException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}

	private void refresh() {
		if (templates.size() > 0) {
			template = templates.get(index);
			lblTpName.setText(template.getName());
			canvas.redraw();
			btnRight.setEnabled(true);
			((SettingWizardPage) getNextPage()).setTemplate(template);
			((SettingWizardPage) getNextPage()).createRegiog();
			setDescription(template.getDescription());
		} else {
			lblTpName.setText("");
			canvas.redraw();
			btnRight.setEnabled(false);
			setDescription("通过模板创建meta文件");
		}
		try {
			dialogChanged();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	private void dialogChanged() throws CoreException {
		IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getPath()));
		String fileName = getFileName();
		int dotLoc = fileName.lastIndexOf('.');
		if (getPath().length() == 0) {
			updateStatus("必须为文件指定目录");
		} else if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("目录必须有效");
		} else if (!container.isAccessible()) {
			updateStatus("工程必须可写");
		} else if (!container.getProject().hasNature("aurora.ide.meta.nature")) {
			updateStatus("该工程不是一个有效的Aurora原型工程");
		} else if (getPath().lastIndexOf("screen") == -1) {
			updateStatus("文件须位于screen目录下");
		} else {
			metaProject = container.getProject();
			if (fileName != null && !fileName.equals("") && ((IContainer) container).getFile(new Path(fileName)).exists()) {
				updateStatus("此文件名已经被使用！");
			} else if (fileName.length() == 0) {
				updateStatus("必须指定文件名");
			} else if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
				updateStatus("文件名无效");
			} else if (dotLoc != -1 && (!fileName.substring(dotLoc + 1).equalsIgnoreCase("meta"))) {
				updateStatus("文件扩展名必须是meta");
			} else {
				setDescription(template.getDescription());
				updateStatus(null);
			}
			return;
		}
		metaProject = null;
	}

	private void initTemplates() {
		templates.clear();
		IFolder folder = metaProject.getFolder("template");
		try {
			for (IResource r : folder.members()) {
				if ((r instanceof IFile) && ((IFile) r).getFileExtension().equalsIgnoreCase("xml")) {
					SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
					TemplateParse tp = new TemplateParse();
					parser.parse(((IFile) r).getContents(), tp);
					Template tm = tp.getTemplate();
					String path = r.getLocation().toString();
					tm.setPath(path.substring(path.indexOf("template") + "template".length() + 1));
					templates.add(tm);
				}
			}
			template = templates.get(index);
		} catch (CoreException e1) {
			e1.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	class ClickButtonBrower implements SelectionListener {
		public void widgetSelected(SelectionEvent e) {
			ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot().getProject(), true, "");
			dialog.setValidator(new ISelectionValidator() {
				public String isValid(Object selection) {
					try {
						metaProject = ResourcesPlugin.getWorkspace().getRoot().getFolder((IPath) selection).getProject();
					} catch (IllegalArgumentException e) {
						metaProject = ResourcesPlugin.getWorkspace().getRoot().getProject(selection.toString());
					}
					try {
						if (metaProject.hasNature("aurora.ide.meta.nature")) {
							if (((IPath) selection).toString().indexOf("screen") != -1) {
								return null;
							}
							return "文件须位于screen目录下";
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
					return "该工程不是一个有效的Aurora原型工程";
				}
			});
			dialog.setTitle("选择目录");
			if (dialog.open() == Dialog.OK && dialog.getResult().length != 0) {
				txtPath.setText(dialog.getResult()[0].toString());
				index = 0;
				initTemplates();
				btnLeft.setEnabled(false);
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}
}
