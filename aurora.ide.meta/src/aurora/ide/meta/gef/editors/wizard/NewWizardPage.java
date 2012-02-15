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
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.xml.sax.SAXException;

import aurora.ide.AuroraPlugin;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.wizard.template.Temlpate;
import aurora.ide.meta.gef.editors.wizard.template.TemplateParsing;

public class NewWizardPage extends WizardPage {
	private Text txtPath;
	private Text txtFile;
	private List list;

	private java.util.List<Temlpate> templates = new ArrayList<Temlpate>();
	private Temlpate template;

	private ViewDiagram viewDiagram = new ViewDiagram();

	public NewWizardPage() {
		super("aurora.wizard.new.Page");
		setTitle("新建");
		setDescription("新建文件");
		setPageComplete(false);
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

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTextFieldPart(composite);
		createListPart(composite);
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

		txtPath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		txtFile.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		btnBrower.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot().getProject(), true, "");
				dialog.setHelpAvailable(true);
				dialog.setTitle("选择目录");
				if (dialog.open() == Dialog.OK) {
					if (dialog.getResult().length != 0) {
						txtPath.setText(dialog.getResult()[0].toString());
						IProject p = AuroraPlugin.getWorkspace().getRoot().getProject(getPath());
						IFolder f = p.getFolder("template");
						try {
							for (IResource r : f.members()) {
								if (r instanceof IFile) {
									IFile file = (IFile) r;
									if (file.getFileExtension().equalsIgnoreCase("xml")) {
										SAXParserFactory factory = SAXParserFactory.newInstance();
										SAXParser parser = factory.newSAXParser();
										TemplateParsing tp = new TemplateParsing();
										parser.parse(file.getContents(), tp);
										Temlpate tm = tp.getTemplate();
										templates.add(tm);
										list.add(tm.getName());
									}
								}
							}
						} catch (CoreException e1) {
							e1.printStackTrace();
						} catch (SAXException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ParserConfigurationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createListPart(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		list = new List(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		list.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		Canvas canvas = new Canvas(composite, SWT.BORDER);
		canvas.setLayoutData(new GridData(GridData.FILL_BOTH));

		list.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				template = templates.get(list.getSelectionIndex());
				setDescription(template.getDescription());
				dialogChanged();
				SettingWizardPage settingPage = ((SettingWizardPage) getNextPage());
				settingPage.deleteArea();
				settingPage.setTemplate(template);
				for(AuroraComponent a:template.getModels()){
					viewDiagram.addChild(a);
				}
				setPageComplete(true);
				// for (String s : template.getAreas()) {
				// if (s.equals(TemlpateModel.QUERY_AREA)) {
				// settingPage.createQueryArea();
				// }
				// }

			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getPath()));
		String fileName = getFileName();

		if (getPath().length() == 0) {
			updateStatus(LocaleMessage.getString("file.container.must.be.specified"));
			return;
		}
		if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus(LocaleMessage.getString("file.container.must.exist"));
			return;
		}
		if (fileName != null && !fileName.equals("") && ((IContainer) container).getFile(new Path(fileName)).exists()) {
			updateStatus(LocaleMessage.getString("filename.used"));
			return;
		}
		if (!container.isAccessible()) {
			updateStatus(LocaleMessage.getString("project.must.be.writable"));
			return;
		}
		if (fileName.length() == 0) {
			updateStatus(LocaleMessage.getString("file.name.must.be.specified"));
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus(LocaleMessage.getString("file.name.must.be.valid"));
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("meta") == false) {
				updateStatus("文件扩展名必须是meta");
				return;
			}
		}
		if (template == null) {
			updateStatus("必须选择一个模板");
			return;
		}
		updateStatus(null);
	}

	public Temlpate getTemplate() {
		return template;
	}

	public ViewDiagram getViewDiagram() {
		return viewDiagram;
	}

	public void setViewDiagram(ViewDiagram viewDiagram) {
		this.viewDiagram = viewDiagram;
	}

}
