package aurora.ide.meta.gef.designer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import uncertain.composite.CompositeMap;
import aurora.ide.bm.AuroraDataBase;
import aurora.ide.bm.wizard.table.BMFieldsPage;
import aurora.ide.bm.wizard.table.BMFromDBWizard;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;
import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.project.AuroraMetaProject;

public class ImportFieldAction extends Action {

	private BMDesigner designer;
	private BMModel model;
	private Connection conn;
	private CompositeMap selection;

	public ImportFieldAction(BMModel model, BMDesigner designer) {
		super("Import Field from table");
		setImageDescriptor(PrototypeImagesUtils.getImageDescriptor("importColumn.png"));
		this.model = model;
		this.designer = designer;
	}

	@Override
	public void run() {
		WizardDialog wd = new WizardDialog(designer.getSite().getShell(),
				new BMFromDBWizard() {
					private String tableName = model.getTitle();
					private BMFieldsPage fieldsPage;

					@Override
					public void addPages() {
						fieldsPage = new BMFieldsPage(null, this) {
							{
								setTitle("字段选择");
								setDescription("从列表中选择一些字段,他们会被添加到BM原型设计器中");
							}

							@Override
							public void createControl(Composite parent) {
								Composite com = new Composite(parent, SWT.NONE);
								com.setLayout(new GridLayout(3, false));
								Label lbl = new Label(com, SWT.NONE);
								lbl.setText("Table Name:");
								final Text t = new Text(com, SWT.BORDER
										| SWT.SEARCH);
								t.setText(tableName);
								t.addModifyListener(new ModifyListener() {

									public void modifyText(ModifyEvent e) {
										tableName = t.getText();
									}
								});
								Button btn = new Button(com, SWT.NONE);
								btn.setText("refresh");
								btn.addSelectionListener(new SelectionAdapter() {

									@Override
									public void widgetSelected(SelectionEvent e) {
										try {
											refresh();
										} catch (ApplicationException e1) {
											e1.printStackTrace();
										}
									}
								});
								GridData gd = new GridData();
								gd.grabExcessHorizontalSpace = true;
								gd.horizontalAlignment = SWT.FILL;
								t.setLayoutData(gd);
								super.createControl(com);
								gd = new GridData();
								gd.grabExcessHorizontalSpace = true;
								gd.grabExcessVerticalSpace = true;
								gd.horizontalSpan = 3;
								getControl().setLayoutData(gd);
								setControl(com);
							}

						};
						addPage(fieldsPage);
					}

					@Override
					public DatabaseMetaData getDBMetaData() {
						try {
							return getConnection().getMetaData();
						} catch (SQLException e) {
							e.printStackTrace();
						} catch (ApplicationException e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					public String getTableName() {
						return tableName.toUpperCase();
					}

					@Override
					public Connection getConnection()
							throws ApplicationException {
						try {
							return getConn();
						} catch (Exception e) {
							throw new ApplicationException(e.getMessage(), e
									.getCause());
						}
					}

					@Override
					public boolean performFinish() {
						try {
							selection = fieldsPage.getSelectedFields();
						} catch (ApplicationException e) {
							e.printStackTrace();
						}
						return true;
					}

				});
		if (Window.OK == wd.open() && selection != null) {
			addFields();
		}

	}

	private void addFields() {
		@SuppressWarnings("unchecked")
		List<CompositeMap> list = selection.getChildsNotNull();
		for (CompositeMap m : list) {
			Record r = model.getRecordByName(m.getString("name"));
			if (r == null) {
				r = DesignerUtil.createRecord(m);
				model.add(r);
			}
		}
		((BMDesignPage) designer.getActivePageInstance()).refreshRecordViewer();
	}

	private IProject getAuroraProject() throws ResourceNotFoundException {
		IFile file = designer.getInputFile();
		IProject proj = file.getProject();
		AuroraMetaProject amp = new AuroraMetaProject(proj);
		return amp.getAuroraProject();
	}

	private Connection getConn() throws Exception {
		IProject ap = getAuroraProject();
		AuroraDataBase adb = new AuroraDataBase(ap);
		return adb.getDBConnection();
	}

	private void closeConn() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
	}
}
