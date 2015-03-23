package aurora.ide.celleditor;


import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.bm.editor.GridDialog;
import aurora.ide.editor.widgets.GridViewer;
import aurora.ide.editor.widgets.core.IGridViewer;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.ProjectUtil;

public class ModelReferenceCellEditor extends StringTextCellEditor {

	public ModelReferenceCellEditor(CellInfo cellProperties) {
		super(cellProperties);
	}


	protected void addCellListener() {
		getCellControl().addMouseListener(new MouseListener() {
			
			public void mouseUp(MouseEvent e) {
			}
			
			public void mouseDown(MouseEvent e) {
				try {
					fireEvent();
				} catch (Exception e1) {
					DialogUtil.showExceptionMessageBox(e1);
				}
				
			}
			
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		getCellControl().addFocusListener(new FocusListener() {
			
			public void focusLost(FocusEvent e) {
				if(isTableItemEditor())
					rebuildCellEditor(cellProperties.getTableItem());				
			}
			
			public void focusGained(FocusEvent e) {
//				try {
//					fireEvent();
//				} catch (Exception e1) {
//					DialogUtil.logErrorException(e1);
//				}
			}
		});
	}
	private void fireEvent() throws ApplicationException{

		IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
		IFile ifile = ((IFileEditorInput) input).getFile();
		IProject project = ifile.getProject();
		String bmFilesDir = ProjectUtil.getBMHomeLocalPath(project);
		File baseDir = new File(bmFilesDir);
		String fullPath = baseDir.getAbsolutePath();
		CompositeMap bmFiles = getAllBMFiles(baseDir,fullPath);
		
		String[] columnProperties = {"name","fullpath"};
		GridViewer grid = new GridViewer(null,IGridViewer.filterBar|IGridViewer.NoToolBar);
		grid.setData(bmFiles);
		grid.setFilterColumn("name");
		grid.setColumnNames(columnProperties);
		GridDialog dialog = new GridDialog(new Shell(),grid);
		if (dialog.open() == Window.OK) {
			String value = dialog.getSelected().getString("fullpath");
			setValue(value);
			if(isTableItemEditor()){
				cellProperties.getRecord().put(cellProperties.getColumnName(), value);
			}else{
				TableItem item =cellProperties.getTableViewer().getViewer().getTable().getSelection()[0];
				CompositeMap data = (CompositeMap)item.getData();
				data.put(cellProperties.getColumnName(), value);
			}
			cellProperties.getTableViewer().refresh(true);
		}
	}


	private CompositeMap getAllBMFiles(File rootFile,String fullPath) {
		CompositeMap bmFiles = new CommentCompositeMap();
		getChilds(rootFile,bmFiles,fullPath);
		return bmFiles;
		
	}
	private void getChilds(File file,CompositeMap parent,String fullPath){
		if(file.isDirectory()){
			File[] nextLevel = file.listFiles();
			for(int i = 0;i<nextLevel.length;i++){
				getChilds(nextLevel[i],parent,fullPath);
			}
		}
		else if(file.getName().toLowerCase().endsWith(".bm")){
			CompositeMap child = new CommentCompositeMap();
			String fullpath = getClassName(file,fullPath);
			child.put("name",file.getName());
			child.put("fullpath",fullpath);
			parent.addChild(child);
		}
	}


	private String getClassName(File file,String fullpath) {
		String path = file.getPath();
		int end = path.lastIndexOf(".");
		path = path.substring(fullpath.length()+1,end);
		path = path.replace(File.separatorChar, '.');
		return path;
	}
	private boolean isTableItemEditor(){
		return cellProperties.getTableItem() != null;
	}
}
