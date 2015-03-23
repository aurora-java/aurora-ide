package aurora.ide.statistics.viewer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;
import org.eclipse.ui.part.ViewPart;

import aurora.ide.AuroraPlugin;
import aurora.ide.api.statistics.Statistician;
import aurora.ide.api.statistics.map.StatisticsResult;
import aurora.ide.api.statistics.model.ProjectObject;
import aurora.ide.api.statistics.model.StatisticsProject;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.i18n.Messages;
import aurora.ide.search.ui.EditorOpener;
import aurora.ide.statistics.wizard.dialog.LoadDataWizard;
import aurora.ide.statistics.wizard.dialog.SaveDataWizard;

public class StatisticsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "aurora.ide.viewer.statistics.StatisticsView"; //$NON-NLS-1$

	static final private String[] pViewColTitles = { Messages.StatisticsView_Project_Name, Messages.StatisticsView_Value, Messages.StatisticsView_MAX_Value, Messages.StatisticsView_MIN_Value, Messages.StatisticsView_AVG_Value }; //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	static final private String[] pViewColTooltips = { Messages.StatisticsView_Project_Name, Messages.StatisticsView_Value_Of_Num, Messages.StatisticsView_MAX_Value, Messages.StatisticsView_MIN_Value, Messages.StatisticsView_AVG_Value }; //$NON-NLS-1$  //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

	static final private String[] oViewColTitles = { Messages.StatisticsView_Type, Messages.StatisticsView_File_Name, Messages.StatisticsView_Path, Messages.StatisticsView_File_Size, Messages.StatisticsView_Script_Size, Messages.StatisticsView_Tag_Num, Messages.StatisticsView_Reference, Messages.StatisticsView_Referenced }; //$NON-NLS-2$  //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
	static final private String[] oViewColTooltips = { Messages.StatisticsView_Type, Messages.StatisticsView_File_Name, Messages.StatisticsView_Path, Messages.StatisticsView_File_Size, Messages.StatisticsView_Script_Size, Messages.StatisticsView_Tag_Num, Messages.StatisticsView_Reference, Messages.StatisticsView_Referenced }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

	private TreeViewer projectViewer;
	private TreeViewer objectViewer;
	private ObjectViewerFilter filter;

	private Action fileSelectionAction;
	private Action projectSelectionAction;
	private Action saveToXLSAction;
	private Action saveToDBAction;
	private Action dbLoadAction;

	private Statistician statistician;
	private ObjectNode selectNode;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		// createViewer(parent); /* Create the example widgets */
		TabFolder tabFolder = new TabFolder(parent, SWT.TOP);

		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText("Objects"); //$NON-NLS-1$
		item.setToolTipText("Project Objects:bm,screen,svc..."); //$NON-NLS-1$
		Composite objectComp = new Composite(tabFolder, SWT.None);
		objectComp.setLayout(new GridLayout(2, false));
		Label lbl = new Label(objectComp, SWT.NONE);
		lbl.setText("文件名：");
		Text txt = new Text(objectComp, SWT.BORDER);
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				objectViewer.removeFilter(filter);
				filter = new ObjectViewerFilter(((Text) e.getSource()).getText());
				objectViewer.addFilter(filter);
				objectViewer.getControl().setRedraw(false);
				objectViewer.expandAll();
				objectViewer.getControl().setRedraw(true);
			}
		});
		createObjectViewer(objectComp);
		item.setControl(objectComp);
		// item.setControl(objectViewer.getControl());

		item = new TabItem(tabFolder, SWT.NONE);
		item.setText("Project"); //$NON-NLS-1$
		item.setToolTipText("Project Descripttion."); //$NON-NLS-1$
		createProjectViewer(tabFolder);
		item.setControl(projectViewer.getControl());

		makeActions();
		// hookContextMenu();
		contributeToActionBars();
		saveToDBAction.setEnabled(false);
		saveToXLSAction.setEnabled(false);
	}

	private void createObjectViewer(Composite parent) {
		objectViewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		objectViewer.getTree().setLayoutData(gd);
		objectViewer.setContentProvider(new ObjectViewContentProvider());
		objectViewer.setLabelProvider(new ObjectViewLabelProvider());
		objectViewer.addTreeListener(new TreeViewerAutoFitListener());
		filter = new ObjectViewerFilter("");
		objectViewer.addFilter(filter);
		Tree tree = objectViewer.getTree();
		for (int i = 0; i < oViewColTitles.length; i++) {
			TreeColumn treeColumn = new TreeColumn(tree, SWT.NONE);
			treeColumn.setMoveable(true);
			treeColumn.setResizable(true);
			treeColumn.setText(oViewColTitles[i]);
			treeColumn.setToolTipText(oViewColTooltips[i]);
			treeColumn.pack();
			treeColumn.addSelectionListener(new SelectionAdapter() {
				boolean asc = true;

				public void widgetSelected(SelectionEvent e) {
					TreeColumn tc = (TreeColumn) e.getSource();
					setSorter(tc.getText(), asc);
					asc = !asc;
					objectViewer.expandAll();
				}
			});
		}
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		objectViewer.expandAll();
		objectViewer.getTree().addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseDoubleClick(MouseEvent e) {
				if (e.button == 1 && selectNode != null) {
					ObjectNode osr = (ObjectNode) selectNode;
					try {
						IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(osr.path));
						EditorOpener editorOpener = new EditorOpener();
						editorOpener.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file, true);
					} catch (PartInitException e1) {
						DialogUtil.logErrorException(e1);
					}
				}
			}
		});

		objectViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				TreeSelection ts = (TreeSelection) event.getSelection();
				if (ts.getFirstElement() instanceof ObjectNode) {
					selectNode = (ObjectNode) ts.getFirstElement();
				}
			}
		});
	}

	private void setSorter(String text, boolean asc) {
		if (text.equals(Messages.StatisticsView_File_Name)) {
			objectViewer.setSorter(asc ? ObjectViewerSorter.FILENAME_ASC : ObjectViewerSorter.FILENAME_DESC);
		} else if (text.equals(Messages.StatisticsView_Path)) {
			objectViewer.setSorter(asc ? ObjectViewerSorter.PATH_ASC : ObjectViewerSorter.PATH_DESC);
		} else if (text.equals(Messages.StatisticsView_File_Size)) {
			objectViewer.setSorter(asc ? ObjectViewerSorter.FILESIZE_ASC : ObjectViewerSorter.FILESIZE_DESC);
		} else if (text.equals(Messages.StatisticsView_Script_Size)) {
			objectViewer.setSorter(asc ? ObjectViewerSorter.SCRIPTSIZE_ASC : ObjectViewerSorter.SCRIPTSIZE_DESC);
		} else if (text.equals(Messages.StatisticsView_Tag_Num)) {
			objectViewer.setSorter(asc ? ObjectViewerSorter.TAGCOUNT_ASC : ObjectViewerSorter.TAGCOUNT_DESC);
		} else if (text.equals(Messages.StatisticsView_Reference)) {
			objectViewer.setSorter(asc ? ObjectViewerSorter.REFINCOUNT_ASC : ObjectViewerSorter.REFINCOUNT_DESC);
		} else if (text.equals(Messages.StatisticsView_Referenced)) {
			objectViewer.setSorter(asc ? ObjectViewerSorter.REFOUTCOUNT_ASC : ObjectViewerSorter.REFOUTCOUNT_DESC);
		} else {
			return;
		}
	}

	private void createProjectViewer(Composite parent) {
		projectViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		projectViewer.setContentProvider(new ProjectViewContentProvider());
		projectViewer.setLabelProvider(new ProjectViewLabelProvider());
		projectViewer.addTreeListener(new TreeViewerAutoFitListener());
		Tree tree = projectViewer.getTree();
		for (int i = 0; i < pViewColTitles.length; i++) {
			TreeColumn treeColumn = new TreeColumn(tree, SWT.NONE);
			treeColumn.setMoveable(true);
			treeColumn.setResizable(true);
			treeColumn.setText(pViewColTitles[i]);
			treeColumn.setToolTipText(pViewColTooltips[i]);
			treeColumn.pack();
		}
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				StatisticsView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(objectViewer.getControl());
		this.objectViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, objectViewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(fileSelectionAction);
		manager.add(new Separator());
		manager.add(projectSelectionAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(fileSelectionAction);
		manager.add(projectSelectionAction);
		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(fileSelectionAction);
		manager.add(projectSelectionAction);
		manager.add(new Separator());
		manager.add(saveToXLSAction);
		manager.add(new Separator());
		manager.add(dbLoadAction);
		manager.add(saveToDBAction);
		manager.add(new Separator());

		// manager.add(new Action() {
		// public void run(){
		//
		// }
		// });
	}

	public void setInput(final StatisticsResult statisticsResult, Statistician statistician) {
		this.statistician = statistician;
		this.getSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				objectViewer.setInput(statisticsResult);
				objectViewer.expandAll();
				TreeViewerAutoFitListener.packColumns(objectViewer);

				projectViewer.setInput(statisticsResult);
				projectViewer.expandAll();
				TreeViewerAutoFitListener.packColumns(projectViewer);
			}
		});
	}

	private void makeActions() {
		saveToDBAction = new Action() {
			public void run() {
				if (statistician == null || StatisticsProject.NONE_PROJECT.equals(statistician.getProject()) || null == statistician.getProject().getEclipseProjectName()) {
					showMessage(Messages.StatisticsView_Can_Not_Save);
					return;
				}
				SaveDataWizard wizard = new SaveDataWizard(statistician);
				WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
				if (WizardDialog.OK == dialog.open()) {
					statistician.setProject(wizard.getProject());
					SaveToDBJob job = new SaveToDBJob(statistician, StatisticsView.this);
					job.setUser(true);
					job.schedule();
					setSaveToDBActionEnabled(false);
					setSaveToXLSActionEnabled(false);
				}
			}
		};
		saveToDBAction.setToolTipText(Messages.StatisticsView_Save_To_DB);
		saveToDBAction.setImageDescriptor(AuroraPlugin.getImageDescriptor("icons/export.png")); //$NON-NLS-1$

		dbLoadAction = new Action() {
			public void run() {
				LoadDataWizard wizard = new LoadDataWizard();
				WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
				int reslut = dialog.open();
				if (WizardDialog.OK == reslut) {
					LoadFromDBJob job = new LoadFromDBJob(wizard.getProject(), wizard.getStatisticsProject(), StatisticsView.this);
					job.setUser(true);
					job.schedule();
					setSaveToDBActionEnabled(false);
					setSaveToXLSActionEnabled(false);
				}
			}
		};
		dbLoadAction.setToolTipText(Messages.StatisticsView_Load_From_DB);
		dbLoadAction.setImageDescriptor(AuroraPlugin.getImageDescriptor("icons/import.png")); //$NON-NLS-1$

		fileSelectionAction = new Action() {
			public void run() {
				ResourceSelectionDialog dialog = new ResourceSelectionDialog(getSite().getShell(), AuroraPlugin.getWorkspace().getRoot(), Messages.StatisticsView_Select_Need_Statistics_File);
				dialog.setHelpAvailable(false);
				dialog.setTitle(Messages.StatisticsView_Select_File);
				int open = dialog.open();
				if (open == Dialog.OK) {
					Object[] selected = dialog.getResult();
					StatisticianRunner runner = new StatisticianRunner(StatisticsView.this);
					runner.noProjectRun(selected);
					setSaveToDBActionEnabled(false);
					setSaveToXLSActionEnabled(false);
				}
			}
		};
		fileSelectionAction.setText(Messages.StatisticsView_Select_File);
		fileSelectionAction.setToolTipText(Messages.StatisticsView_Select_Need_Statistics_File); //$NON-NLS-1$
		fileSelectionAction.setImageDescriptor(AuroraPlugin.getImageDescriptor("icons/file.png")); //$NON-NLS-1$

		projectSelectionAction = new Action() {
			public void run() {
				ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(getSite().getShell(), AuroraPlugin.getWorkspace().getRoot(), IResource.PROJECT);
				dialog.setHelpAvailable(false);
				dialog.setTitle(Messages.StatisticsView_Select_Project);
				int open = dialog.open();
				if (open == Dialog.OK) {
					Object[] selected = dialog.getResult();
					StatisticianRunner runner = new StatisticianRunner(StatisticsView.this);
					runner.projectRun(selected);
					setSaveToDBActionEnabled(false);
					setSaveToXLSActionEnabled(false);
				}
			}
		};
		projectSelectionAction.setText(Messages.StatisticsView_Select_Project); //$NON-NLS-1$
		projectSelectionAction.setToolTipText(Messages.StatisticsView_Select_Need_Statistics_Project);
		projectSelectionAction.setImageDescriptor(AuroraPlugin.getImageDescriptor("icons/project.png")); //$NON-NLS-1$

		saveToXLSAction = new Action() {
			public void run() {
				FileDialog dialog = new FileDialog(getSite().getShell(), SWT.SAVE);
				dialog.setFilterExtensions(new String[] { "*.xls", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
				final String path = dialog.open();
				if (path == null) {
					return;
				}
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						try {
							HSSFWorkbook workbook = new HSSFWorkbook();
							HSSFSheet sheet = workbook.createSheet();
							HSSFRow row = sheet.createRow(0);
							for (int i = 0; i < oViewColTitles.length; i++) {
								HSSFCell cell = row.createCell(i);
								cell.setCellType(HSSFCell.CELL_TYPE_STRING);
								cell.setCellValue(oViewColTitles[i]);
							}
							fillExcelContent(sheet);
							sheet.autoSizeColumn(1);
							sheet.autoSizeColumn(2);
							FileOutputStream fOut = new FileOutputStream(path);
							workbook.write(fOut);
							fOut.flush();
							fOut.close();
						} catch (IOException e) {
							DialogUtil.logErrorException(e);
						}
					}
				});
			}

			private void fillExcelContent(HSSFSheet sheet) {
				HSSFRow row;
				StatisticsResult result = (StatisticsResult) objectViewer.getInput();
				List<ProjectObject> listAll = result.getProjectObjects();
				for (int i = 0; i < listAll.size(); i++) {
					row = sheet.createRow(i + 1);
					HSSFCell cell = row.createCell(0);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(listAll.get(i).getType());

					cell = row.createCell(1);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(listAll.get(i).getName());

					cell = row.createCell(2);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(listAll.get(i).getPath());

					cell = row.createCell(3);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(conversion(listAll.get(i).getFileSize()));

					cell = row.createCell(4);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(conversion(listAll.get(i).getScriptSize()));

					cell = row.createCell(5);
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellValue(listAll.get(i).getTags().size());

					cell = row.createCell(6);
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellValue(listAll.get(i).getDependencies().size());

					cell = row.createCell(7);
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellValue(listAll.get(i).getReferenced());
				}
			}

			private String conversion(int num) {
				String value = Integer.toString(num);
				DecimalFormat df = new DecimalFormat("0.00"); //$NON-NLS-1$
				double v = Double.parseDouble(value);
				if (value.length() > 3 && value.length() <= 6) {
					v /= 1024.0;
					return df.format(v) + " KB"; //$NON-NLS-1$
				} else if (value.length() > 6) {
					v /= (1024.0 * 1024.0);
					return df.format(v) + " MB"; //$NON-NLS-1$
				} else {
					return (int) v + " Byte"; //$NON-NLS-1$
				}
			}
		};
		saveToXLSAction.setText(Messages.StatisticsView_Export_Excel);
		saveToXLSAction.setToolTipText(Messages.StatisticsView_Export_Excel); //$NON-NLS-1$
		saveToXLSAction.setImageDescriptor(AuroraPlugin.getImageDescriptor("icons/palette/toolbar_btn_05.png")); //$NON-NLS-1$
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(objectViewer.getControl().getShell(), "Sample View", message); //$NON-NLS-1$
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		objectViewer.getControl().setFocus();
	}

	public void setSaveToDBActionEnabled(boolean bool) {
		saveToDBAction.setEnabled(bool);
	}

	public void setSaveToXLSActionEnabled(boolean bool) {
		saveToXLSAction.setEnabled(bool);
	}
}
