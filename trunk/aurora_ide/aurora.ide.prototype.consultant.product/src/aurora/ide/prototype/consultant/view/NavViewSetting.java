package aurora.ide.prototype.consultant.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;

import aurora.ide.prototype.consultant.product.Activator;

public class NavViewSetting {

	public void addFolder(String folder) {
		String[] folders = getFolders();
		String[] n = new String[folders.length + 1];
		for (int i = 0; i < n.length - 1; i++) {
			n[i] = folders[i];
		}
		n[n.length - 1] = folder;
		putFolders(n);
	}

	public void removeFolder(String folder) {
		String[] folders = getFolders();
		List<String> arrayList = new ArrayList<String>();
		List<String> asList = Arrays.asList(folders);
		arrayList.addAll(asList);
		arrayList.remove(folder);
		String[] n = arrayList.toArray(new String[arrayList.size()]);
		putFolders(n);
	}

	public String[] getFolders() {
		String[] array = getDialogSetting().getArray("NAV_FOLDERS");
		return array == null ? new String[0] : array;
	}

	public void putFolders(String[] folders) {
		getDialogSetting().put("NAV_FOLDERS", folders);
	}

	private IDialogSettings getDialogSetting() {
		IDialogSettings section = Activator.getDefault().getDialogSettings()
				.getSection("CONSULTANT_NAV_VIEWER");
		if (section == null) {
			section = Activator.getDefault().getDialogSettings()
					.addNewSection("CONSULTANT_NAV_VIEWER");
		}
		return section;
	}

	public void addFolder(File f) {
		this.addFolder(f.getPath());
	}
}
