package aurora.ide.meta.gef.designer.wizard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.wizard.Wizard;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.designer.editor.LookupCodeUtil;

public class CreateSyscodeWizard extends Wizard {
	CreateSyscodeWizardPage page = new CreateSyscodeWizardPage();
	private CompositeMap codeMap;
	private List<CompositeMap> list;

	public CreateSyscodeWizard() {
		setWindowTitle("Create Syscode Wizard");
	}

	@Override
	public void addPages() {
		page.setCodeMap(codeMap);
		addPage(page);
	}

	public void setConnection(Connection conn) {
		if (codeMap != null && conn != null) {
			PreparedStatement stmt = null;
			try {
				stmt = conn
						.prepareStatement("select 1 from sys_codes where code=?");
				List<CompositeMap> list = codeMap.getChildsNotNull();
				for (CompositeMap m : list) {
					stmt.setString(1, LookupCodeUtil.getCode(m));
					ResultSet rs = stmt.executeQuery();
					m.put("exists", rs.next());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (stmt != null)
					try {
						stmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}
	}

	public void setSysCodeFile(IFile f) {
		if (LookupCodeUtil.isSyscodeFile(f)) {
			try {
				codeMap = LookupCodeUtil.load(f.getProject());
			} catch (ResourceNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean performFinish() {
		list = page.getCodeToCreate();
		return true;
	}

	public List<CompositeMap> getResult() {
		return list;
	}
}
